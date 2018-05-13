package com.happylich.bridge.game;

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
import com.happylich.bridge.game.player.Robot;

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

    /**
     * 用来建立游戏的函数
     */
    public void createLanGame() {
        setContentView(R.layout.game_loading);

        Game game = new Game(this);
        game.setGameType(2);

        // cards和direction需要分配给连接到服务器的玩家
        Direction direction = new Direction();
        Cards cards = new Cards(52);

        // 建立一个本地玩家
        // 其他玩家可以选择
        //  1. 使用机器人填充：Robot
        //  2. 等待其他玩家接入：Proxy
        AbstractPlayer player = new Player(this);
        player.setDirection(direction.getDirections());
        player.setCards(cards.getCards(0));

        game.setLocalPlayerNumber(player.direction);
        game.setGamePlayer(player);

        GameView gameview = new GameView(this, game);
        setContentView(gameview);
    }

}
