package com.happylich.bridge.game.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.engine.thread.GameThread;
import com.happylich.bridge.game.Scene.Call;
import com.happylich.bridge.game.Scene.Table;
import com.happylich.bridge.game.player.AbstractPlayer;

import java.util.Date;

/**
 * Created by lich on 2018/3/25.
 * 叫牌阶段要确定庄家和定约，庄家的下一家是首攻，对家是明手
 */

public class Game extends com.happylich.bridge.engine.game.Game{

    private Context context;

    // stage和player一起推动游戏进程向前进行
    protected int stage = 2;
    protected int player = 0;

    // 叫牌
    private Call call;
    // 打牌
    private Table table;

    // 玩家
    private AbstractPlayer playerN;
    private AbstractPlayer playerS;
    private AbstractPlayer playerW;
    private AbstractPlayer playerE;

    private AbstractPlayer localPlayer;
    private int localPlayerNumber;

    // 赢墩（界面上显示当前玩家的）
    private int nsContract = -1;
    private int weContract = -1;
    private int nsNow = -1;
    private int weNow = -1;

    //各类绘制尺寸
    private int[] callPosition = new int[2];
    private int[] tablePosition = new int[2];
    private int[] playerPositionTop = new int[2];
    private int[] playerPositionLeft = new int[2];
    private int[] playerPositionRight = new int[2];
    private int[] playerPositionBottom = new int[2];

    private int hasSetScale = 0;

    /**
     * 构造函数
     */
    public Game(Context context) {
        this.context = context;
        // TODO:关键在初始化玩家的时候没有将宽高进行正确的初始化
        // TODO:但是宽高是在setWidthHeight之后才执行的操作
    }

    /**
     * 设置叫牌矩阵
     * @param call
     */
    public void setCall(Call call) {
        this.call = call;
    }

    /**
     * 设置牌桌
     * @param table
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * 设置本地玩家
     * @param player
     */
    public void setLocalPlayer(AbstractPlayer player) {
        localPlayer = player;
    }

    public void setLocalPlayerNumber(int number) {
        localPlayerNumber = number;
    }

    /**
     * 设置玩家N
     * @param player
     */
    public void setPlayerN(AbstractPlayer player) {
        playerN = player;
    }

    /**
     * 设置玩家S
     * @param player
     */
    public void setPlayerS(AbstractPlayer player) {
        playerS = player;
    }

    /**
     * 设置玩家W
     * @param player
     */
    public void setPlayerW(AbstractPlayer player) {
        playerW = player;
    }

    /**
     * 设置玩家E
     * @param player
     */
    public void setPlayerE(AbstractPlayer player) {
        playerE = player;
    }



    /**
     * 对于本地人类玩家：处理触摸事件
     * @param x
     * @param y
     */
    @Override
    public void onTouch(int x, int y) {
        switch (stage) {
            case 0:
                // 未准备界面
                break;
            case 1:
                // 已准备界面
                break;
            case 2:
                if (player == 0)
                {
                    switch (call.onTouch(x, y)) {
                        case 0:
                            stage = 2;
                            break;
                        case 1:
                            stage = 3;
                            break;
                        case 2:
                            stage = 4;
                            break;
                        case 3:
                            // 为了解决之前的bug
                            stage = 2;
                            player++;
                            break;
                    }
                }
                break;
            case 3:
                if (player == 0) {
                    switch (call.onTouch(x, y)) {
                        case 0:
                            // TODO:这个地方可以有吗？不可以，会引起big的情况跳过玩家的bug
                            // TODO:玩家按PASS后player不会++
//                            player++;
                            stage = 2;
                            break;
                        case 1:
                            stage = 3;
                            break;
                        case 2:
                            stage = 4;
                            break;
                        case 3:
                            // 为了解决之前的bug
                            stage = 2;
                            player++;
                            break;
                    }
                }
                break;
            case 4:
                if (player == 0) {
                    switch (call.onTouch(x, y)) {
                        case 0:
                            player++;
                            stage = 2;
                            break;
                        case 1:
                            stage = 3;
                            break;
                        case 2:
                            stage = 4;
                            break;
                        case 3:
                            // 为了解决之前的bug
                            stage = 2;
                            player++;
                            break;
                    }
                }
                break;
            case 5:
                // 确定首攻：庄家的下家
                player = table.getPlayer();
                Log.v(this.getClass().getName(),"首攻:"+String.valueOf(player));
                stage = 6;
                break;
            case 6:
                // 出牌检测（为什么是player0）
                // 0表示当前阶段——什么都不按
                // 1表示显示选中
                // 2表示出牌
                // 应该有一个标志位表示本地玩家是哪个？

                Log.v(this.getClass().getName(), "Player: " + String.valueOf(player));
                // TODO:还需要检测玩家的类型是否是机器人
                // 如果当前玩家时本地玩家——需要检测触摸事件
                // 如果本地玩家是庄家并且当前玩家是本地玩家（庄家）的对家——需要检测触摸事件
                Log.v(this.getClass().getName(), "需要检测触摸事件吗");
                if (player == localPlayerNumber ||
                        ( localPlayerNumber == call.getDealer() &&
                                (player == (localPlayerNumber + 2) || (player == (localPlayerNumber - 2))))) {
                    Log.v(this.getClass().getName(), "需要");
                    switch (table.onTouch(x, y)) {
                        case 0:
                            // 触摸无效部分
                            Log.v(this.getClass().getName(), "无效触摸事件");
//                            stage = 6;
                            break;
                        case 1:
                            // 选中牌的状态
                            Log.v(this.getClass().getName(), "选中牌事件");
//                            stage = 6;
                            break;
                        case 2:
                            // 出牌的状态
                            Log.v(this.getClass().getName(), "出牌事件");
//                            player++;
//                            stage = 6;
                            break;
                    }
                }
                Log.v(this.getClass().getName(), "不需要");
                break;
            default:
                break;
        }
    }

    /**
     * TODO:需要把process做成非阻塞式的工作方式
     * 逻辑更新在这里完成（绘图工作交给draw）
     * 区分每个阶段的原则是：阶段检测按键的逻辑发生变化后就引入新的阶段来表现
     * 0. 叫牌阶段1：只有下方的显示内容，显示叫牌矩阵和历史叫牌数据？，点击叫牌矩阵进入阶段1
     * 1. 叫牌阶段2：只有下方的显示内容，显示大号的叫牌矩阵，点击无效区域进入阶段1/点击有效区域进入阶段2
     * 2. 叫牌阶段3：只有下方的显示内容，显示大号选中的叫牌矩阵，点击无效区域进入阶段1/点击有效区域进入阶段1
     * 3. 叫牌结束阶段：当连续三名玩家PASS之后，进入叫牌结束阶段
     * 4. 调整座位阶段：叫牌结束阶段之后，调整玩家的座位
     * 5. 按照顺序出牌
     *
     * 要求没有touch的情况下，系统依旧可以工作
     *
     * 与onTouch不同，draw是客户端行为，与玩家无关
     * @param canvas
     */
    @Override
    public void process(Canvas canvas) {
        switch (stage) {
            case 0:

                break;
            case 1:
                break;
            case 2:
            case 3:
            case 4:
                // TODO:修改game的stage可以将游戏进程向前推进
                if (call.isFinish()) {
                    stage = 5;
                } else {
//                    Log.v(this.getClass().getName(), "player:" + String.valueOf(player));
                    switch (player) {
                        case 0:
                            // TODO:怎么获得人类玩家的叫牌值
                            // TODO:怎么在获得机器人玩家的叫牌值得同时，不阻碍界面绘制
                            // TODO:不够抽象？
                            if (playerS.callCard()) {
                                player = 1;
                            }
                            break;
                        case 1:
                            playerW.callCard();
                            player = 2;
                            break;
                        case 2:
                            playerN.callCard();
                            player = 3;
                            break;
                        case 3:
                            playerE.callCard();
                            player = 0;
                            break;
                    }
                }
                break;
            case 5:
                // TODO:坐庄提示
                // TODO:没有逻辑处理
                break;
            case 6:
                // TODO:出牌循环
                // TODO:修改game的stage可以将游戏进程向前推进
                // TODO:要设置table的位置（左，中，右）
                if (table.isFinish()) {
                    // 跳转到结算画面
                    stage = 7;
                } else {
                    // 轮流出牌
                    // 不是leader不能出牌
                    // TODO:leader还是player
                    // player表示玩家
                    // leader表示同样的意思
                    Log.v(this.getClass().getName(), "Player: " + String.valueOf(table.getPlayer()));
                    switch (table.getPlayer()) {
                        case 0:
                            table.setDropStage(0);
                            Log.v(this.getClass().getName(), "南 家 出牌");
//                            Log.v(this.getClass().getName(), String.valueOf(playerS.getCards()));
//                            table.dropCardS(playerS.dropCard());
                            if (playerS.dropCard()) {
                                Log.v(this.getClass().getName(), "南 家 出牌了");
                                player = 1;
                            }
                            break;
                        case 1:
                            Log.v(this.getClass().getName(), "西 家 出牌");
                            playerW.dropCard();
                            break;
                        case 2:
                            Log.v(this.getClass().getName(), "北 家 出牌");
                            playerN.dropCard();
                            break;
                        case 3:
                            Log.v(this.getClass().getName(), "东 家 出牌");
                            playerE.dropCard();
                            break;
                    }
                }
                break;
            case 7:
                break;
            default:
                break;
        }
    }

    /**
     * draw专门负责处理绘制
     * @param canvas
     */
    public void draw(Canvas canvas) {
        Paint paint  = new Paint();
        Rect des = new Rect();

        // drawText(canvas);
//        Log.v(this.getClass().getName(), "stage:" + String.valueOf(stage));

        Date d = new Date();
        initCanvas(canvas, paint);
//        Log.v(this.getClass().getName(), "init    " + String.valueOf((new Date().getTime() - d.getTime())));
        switch(stage) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                playerS.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage2:1  " + String.valueOf((new Date().getTime() - d.getTime())));
                call.setCallStage(0);
                call.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage2:2  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 3:
                playerS.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage3:1  " + String.valueOf((new Date().getTime() - d.getTime())));
                call.setCallStage(1);
                call.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage3:2  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 4:
                playerS.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage4:1  " + String.valueOf((new Date().getTime() - d.getTime())));
                call.setCallStage(2);
                call.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage4:2  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 5:
                // TODO:画点什么好呢？
                // TODO:坐庄
                playerS.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage5:1  " + String.valueOf((new Date().getTime() - d.getTime())));
                call.setCallStage(0);
                call.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage5:2  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 6:
                // TODO:出牌循环
                // 根据叫牌情况选择不同的绘制形态
                table.setModifier(getModifier());
                table.setDealerAndContract(this.call.getDealer(),
                        this.call.getLevel(), this.call.getSuits());
                table.draw(canvas, paint, des);
                localPlayer.draw(canvas, paint, des);
                if (this.call.getDealer() == 0 || this.call.getDealer() == 2) {
                    playerN.draw(canvas, paint, des);
                } else if (this.call.getDealer() == 1) {
                    playerN.setStage(222);
                    playerN.draw(canvas, paint, des);
                    playerE.draw(canvas, paint, des);
                } else if (this.call.getDealer() == 3) {
                    playerN.setStage(222);
                    playerN.draw(canvas, paint, des);
                    playerW.draw(canvas, paint, des);
                }
//                Log.v(this.getClass().getName(), "stage6  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 7:
                break;
            default:
                break;
        }
    }

    /**
     * 初始化画布
     * @param canvas
     */
    public void initCanvas(Canvas canvas, Paint paint) {
        // 绘制底色
//        paint.setColor(Color.WHITE);
        paint.setColor(Color.parseColor("#408030"));
        canvas.drawRect(0, 0, this.width, this.height, paint);

        // 设置缩放
//        if (hasSetScale == 0) {
            canvas.scale( (float)this.width / (float)1440 ,(float)this.width / (float)1440);
//            hasSetScale = 1;
//        } else {
//            hasSetScale = 0;
//        }
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
     * 从庄家获得明手
     * @return
     */
    public int getModifier() {
        if (this.call.getDealer() == 0) {
            return 1;
        } else if (this.call.getDealer() == 1) {
            return 0;
        } else if (this.call.getDealer() == 2) {
            return 1;
        } else if (this.call.getDealer() == 3) {
            return 2;
        }
        return 1;
    }

    /**
     * 初始化宽高
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
        playerPositionRight[0] = 1440;
        playerPositionRight[1] = 720;
        // 下
        playerPositionBottom[0] = 360;
        playerPositionBottom[1] = 1980;
        // 叫牌矩阵
        callPosition[0] = 360;
        callPosition[1] = 360 + 80;
        // 牌桌
        tablePosition[0] = 360;
        tablePosition[1] = 360;

    }

    /**
     * 设置玩家位置
     */
    public void setWidgetPosition() {
        // 牌桌
        playerN.setPosition(playerPositionTop);
        playerW.setPosition(playerPositionLeft);
        playerE.setPosition(playerPositionRight);
        playerS.setPosition(playerPositionBottom);

        // 叫牌矩阵
        call.setPosition(callPosition);

        table.setPosition(tablePosition);
    }

    /**
     * 设置宽高
     */
    public void setWidgetWidthHeight() {
        // 玩家
        playerN.setWidthHeight(this.width, this.height);
        playerW.setWidthHeight(this.width, this.height);
        playerE.setWidthHeight(this.width, this.height);
        playerS.setWidthHeight(this.width, this.height);

        // 叫牌矩阵
        call.setWidthHeight(this.width, this.height);

        // 牌桌
        table.setWidthHeight(this.width, this.height);
    }
}
