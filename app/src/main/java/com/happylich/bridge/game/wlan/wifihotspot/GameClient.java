package com.happylich.bridge.game.wlan.wifihotspot;

/**
 * Created by lich on 2018/5/21.
 */

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 扮演客户端的角色，向服务器传输数据，从服务器接收数据
 *
 * 1. 向服务器发送消息
 * 2. 接收服务器发送的消息
 */
public class GameClient extends Thread {

    private Boolean running = false;
    private Socket socket;
    public Socket getSocket() {
        return this.socket;
    }

    private String serverIP;

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

    /**
     * 构造函数
     */
    public GameClient(String serverIP) {
        this.serverIP = serverIP;
//        connectToServer(serverIP);
    }

    public void setRunning(Boolean running) {
        this.running = running;
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

//    in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
//    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
//            mSocket.getOutputStream())), true);

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

    @Override
    public void run() {
        while(running) {
            try {
                // 创建Socket对象 & 指定服务端的IP 及 端口号
                this.socket = new Socket(serverIP, 8003);
//                socket.connect();
                // 判断客户端和服务器是否连接成功
    //            System.out.println(socket.isConnected());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (socket != null) {
                try {
                    Log.v(this.getClass().getName(), String.valueOf(socket.isConnected()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                running = false;
            }
            // TODO:设定一个通知机制通知连接失败
        }
    }
}
