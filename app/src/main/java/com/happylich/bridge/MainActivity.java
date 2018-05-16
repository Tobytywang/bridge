package com.happylich.bridge;

import android.net.Uri;

import com.happylich.bridge.dummy.DummyContent;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

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
                    ft.replace(R.id.fragment_content, menuFragment).commit();
                    return true;
                case R.id.navigation_history:
                    ft.replace(R.id.fragment_content, historyFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 底部导航栏
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Fragment管理机制
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_content, menuFragment);
        ft.commit();
    }

    public void onFragmentInteraction(Uri uri) {
        //
    }

    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        //
    }
}
