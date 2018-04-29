package com.happylich.bridge.game.main;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lich on 2018/4/4.
 * 这个类取代原先由Game管理的Cards
 * 这个类被Game用来发牌
 * Player自己维护一个cards数组
 *
 */

public class Cards {
//    private int[] cards;
    private ArrayList<Integer> cards;
    private int   numberOfPlayer = 0;
    private int   numberOfCards = 0;

    /**
     * 构造函数
     */
    public Cards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
        this.cards = new ArrayList<>();
        for (int i = 0; i < this.numberOfCards; i++) {
            cards.add(i);
        }
        for (int i = 0; i < cards.size(); i++) {
            int des = new Random().nextInt(52);
            int temp = cards.get(i);
            cards.set(i, cards.get(des));
            cards.set(des, temp);
        }
    }

    /**
     * 玩家用这个函数获得手牌(而不是之前的fapai函数)
     * @return 取得的牌
     */
    public ArrayList<Integer> getCards(int numberOfPlayer) {
        ArrayList<Integer> cards = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            cards.add(this.cards.get(numberOfPlayer * 13 + i));
        }
        // 发牌前将牌序整理好
        sort(cards);
        return cards;
    }

    /**
     * 对牌进行排序
     * @param cards
     */
    public void sort(ArrayList<Integer> cards) {
        for (int i = 0; i < cards.size(); i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                if (cards.get(i) < cards.get(j)) {
                    int temp = cards.get(j);
                    cards.set(j, cards.get(i));
                    cards.set(i, temp);
                }
            }
        }
    }
}
