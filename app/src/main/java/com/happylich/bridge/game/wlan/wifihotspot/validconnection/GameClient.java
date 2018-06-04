package com.happylich.bridge.game.wlan.wifihotspot.validconnection;

/**
 * Created by lich on 2018/5/21.
 */

import android.util.Log;

import com.happylich.bridge.game.main.Game;

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
 *
 * 1. 客户端只能维持一个Socket
 * 2. 服务端需要维持多个Socket
 */
public class GameClient extends Thread {

    private Boolean running = false;
    private String serverIP;
    private Game game;

    public GameClient(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }


    @Override
    public void run() {
        this.game.mHandler.sendEmptyMessage(234);
        while(running) {
            if (this.game.socket == null) {
                try {
                    this.game.mHandler.sendEmptyMessage(235);
                    game.socket = new Socket(serverIP, 8003);
//                    game.socket.setTcpNoDelay(true);
                    this.game.mHandler.sendEmptyMessage(236);
                } catch (Exception e) {
                }
            }
            // TODO:设定一个通知机制通知连接失败
        }
    }
}
