package com.happylich.bridge.engine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.happylich.bridge.R;
import com.happylich.bridge.engine.view.GameView;

/**
 * GameActivity着眼于系统层级的控制
 * 如：Activity何时创建和销毁，Activity界面如何切换前台和后台等
 * 基于游戏的具体需求，还可以添加对按键和触摸事件的处理：如按下返回键后退出整个游戏进程等
 */
public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引入GameView
        GameView gameview = new GameView(this);
        setContentView(gameview);

//        setContentView(R.layout.activity_play);
    }
}
