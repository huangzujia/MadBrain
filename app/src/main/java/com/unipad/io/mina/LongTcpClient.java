package com.unipad.io.mina;

import android.widget.Toast;

import com.unipad.brain.App;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class LongTcpClient implements ClientSessionHandler.IDataHandler {

    private static LongTcpClient instance;

    private static final String HOSTNAME = "192.168.0.104";

    private static final int PORT = 7003;

    private static final long CONNECT_TIMEOUT = 30 * 1000L; // 30 seconds

    /**
     * 15�뷢��һ�������
     */
    private static final int HEARTBEATRATE = 15;

    private ClientSessionHandler handler;

    private static boolean isInstanceed;

    private  NioSocketConnector connector;

    public IoSession session;

    private ClientSessionHandler.IDataHandler dataHandler;

    private LongTcpClient() {

    }

    public static LongTcpClient instant() {
        if (instance == null || !isInstanceed) {
            synchronized (LongTcpClient.class) {
                if (instance == null) {
                    instance = new LongTcpClient();
                    instance.init();
                    isInstanceed = true;
                }
            }
        }

        return instance;
    }

    public boolean init() {
        if(!isInstanceed) {
            connector = new NioSocketConnector();
            // Configure the service.
            connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
            handler = new ClientSessionHandler(this);
            connector.setHandler(handler);
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("GBK"))));
//        connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
            connector.getFilterChain().addLast("log", new LoggingFilter());
            ClientKeepAliveMessageFactoryImp heartBeatFactory = new ClientKeepAliveMessageFactoryImp();

            KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,
                    IdleStatus.BOTH_IDLE);

            //�����Ƿ�forward����һ��filter
            heartBeat.setForwardEvent(true);
            //��������Ƶ��
            heartBeat.setRequestInterval(HEARTBEATRATE);
            connector.getFilterChain().addLast("keeplive", new KeepAliveFilter(new ClientKeepAliveMessageFactoryImp(), IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.DEAF_SPEAKER, 10, 5));

            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        HOSTNAME, PORT));
                future.awaitUninterruptibly();// 等待连接创建完成
                session = future.getSession();
                if (session.isConnected()) {

                } else {

                }
                isInstanceed = true;
            } catch (RuntimeIoException e) {
                System.err.println("Failed to connect.");
                e.printStackTrace();

            }
        }
        return isInstanceed;
    }

    public void release(){
        disConnect();
        if (instance != null) {
            isInstanceed = false;
            instance = null;
        }
    }

    public boolean reConnect() {
        boolean connect = false;
        ConnectFuture future = connector.connect(new InetSocketAddress(
                HOSTNAME, PORT));

        future.awaitUninterruptibly();// 等待连接创建完成
        session = future.getSession();
        if (session.isConnected()) {
            connect = true;
        } else {
            Toast.makeText(App.getContext(), "请检查网络", Toast.LENGTH_SHORT).show();
        }

        return connect;
    }

    public void sendMsg(String data) {
        if (session != null) {
            session.write(data);
        }
    }
    public void disConnect() {
        if (handler != null) {
            handler.setIsReconect(false);
        }
        if (session != null) {
            session.closeNow();
        }
    }
    public void sendMsg(Request request) {
        if (request != null) {
            request.sendMsg(session);
        }
    }

    @Override
    public void processPack(IPack pack) {
        if (dataHandler != null) {
            dataHandler.processPack(pack);
        }
    }

    public void setDataHandler(ClientSessionHandler.IDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

}
