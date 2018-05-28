package com.happylich.bridge.game.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.happylich.bridge.R;
import com.happylich.bridge.engine.view.GameView;
import com.happylich.bridge.game.main.Cards;
import com.happylich.bridge.game.main.Direction;
import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.player.ProxyPlayer;
import com.happylich.bridge.game.player.Robot;
import com.happylich.bridge.game.res.CardImage;
import com.happylich.bridge.game.wlan.wifihotspot.WifiBroadcastThread;

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
/**
 * 工作原理
 *
 * 1. 获得wifiManager
 * 2. 检测WiFi是否可用
 * 3. 获得wifiInfo
 * 4. 获得IP
 * 5. 新建multicastSocket等
 * 6. 发送消息
 */
public class WifiHotspotGameActivity extends AppCompatActivity{

    private WifiManager mWifiManager;
    private WifiManager.MulticastLock multicastLock;

    private Game game;
    private WifiBroadcastThread mWifiBroadcastThread;

    /**
     * Activity的onCreate函数
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String gameType = intent.getStringExtra("type");
        String serverIP = intent.getStringExtra("ip");
        if (gameType.equals("CREATE_GAME")) {
            createLanGame(this);
        } else if (gameType.equals("JOIN_GAME")) {
            joinLanGame(this, serverIP);
        }
    }

    /**
     * 用来建立游戏的函数
     */
    public void createLanGame(Context context) {
        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        multicastLock = mWifiManager.createMulticastLock("multicast");
        mWifiBroadcastThread = new WifiBroadcastThread(mWifiManager);


        setContentView(R.layout.game_loading);

        game = new Game(context);
        game.setGameType(2);


        Direction direction = new Direction();

        Player player = new Player(context);
        ProxyPlayer proxy1 = new ProxyPlayer(context);
        ProxyPlayer proxy2 = new ProxyPlayer(context);
        ProxyPlayer proxy3 = new ProxyPlayer(context);

        player.setDirection(direction.getDirections());
        proxy1.setDirection(direction.getDirections());
        proxy2.setDirection(direction.getDirections());
        proxy3.setDirection(direction.getDirections());


        game.setlocalPlayerDirection(player.direction);
        game.setGamePlayer(player);
        game.setGamePlayer(proxy1);
        game.setGamePlayer(proxy2);
        game.setGamePlayer(proxy3);
        game.setGameStage(0);

        // 作为服务器
        game.setGameServer();

        GameView gameview = new GameView(context, game);
        setContentView(gameview);

        mWifiBroadcastThread.setGame(game);
        mWifiBroadcastThread.setMulticastLock(multicastLock);
        mWifiBroadcastThread.setRunning(true);
        mWifiBroadcastThread.start();
    }

    /**
     * 用来建立游戏的函数
     */
    public void joinLanGame(Context context, String serverIP) {
        // 可以从Intent中获得IP，建立Socket连接到主机
        setContentView(R.layout.game_loading);

        game = new Game(context);
        game.setGameType(2);

        // 作为客户端——与服务端通信
        // 建立服务器后，当客户端请求和服务器建立联系时，服务器就会将连接接入ServerSocket
        //
        // 1. 玩家加入后，首先发送进入消息
        // 2.

        // 这里需要建立和服务器的远程连接
        // 但是如果建立失败怎么办呢？

        Direction direction = new Direction();

        Player player = new Player(context);
        ProxyPlayer proxy1 = new ProxyPlayer(context);
        ProxyPlayer proxy2 = new ProxyPlayer(context);
        ProxyPlayer proxy3 = new ProxyPlayer(context);

        player.setDirection(direction.getDirections());
        proxy1.setDirection(direction.getDirections());
        proxy2.setDirection(direction.getDirections());
        proxy3.setDirection(direction.getDirections());


        game.setlocalPlayerDirection(player.direction);
        game.setGamePlayer(player);
        game.setGamePlayer(proxy1);
        game.setGamePlayer(proxy2);
        game.setGamePlayer(proxy3);
        game.setGameStage(0);

        game.setGameClient(serverIP);

        GameView gameview = new GameView(context, game);
        setContentView(gameview);
    }

    protected void onStop() {
        super.onStop();
        Log.v(this.getClass().getName(), "onStop......");
        CardImage.releaseResource();
        game.stopGameThreads();
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.v(this.getClass().getName(), "onDestroy...");
        if (mWifiBroadcastThread != null) {
            mWifiBroadcastThread.setRunning(false);
        }
    }
}
