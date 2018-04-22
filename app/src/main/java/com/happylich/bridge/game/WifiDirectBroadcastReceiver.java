package com.happylich.bridge.game;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by lich on 2018/4/21.
 */

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Activity mActivity;
    private WifiP2pManager.PeerListListener mPeerListListener;
    private WifiP2pManager.ConnectionInfoListener mInfoListener;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                          Activity activity,
                          WifiP2pManager.PeerListListener peerListListener,
                          WifiP2pManager.ConnectionInfoListener infoListener) {
        this.mManager = manager;
        this.mChannel = channel;
        this.mPeerListListener = peerListListener;
        this.mActivity = activity;
        this.mInfoListener = infoListener;
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
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//                activity.setIsWifiP2pEnabled(true);
            } else {
//                activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            mManager.requestPeers(mChannel, mPeerListListener);
        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            int State = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
            if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
//                Toast.makeText(mActivity, "搜索开启", Toast.LENGTH_SHORT).show();
            }
            else if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
//                Toast.makeText(mActivity, "搜索已关闭", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel, mInfoListener);
            } else {
                return;
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}
