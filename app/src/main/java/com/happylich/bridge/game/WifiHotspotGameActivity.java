package com.happylich.bridge.game;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.happylich.bridge.R;
import com.happylich.bridge.engine.view.GameView;
import com.happylich.bridge.game.main.Cards;
import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.player.Player;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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

    /**
     * Activity的onCreate函数
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        createLanGame();
    }

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

    public void createLanGame() {
        setContentView(R.layout.game_loading);

        // 向局域网发送广播服务器状态（等待加入，已满还是游戏中）
        // 当游戏处于等待加入状态时，监听局域网
        // 当有玩家请求加入时，更新玩家列表和游戏状态
        // 所有玩家都准备就绪后，开始游戏，切换游戏状态为游戏中

        Game game = new Game(this);
        Cards cards = new Cards(52);

        // 这里只能建立本地玩家，更多的玩家需要通过网络建立或者添加人机
        // 先实现添加人机
        AbstractPlayer player = new Player(this);
        player.setCards(cards.getCards(0));
//        AbstractPlayer robot1 = new Robot(this,1);
//        robot1.setCards(cards.getCards(1));
//        AbstractPlayer robot2 = new Robot(this,2);
//        robot2.setCards(cards.getCards(2));
//        AbstractPlayer robot3 = new Robot(this,3);
//        robot3.setCards(cards.getCards(3));

        player.setStage(1);
//        robot1.setStage(2);
//        robot2.setStage(2);
//        robot3.setStage(2);


        game.setGamePlayer(player);
//        game.setLeftPlayer(robot1);
//        game.setTopPlayer(robot2);
//        game.setRightPlayer(robot3);

        // 这里的localPlayerNumber就是分配的东西南北
//        game.setLocalPlayerNumber(0);

        game.setGameType(1);

        GameView gameview = new GameView(this, game);
        setContentView(gameview);
    }

}
