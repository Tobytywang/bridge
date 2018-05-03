package com.happylich.bridge.game;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by lich on 2018/4/23.
 */

/**
 *
 * 创建基于Wifi热点的游戏
 */
public class WifiHotspotGameActivity extends AppCompatActivity{

    private WifiManager wifiManager;

    /**
     * Activity的onCreate函数
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.game_loading.activity_wifi_game);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    }

}
