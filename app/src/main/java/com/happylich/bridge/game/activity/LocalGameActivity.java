package com.happylich.bridge.game.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.happylich.bridge.R;
import com.happylich.bridge.engine.view.GameView;
import com.happylich.bridge.game.main.Direction;
import com.happylich.bridge.game.main.Cards;
import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.player.AbstractPlayerWithDraw;
import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.player.ProxyPlayer;
import com.happylich.bridge.game.player.Robot;
import com.happylich.bridge.game.res.CardImage;

/**
 * GameActivity着眼于系统层级的控制
 * 如：Activity何时创建和销毁，Activity界面如何切换前台和后台等
 * 基于游戏的具体需求，还可以添加对按键和触摸事件的处理：如按下返回键后退出整个游戏进程等
 */
public class LocalGameActivity extends AppCompatActivity {

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver = null;
    private WifiP2pManager.PeerListListener mPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
//            peers.clear();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String gameType = intent.getStringExtra("type");
        if (gameType.equals("EVE")) {
            EVE(this);
        } else if (gameType.equals("PVE")) {
            PVE(this);
        }
    }

    protected void onStop() {
        super.onStop();
        Log.v(this.getClass().getName(), "onStop......");
        CardImage.releaseResource();
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.v(this.getClass().getName(), "onDestroy...");
    }

    /**
     * 专门处理人机模式的函数
     */
    public void EVE(Context context) {
        setContentView(R.layout.game_loading);

        // TODO:放在这里的缺点是没有办法在构造函数中获得宽高
        // S/C:服务器/客户端负责（每个客户端由一个Game类）
        Game game = new Game(this);
        game.setGameType(0);

        // TODO:主机的Cards给玩家发牌(如果是从机，则不需要创建Cards对象，或者说只要创建Cards的副本）
        // S:服务器负责（负责发牌一致）
        Direction direction = new Direction();

        // S:服务器负责（负责一致性）
        ProxyPlayer proxy1 = new ProxyPlayer(this);
        ProxyPlayer proxy2 = new ProxyPlayer(this);
        ProxyPlayer proxy3 = new ProxyPlayer(this);
        ProxyPlayer proxy4 = new ProxyPlayer(this);


        proxy1.setDirection(direction.getDirections());
        proxy2.setDirection(direction.getDirections());
        proxy3.setDirection(direction.getDirections());
        proxy4.setDirection(direction.getDirections());

        // 这一段可以进行隐藏
        game.setlocalPlayerDirection(proxy1.direction);
        game.setGamePlayer(proxy1);
        game.setGamePlayer(proxy2);
        game.setGamePlayer(proxy3);
        game.setGamePlayer(proxy4);
        game.setGameStage(3);


        Robot robot1 = new Robot(this);
        Robot robot2 = new Robot(this);
        Robot robot3 = new Robot(this);
        Robot robot4 = new Robot(this);

        proxy1.setRealPlayer(robot1);
        proxy2.setRealPlayer(robot2);
        proxy3.setRealPlayer(robot3);
        proxy4.setRealPlayer(robot4);

        GameView gameview = new GameView(this, game);
        setContentView(gameview);
    }

    /**
     * 专门处理人机模式的函数
     */
    public void PVE(Context context) {
        setContentView(R.layout.game_loading);

        // TODO:在加载这些资源的时候显示loading

        // TODO:放在这里的缺点是没有办法在构造函数中获得宽高
        Game game = new Game(this);
        game.setGameType(1);

        // TODO:主机的Cards给玩家发牌(如果是从机，则不需要创建Cards对象，或者说只要创建Cards的副本）
        Direction direction = new Direction();

        // 建立玩家
        Player player = new Player(this);
        ProxyPlayer proxy1 = new ProxyPlayer(this);
        ProxyPlayer proxy2 = new ProxyPlayer(this);
        ProxyPlayer proxy3 = new ProxyPlayer(this);


        player.setDirection(direction.getDirections());
        player.setInOrder(true);
        proxy1.setDirection(direction.getDirections());
        proxy2.setDirection(direction.getDirections());
        proxy3.setDirection(direction.getDirections());


        //
        game.setlocalPlayerDirection(player.direction);
        game.setGamePlayer(proxy1);
        game.setGamePlayer(proxy2);
        game.setGamePlayer(proxy3);
        game.setGamePlayer(player);
        game.setGameStage(3);


        AbstractPlayer robot1 = new Robot(this);
        AbstractPlayer robot2 = new Robot(this);
        AbstractPlayer robot3 = new Robot(this);
        proxy1.setRealPlayer(robot1);
        proxy2.setRealPlayer(robot2);
        proxy3.setRealPlayer(robot3);

        GameView gameview = new GameView(this, game);
        setContentView(gameview);
    }


    private void DiscoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }


}
