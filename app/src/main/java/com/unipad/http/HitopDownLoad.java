package com.unipad.http;

import android.util.Log;

import com.lidroid.xutils.http.ResponseInfo;
import com.unipad.ICoreService;
import com.unipad.brain.AbsBaseGameService;
import com.unipad.common.Constant;
import com.unipad.io.mina.SocketThreadManager;
import com.unipad.utils.FileUtil;
import com.unipad.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by gongkan on 2016/6/21.
 */
public class HitopDownLoad extends HitopRequest<File>{

    public HitopDownLoad(String path) {
        super(path);
    }

    private AbsBaseGameService service;
    public HitopDownLoad(){
        super(HttpConstant.DOWNLOAD_QUESTION);
    }
    private String matchId;
    @Override
    public String buildRequestURL() {
        return null;
    }

    public void downLoad(String name){
        final String filePath = Constant.GAME_FILE_PATH+File.separator+name;
                File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
        //设置断点续传
        mParams.setAutoResume(true);
        mParams.setSaveFilePath(filePath);
      x.http().get(mParams, new Callback.ProgressCallback<File>() {


          @Override
          public void onSuccess(File file) {
              if (file.getName().endsWith(".zip")) {
                  FileUtil.upZip(filePath);

              }

              if (service != null) {
                  service.initResourse(file.getAbsolutePath());
                  service.setIsInitResourseAready(true);
              }
          }

          @Override
          public void onError(Throwable throwable, boolean b) {
              if (throwable instanceof HttpException) {
                  ToastUtil.showToast("网络错误");
              }
          }

          @Override
          public void onCancelled(CancelledException e) {
              Log.e("", "onCancelled");

          }

          @Override
          public void onFinished() {
              Log.e("", "onFinished");
          }

          @Override
          public void onWaiting() {
              Log.e("", "onWaiting");
          }

          @Override
          public void onStarted() {
              Log.e("", "onStarted");
          }

          @Override
          public void onLoading(long total, long current, boolean isDownloading) {
              Log.e("", "total = " + total + ",current=" + current + ",isDownloading=" + isDownloading);
              if (total != current) {
                  int progress = (int) (current*100/total);
                  SocketThreadManager.sharedInstance().downLoadQuestionOK(matchId,progress);
              }
          }
      });

    }
    @Override
    public File handleJsonData(String json) {
        return null;
    }

    @Override
    public void buildRequestParams() {

    }

    public void setService(AbsBaseGameService service) {
        this.service = service;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
}
