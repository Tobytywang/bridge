package com.happylich.bridge.game;

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
import com.happylich.bridge.game.scene.Call;
import com.happylich.bridge.game.main.Cards;
import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.scene.Table;
import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.player.Player;
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

        // TODO:在加载这些资源的时候显示loading
//        CardImage.getResource(context);

        // TODO:放在这里的缺点是没有办法在构造函数中获得宽高
        // C:客户端负责（每个客户端由一个Game类）
        Game game = new Game(this);

        // TODO:主机的Cards给玩家发牌(如果是从机，则不需要创建Cards对象，或者说只要创建Cards的副本）
        // S:服务器负责（负责发牌一致）
        Cards cards = new Cards(52);

        // TODO:应该对不同玩家的行为模式进行抽象(CallCard,DropCard)
        // TODO:玩家的位置应该是随机的
        // TODO:position代表东西南北
        // TODO:本地主机会设置Left,Right,Top,Bottom等

        // S:服务器负责（负责一致性）
        AbstractPlayer robot1 = new Robot(this,0);
        robot1.setCards(cards.getCards(0));
        AbstractPlayer robot2 = new Robot(this,1);
        robot2.setCards(cards.getCards(1));
        AbstractPlayer robot3 = new Robot(this,2);
        robot3.setCards(cards.getCards(2));
        AbstractPlayer robot4 = new Robot(this,3);
        robot4.setCards(cards.getCards(3));

        // C:客户端负责（与绘制有关）
        // 除了本人之外，都设置为背面，到适合的时候在重新设置
        robot1.setStage(1);
        robot2.setStage(2);
        robot3.setStage(2);
        robot4.setStage(2);

        // 设置game元素
        // TODO:这里已经设置了玩家的座位，但是在绘制过程中，各个客户端要根据自己的角色绘制上下左右
        // TODO:游戏需要维持一个四个玩家的轮询结构，实现四个玩家轮流叫牌，出牌的过程
        // C:客户端负责（与绘制有关）
        game.setLocalPlayer(robot1);
        game.setLeftPlayer(robot2);
        game.setTopPlayer(robot3);
        game.setRightPlayer(robot4);

        game.setGameType(0);

        // C:与客户端负责
        game.setLocalPlayerNumber(0);

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

        // TODO:主机的Cards给玩家发牌(如果是从机，则不需要创建Cards对象，或者说只要创建Cards的副本）
        Cards cards = new Cards(52);

        // TODO:应该对不同玩家的行为模式进行抽象(CallCard,DropCard)
        // TODO:玩家的位置应该是随机的
        // 如何随机分配位置呢？给每个玩家随机分配一个数组position，玩家根据position决定其他
        AbstractPlayer player = new Player(this,0);
        player.setCards(cards.getCards(0));
        AbstractPlayer robot1 = new Robot(this,1);
        robot1.setCards(cards.getCards(1));
        AbstractPlayer robot2 = new Robot(this,2);
        robot2.setCards(cards.getCards(2));
        AbstractPlayer robot3 = new Robot(this,3);
        robot3.setCards(cards.getCards(3));

        // C:客户端负责（与绘制有关）
        player.setStage(1);
        robot1.setStage(2);
        robot2.setStage(2);
        robot3.setStage(2);

        // 设置game元素
        // TODO:这里已经设置了玩家的座位，但是在绘制过程中，各个客户端要根据自己的角色绘制上下左右
        // TODO:游戏需要维持一个四个玩家的轮询结构，实现四个玩家轮流叫牌，出牌的过程
        // 这里是设置位置的关键，setPlayer过程
        game.setLeftPlayer(robot1);
        game.setTopPlayer(robot2);
        game.setRightPlayer(robot3);
        game.setLocalPlayer(player);

        game.setLocalPlayerNumber(0);



        game.setGameType(1);

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
