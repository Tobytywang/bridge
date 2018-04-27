package com.happylich.bridge.game.wlan.wifip2p;

import android.util.Log;

import java.io.PrintWriter;

/**
 * Created by lich on 2018/4/22.
 */

public class Sender implements Runnable {

    private String mMessage;
    private PrintWriter mWriter;

    public Sender(String message, PrintWriter writer) {
        mMessage = message;
        mWriter = writer;
    }

    @Override
    public void run() {
        if (mWriter != null) {
            mWriter.println(mMessage);
        } else {
            Log.d("Sender", "Writer null!");
        }
    }
}
