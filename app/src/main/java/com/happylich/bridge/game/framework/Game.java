package com.happylich.bridge.game.framework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.game.framework.call.Call;
import com.happylich.bridge.game.framework.player.AbstractPlayer;
import com.happylich.bridge.game.framework.player.Player;
import com.happylich.bridge.game.framework.player.Robot;

import java.util.Random;

/**
 * 代表桥牌游戏实体
 * Created by lich on 2018/3/25.
 */

public class Game extends com.happylich.bridge.engine.game.Game{

    // 有两个变量在推进游戏进程向前：
    // 1. stage（循环与线性交替）
    // 2. player（这个是在四个玩家之间不断循环的）

    // 实体：有绘制界面和处理触摸事件需求的实体
    // 1. player 玩家
    //   1.1 card
    // 2. call 叫牌
    // 3. tips 提示
    // 4. table 桌面
    // 5. widget 按钮等

    // 1 叫牌
    // 2 调整座位
    // 3 打牌
    // 3.1 列牌
    // 4 结算
    // 4.1 摊牌
    int x = 100;
    int y = 100;

    private Context context;

    // 游戏进行的阶段
    protected int stage = 0;

    // 本局牌库
    private int[] allCards;
    // 玩家手牌
    private int[][] playerCards = new int[4][13];

    // 叫牌矩阵
    private Call call;
    // 叫牌矩阵的绘制空间
    private int[] callPosition = new int[2];

    // 玩家
    private AbstractPlayer[] players = new AbstractPlayer[4];
    // 每个玩家的绘制区间
    private int[] playerPositionTop = new int[2];
    private int[] playerPositionLeft = new int[2];
    private int[] playerPositionRight = new int[2];
    private int[] playerPositionBottom = new int[2];
    // 叫牌区域的绘制区间
    // 打牌区域的绘制区间

    /**
     * 构造函数
     */
    public Game(Context context) {
        // 绑定资源
        this.context = context;

        // 计算每个玩家的绘制区间
        // 错误的调用时机
//        initPlayersPosition();
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

        // 初始化玩家
        // TODO:关键在初始化玩家的时候没有将宽高进行正确的初始化
        // TODO:但是宽高是在setWidthHeight之后才执行的操作
        players[0] = new Robot(context,0, playerCards[0]);
//        players[0].setPosition(playerPositionBottom);
        players[1] = new Robot(context,1, playerCards[1]);
        players[2] = new Robot(context,2, playerCards[2]);
        players[3] = new Player(context,3, playerCards[3]);
        players[3].setStage(201);

        call = new Call(context);
    }

    /**
     * 设置宽高
     * @param width 宽度
     * @param height 高度
     */
    @Override
    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
        initWidgetPosition();
        setWidgetPosition();
        setWidgetWidthHeight();
    }

    /**
     * 触摸事件
     * @param x
     * @param y
     */
    @Override
    public void onTouch(int x, int y) {
//        super.onTouch(x, y);
        Log.v(this.getClass().getName(), "GameStage");
        Log.v(this.getClass().getName(), String.valueOf(stage));
        switch(stage) {
            case 0:
                // 根据当前玩家的动作调整阶段
                // 但是参与游戏的不一定有当前玩家
                // 还应当有一个标志位用来区分是否是当前玩家的回合
                switch(call.onTouch(x, y)) {
                    case 0:
                        stage = 0;
                        break;
                    case 1:
                        stage = 1;
                        break;
                    case 2:
                        stage = 2;
                        break;
                }
                break;
            case 1:
                switch(call.onTouch(x, y)) {
                    case 0:
                        stage = 0;
                        break;
                    case 1:
                        stage = 1;
                        break;
                    case 2:
                        stage = 2;
                        break;
                }
                break;
            case 2:
                switch(call.onTouch(x, y)) {
                    case 0:
                        stage = 0;
                        break;
                    case 1:
                        stage = 1;
                        break;
                    case 2:
                        stage = 2;
                        break;
                }
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
     * 绘图事件（代表每100ms要进行的绘图操作）
     * 逻辑更新在这里完成
     * 区分每个阶段的原则是：阶段检测按键的逻辑发生变化后就引入新的阶段来表现
     * 0. 叫牌阶段1：只有下方的显示内容，显示叫牌矩阵和历史叫牌数据？，点击叫牌矩阵进入阶段1
     * 1. 叫牌阶段2：只有下方的显示内容，显示大号的叫牌矩阵，点击无效区域进入阶段1/点击有效区域进入阶段2
     * 2. 叫牌阶段3：只有下方的显示内容，显示大号选中的叫牌矩阵，点击无效区域进入阶段1/点击有效区域进入阶段1
     * 3. 叫牌结束阶段：当连续三名玩家PASS之后，进入叫牌结束阶段
     * 4. 调整座位阶段：叫牌结束阶段之后，调整玩家的座位
     * 5. 按照顺序出牌
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        // drawText(canvas);
        initCanvas(canvas);
        switch(stage) {
            case 0:
                // 叫牌I阶段
                // 绘制下面的玩家和叫牌矩阵
                // 只有叫牌矩阵是响应按键的
                players[3].draw(canvas);
                call.setCallFlag(0);
                call.draw(canvas);
                break;
            case 1:
                // 叫牌II阶段
                // 绘制下面的玩家和叫牌矩阵
                // 只有叫牌矩阵是响应按键的
                players[3].draw(canvas);
                call.setCallFlag(1);
                call.draw(canvas);
                break;
            case 2:
                // 叫牌III阶段
                // 绘制下面的玩家和叫牌矩阵
                // 只有叫牌矩阵是响应按键的/或者机器人的叫牌/或者远程玩家的叫牌
                players[3].draw(canvas);
                call.setCallFlag(2);
                call.draw(canvas);
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    /**
     * 初始化画布
     * @param canvas
     */
    public void initCanvas(Canvas canvas) {
        Paint paint = new Paint();
        // 绘制底色
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, this.width, this.height, paint);

        // 设置缩放
        canvas.scale( (float)this.width / (float)1440 ,(float)this.width / (float)1440);

        // 绘制一个描述框
        paint.setColor(Color.CYAN);
        canvas.drawRect(0, 0, 1440, 360, paint);
    }

    /**
     * 适应性测试
     */
    public void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);

        // 绘制底色
        canvas.drawRect(0, 0, this.width, this.height, paint);

        // 统一化
        canvas.scale( (float)this.width / (float)1440 ,(float)this.width / (float)1440);

        // 绘制有效区域
        paint.setColor(Color.RED);
        canvas.drawRect(0, 0, 1440, 2160, paint);

        // 绘制参考线
        paint.setColor(Color.BLUE);
        canvas.drawLine(0, 1080, 1440, 1080, paint);
        canvas.drawLine(0, 2159, 1440, 2159, paint);
        canvas.drawLine(720, 0, 720, 2160, paint);
        canvas.drawLine(1439, 0, 1439, 2160, paint);

        // 绘制尺寸数据
        paint.setColor(Color.WHITE);
        canvas.drawText(String.valueOf((float)this.width / (float)1440), 100, 100, paint);
        canvas.drawText(String.valueOf(this.width), 100, 200, paint);
        canvas.drawText(String.valueOf(this.height), 100, 300, paint);

        // 绘制参考点
        paint.setColor(Color.MAGENTA);
        canvas.drawCircle(1440, 2160, 100, paint);
    }
    /**
     * 计算玩家位置
     */
    public void initWidgetPosition() {
        // 上
        playerPositionTop[0] = 360;
        playerPositionTop[1] = 360;
        // 左
        playerPositionLeft[0] = 0;
        playerPositionLeft[1] = 720;
        // 右
        playerPositionRight[0] = 1170;
        playerPositionRight[1] = 720;
        // 下
        playerPositionBottom[0] = 360;
        playerPositionBottom[1] = 1800;
        // 叫牌矩阵
        callPosition[0] = 360;
        callPosition[1] = 360;
        // 牌桌
    }

    /**
     * 设置玩家位置
     */
    public void setWidgetPosition() {
        // 牌桌
        players[0].setPosition(playerPositionTop);
        players[1].setPosition(playerPositionLeft);
        players[1].setPosition(playerPositionRight);
        players[3].setPosition(playerPositionBottom);

        // 叫牌矩阵
        call.setPosition(callPosition);
    }

    /**
     * 设置宽高
     */
    public void setWidgetWidthHeight() {
        // 玩家
        players[0].setWidthHeight(this.width, this.height);
        players[1].setWidthHeight(this.width, this.height);
        players[2].setWidthHeight(this.width, this.height);
        players[3].setWidthHeight(this.width, this.height);

        // 叫牌矩阵
        call.setWidthHeight(this.width, this.height);

        // 牌桌
    }

    /**
     * 洗牌
     * @param cards
     */
    private void xipai(int[] cards) {
        int len = cards.length;
        for (int i=0; i<len; i++) {
            int des = new Random().nextInt(52);
            int temp = cards[i];
            cards[i] = cards[des];
            cards[des] = temp;
        }
    }

    /**
     * 发牌
     * @param cards
     */
    private void fapai(int[] cards) {
        for (int i=0; i<52; i++) {
            playerCards[i/13][i%13] = cards[i];
        }
    }

    /**
     * 对牌进行排序
     * @param cards
     */
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
}
