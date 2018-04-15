package com.happylich.bridge.game.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.happylich.bridge.game.player.AbstractPlayer;

/**
 * Created by lich on 2018/3/25.
 * 叫牌阶段要确定庄家和定约，庄家的下一家是首攻，对家是明手
 */

public class Game extends com.happylich.bridge.engine.game.Game{

    private Context context;

    // stage和player一起推动游戏进程向前进行
    protected int stage = 0;
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
                if (player == 0)
                {
                    switch (call.onTouch(x, y)) {
                        case 0:
                            stage = 0;
                            break;
                        case 1:
                            stage = 1;
                            break;
                        case 2:
                            stage = 2;
                            break;
                        case 3:
                            // 为了解决之前的bug
                            stage = 0;
                            player++;
                            break;
                    }
                }
                break;
            case 1:
                if (player == 0) {
                    switch (call.onTouch(x, y)) {
                        case 0:
                            // TODO:这个地方可以有吗？不可以，会引起big的情况跳过玩家的bug
                            // TODO:玩家按PASS后player不会++
//                            player++;
                            stage = 0;
                            break;
                        case 1:
                            stage = 1;
                            break;
                        case 2:
                            stage = 2;
                            break;
                        case 3:
                            // 为了解决之前的bug
                            stage = 0;
                            player++;
                            break;
                    }
                }
                break;
            case 2:
                if (player == 0) {
                    switch (call.onTouch(x, y)) {
                        case 0:
                            player++;
                            stage = 0;
                            break;
                        case 1:
                            stage = 1;
                            break;
                        case 2:
                            stage = 2;
                            break;
                        case 3:
                            // 为了解决之前的bug
                            stage = 0;
                            player++;
                            break;
                    }
                }
                break;
            case 3:
                stage = 4;
                break;
            case 4:
                break;
            default:
                break;
        }
    }

    /**
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
        // TODO:刷帧的函数应该放在哪儿？
        draw(canvas);
        switch (stage) {
            case 0:
            case 1:
            case 2:
                // TODO:修改game的stage可以将游戏进程向前推进
                if (call.isFinish()) {
                    stage = 3;
                } else {
//                    Log.v(this.getClass().getName(), "player:" + String.valueOf(player));
                    switch (player) {
                        case 0:
                            // TODO:怎么获得人类玩家的叫牌值
                            // TODO:怎么在获得机器人玩家的叫牌值得同时，不阻碍界面绘制
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
            case 3:
                // TODO:坐庄提示
                // TODO:没有逻辑处理
                break;
            case 4:
                // TODO:出牌循环
                // TODO:修改game的stage可以将游戏进程向前推进
                // TODO:要设置table的位置（左，中，右）
                if (table.isFinish()) {
                    // 跳转到结算画面
                    stage = 5;
                } else {
                    // 轮流出牌
//                    table.setModifier(1);
                    switch (player) {
                        case 0:
                            table.dropCardS(playerS.dropCard());
                            player = 1;
                            break;
                        case 1:
                            table.dropCardW(playerW.dropCard());
                            player = 2;
                            break;
                        case 2:
                            table.dropCardN(playerN.dropCard());
                            player = 3;
                            break;
                        case 3:
                            table.dropCardE(playerE.dropCard());
                            player = 0;
                            break;
                    }
                }
                break;
            default:
                break;
        }
        draw(canvas);
    }

    /**
     * draw专门负责处理绘制
     * @param canvas
     */
    public void draw(Canvas canvas) {
        // drawText(canvas);
        Log.v(this.getClass().getName(), "stage:" + String.valueOf(stage));
        initCanvas(canvas);
        switch(stage) {
            case 0:
                playerS.draw(canvas);
                call.setCallStage(0);
                call.draw(canvas);
                break;
            case 1:
                playerS.draw(canvas);
                call.setCallStage(1);
                call.draw(canvas);
                break;
            case 2:
                playerS.draw(canvas);
                call.setCallStage(2);
                call.draw(canvas);
                break;
            case 3:
                // TODO:画点什么好呢？
                // TODO:坐庄
                playerS.draw(canvas);
                call.setCallStage(0);
                call.draw(canvas);
                break;
            case 4:
                // TODO:出牌循环
                // 根据叫牌情况选择不同的绘制形态
                playerS.draw(canvas);
                playerW.draw(canvas);
                playerN.draw(canvas);
                playerE.draw(canvas);
                table.draw(canvas);
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
//        paint.setColor(Color.WHITE);
        paint.setColor(Color.parseColor("#408030"));
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
