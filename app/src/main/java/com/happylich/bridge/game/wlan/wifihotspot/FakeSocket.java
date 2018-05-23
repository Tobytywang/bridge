package com.happylich.bridge.game.wlan.wifihotspot;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by lich on 2018/5/23.
 */

public class FakeSocket extends Thread {

    @Override
    public void run() {

        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1",8003);      //!!!!!!!!!!!
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            socket.getOutputStream().write(1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
