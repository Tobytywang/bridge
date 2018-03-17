package com.happylich.bridge;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;

import com.happylich.bridge.game.fragment.FragmentContentOne;
import com.happylich.bridge.game.fragment.FragmentContentTwo;
import com.happylich.bridge.game.fragment.FragmentContentThr;

// 因为要使用Fragment，所以继承自FragmentActivity
public class MainActivityWithFragment extends FragmentActivity
    implements FragmentContentOne.OnFragmentInteractionListener,
    FragmentContentTwo.OnFragmentInteractionListener,
    FragmentContentThr.OnFragmentInteractionListener{

    // 组件管理器
    private FragmentManager fm = getSupportFragmentManager();

    // 加载组件实例
    private FragmentContentOne f1 = FragmentContentOne.newInstance("arg", "f1");
    private FragmentContentTwo f2 = FragmentContentTwo.newInstance("arg", "f2");
    private FragmentContentThr f3 = FragmentContentThr.newInstance("arg", "f3");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_fragment);
        // 顶部导航栏
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("棋牌");
//        toolbar.setSubtitle("副标题");
//        toolbar.setLogo(R.drawable.ic_dashboard_black_24dp);
//        setSupportActionBar(toolbar);

        // 底部导航栏
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content, f1);
        ft.commit();
    }

    // 按键监听：切换Fragment
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            FragmentTransaction ft = fm.beginTransaction();
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    ft.replace(R.id.content, FragmentContentOne.newInstance("arg", "f1")).commit();
//                    return true;
//                case R.id.navigation_dashboard:
//                    ft.replace(R.id.content, FragmentContentTwo.newInstance("arg", "f2")).commit();
//                    return true;
//                case R.id.navigation_notifications:
//                    ft.replace(R.id.content, FragmentContentThr.newInstance("arg", "f3")).commit();
//                    return true;
//            }
//            return false;
//        }
//    };

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }

}
