package com.happylich.bridge.game.wlan.wifihotspot.validconnection;

/**
 * Created by lich on 2018/5/21.
 */

import android.os.Message;
import android.widget.Toast;

import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.wlan.wifihotspot.transmitdata.GameServerReceiveDataThread;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * 这个类负责跟服务器通讯？或者服务器和客户端进行通信？
 *
 * 这一层的主要任务是确保连接的有效性
 * 1. 服务端保持对所有客户端的socket引用
 * 2. 客户端维持对服务端的socket引用
 * 3. 服务器向客户端发送心跳检测
 *
 */
public class GameServer extends Thread {
    Game game;

    private boolean running = false;
    private boolean paused = false;

    private ServerSocket serverSocket;


    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象

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

    public void setGame(Game game) {
        this.game = game;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        this.game.mHandler.sendEmptyMessage(233);
        while (running) {
            if (game.getReady().hasFreePlace()) {
                if (serverSocket != null) {
                    try {
                        this.game.mHandler.sendEmptyMessage(244);
                        Socket socket = serverSocket.accept();

                        this.game.mHandler.sendEmptyMessage(245);
//                        Toast.makeText(this.game.context, "有客户端加入", Toast.LENGTH_SHORT).show();
                        GameServerReceiveDataThread mGameServerReceiveDataThread = new GameServerReceiveDataThread();
                        mGameServerReceiveDataThread.setSocket(socket);
                        mGameServerReceiveDataThread.setGame(this.game);

                        game.sockets.add(socket);

                        this.game.mHandler.sendEmptyMessage(246);
                        mGameServerReceiveDataThread.setRunning(true);
                        mGameServerReceiveDataThread.start();

                        // 客户端刚刚接入服务器时，向客户端发送服务器玩家设置
                        Message message = new Message();
                        message.what = 0;
                        message.obj = "players";
                        game.mHandler.sendMessage(message);
                    } catch (Exception e) {
                        //                        e.printStackTrace();
                    }
                } else {

                }
            }
        }
    }
}
