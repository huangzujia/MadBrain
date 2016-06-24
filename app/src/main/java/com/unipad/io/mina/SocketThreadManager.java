package com.unipad.io.mina;

import android.text.TextUtils;

import com.unipad.AppContext;
import com.unipad.ICoreService;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.common.Constant;
import com.unipad.common.MobileInfo;
import com.unipad.http.HitopDownLoad;
import com.unipad.http.HitopGetQuestion;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class SocketThreadManager implements ClientSessionHandler.IDataHandler {

    private static SocketThreadManager s_SocketManager = null;


    private SocketOutputThread mOutThread = null;

    private AbsBaseGameService service;

    // 获取单例
    public static SocketThreadManager sharedInstance() {
        if (s_SocketManager == null) {
            s_SocketManager = new SocketThreadManager();
            s_SocketManager.startThreads();
        }
        return s_SocketManager;
    }

    // 单例，不允许在外部构建对象
    private SocketThreadManager() {
        mOutThread = new SocketOutputThread();
        LongTcpClient.instant().setDataHandler(this);
    }

    /**
     * 启动线程
     */

    private void startThreads() {
        mOutThread.start();
        // mDnsthread.start();
    }

    /**
     * stop线程
     */
    public void stopThreads() {
        mOutThread.setStart(false);
    }

    public void releaseInstance() {
        LongTcpClient.instant().release();

        if (s_SocketManager != null) {
            s_SocketManager.stopThreads();
            s_SocketManager = null;
        }
    }


    public void sendMsg(Request request) {

        mOutThread.addMsgToSendList(request);
    }

    public void signOK(String id) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("USERID", AppContext.instance().loginUser.getUserId());
        body.put("SCHEDULEID", id);
        body.put("DEVICE", MobileInfo.getDeviceId());
        Request request = new Request("10001", body);
        sendMsg(request);
    }
    public void downLoadQuestionOK(String id) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("USERID", AppContext.instance().loginUser.getUserId());
        body.put("SCHEDULEID", id);
        Request request = new Request(IOConstant.LOAD_QUSETION_END, body);
        sendMsg(request);
    }
    @Override
    public void processPack(IPack pack) {
        handPack((Response) pack);
    }

    private void handPack(Response response) {
        Map<String, String> data = response.getDatas();
        if (IOConstant.SEND_QUESTIONS.equals(data.get("TRXCODE"))) {//收到服务器下发试题的通知
            handDownQuestion(data);
        }else if(IOConstant.GAME_START.equals(data.get("TRXCODE"))){
            if (service != null) {
                service.startGame();
            }
        }else if(IOConstant.GAME_PAUSE.equals(data.get("TRXCODE"))){
            if (service != null) {
                service.pauseGame();
            }
        }else if (IOConstant.GAME_RESTART.equals(data.get("TRXCODE"))){
            if (service != null) {
                service.reStartGame();
            }
        }
    }

    private void handDownQuestion(Map<String, String> data) {
        String projectId = data.get("PROJECTID");
        if (Constant.GAME_ABS_PICTURE.equals(projectId) || Constant.GAME_LISTON_AND_MEMORY_WORDS.equals(projectId)
                || Constant.GAME_PORTRAITS.equals(projectId)) {
            String fileDir = Constant.GAME_FILE_PATH;
            HitopDownLoad httpDown = new HitopDownLoad();
            httpDown.buildRequestParams("questionId", data.get("QUESTIONID"));
            String filePath;
            String fileData = data.get("VOICE");
            if (TextUtils.isEmpty(fileData)) {
                filePath = fileDir + "/question.zip";

            } else {
                String taile = fileData.split(".")[1];
                filePath = fileDir + "/voice" + taile;

            }
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            httpDown.setService(service);
            httpDown.downLoad(filePath);
        }
        HitopGetQuestion httpGetQuestion = new HitopGetQuestion();
        httpGetQuestion.buildRequestParams("questionId", data.get("QUESTIONID"));
        httpGetQuestion.setService(service);
        httpGetQuestion.post();
    }

    public void clear() {

    }

    public void setService(AbsBaseGameService service) {
        this.service = service;
    }
}
