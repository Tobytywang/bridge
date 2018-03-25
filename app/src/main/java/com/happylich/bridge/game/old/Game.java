package com.happylich.bridge.game.old;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.happylich.bridge.R;

import java.util.Random;

/**
 * Created by wangt on 2017/11/10.
 * View->Game->Players->Card
 * Manager控制整个游戏的流程，提供游戏辅助资源
 *
 */

/**
 * 由于绘制工作大多由Manager完成，所以Manager也要负责适配工作
 */

public class Game {
    private AbstractPlayer[] players = new AbstractPlayer[4];
    private int[] allCards = new int[52];
    private int[][] playerCards = new int[4][13];
    private int[][] playerCardsPosition = {{160, 980}, {0, 300}, {160, 0}, {600, 300}};

    private Context context;
    private Canvas canvas;

    private int op = -1;
    private int selectFlag = -1;
    private int lastCallCard = -1;
    private int currentPlayer;
    private int hostPlayer;
//    private int currentId;

    /**
     * 构造函数
     * @param context
     */
    public Game(Context context) {
        this.context = context;
        init();
    }

    /**
     * 初始化游戏，包括发牌和确定玩家顺序等
     */
    public void init() {
        // 初始化绘制位置

        // 初始化牌组
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
        players[0] = new PlayerBottom(1, playerCards[0], playerCardsPosition[0][0],
                playerCardsPosition[0][1], true, this, context);
        players[1] = new PlayerLeft(2, playerCards[1], playerCardsPosition[1][0],
                playerCardsPosition[1][1], false, this, context);
        players[2] = new PlayerTop(3, playerCards[2], playerCardsPosition[2][0],
                playerCardsPosition[2][1], false, this, context);
        players[3] = new PlayerRight(4, playerCards[3], playerCardsPosition[3][0],
                playerCardsPosition[3][1], false, this, context);
        // 设置玩家游戏顺序
        players[0].setLastAndNext(players[3], players[1]);
        players[1].setLastAndNext(players[0], players[2]);
        players[2].setLastAndNext(players[1], players[3]);
        players[3].setLastAndNext(players[2], players[0]);
        // 设置初始玩家
        hostPlayer = 0;
        currentPlayer = (int)(1+Math.random()*(3)); //从1到10的int型随数
        // 设置游戏阶段
        op = 0;
    }

    // 游戏由四个人进行游戏，每个人操作完后都交给GamePaint进行绘制
    // currentPlayer和op共同决定了游戏的逻辑
    // 绘制函数GamePaint需要op作为参数
    // GamePaint内会依次绘制各个玩家的状态
    public void process(Canvas canvas) {
        this.canvas = canvas;
        switch(op) {
            case 0:
                GamePaint(canvas, 0);
                break;
            case 1:
                GamePaint(canvas, 1);
                break;
            case 2:
                GamePaint(canvas, 2);
                break;
            case 3:
                GamePaint(canvas, 3);
                break;
            default:
                GamePaint(canvas, 4);
                break;
        }
    }

    /**
     * 绘制游戏画面
     * @param canvas
     */
    private void GamePaint(Canvas canvas, int op) {
        Bitmap Image;
        canvas.drawColor(Color.WHITE);
        String string;
        // 如果currentPlayer是hostPlayer，就等hostPlayer选择
        // 如果currentPlayer不是hostPlayer，就直接+1
        switch(op) {
            case 0: // MetricsPaint阶段
                players[0].paint0(canvas);
//                players[1].paint0(canvas);
//                players[2].paint0(canvas);
//                players[3].paint0(canvas);
                MetricsPaint(canvas);
                string = "阶段0 "+ "玩家" + currentPlayer + " " + "上一张牌" + lastCallCard + " 选择" +selectFlag;
                TipsPaint(canvas, players[0].cards, string);
                break;
            case 1: // MetricsPaintBig阶段
                players[0].paint0(canvas);
//                players[1].paint0(canvas);
//                players[2].paint0(canvas);
//                players[3].paint0(canvas);
                MetricsPaintBig(canvas);
                string = "阶段1 "+ "玩家" + currentPlayer + " " + "上一张牌" + lastCallCard + " 选择" + selectFlag;
                TipsPaint(canvas, players[0].cards, string);
                break;
            case 2: // MetricsPaintBigSelect阶段
                players[0].paint0(canvas);
//                players[1].paint0(canvas);
//                players[2].paint0(canvas);
//                players[3].paint0(canvas);
                MetricsPaintBigSelect(canvas);
                string = "阶段2 "+ "玩家" + currentPlayer + " " + "上一张牌" + lastCallCard + " 选择" + selectFlag;
                TipsPaint(canvas, players[0].cards, string);
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                break;
        }
    }

    /**
     * 处理游戏事件
     * @param x
     * @param y
     */
    public void onTouch(int x, int y) {
        if (currentPlayer == hostPlayer) {
            switch(op) {
                case 0: // Metrics阶段，如果在区域中间，切换到MetricsBig
                    if(inRect(x, y, 120, 295, 480, 537)) {
                        op = 1;
                    } else {
                        op = 0;
                    }
                    break;
                case 1: // MetricsBig阶段，如果
                    for(int i=0; i<7; i++) {
                        for (int j=0; j<5; j++) {
                            if ((i*5+j)>lastCallCard) {
                                if ((op == 1) && inRect(x, y, (720 - 470) / 2 + 78 * j + 20 * j, 315 + 78 * i - 2 * i, 78, 78)) {
                                    // 表示选中效果，绘制更大幅的该图像
                                    op = 2;
                                    selectFlag = i * 5 + j;
                                }
                            }
                        }
                    }
                    if ((op == 1) && inRect(x, y,135, 850, 255, 80)) {
                        op = 0;
                        selectFlag = -1;
                    }
//                else if ((op == 1) && inRect(x, y,428, 850, 160, 80)) {
//                    op = 0;
//                    selectFlag = -1;
//                }
                    break;
                case 2: // MetricsBig2阶段，如果确认就进入轮询，如果点其他地方就返回阶段1
                    int flag = 0;
                    // 如果有选中的牌，再次点击选择(2阶段)
                    // 如果没有选中的牌，点击选择(1阶段)
                    for(int i=0; i<7; i++) {
                        for (int j=0; j<5; j++) {
                            if ((i*5+j)>lastCallCard) {
                                if (selectFlag == (i * 5 + j)) {
                                    if (inRect(x, y, (720 - 470) / 2 + 78 * j + 20 * j - 15, 315 + 78 * i - 2 * i - 15, 108, 108)) {
                                        // 点击大卡进行确认操作
                                        flag = 1;
                                        lastCallCard = selectFlag;
                                        players[currentPlayer].calls.add(selectFlag);
                                        currentPlayer++;
                                        op = 0;
                                        selectFlag = -1;
                                        break;
                                    }
                                } else {
                                    if (inRect(x, y, (720 - 470) / 2 + 78 * j + 20 * j, 315 + 78 * i - 2 * i, 78, 78)) {
                                        // 更换另外的卡片
                                        flag = 1;
                                        op = 2;
                                        selectFlag = i * 5 + j;
                                        break;
                                    }
                                }
                            }
                        }
                        if (flag == 1) break;
                    }
                    if (flag == 0) {
                        if ((op == 1) && inRect(x, y,135, 850, 255, 80)) {
                            currentPlayer++;
                            if (currentPlayer > 3) {
                                currentPlayer = 0;
                            }
                            op = 0;
                            selectFlag = -1;
                        } else {
                            op = 1;
                            selectFlag = -1;
                        }
                    }
                    break;
                case 3:
                    if (currentPlayer == 0) {
                        players[0].onTouch(x, y);
                    }
                    break;
                case 4:
                    break;
                default:
                    break;
            }
        } else {
            players[currentPlayer].calls.add(lastCallCard++);
            currentPlayer++;
            if (currentPlayer > 3) {
                currentPlayer = 0;
            }
            op = 0;
        }

    }
    private void TipsPaint(Canvas canvas, int[] cards, String string) {
        Paint paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setTextSize(40);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        // 构造显示的字符串
//        String string;
        Integer point = 0;
        for (int i=0; i<cards.length; i++) {
            if (cards[i]%13 >= 9) {
                point += (cards[i]%13-8);
            }
        }
//        canvas.drawText("大牌点："+point.toString(), 0, 40, paint);
        canvas.drawText(string, 0, 40, paint);
    }
    private void MetricsPaint(Canvas canvas) {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();
        // 绘制选择矩阵
        for(int i=0; i<7; i++) {
            for (int j=0; j<5; j++) {
                if ((i*5+j)>lastCallCard) {
                    Image = BitmapFactory.decodeResource(context.getResources(), CardImage.resImages[i * 5 + j]);
                    des.set((720 - 410) / 2 + 78 * j + 5 * j, 315 + 78 * i - 23 * i, (720 - 410) / 2 + 78 * (j + 1) + 5 * j, 315 + 78 * (i + 1) - 23 * i);
                    canvas.drawBitmap(Image, null, des, paint);
                    Image = null;
                }
            }
        }
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pass);
        des.set(155, 725, 400, 805);
        canvas.drawBitmap(Image, null, des, paint);
//        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.enter);
//        des.set(405, 725, 565, 805);
//        canvas.drawBitmap(Image, null, des, paint);
        // 绘制线条
        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setStrokeWidth(5);
        canvas.drawLine(120, 295, 320, 295, paint1);
        canvas.drawLine(400, 295, 600, 295, paint1);

        canvas.drawLine(600, 295, 600, 520, paint1);
        canvas.drawLine(600, 580, 600, 832, paint1);

        canvas.drawLine(600, 832, 400, 832, paint1);
        canvas.drawLine(320, 832, 120, 832, paint1);

        canvas.drawLine(120, 832, 120, 580, paint1);
        canvas.drawLine(120, 520, 120, 295, paint1);
        // 绘制文字
        Paint paint2 = new Paint();
        paint2.setStrokeWidth(3);
        paint2.setTextSize(40);
        paint2.setColor(Color.BLACK);
        paint2.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("北", 360, 307, paint2);
        canvas.drawText("东", 600, 565, paint2);
        canvas.drawText("南", 360, 847, paint2);
        canvas.drawText("西", 120, 565, paint2);
//        for(int i=0; i<7; i++) {
//            for (int j=0; j<5; j++) {
//                Image = BitmapFactory.decodeResource(context.getResources(), CardImage.resImages[i*5+j]);
//                des.set((720-618)/2+84*i+5*i, 360+84*j+5*j, (720-618)/2+84*(i+1)+5*i, 360+84*(j+1)+5*j);
//                canvas.drawBitmap(Image, null, des, paint);
//                Image = null;
//            }
//        }
    }
    private void MetricsPaintBig(Canvas canvas) {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();
        int x=-1;
        int y=-1;
        // 绘制选择矩阵
        for(int i=0; i<7; i++) {
            for (int j=0; j<5; j++) {
                if ((i*5+j)>lastCallCard) {
                    Image = BitmapFactory.decodeResource(context.getResources(), CardImage.resImages[i * 5 + j]);
                    des.set((720 - 470) / 2 + 78 * j + 20 * j, 315 + 78 * i - 2 * i, (720 - 470) / 2 + 78 * (j + 1) + 20 * j, 315 + 78 * (i + 1) - 2 * i);
                    canvas.drawBitmap(Image, null, des, paint);
                    Image = null;
                }
            }
        }
        // 绘制pass
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pass);
        des.set(135, 855, 390, 935);
        canvas.drawBitmap(Image, null, des, paint);
        // 绘制enter
//        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.enter);
//        des.set(428, 855, 588, 935);
//        canvas.drawBitmap(Image, null, des, paint);
    }
    private void MetricsPaintBigSelect(Canvas canvas) {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();
        int x=-1;
        int y=-1;
        // 绘制选择矩阵
        for(int i=0; i<7; i++) {
            for (int j=0; j<5; j++) {
                if ((i*5+j)>lastCallCard) {
                    if (selectFlag == (i * 5 + j)) {
                        // 表示选中效果，绘制更大幅的该图像
                        x = i;
                        y = j;
                    } else {
                        Image = BitmapFactory.decodeResource(context.getResources(), CardImage.resImages[i * 5 + j]);
                        des.set((720 - 470) / 2 + 78 * j + 20 * j, 315 + 78 * i - 2 * i, (720 - 470) / 2 + 78 * (j + 1) + 20 * j, 315 + 78 * (i + 1) - 2 * i);
                        canvas.drawBitmap(Image, null, des, paint);
                    }
                    Image = null;
                }
            }
        }
        // 绘制pass
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pass);
        des.set(135, 855, 390, 935);
        canvas.drawBitmap(Image, null, des, paint);
        // 绘制enter
//        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.enter);
//        des.set(428, 855, 588, 935);
//        canvas.drawBitmap(Image, null, des, paint);
        // 绘制大卡片
        if (x!=-1 && y!=-1) {
            Image = BitmapFactory.decodeResource(context.getResources(), CardImage.resImages[x * 5 + y]);
            des.set((720 - 470) / 2 + 78 * y + 20 * y - 15, 315 + 78 * x - 2 * x - 15, (720 - 470) / 2 + 78 * (y + 1) + 20 * y + 15, 315 + 78 * (x + 1) - 2 * x + 15);
            canvas.drawBitmap(Image, null, des, paint);
        }
    }
    private void DrawBigCard(Canvas canvas) {
        // 根据标志位，直接绘制一张比原来大的卡片
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();
        for(int i=0; i<7; i++) {
            for (int j=0; j<5; j++) {
                if (selectFlag == (i*5+j)) {
                    // 表示选中效果，绘制更大幅的该图像
                    Image = BitmapFactory.decodeResource(context.getResources(), CardImage.resImages[i*5+j]);
                    des.set((720-470)/2+78*j+20*j-15, 315+78*i-2*i-15, (720-470)/2+78*(j+1)+20*j+15, 315+78*(i+1)-2*i+15);
                    canvas.drawBitmap(Image, null, des, paint);
                }
            }
        }
    }
    private void xipai(int[] cards) {
        int len = cards.length;
        for (int i=0; i<len; i++) {
            int des = new Random().nextInt(52);
            int temp = cards[i];
            cards[i] = cards[des];
            cards[des] = temp;
        }
    }
    private void fapai(int[] cards) {
        for (int i=0; i<52; i++) {
            playerCards[i/13][i%13] = cards[i];
        }
    }
    private void sort(int[] cards) {
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
    private boolean inRect(int x, int y, int rectX, int rectY, int rectW, int rectH) {
        if (x <= rectX || x >= rectX + rectW || y<=rectY || y >= rectY + rectH){
            return false;
        }
        return true;
    }
}
