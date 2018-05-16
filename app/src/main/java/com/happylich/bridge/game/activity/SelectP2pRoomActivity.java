package com.happylich.bridge.game.activity;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.happylich.bridge.R;
import com.happylich.bridge.game.utils.AdapterImageView;
import com.happylich.bridge.game.wlan.wifip2p.ActionListenerHandler;
import com.happylich.bridge.game.wlan.wifip2p.WifiDirectReceiver;

import java.util.ArrayList;

/**
 * 这个Activity负责找出所有建立Wi-Fi direct连接的设备，并列出其中建立的房间的设备
 *
 * 被连接方：
 *     1. WifiP2pManager.discoverPeers()
 *     2. WIFI_P2P_PEERS_CHANGED_ACTION
 *     3. WIFI_P2P_CONNECTION_CHANGED_ACTION
 *     4. connect/accept
 *
 * 连接方：
 *     1. WifiP2pManager.discoverPeers()
 *     2. WIFI_P2P_PEERS_CHANGED_ACTION
 *     3. WifiP2pManager.requestPeers()
 *     4. WifiP2pManager.connect()
 *     5. WIFI_P2P_CONNECTION_CHANGED_ACTION
 *     6. connect/accept
 *
 * GO端先建立WifiP2pManager.createGroup
 *
 * WifiP2pManager.removeGroup
 * WifiP2pManager.cancelConnect
 *
 * 如何保证时钟搜索的到
 *
 * SelectActivity负责选择连接的设备并建立连接
 *     SelectActivity只负责
 * WifiGameActivity负责游戏
 *
 *
 */
public class SelectP2pRoomActivity extends AppCompatActivity
        implements WifiP2pManager.ChannelListener {

    Context context;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    public WifiDirectReceiver mReceiver;

    private ArrayList<String> devicesNames;
    private ArrayList<String> devicesAddress;
    private ArrayList<WifiP2pDevice> devicesList;

    private AdapterImageView hybridAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        recyclerView = (RecyclerView) findViewById(R.id.content_select);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.scrollToPosition(0);
//        recyclerView.setLayoutManager(layoutManager);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        devicesNames = new ArrayList<>();
        devicesAddress = new ArrayList<>();
        hybridAdapter = new AdapterImageView(this,
                R.layout.list_item_hybrid,
                R.id.list_item_device_textview,
                R.id.list_item_avatar_imageView,
                devicesNames);

        final ListView deviceListView = (ListView)findViewById(R.id.list_view);
        deviceListView.setAdapter(hybridAdapter);

        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), this);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (devicesList != null) {
                    String address = devicesAddress.get(position);
                    String name = devicesNames.get(position);

//                    Toast.makeText(context, "Connecting to " + name, Toast.LENGTH_SHORT).show();
                    connectToPeer(devicesList.get(position));
                }
            }
        });
    }



    /**
     * Activity的onStart函数
     * 获得mManager
     * 注册Receiver()
     * 使用mManager来监听节点
     * 以回调的方式通知onPeersAvailable方法
     */
    @Override
    protected void onStart() {
        super.onStart();
        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        // 注册广播->创建IntentFilter对象->调用registerReceiver
        registerWifiReceiver();
        // 启动设备发现
        // 系统会自动处理相关的操作
        // 扫描结束后以广播的形式通知应用
        mManager.discoverPeers(mChannel, new ActionListenerHandler(this, "Discover peers"));
    }

    /**
     * Activity的onStop函数
     */
    @Override
    protected void onStop() {
        super.onStop();
        mManager.removeGroup(mChannel, new ActionListenerHandler(this, "Group removal"));
        mManager.cancelConnect(mChannel, new ActionListenerHandler(this, "Canceling connect"));
        unregisterWifiReceiver();
        mManager=null;
    }

    @Override
    public void onChannelDisconnected() {
        reinitialize();
    }

    /**
     * 初始化函数（被onChannelDisconnected()）
     * 初始化mChannel
     */
    public void reinitialize() {
        mChannel = mManager.initialize(this, getMainLooper(), this);
        if(mChannel!=null){
//            Log.d(TAG,"WIFI Direct reinitialize : SUCCESS");
        }else{
//            Log.d(TAG, "WIFI Direct reinitialize : FAILURE");
        }
    }

    /**
     * 获得了设备列表
     * TODO:在这里显示所有的设备列表？
     */
    public void peerAvailable() {
        Log.v(this.getClass().getName(), "列出所有设备");
        devicesList = mReceiver.getDeviceList();
        Log.v(this.getClass().getName(), devicesList.toString());
        if (devicesList != null) {
            //clear adapter from outdated data
//            hybridAdapter.clear();
            // update the names of available devices
            if(devicesNames!=null) devicesNames.clear();
            if(devicesAddress!=null) devicesAddress.clear();
            for (int i=0;i<devicesList.size();i++) {
                devicesNames.add(devicesList.get(i).deviceName);
                devicesAddress.add(devicesList.get(i).deviceAddress);
            }
            //notify adapter to change listView content
//            hybridAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 连接到某个节点（手动触发）
     * TODO:何时触发？
     * @param device
     */
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



    /**
     * 注册BroadcastReceiver接收广播消息
     */
    private void registerWifiReceiver() {
        // 注册Receiver
        // 需要mManager，mChannel，Activity
//        mReceiver = new WifiDirectReceiver(mManager,mChannel,this);
        mReceiver.registerReceiver();
    }

    /**
     * 解注册广播接收器
     */
    private void unregisterWifiReceiver() {
        if(mReceiver!=null) {
            mReceiver.unregisterReceiver();
        }
        mReceiver=null;
    }



    /**
     * 一套消息处理机制
     * @param msg
     */
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

    /**
     * 一套消息处理机制
     * @param msg
     */
    public void sendMsg(String msg){
        mReceiver.sendMsg(msg);
//        Log.d(TAG, "Sending move");
    }
}
