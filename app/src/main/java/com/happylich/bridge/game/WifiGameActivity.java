package com.happylich.bridge.game;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;

import com.happylich.bridge.R;
import com.happylich.bridge.game.wifi.ActionListenerHandler;
import com.happylich.bridge.game.wifi.WifiDirectReceiver;

import java.util.ArrayList;

/**
 * Created by lich on 2018/4/23.
 */

/**
 * 这个Activity同时充当服务器和客户端
 * 这个Activity实现了WifiP2p的一些方法，使得他能够获取与远程机器的连接
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

    /**
     * onCreate函数
     * @param savedInstanceState
     */
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
        mChannel=mManager.initialize(this, getMainLooper(), this);
        if(mChannel!=null){
//            Log.d(TAG,"WIFI Direct reinitialize : SUCCESS");
        }else{
//            Log.d(TAG, "WIFI Direct reinitialize : FAILURE");
        }
    }

    public void peerAvailable() {
        devicesList = mReceiver.getDeviceList();
        if (devicesList != null) {
            //clear adapter from outdated data
//            hybridAdapter.clear();
            // update the names of available devices
            calculateDevices();
            //notify adapter to change listView content
//            hybridAdapter.notifyDataSetChanged();
        }
    }

    private void calculateDevices() {
        if(devicesList!=null) {
            if(devicesNames!=null) devicesNames.clear();
            if(devicesAddress!=null) devicesAddress.clear();
            for (int i=0;i<devicesList.size();i++) {
                devicesNames.add(devicesList.get(i).deviceName);
                devicesAddress.add(devicesList.get(i).deviceAddress);
            }
        }
    }

    public void connectToPeer(WifiP2pDevice device) {
        if(device!=null){
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
//            opponentsName=device.deviceName;
            config.wps.setup= WpsInfo.PBC;
            mManager.connect(mChannel, config, new ActionListenerHandler(this, "Connection to peer"));
        }else{
//            Log.d(TAG, "Can not find that device");
        }
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

    public void handleIncoming(String msg){
        int x,y;
//        Log.d(TAG, "Incoming " + msg);
        if(msg.equals("RESET")){
//            resetCanvasBoard();
        }else if (msg.equals("LEFT")) {
            onBackPressed();
        }
        else{
            x = Character.getNumericValue(msg.charAt(0));
            y = Character.getNumericValue(msg.charAt(1));
//            Log.d(TAG, "interpretered as x = " + x + " y= " + y);
//            board.incomingMove(x, y);
        }
    }

    public void sendMsg(String msg){
        mReceiver.sendMsg(msg);
//        Log.d(TAG, "Sending move");
    }
}
