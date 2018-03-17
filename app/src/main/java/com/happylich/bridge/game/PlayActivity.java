package com.happylich.bridge.game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.happylich.bridge.R;

// 用于游戏的Activity，继承自AppCompatActivity
public class PlayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
    }
}
