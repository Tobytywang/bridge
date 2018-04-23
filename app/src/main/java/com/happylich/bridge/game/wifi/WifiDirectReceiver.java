package com.happylich.bridge.game.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.happylich.bridge.game.WifiGameActivity;
import com.happylich.bridge.game.main.Game;

import java.util.ArrayList;

/**
 * Created by lich on 2018/4/21.
 */

public class WifiDirectReceiver extends BroadcastReceiver
    implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener{

    private final int SERVER_PORT = 8888;

    private WifiP2pManager.PeerListListener myPeerListListener;
    private boolean PeersFound = false;
    private WifiP2pDevice mDevice;
    private WifiP2pConfig mConfig;
    private WifiP2pDeviceList mList;

    public WifiP2pDevice getThisDevice() {
        return thisDevice;
    }

    private WifiP2pDevice thisDevice;
    private IntentFilter mIntentFilter;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiGameActivity mActivity;
    private boolean isWifiDirectEnabled;
    private ArrayList<WifiP2pDevice> wifiDevices;

    private Game game;

//    private WifiP2pManager.PeerListListener mPeerListListener;
//    private WifiP2pManager.ConnectionInfoListener mInfoListener;

    public WifiDirectReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                          WifiGameActivity activity) {
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
//        this.mPeerListListener = peerListListener;
//        this.mInfoListener = infoListener;
    }

    @Override
    public void onReceive(Context context,Intent intent) {
        String action = intent.getAction();

        // 检测设备Wi-Fi是否打开
        // 开始搜索之后，设备列表发生变化
        // 是否处于搜索状态
        // 两个设备的连接状态是否发生变化
        // 设备名称发生变化
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            handleWifiP2pStateChanged(intent);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            handleWifiP2pPeersChanged(intent);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            handleWifiP2pConnectionChanged(intent);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            handleWifiP2pDeviceChanged(intent);
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        if (wifiP2pDeviceList != null &&
                wifiP2pDeviceList.getDeviceList() != null &&
                wifiP2pDeviceList.getDeviceList().size() > 0) {
            wifiDevices = new ArrayList<>(wifiP2pDeviceList.getDeviceList());
//            Log.d(TAG, "Peers List updated");
//            mActivity.peerAvailable();
        } else {
            wifiDevices = null;
//            Log.d(TAG, "No available devices");
        }

    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo wifiP2pInfo) {
//        Log.d(TAG, "Connection Established");
//        if (wifiP2pInfo.groupFormed) {
//            mActivity.setPlayers();
//            if (wifiP2pInfo.isGroupOwner) {
//                //server side
//                game = new Game(mActivity);
//                mActivity.getLocalPlayer().setSide(-1);
//                mActivity.getP2().setSide(1);
//            } else {
//                game = new Game(mActivity,wifiP2pInfo.groupOwnerAddress);
//                mActivity.getLocalPlayer().setSide(1);
//                mActivity.getP2().setSide(-1);
//            }
//            game.execute();
//            mActivity.MoveToCanvas();
//        }else{
//            Log.d(TAG,"Failed to form group");
//        }
    }

    private void handleWifiP2pStateChanged(Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        isWifiDirectEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED;

        if(!isWifiDirectEnabled) {
            mActivity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            Toast.makeText(mActivity.getApplication().getApplicationContext(),
                    "Enabled Wifi and then press Back", Toast.LENGTH_LONG).show();
        }
    }

    private void handleWifiP2pPeersChanged(Intent intent) {
        mManager.requestPeers(mChannel, this);
    }

    private void handleWifiP2pConnectionChanged(Intent intent) {
        NetworkInfo info =
                intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (info != null && info.isConnected()) {
            mManager.requestConnectionInfo(mChannel, this);
        } else {
//            Log.d(TAG, "No Connection");
        }
    }

    private void handleWifiP2pDeviceChanged(Intent intent) {
        thisDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
    }

    public void registerReceiver() {
        mActivity.registerReceiver(this, getIntentFilter());
    }
    public void unregisterReceiver() {
        mActivity.unregisterReceiver(this);
        if (game != null) {
//            game.cancel(true);
        }
    }

    public IntentFilter getIntentFilter() {
        if (mIntentFilter == null) {
            //Intent Filter
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        }
        return mIntentFilter;
    }

    public ArrayList<WifiP2pDevice> getDeviceList() {
        if (wifiDevices != null) {
            return wifiDevices;
        }
        return null;
    }

    public void sendMsg(String msg) {
        if(game!=null) {
//            game.sendMsg(msg);
        }else{
//            Log.d(TAG,"Game null");
        }
    }
}
