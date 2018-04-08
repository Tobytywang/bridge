package com.happylich.bridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.happylich.bridge.game.GameActivity;

// 没有Fragment的Activity
// 要考虑向后兼容，继承自AppCompatActivity
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 四种游戏模式下，游戏界面（也就是Game）是通用的
        // 机-机和人-机游戏暂不考虑
        // 蓝牙和WiFi模式下，需要先建立C/S关系才能运行游戏
        // 考虑如何建立这个C/S关系，以及如何对机-机和人-机对战方式进行抽象


        // 处理按钮点击事件（切换到机-机模式）
        Button button1 = (Button) this.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do Nothings
            }
        });

        // 处理按钮点击事件（切换到人-机模式）
        Button button2 = (Button) this.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(
                        MainActivity.this,
                        GameActivity.class);
                startActivity(intent1);
            }
        });

        // 处理按钮点击事件（切换到蓝牙联机模式）
        Button button3 = (Button) this.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do Nothing
            }
        });


        // 处理按钮点击事件（切换到Wifi对战模式）
        Button button4 = (Button) this.findViewById(R.id.button4);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do Nothing
            }
        });
    }
}
