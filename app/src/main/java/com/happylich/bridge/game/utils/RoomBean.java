package com.happylich.bridge.game.utils;

/**
 * Created by lich on 2018/5/18.
 */

/**
 * 房间类
 * 1. 房间的IP
 * 2. 房间的状态
 */
public class RoomBean {
    private String mIP = "";
    private String mState;

    private int maxTime;
    private int time;

    public void setIP(String ip) {
        this.mIP = ip;
    }

    public String getIP() {
        return mIP;
    }

    public void setState(String state) {
        if (state.equals("0")) {
            this.mState = "等待加入";
        } else if (state.equals("1")) {
            this.mState = "房间已满";
        } else if (state.equals("2")) {
            this.mState = "正在游戏";
        } else {
            this.mState = String.valueOf(time);
        }
//        this.mState = state;
    }

    public String getState() {
        return String.valueOf(this.time);
    }

    public void setTime(int time) {
        this.maxTime = time;
        this.time = time;
    }

    public void refreshTime() {
        this.time--;
    }


    public void addTime() {
        if (this.time < this.maxTime) {
            this.time++;
        }
    }

    public int getTime() {
        return this.time;
    }


}
