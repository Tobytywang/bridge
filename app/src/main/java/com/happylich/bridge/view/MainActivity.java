package com.happylich.bridge.view;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.widget.TextView;

import com.happylich.bridge.R;

public class MainActivity extends FragmentActivity
    implements FragmentContentOne.OnFragmentInteractionListener,
    FragmentContentTwo.OnFragmentInteractionListener,
    FragmentContentThr.OnFragmentInteractionListener{

//    private FrameLayout mContent;
//    private TextView mTextMessage;

    private FragmentManager fm = getSupportFragmentManager();

    private FragmentContentOne f1 = FragmentContentOne.newInstance("arg", "f1");
    private FragmentContentTwo f2 = FragmentContentTwo.newInstance("arg", "f2");
    private FragmentContentThr f3 = FragmentContentThr.newInstance("arg", "f3");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("棋牌");
//        toolbar.setSubtitle("副标题");
//        toolbar.setLogo(R.drawable.ic_dashboard_black_24dp);
//        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content, f1);
        ft.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ft.replace(R.id.content, FragmentContentOne.newInstance("arg", "f1")).commit();
                    return true;
                case R.id.navigation_dashboard:
                    ft.replace(R.id.content, FragmentContentTwo.newInstance("arg", "f2")).commit();
                    return true;
//                    TextView textview = (TextView) findViewById(R.id.textview);
//                    textview.setText("你好");
                case R.id.navigation_notifications:
                    ft.replace(R.id.content, FragmentContentThr.newInstance("arg", "f3")).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }

}
