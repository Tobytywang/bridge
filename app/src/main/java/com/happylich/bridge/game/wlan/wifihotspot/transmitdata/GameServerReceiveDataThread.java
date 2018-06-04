package com.happylich.bridge.game.wlan.wifihotspot.transmitdata;

/**
 * Created by lich on 2018/6/3.
 */

import android.os.Message;

import com.happylich.bridge.game.main.Game;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 负责接收消息
 */
public class GameServerReceiveDataThread extends Thread {
    private Socket socket;
    private boolean running = false;
    private InputStream mInputStream;
    private InputStreamReader mInputStreamReader;
    private BufferedReader mBufferReader;
    private String response;
    private Game game;

    public GameServerReceiveDataThread() {
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        while(running) {
            try {
                // 只负责监听，发送工作交给GameTransmitData
                // 发送给除了发送者之外的其他socket
                // 还是在接收时选择过滤发送给自己的消息
                // 应该开三个线程监听三个不同的
                mInputStream = socket.getInputStream();
                mInputStreamReader = new InputStreamReader(mInputStream);
                mBufferReader = new BufferedReader(mInputStreamReader);
                response = mBufferReader.readLine();

//                String[] message = response.split(" ");
//                String type = message[0];
//                String content = message[1];
//                game.onMessage(type, content);

                // 接收到消息后，使用handler处理
                Message message1 = new Message();
                message1.what = 0;
                message1.obj = response;
                game.mHandler.sendMessage(message1);
            } catch (Exception e){
            }
        }
    }
}
