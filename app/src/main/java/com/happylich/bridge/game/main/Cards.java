package com.happylich.bridge.game.main;

import java.util.Random;

/**
 * Created by lich on 2018/4/4.
 * 这个类取代原先由Game管理的Cards
 * 这个类被Game用来发牌
 * Player自己维护一个cards数组
 *
 */

public class Cards {
    private int[] cards;
    private int   numberOfPlayer = 0;

    /**
     * 构造函数
     */
    public Cards(int numberOfCards) {
        initCards(numberOfCards);
    }

    /**
     * 初始化所有牌，并打乱
     */
    public void initCards(int numberOfCards) {
        cards = new int[numberOfCards];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = i;
        }
        for (int i = 0; i < cards.length; i++) {
            int des = new Random().nextInt(52);
            int temp = cards[i];
            cards[i] = cards[des];
            cards[des] = temp;
        }
    }

    /**
     * 玩家用这个函数获得手牌(而不是之前的fapai函数)
     * @return 取得的牌
     */
    public int[] getCards(int numberOfCards) {
        int[] cards = new int[numberOfCards];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = this.cards[numberOfPlayer * numberOfCards + i];
        }
        // 发牌前将牌序整理好
        numberOfPlayer++;
        if (numberOfPlayer >= 4) {
            numberOfPlayer = 0;
        }
        sort(cards);
        return cards;
    }

    /**
     * 对牌进行排序
     * @param cards
     */
    public void sort(int[] cards) {
        for (int i = 0; i < cards.length; i++) {
            for (int j = i + 1; j < cards.length; j++) {
                if (cards[i] < cards[j]) {
                    int temp = cards[i];
                    cards[i] = cards[j];
                    cards[j] = temp;
                }
            }
        }
    }
}
