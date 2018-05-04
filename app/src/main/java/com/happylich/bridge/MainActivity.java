package com.happylich.bridge;

import android.net.Uri;

import com.happylich.bridge.dummy.DummyContent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.happylich.bridge.game.LocalGameActivity;
import com.happylich.bridge.game.WifiHotspotGameActivity;
import com.happylich.bridge.game.SelectHotspotRoomActivity;

// 没有Fragment的Activity
// 要考虑向后兼容，继承自AppCompatActivity
// 没有Fragment的Activity
// 要考虑向后兼容，继承自AppCompatActivity
public class MainActivity extends AppCompatActivity
    implements MenuFragment.OnFragmentInteractionListener,
        HistoryFragment.OnListFragmentInteractionListener {

    private FragmentManager fm = getSupportFragmentManager();

    private MenuFragment menuFragment =
            MenuFragment.newInstance("arg", "menuFragment");
    private HistoryFragment historyFragment =
            HistoryFragment.newInstance(0);

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_menu:
                    ft.replace(R.id.fragment_content,
                            MenuFragment.newInstance("arg", "menuFragment")).commit();
                    return true;
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
                case R.id.navigation_history:
                    ft.replace(R.id.fragment_content,
                            HistoryFragment.newInstance(0)).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_content, menuFragment);
        ft.commit();
        // 四种游戏模式下，游戏界面（也就是Game）是通用的
        // 机-机和人-机游戏暂不考虑
        // 蓝牙和WiFi模式下，需要先建立C/S关系才能运行游戏
        // 考虑如何建立这个C/S关系，以及如何对机-机和人-机对战方式进行抽象


        // 处理按钮点击事件（切换到机-机模式）
//        Button button1 = (Button) this.findViewById(R.id.button1);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Do Nothings
//                Intent intent = new Intent(
//                        MainActivity.this,
//                        LocalGameActivity.class);
//                intent.putExtra("type", "EVE");
//                startActivity(intent);
//            }
//        });
//
//        // 处理按钮点击事件（切换到人-机模式）
//        Button button2 = (Button) this.findViewById(R.id.button2);
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(
//                        MainActivity.this,
//                        LocalGameActivity.class);
//                intent.putExtra("type", "PVE");
//                startActivity(intent);
//            }
//        });
//
//        // 处理按钮点击事件（切换到蓝牙联机模式）
////        Button button3 = (Button) this.findViewById(R.id.button3);
////        button3.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                // Do Nothing
////            }
////        });
//
//
//        // 处理按钮点击事件（创建Wifi对战——创建并进入房间）
//        Button button5 = (Button) this.findViewById(R.id.button5);
//        button5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Do Nothing
//                // 创建并且进入房间
//                Intent intent = new Intent(
//                MainActivity.this,
//                        WifiHotspotGameActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        // 处理按钮点击事件（切换到Wifi对战——选择房间并进入）
//        Button button6 = (Button) this.findViewById(R.id.button6);
//        button6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Do Nothing
//                // 进入选择房间Activity
//                Intent intent = new Intent(
//                        MainActivity.this,
//                        SelectHotspotRoomActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    public void onFragmentInteraction(Uri uri) {
        //
    }

    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        //
    }
}
