package com.happylich.bridge.game.wlan.wifihotspot;

import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.happylich.bridge.engine.game.Game;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by lich on 2018/5/17.
 */

/**
 * 这个类监听局域网中发送"IP + 状态"UDP包
 * 将这些IP和状态显示在列表中
 *
 * 点击列表后，向服务器发送连接请求
 * 客户端所有的请求都发送给服务器å
 */
public class WifiBroadcastReceiverThread extends Thread {

    private WifiInfo mWifiInfo;
    private String ip;
    private static int BROADCAST_PORT = 8003;
    private static String BROADCAST_IP = "224.0.0.1";

    InetAddress     mInetAddress = null;
    MulticastSocket mMulticastSocket = null;
    DatagramPacket  mDatagramPacket = null;

    // 游戏线程运行开关
    private boolean running = false;
    private final static int DELAY_TIME = 5000;
    private boolean isPaused = false;

    byte[] data = new byte[1024];
    /**
     * 线程构造函数
     */
    public WifiBroadcastReceiverThread(WifiManager mWifiManager) {
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
        try {
            InetAddress groupAddress = InetAddress.getByName("224.0.0.1");
            mMulticastSocket = new MulticastSocket(8003);
            mMulticastSocket.joinGroup(groupAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        byte[] data = ip.getBytes();
//        dataPacket = new DatagramPacket(data, data.length, inetAddress, BROADCAST_PORT);
        while(running) {
            if(!isPaused) {
                try {
                    mDatagramPacket = new DatagramPacket(data, data.length);
                    if (mMulticastSocket != null) {
                        mMulticastSocket.receive(mDatagramPacket);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 获得发送的地址和数据
                mDatagramPacket.getAddress();
                mDatagramPacket.getData();
//                if(mDatagramPacket.getAddress() != null) {
//                    final String quest_ip = dataPacket.getAddress().toString();
//                }
            }
        }
    }
}
