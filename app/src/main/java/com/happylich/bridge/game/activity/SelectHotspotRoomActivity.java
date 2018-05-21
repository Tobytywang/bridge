package com.happylich.bridge.game.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.happylich.bridge.R;
import com.happylich.bridge.game.utils.AdapterImageView;
import com.happylich.bridge.game.utils.RoomAdapter;
import com.happylich.bridge.game.utils.RoomBean;
import com.happylich.bridge.game.wlan.wifihotspot.WifiBroadcastReceiverThread;
import com.happylich.bridge.game.wlan.wifihotspot.WifiBroadcastThread;
import com.happylich.bridge.game.wlan.wifip2p.ActionListenerHandler;
import com.happylich.bridge.game.wlan.wifip2p.WifiDirectReceiver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 这个Activity负责找出所有在局域网中发送游戏广播消息的主机
 *
 * 1. 广播消息的内容：ip:port,
 * 2. 接收方要做的事：向ip:port发送消息，说明自己的ip:port，声明加入意图
 * 3. 服务端new一个remoteplayer，将它作为proxyplayer的realplayer，同时也获得了本次发的牌？将准备界面的图标进行更新
 * 4. 所有玩家就位后，确认所有主机的玩家状态是OK的
 * 5. 对有必要的动作进行同步
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

/**
 * 1. 监听局域网广播消息
 * 2. 将所有广播加入列表
 * 3. 点击列表项目，加入服务器
 */
public class SelectHotspotRoomActivity extends AppCompatActivity {

    Context context;

    private WifiManager mWifiManager;
    private WifiManager.MulticastLock multicastLock;


    private ListView listView;

    private CopyOnWriteArrayList<RoomBean> mRoomList;

    private RoomAdapter mRoomAdapter;
    private Handler mHandler;

    private WifiBroadcastReceiverThread mWifiBroadcastReceiverThread;

    private ArrayList<String> devicesNames;
    private ArrayList<String> devicesAddress;
    private ArrayList<WifiP2pDevice> devicesList;
//    devicesNames = new ArrayList<>();
//    devicesAddress = new ArrayList<>();

//    private AdapterImageView hybridAdapter;
//    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        multicastLock = mWifiManager.createMulticastLock("multicast");
        listView = (ListView)findViewById(R.id.list_view);
        mRoomList = new CopyOnWriteArrayList<RoomBean>();
        mRoomAdapter = new RoomAdapter(mRoomList, this);
        listView.setAdapter(mRoomAdapter);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        mRoomAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };

        RoomBean roomBean = new RoomBean();
        roomBean.setIP("122.122.122.122");
        roomBean.setState("你好");
        roomBean.setTime(1000);

        mRoomList.add(roomBean);
        mRoomAdapter.notifyDataSetChanged();


        mWifiBroadcastReceiverThread = new WifiBroadcastReceiverThread(mWifiManager);
        mWifiBroadcastReceiverThread.setRoomAdapter(mRoomAdapter);
        mWifiBroadcastReceiverThread.setRoomList(mRoomList);
        mWifiBroadcastReceiverThread.setHandler(mHandler);
        mWifiBroadcastReceiverThread.setMulticastLock(multicastLock);
        mWifiBroadcastReceiverThread.setRunning(true);
        mWifiBroadcastReceiverThread.start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mRoomList != null) {
                    Intent intent = new Intent(view.getContext(), WifiHotspotGameActivity.class);
                    intent.putExtra("type", "JOIN_GAME");
                    intent.putExtra("ip", mRoomAdapter.getItem(position).getIP());
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.v(this.getClass().getName(), "Select onResume");
        mWifiBroadcastReceiverThread.setPause(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(this.getClass().getName(), "Select onPause");
        mWifiBroadcastReceiverThread.setPause(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWifiBroadcastReceiverThread.setRunning(false);
    }
}