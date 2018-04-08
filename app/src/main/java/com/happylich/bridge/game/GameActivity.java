package com.happylich.bridge.game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.happylich.bridge.engine.view.GameView;
import com.happylich.bridge.game.call.Call;
import com.happylich.bridge.game.cards.Cards;
import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.player.Robot;

/**
 * GameActivity着眼于系统层级的控制
 * 如：Activity何时创建和销毁，Activity界面如何切换前台和后台等
 * 基于游戏的具体需求，还可以添加对按键和触摸事件的处理：如按下返回键后退出整个游戏进程等
 */
public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        renji();
    }

    /**
     * 专门处理人机模式的函数
     */
    public void renji() {
        // TODO:放在这里的缺点是没有办法在构造函数中获得宽高
        Game game = new Game(this);

        // TODO:主机的Cards给玩家发牌(如果是从机，则不需要创建Cards对象，或者说只要创建Cards的副本）
        Cards cards = new Cards(52);

        // TODO:为什么不设置的话就无法显示？
        Call call = new Call(this);

        // TODO:应该对不同玩家的行为模式进行抽象(CallCard,DropCard)
        AbstractPlayer playerS = new Player(this,0);
        playerS.setCards(cards.getCards(13));
        playerS.setCall(call);
        // TODO：在这里似乎不好？
        playerS.setStage(201);

        AbstractPlayer playerW = new Robot(this,1);
        playerW.setCards(cards.getCards(13));
        playerW.setCall(call);

        AbstractPlayer playerN = new Robot(this,2);
        playerN.setCards(cards.getCards(13));
        playerN.setCall(call);

        AbstractPlayer playerE = new Robot(this,3);
        playerE.setCards(cards.getCards(13));
        playerE.setCall(call);

        // 设置game元素
        // TODO:这里已经设置了玩家的座位，但是在绘制过程中，各个客户端要根据自己的角色绘制上下左右
        // TODO:游戏需要维持一个四个玩家的轮询结构，实现四个玩家轮流叫牌，出牌的过程
        game.setCall(call);
        game.setPlayerW(playerW);
        game.setPlayerN(playerN);
        game.setPlayerE(playerE);
        game.setPlayerS(playerS);

        GameView gameview = new GameView(this, game);
        setContentView(gameview);
    }
}
