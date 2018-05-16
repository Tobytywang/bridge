package com.happylich.bridge.game.activity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.happylich.bridge.R;
import com.happylich.bridge.engine.view.GameView;
import com.happylich.bridge.game.main.Cards;
import com.happylich.bridge.game.main.Direction;
import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.player.ProxyPlayer;
import com.happylich.bridge.game.player.Robot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

/**
 * Created by lich on 2018/4/23.
 */

/**
 * 创建基于Wifi热点的游戏
 *
 * 要做的工作有：
 * 1. 新建Game
 * 2. 向局域网广播游戏
 * 3. 等待其他玩家连接
 * 4. 所有玩家就绪之后开始游戏
 * 5. 游戏就绪之后结算游戏
 */
public class WifiHotspotGameActivity extends AppCompatActivity{

    private WifiManager wifiManager;
    private WifiInfo wifiInfo;

    private MulticastSocket ms;

    /**
     * Activity的onCreate函数
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 向局域网广播消息要在建立game类之后
        // 玩家加入
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        // 设置一个网络类
        // 这个网络类建立之后，不停的向局域网发送广播，声明主机的状态
        // 其他主机回复之后，这个类负责调用game建立remoteplayer类，并且要是不是的发送心跳检测，检测目标主机是否还在线
        // remoteplayer建立之后，保存一个socket引用，当需要发送消息时，直接调用remoteplayer的方法发送
        // 除了remoteplayer之外，还应该有个类，负责全局消息的发送，比如玩家在加入房间之后
        createLanGame();
    }

    public void init() {
        try {
            ms = new MulticastSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发送广播消息
    public void send() {
        int ip = wifiInfo.getIpAddress();
        int broadCastIP = ip | 0xFF000000;
        DatagramPacket datagramPacket = null;
        try {
            ms.setTimeToLive(4);

            byte[] data = "192.168.1.101".getBytes();

            // 广播地址
            InetAddress address = InetAddress.getByName("224.0.0.1");

            datagramPacket = new DatagramPacket(data, data.length, address, 8003);

            ms.send(datagramPacket);
            ms.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发送广播消息
    public void sendBroadCastToCenter(WifiInfo wifiInfo) {
        int ip = wifiInfo.getIpAddress();
        int broadCastIP = ip | 0xFF000000;
        int port = 2333;

        byte[] message = new byte[1024];

        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            DatagramPacket datagramPacket = new DatagramPacket(message, message.length);
            try {
                while(true) {
                    datagramSocket.receive(datagramPacket);
                    //
                }
            }catch (IOException e) {

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来建立游戏的函数
     */
    public void createLanGame() {
        setContentView(R.layout.game_loading);

        Game game = new Game(this);
        game.setGameType(2);

        Direction direction = new Direction();

        AbstractPlayer player = new Player(this);
        player.setDirection(direction.getDirections());

        AbstractPlayer proxy1 = new ProxyPlayer(this);
        proxy1.setDirection(direction.getDirections());

        AbstractPlayer proxy2 = new ProxyPlayer(this);
        proxy2.setDirection(direction.getDirections());

        AbstractPlayer proxy3 = new ProxyPlayer(this);
        proxy3.setDirection(direction.getDirections());
//        Cards cards = new Cards(52);
//        player.setCards(cards.getCards(0));
//        proxy1.setCards(cards.getCards(1));
//        proxy2.setCards(cards.getCards(2));
//        proxy3.setCards(cards.getCards(2));

        game.setLocalPlayerNumber(player.direction);
        game.setGamePlayer(player);
        game.setGamePlayer(proxy1);
        game.setGamePlayer(proxy2);
        game.setGamePlayer(proxy3);
        game.setGameStage(0);

        GameView gameview = new GameView(this, game);
        setContentView(gameview);
    }
}
