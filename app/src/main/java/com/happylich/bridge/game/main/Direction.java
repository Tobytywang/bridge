package com.happylich.bridge.game.main;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lich on 2018/5/11.
 */

// 指定参数的情况下，获得指定的direction
// 未指定参数的情况下，从剩下的direction中取
public class Direction {

    private ArrayList<Integer> directions;

    /**
     * 构造函数
     */
    public Direction() {
        this.directions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.directions.add(i);
        }
        for (int i = 0; i < 4; i++) {
            int des = new Random().nextInt(4);
            int temp = directions.get(i);
            this.directions.set(i, directions.get(des));
            this.directions.set(des, temp);
        }
    }

    /**
     * 玩家用这个函数获得指定的位置
     * @return 取得的牌
     */
    public int getDirections(int direction) {
        return directions.remove(directions.indexOf(direction));
    }

    /**
     * 玩家用这个函数获得随机的位置
     */
    public int getDirections() {
        int index = new Random().nextInt(this.directions.size());
        return directions.remove(index);
    }
}
