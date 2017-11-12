package com.happylich.bridge.game;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by wangt on 2017/11/10.
 * View->Manager->Player->Card
 * Manager控制整个游戏的流程，提供游戏辅助资源
 *
 */

public class Manager {
    private Player[] players = new Player[4];
    private int[] allCards = new int[52];
    private int[][] playerCards = new int[4][13];
    private int[][] playerCardsPosition = {{160, 660}, {0, 160}, {160, 0}, {600, 160}};

    private Context context;
    private Canvas  canvas;

    private int op = -1;

    public Manager(Context context) {
        this.context = context;
        init();
//        redoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_redo);
//        passImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_pass);
//        chuPaiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_chupai);
//        tiShiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_tishi);
//        farmerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_farmer);
//        landlordImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_landlord);
    }

    /**
     * 初始化游戏函数，也就是发牌的逻辑
     */
    public void init() {
        allCards = new int[52];
        for (int i = 0; i < allCards.length; i++) {
            allCards[i] = i;
        }
        playerCards = new int[4][13];
        // 洗牌
        xipai(allCards);
        // 发牌
        fapai(allCards);
        // 排序
        sort(playerCards[0]);
        sort(playerCards[1]);
        sort(playerCards[2]);
        sort(playerCards[3]);
        // 使用这些牌新建玩家
        players[0] = new Player(playerCards[0], playerCardsPosition[0][0],
                playerCardsPosition[0][1], true,0, this, context);
        players[1] = new Player(playerCards[1], playerCardsPosition[1][0],
                playerCardsPosition[1][1], false, 0, this, context);
        players[2] = new Player(playerCards[2], playerCardsPosition[2][0],
                playerCardsPosition[2][1], true, 0, this, context);
        players[3] = new Player(playerCards[3], playerCardsPosition[3][0],
                playerCardsPosition[3][1], false,0, this, context);
        // 设置玩家游戏顺序
        players[0].setLastAndNext(players[3], players[1]);
        players[1].setLastAndNext(players[0], players[2]);
        players[2].setLastAndNext(players[1], players[3]);
        players[3].setLastAndNext(players[2], players[0]);
    }
    public void processDraw(Canvas canvas) {
        paintGaming(canvas);
//        switch(op) {
//            case -1:
//                break;
//            case 0:
//                paintGaming(canvas);
//                break;
//            case 1:
////                paintResult(canvas);
//                break;
//        }
    }
    private void paintGaming(Canvas canvas) {
        players[0].paint(canvas);
        players[1].paint(canvas);
        players[2].paint(canvas);
        players[3].paint(canvas);
    }
    public void onTouch(int x, int y) {
        // for (int i = 0; i < players.length; i++) {
        // StringBuffer sb = new StringBuffer();
        // sb.append(i + " : ");
        // for (int j = 0; j < players[i].cards.length; j++) {
        // sb.append(players[i].cards[j] + (players[i].cards[j] >= 10 ? "" :
        // " ") + ",");
        // }
        // System.out.println(sb.toString());
        // }

        // 若游戏结束，则点击任意屏幕重新开始
//        if (op == 1) {
//            op = -1;
//        }
//        players[0].onTuch(x, y);
//        if (currentId == 0) {
//
//            if (CardsManager.inRect(x, y, (int) (buttonPosition_X * MainActivity.SCALE_HORIAONTAL),
//                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
//                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
//                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
//                System.out.println("出牌");
//                ifClickChupai = true;
//
//            }
//            if (currentCircle != 0) {
//                if (CardsManager.inRect(x, y,
//                        (int) ((buttonPosition_X - 80) * MainActivity.SCALE_HORIAONTAL),
//                        (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
//                        (int) (80 * MainActivity.SCALE_HORIAONTAL),
//                        (int) (40 * MainActivity.SCALE_VERTICAL))) {
//                    System.out.println("不要");
//                    buyao();
//                }
//            }
//            if (CardsManager.inRect(x, y,
//                    (int) ((buttonPosition_X + 80) * MainActivity.SCALE_HORIAONTAL),
//                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
//                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
//                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
//                System.out.println("重选");
//                players[0].redo();
//            }
//            if (CardsManager.inRect(x, y,
//                    (int) ((buttonPosition_X + 160) * MainActivity.SCALE_HORIAONTAL),
//                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
//                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
//                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
//                System.out.println("提示（重新）");
//                restart();
//            }
//        }
    }
    public void processGame() {
        switch(op) {
            case -1 :
                init();
                op = 0;
                break;
            case 0 :
                IsOver();
                break;
            case 1:
                break;
        }
    }
    private void IsOver() {
        return;
    }
    public void xipai(int[] cards) {
        int len = cards.length;
        for (int i=0; i<len; i++) {
            int des = new Random().nextInt(52);
            int temp = cards[i];
            cards[i] = cards[des];
            cards[des] = temp;
        }
    }
    public void fapai(int[] cards) {
        for (int i=0; i<52; i++) {
            playerCards[i/13][i%13] = cards[i];
        }
    }
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
