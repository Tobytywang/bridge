package com.happylich.bridge.game.wlan.wifip2p;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by lich on 2018/4/23.
 */

public class ActionListenerHandler implements WifiP2pManager.ActionListener {

    private Activity mActivity;
    private String message;

    public ActionListenerHandler(Activity mActivity, String message) {
        this.mActivity = mActivity;
        this.message = message;
    }

    @Override
    public void onSuccess() {
        // Do Nothing
    }

    @Override
    public void onFailure(int i) {
        // Do Nothing
    }
}
