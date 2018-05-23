package com.happylich.bridge.game.player;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by lich on 2018/5/14.
 *
 * 这个类用于和远程玩家的通讯（Remote,Player)
 */

public class RemotePlayer extends AbstractPlayer {


    // IP
    // 通过记录该客户端的IP进行通讯

    private boolean inOrder;



    private Socket socket;
    /**
     * 接收服务器消息 变量
     */
    // 输入流对象
    InputStream mInputStream;

    // 输入流读取器对象
    InputStreamReader mInputStreamReader ;
    BufferedReader mBufferedReader ;

    // 接收服务器发送过来的消息
    String response;

    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;

    public RemotePlayer(Socket socket) {
        this.socket = socket;
    }

    /**
     * 玩家是否就绪
     * @return
     */
    @Override
    public boolean isInOrder() {
        return inOrder;
    }

    /**
     * 切换就绪状态
     * @param inOrder
     * @return
     */
    public void setInOrder(boolean inOrder) {
        this.inOrder = inOrder;
    }

    @Override
    public boolean callCard() {
        return false;
    }

    @Override
    public boolean dropCard() {
        return false;
    }

    // 向服务器发送消息
    public void sendToServer(String msg) {
        try {
            outputStream = socket.getOutputStream();
            outputStream.write((msg+"\n").getBytes("utf-8"));
            outputStream.flush();
        } catch (Exception e) {

        }
    }

    /**
     * 接收服务器发送的消息
     * @return
     */
    public String getFromServer() {
        try {
            mInputStream = socket.getInputStream();
            mInputStreamReader = new InputStreamReader(mInputStream);
            mBufferedReader = new BufferedReader(mInputStreamReader);
            response = mBufferedReader.readLine();

            // 步骤4:通知主线程,将接收的消息显示到界面
//            Message msg = Message.obtain();
//            msg.what = 0;
//            mMainHandler.sendMessage(msg);
        } catch (Exception e){
        }
        return response;
    }
}
