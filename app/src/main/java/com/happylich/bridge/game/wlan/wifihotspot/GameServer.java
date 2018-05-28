package com.happylich.bridge.game.wlan.wifihotspot;

/**
 * Created by lich on 2018/5/21.
 */

import android.content.Context;
import android.util.Log;

import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.player.RemotePlayer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * 这个类负责跟服务器通讯？或者服务器和客户端进行通信？
 *
 * GameServer类需要将消息发送给所有客户端
 * 1. 监听所有客户端
 * 2. 给所有客户端发送消息
 *
 * 1. 怎么区分不同的socket
 * 2. 加入游戏之后
 *   1. 分配direction给客户端
 *   2. 将其他玩家的状态广播到所有客户端
 * 3. 开始游戏之后
 */
public class GameServer extends Thread {

    protected Context context;
    protected Game game;
    protected String ip;

    private boolean running = false;
    private boolean paused = false;

    private ServerSocket serverSocket;
    public static ArrayList<Socket> sockets = new ArrayList<>();

    /**
     * 接收服务器消息 变量
     */
    // 输入流对象
    InputStream mInputStream;

    // 输入流读取器对象
    InputStreamReader mInputStreamReader ;
    BufferedReader mBufferReader ;

    // 接收服务器发送过来的消息
    String response;

    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream mOutputStream;

    public GameServer() {
        try {
            this.serverSocket = new ServerSocket(8003, 3);
        } catch (Exception e){
//            e.printStackTrace();
        }
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public void setIP(String gameIP) {
        this.ip = gameIP;
    }

    /**
     * 设置game引用
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
    }


    public void setRunning(Boolean running) {
        this.running = running;
    }

    public void setPaused(Boolean paused) {
        this.paused = paused;
    }

    public void closeSockets() {
        for (Socket sokt:sockets) {
            if (sokt != null) {
                try {
                    sokt.close();
                } catch (Exception e) {

                }
            }
        }
    }
    // 向服务器发送消息
    public void sendToClients(Socket socket, String msg) {
        try {
            mOutputStream = socket.getOutputStream();
            mOutputStream.write((msg+"\n").getBytes("utf-8"));
            mOutputStream.flush();
        } catch (Exception e) {

        }
    }
    /**
     * 接收服务器发送的消息
     * @return
     */
    public String getFromClient(Socket socket) {
        try {
//            Socket socket = serverSocket.accept();
            mInputStream = socket.getInputStream();
            mInputStreamReader = new InputStreamReader(mInputStream);
            mBufferReader = new BufferedReader(mInputStreamReader);
            response = mBufferReader.readLine();

            for (Socket sokt:sockets) {
                if (sokt != null) {
                    String[] messageList = response.split(" ");
                    String ip = messageList[0];
                    String msg = messageList[1];
                    switch (msg) {
                        case "ready":
                            // 表示客户端就绪了
                            break;
                        case "unready":
                            // 表示客户端不准备了
                            break;
                    }
                }
            }
            // 步骤4:通知主线程,将接收的消息显示到界面
//            Message msg = Message.obtain();
//            msg.what = 0;
//            mMainHandler.sendMessage(msg);
        } catch (Exception e){
        }
        return response;
    }

    /**
     * 获取某一个（怎么区分某一个？）
     */
//    public Socket getSocketNum(int socketNum) {
//        if (sockets.get(socketNum) instanceof Socket) {
//            return sockets.get(socketNum);
//        }
//        return null;
//    }

    @Override
    public void run() {
        while (running) {
            if (!paused) {
                // 当客户端没有满的时候，接收来自外界的连接
                if (game.getReady().hasFreePlace()) {
                    if (serverSocket != null) {
                        try {
                            Socket sokt = serverSocket.accept();
                            // 把这个socket传给一个随机的用户
//                            game.getReady().setRealPlayer(new RemotePlayer(sokt));
                        } catch (Exception e) {
                            //                        e.printStackTrace();
                        }
                    } else {

                    }
                }
            }
        }
    }
}
