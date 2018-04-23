package com.happylich.bridge.game;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.happylich.bridge.R;
import com.happylich.bridge.game.wifi.ActionListenerHandler;
import com.happylich.bridge.game.wifi.WifiDirectReceiver;

import java.util.ArrayList;

/**
 * Created by lich on 2018/4/23.
 */

public class WifiGameActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener {

    Context context;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    public WifiDirectReceiver mReceiver;

    private ArrayList<String> devicesNames;
    private ArrayList<String> devicesAddress;
    private ArrayList<WifiP2pDevice> devicesList;

    private AdapterView hybridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_game);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        registerWifiReceiver();
        mManager.discoverPeers(mChannel, new ActionListenerHandler(this, "Discover peers"));
//
//        findViewById(R.id.helpFAB).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        sendMsg("LEFT");
        mManager.removeGroup(mChannel, new ActionListenerHandler(this, "Group removal"));
        mManager.cancelConnect(mChannel, new ActionListenerHandler(this, "Canceling connect"));
        unregisterWifiReceiver();
        mManager=null;
    }

    @Override
    public void onChannelDisconnected() {
        reinitialize();
    }

    public void reinitialize() {

    }

    private void registerWifiReceiver() {
        mReceiver = new WifiDirectReceiver(mManager,mChannel,this);
        mReceiver.registerReceiver();
    }

    private void unregisterWifiReceiver() {

        if(mReceiver!=null) {
            mReceiver.unregisterReceiver();
        }
        mReceiver=null;
    }
}
