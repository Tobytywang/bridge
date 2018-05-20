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

import com.happylich.bridge.game.main.Game;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by lich on 2018/5/16.
 */

public class WifiBroadcastThread extends Thread {

    private Game game;
    private WifiInfo mWifiInfo;
    private WifiManager.MulticastLock mMulticastLock;
    private String ip;
    private String message;
    private static int BROADCAST_PORT = 8003;
    private static String BROADCAST_IP = "224.0.0.1";

    InetAddress     mInetAddress = null;
    MulticastSocket mMulticastSocket = null;
    DatagramPacket  mDatagramPacket = null;

    byte[] data = new byte[1024];

    // 游戏线程运行开关
    private boolean running = false;
    private final static int DELAY_TIME = 500;
    private boolean isPaused = false;

    /**
     * 线程构造函数
     */
    public WifiBroadcastThread(WifiManager mWifiManager) {
        // 自动获得IP地址
        if (mWifiManager.isWifiEnabled()) {
            Log.v(this.getClass().getName(), "Wifi启用了");
            mWifiInfo = mWifiManager.getConnectionInfo();
            ip = getIpString(mWifiInfo.getIpAddress());
        }
        Log.v(this.getClass().getName(), "Wifi没有启用");
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

    public void setMulticastLock (WifiManager.MulticastLock mMulticastLock) {
        this.mMulticastLock = mMulticastLock;
    }

    /**
     * 线程更新函数
     */
    public void run() {

        // 这里是广播IP
        // 视情况的不同，这个类还可能广播
        // 1. 房间的情况
        //   0. 未满员：可以点击加入
        //   1. 满员：不可以加入了
        //   2. 游戏中：游戏已经开始
        // 2. 心跳检测
        //   1. 依次向连接的客户端发起连接请求
        while(running) {
            if (!isPaused) {
                mMulticastLock.acquire();
                try {
                    // 先加入，后发送原则
                    mMulticastSocket = new MulticastSocket(8003);
                    mInetAddress = InetAddress.getByName("239.0.0.1");
                    mMulticastSocket.joinGroup(mInetAddress);
                    message = ip + " " + String.valueOf(game.getGameState());
                    Log.v(this.getClass().getName(), message);
                    data = message.getBytes("utf-8");
                    mDatagramPacket = new DatagramPacket(data, data.length, mInetAddress, BROADCAST_PORT);
                    mMulticastSocket.send(mDatagramPacket);
                    Thread.sleep(DELAY_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mMulticastSocket.close();
                    mMulticastLock.release();
                }
            }
        }
    }
}
