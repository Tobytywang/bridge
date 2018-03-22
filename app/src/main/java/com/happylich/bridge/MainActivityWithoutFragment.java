package com.happylich.bridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.happylich.bridge.engine.PlayActivity;

// 没有Fragment的Activity
// 要考虑向后兼容，继承自AppCompatActivity
public class MainActivityWithoutFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_without_fragment);

        // 处理按钮点击事件（切换到机-机模式）
        Button button1 = (Button) this.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(
                        MainActivityWithoutFragment.this,
                        PlayActivity.class);
                startActivity(intent1);
            }
        });

        // 处理按钮点击事件（切换到人-机模式）
        Button button2 = (Button) this.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do Nothing
            }
        });

        // 处理按钮点击事件（切换到人-人模式）
        Button button3 = (Button) this.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do Nothing
            }
        });
    }
}
