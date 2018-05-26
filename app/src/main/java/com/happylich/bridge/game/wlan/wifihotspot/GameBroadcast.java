package com.happylich.bridge.game.wlan.wifihotspot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by lich on 2018/5/24.
 */

public class GameBroadcast implements Runnable {
    Socket s =null;
    BufferedReader br =null;

    public GameBroadcast(Socket s) throws IOException {
        this.s = s;
        // 初始化该Socket对应的输入流
        br =new BufferedReader(new InputStreamReader(s.getInputStream() ,"utf-8"));
    }

    public void run() {
        try {
            String content =null;
            // 采用循环不断从Socket中读取客户端发送过来的数据
            while ((content = readFromClient()) !=null)
            {
                // 遍历socketList中的每个Socket，
                // 将读到的内容向每个Socket发送一次
                for (Socket s : GameServer.sockets) {
                    OutputStream os = s.getOutputStream();
                    os.write((content +"\n").getBytes("utf-8"));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 定义读取客户端数据的方法

    private String readFromClient() {
        try {
            return br.readLine();
        } catch (IOException e) {
            // 删除该Socket。
            GameServer.sockets.remove(s);
        }
        return null;
    }
}
