package com.happylich.bridge.game.wlan.wifip2p;

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

import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.wlan.SelectActivity;

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
    private SelectActivity mActivity;
    private boolean isWifiDirectEnabled;
    private ArrayList<WifiP2pDevice> wifiDevices;

    private Game game;

    /**
     * 构造函数
     * @param manager
     * @param channel
     * @param activity
     */
    public WifiDirectReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                          SelectActivity activity) {
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    /**
     * 接收消息
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // 1. 检测设备Wi-Fi P2p是否打开或者设备是否支持Wi-Fi direct
        // 2. 开始搜索之后，节点列表发生变化
        // 3. 两个节点的连接状态是否发生变化
        // 4. 设备的详细信息发生了变化
        // 是否处于搜索状态？
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

    /**
     * 执行了requestPeers()之后
     * 当周围设备可用时
     * 调用该函数
     * @param wifiP2pDeviceList
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        // 更新设备列表
        if (wifiP2pDeviceList != null &&
                wifiP2pDeviceList.getDeviceList() != null &&
                wifiP2pDeviceList.getDeviceList().size() > 0) {
            wifiDevices = new ArrayList<>(wifiP2pDeviceList.getDeviceList());
            mActivity.peerAvailable();
        } else {
            wifiDevices = null;
        }

    }

    /**
     * 当连接状态可用时（已经建立了连接）
     * 在这里通过参数WifiP2pInfo可以拿到IP地址
     * 之后就可以建立Socket连接
     * 假如是管理员，就应该创建一个服务器线程等待组成员连接进来
     * 假如是普通成员，就应该创建Socket连接到管理员的设备
     * @param wifiP2pInfo
     */
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo wifiP2pInfo) {
//        Log.d(TAG, "Connection Established");
        if (wifiP2pInfo.groupFormed) {
//            mActivity.setPlayers();
            if (wifiP2pInfo.isGroupOwner) {
                //server side
//                game = new Game(mActivity);
//                mActivity.getLocalPlayer().setSide(-1);
//                mActivity.getP2().setSide(1);
            } else {
//                game = new Game(mActivity,wifiP2pInfo.groupOwnerAddress);
//                mActivity.getLocalPlayer().setSide(1);
//                mActivity.getP2().setSide(-1);
            }
//            game.execute();
//            mActivity.MoveToCanvas();
        }else{
//            Log.d(TAG,"Failed to form group");
        }
    }

    /**
     * 检测设备是否打开或者是否支持Wi-Fi direct
     * @param intent
     */
    private void handleWifiP2pStateChanged(Intent intent) {
        // 给出当前设备是否支持Wi-Fi Direct标准或者Wi-Fi Direct功能是否开启
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        isWifiDirectEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED;

        if(!isWifiDirectEnabled) {
//            mActivity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//            Toast.makeText(mActivity.getApplication().getApplicationContext(),
//                    "Enabled Wifi and then press Back", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 检测节点列表是否发生变化
     * @param intent
     */
    private void handleWifiP2pPeersChanged(Intent intent) {
        // 说明系统已经扫描到设备了
        // 需要交给Acitivity进行处理
        // 调用mManager.requestPeers方法请求列表？
        mManager.requestPeers(mChannel, this);
    }

    /**
     * 检测节点连接状态是否发生变化
     * @param intent
     */
    private void handleWifiP2pConnectionChanged(Intent intent) {
        // 说明Activity已经调用了connect方法
        // 判断连接是否可用，如果可用，就想消息获取出来，传递给Activity处理
        NetworkInfo info =
                intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (info != null && info.isConnected()) {
            mManager.requestConnectionInfo(mChannel, this);
        } else {
//            Log.d(TAG, "No Connection");
        }
    }

    /**
     * 检测节点信息是否发生变化
     * @param intent
     */
    private void handleWifiP2pDeviceChanged(Intent intent) {
        // 在Activity中更新本机设备信息
        thisDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
    }




    public void registerReceiver() {
//        mActivity.registerReceiver(this, getIntentFilter());
    }

    public void unregisterReceiver() {
//        mActivity.unregisterReceiver(this);
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
