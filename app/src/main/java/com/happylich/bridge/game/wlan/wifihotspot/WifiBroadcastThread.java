package com.happylich.bridge.game.wlan.wifihotspot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.happylich.bridge.engine.game.Game;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by lich on 2018/5/16.
 */

public class WifiBroadcastThread extends Thread {

    private Game game;
    private WifiInfo mWifiInfo;
    private String ip;
    private static int BROADCAST_PORT = 8003;
    private static String BROADCAST_IP = "224.0.0.1";

    InetAddress inetAddress = null;
    MulticastSocket mMulticastSocket = null;

    // 游戏线程运行开关
    private boolean running = false;
    private final static int DELAY_TIME = 5000;
    private boolean isPaused = false;

    /**
     * 线程构造函数
     */
    public WifiBroadcastThread(WifiManager mWifiManager) {
        // 自动获得IP地址
        if (mWifiManager.isWifiEnabled()) {
            mWifiInfo = mWifiManager.getConnectionInfo();
            ip = getIpString(mWifiInfo.getIpAddress());
        }
    }

    /**
     * IP转换函数
     * @param i
     * @return
     */
    public String getIpString(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    /**
     * 设置游戏类
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * 线程运行标志设置函数（设置为false后停止线程）
     * @param state
     */
    public void setRunning (boolean state) {
        running = state;
    }

    /**
     * 线程更新函数
     */
    public void run() {
        DatagramPacket dataPacket = null;

        // 这里是广播IP
        // 视情况的不同，这个类还可能广播
        // 1. 房间的情况
        //   1. 未满员：可以点击加入
        //   2. 满员：不可以加入了
        //   3. 游戏中：游戏已经开始
        // 2. 心跳检测
        //   1. 依次向连接的客户端发起连接请求
        byte[] data = ip.getBytes();
        dataPacket = new DatagramPacket(data, data.length, inetAddress, BROADCAST_PORT);
        while(true) {
            if(running) {
                try {
                    mMulticastSocket.send(dataPacket);
                    Thread.sleep(DELAY_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
