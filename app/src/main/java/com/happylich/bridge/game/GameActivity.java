package com.happylich.bridge.game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.happylich.bridge.engine.view.GameView;
import com.happylich.bridge.game.framework.Game;

/**
 * GameActivity着眼于系统层级的控制
 * 如：Activity何时创建和销毁，Activity界面如何切换前台和后台等
 * 基于游戏的具体需求，还可以添加对按键和触摸事件的处理：如按下返回键后退出整个游戏进程等
 */
public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 正常逻辑来说，我们需要知道Actiity和Game
        // 因为这是我们定义的，但是我们不需要知道View
        // 先做一个弱化版的，将新建Game的工作放在这里

        // TODO:在哪里建立与其他主机的链接呢，在哪里建立主机呢？

        // TODO:放在这里的缺点是没有办法在构造函数中获得宽高
        Game game = new Game(this);
        GameView gameview = new GameView(this, game);
        setContentView(gameview);
    }
}
