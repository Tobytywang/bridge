package com.happylich.bridge.game.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.player.Robot;
import com.happylich.bridge.game.res.CardImage;
import com.happylich.bridge.game.scene.Call;
import com.happylich.bridge.game.scene.Table;
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
    // 玩家的position是系统间同步的
    // 本地的game维持了一个localPlayerNumber
    // 以这个字段为基准，确定了position和本地绘制顺序的关系
    // position是绘制无关的
    // localPlayerNumber是绘制相关的
    // player也是与绘制有关的，相对于localPlayerNumber进行位移
    protected int player = 0;
    protected int dealer = -1;
    private int localPlayerNumber;

    protected int gameType = -1;

    // 叫牌
    private Call call;
    // 打牌
    private Table table;

    // 玩家
    private AbstractPlayer topPlayer;
    private AbstractPlayer leftPlayer;
    private AbstractPlayer rightPlayer;
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

    private int hasSetScale = 0;

    /**
     * 构造函数
     */
    public Game(Context context) {
        this.context = context;
        CardImage.getResource(context);
        this.call = new Call(context);
        this.table = new Table(context);
        // TODO:关键在初始化玩家的时候没有将宽高进行正确的初始化
        // TODO:但是宽高是在setWidthHeight之后才执行的操作
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public void setLocalPlayerNumber(int number) {
        localPlayerNumber = number;
    }

    /**
     * 设置本地（下方）玩家
     * @param player
     */
    public void setLocalPlayer(AbstractPlayer player) {
        localPlayer = player;
        localPlayer.setCall(this.call);
        localPlayer.setTable(this.table);
        table.setPlayerBottom(localPlayer);
    }

    /**
     * 设置上方玩家
     * @param player
     */
    public void setTopPlayer(AbstractPlayer player) {
        topPlayer = player;
        topPlayer.setCall(this.call);
        topPlayer.setTable(this.table);
        table.setPlayerTop(topPlayer);
    }

    /**
     * 设置左边玩家
     * @param player
     */
    public void setLeftPlayer(AbstractPlayer player) {
        leftPlayer = player;
        leftPlayer.setCall(this.call);
        leftPlayer.setTable(this.table);
        table.setPlayerLeft(player);
    }

    /**
     * 设置右边玩家
     * @param player
     */
    public void setRightPlayer(AbstractPlayer player) {
        rightPlayer = player;
        rightPlayer.setCall(this.call);
        rightPlayer.setTable(this.table);
        table.setPlayerRight(player);
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
                // 如果本地玩家是人类玩家 并且 轮到本地玩家叫牌
                if (localPlayer instanceof Player && player == localPlayerNumber)
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
                // 如果本地玩家是人类玩家 并且 轮到本地玩家叫牌
                if (localPlayer instanceof Player && player == localPlayerNumber)
                {
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
                // 如果本地玩家是人类玩家 并且 轮到本地玩家叫牌
                if (localPlayer instanceof Player && player == localPlayerNumber)
                {
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
                // BUG:之后game:player就再也没变过了
                // 这里要重新设置player的值为首攻玩家
                player = table.getPlayer();
                stage = 6;
                break;
            case 6:
                // 出牌检测（为什么是player0）
                // 0表示当前阶段——什么都不按
                // 1表示显示选中
                // 2表示出牌
                // 应该有一个标志位表示本地玩家是哪个？

//                Log.v(this.getClass().getName(), "Player: " + String.valueOf(player));
                // TODO:还需要检测玩家的类型是否是机器人
                // 如果当前玩家时本地玩家——需要检测触摸事件
                // 如果本地玩家是庄家并且当前玩家是本地玩家（庄家）的对家——需要检测触摸事件

                // 如果本地玩家是人类玩家
                //     轮到本地玩家时需要检测触摸事件
                // 如果本地玩家是人类玩家
                //     本地玩家或者对家是庄家
                //     轮到本地玩家时需要检测触摸事件
                if (localPlayer instanceof Player) {
                    // 如果本地玩家是人类玩家
                    dealer = call.getDealer();
                    if ( ( player == localPlayerNumber || (player == (localPlayerNumber + 2) || player == (localPlayerNumber - 2) ) &&
                            ( localPlayerNumber == dealer || (localPlayerNumber + 2) == dealer || (localPlayerNumber - 2) == dealer) ) ) {
                        // 如果轮到本地玩家
                        // 如果轮到对家 并且 本地玩家或对家是是庄家
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
                    // 出牌是与绘制无关的，但也要用player
                    switch (player) {
                        case 0:
                            // TODO:怎么获得人类玩家的叫牌值
                            // TODO:怎么在获得机器人玩家的叫牌值得同时，不阻碍界面绘制
                            // TODO:不够抽象？
                            // 这个逻辑仅仅适用于人机模式
                            // 机器人玩家在（自己是人类玩家的对面 并且 自己或者人类玩家是庄家） 的情况下，需要将出牌和叫牌的权利移交给人类玩家
                            // 具体操作是：
                            //     如果localPlayer是机器人
                            if (localPlayer.callCard()) {
                                player = 1;
                            }
                            break;
                        case 1:
                            if (leftPlayer.callCard()) {
                                player = 2;
                            }
                            break;
                        case 2:
                            if (topPlayer.callCard()) {
                                player = 3;
                            }
                            break;
                        case 3:
                            if (rightPlayer.callCard()) {
                                player = 0;
                            }
                            break;
                    }
                }
                break;
            case 5:
                // TODO:坐庄提示
                // TODO:没有逻辑处理
                table.setDealerAndContract(this.call.getDealer(),
                        this.call.getLevel(), this.call.getSuits());
                // 同步game-player和table-player;
//                table.setPlayer();
                break;
            case 6:
                // TODO:出牌循环
                // TODO:修改game的stage可以将游戏进程向前推进
                // TODO:要设置table的位置（左，中，右）
                player = table.getPlayer();
                if (table.isFinish()) {
                    // 跳转到结算画面
                    stage = 7;
                } else {
                    // 轮流出牌
                    // 不是leader不能出牌
                    // TODO:leader还是player
                    // player表示玩家
                    // leader表示同样的意思
//                    Log.v(this.getClass().getName(), "Player: " + String.valueOf(table.getPlayer()));
                    switch (table.getPlayer()) {
                        case 0:
                            table.setDropStage(0);
//                            Log.v(this.getClass().getName(), "南 家 出牌");
//                            Log.v(this.getClass().getName(), String.valueOf(playerS.getCards()));
//                            table.dropCardS(playerS.dropCard());
                            if (localPlayer.dropCard()) {
//                                Log.v(this.getClass().getName(), "南 家 出牌了");
                                player = 1;
                            }
                            break;
                        case 1:
//                            Log.v(this.getClass().getName(), "西 家 出牌");
                            leftPlayer.dropCard();
                            break;
                        case 2:
//                            Log.v(this.getClass().getName(), "北 家 出牌");
                            topPlayer.dropCard();
                            break;
                        case 3:
//                            Log.v(this.getClass().getName(), "东 家 出牌");
                            rightPlayer.dropCard();
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

//        Date d = new Date();
        initCanvas(canvas, paint);
//        Log.v(this.getClass().getName(), "init    " + String.valueOf((new Date().getTime() - d.getTime())));
        switch(stage) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                localPlayer.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage2:1  " + String.valueOf((new Date().getTime() - d.getTime())));
                call.setCallStage(0);
                call.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage2:2  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 3:
                localPlayer.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage3:1  " + String.valueOf((new Date().getTime() - d.getTime())));
                call.setCallStage(1);
                call.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage3:2  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 4:
                localPlayer.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage4:1  " + String.valueOf((new Date().getTime() - d.getTime())));
                call.setCallStage(2);
                call.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage4:2  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 5:
                // TODO:画点什么好呢？
                // TODO:坐庄
                localPlayer.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage5:1  " + String.valueOf((new Date().getTime() - d.getTime())));
                call.setCallStage(0);
                call.draw(canvas, paint, des);
//                Log.v(this.getClass().getName(), "stage5:2  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 6:
                // TODO:出牌循环
                // 根据叫牌情况选择不同的绘制形态
                table.setModifier(getModifier());
                table.draw(canvas, paint, des);
                localPlayer.draw(canvas, paint, des);
                if (this.call.getDealer() == 0 || this.call.getDealer() == 2) {
                    topPlayer.draw(canvas, paint, des);
//                    Log.v(this.getClass().getName(), "stage:61     " + String.valueOf((new Date().getTime() - d.getTime())));

                } else if (this.call.getDealer() == 1) {
                    topPlayer.setStage(222);
//                    Log.v(this.getClass().getName(), "stage:62 1    " + String.valueOf((new Date().getTime() - d.getTime())));
                    topPlayer.draw(canvas, paint, des);
//                    Log.v(this.getClass().getName(), "stage:62 2    " + String.valueOf((new Date().getTime() - d.getTime())));
                    rightPlayer.draw(canvas, paint, des);
//                    Log.v(this.getClass().getName(), "stage:62 3    " + String.valueOf((new Date().getTime() - d.getTime())));

                } else if (this.call.getDealer() == 3) {
                    topPlayer.setStage(222);
//                    Log.v(this.getClass().getName(), "stage:63 1    " + String.valueOf((new Date().getTime() - d.getTime())));
                    topPlayer.draw(canvas, paint, des);
//                    Log.v(this.getClass().getName(), "stage:63 2    " + String.valueOf((new Date().getTime() - d.getTime())));
                    leftPlayer.draw(canvas, paint, des);
//                    Log.v(this.getClass().getName(), "stage:63 3    " + String.valueOf((new Date().getTime() - d.getTime())));

                }
//                Log.v(this.getClass().getName(), "stage6  " + String.valueOf((new Date().getTime() - d.getTime())));
                break;
            case 7:
                break;
            default:
                break;
        }
//        Log.v(this.getClass().getName(), "end     " + String.valueOf((new Date().getTime() - d.getTime())));
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
        topPlayer.setPosition(playerPositionTop);
        leftPlayer.setPosition(playerPositionLeft);
        rightPlayer.setPosition(playerPositionRight);
        localPlayer.setPosition(playerPositionBottom);

        // 叫牌矩阵
        call.setPosition(callPosition);

        table.setPosition(tablePosition);
    }

    /**
     * 设置宽高
     */
    public void setWidgetWidthHeight() {
        // 玩家
        topPlayer.setWidthHeight(this.width, this.height);
        leftPlayer.setWidthHeight(this.width, this.height);
        rightPlayer.setWidthHeight(this.width, this.height);
        localPlayer.setWidthHeight(this.width, this.height);

        // 叫牌矩阵
        call.setWidthHeight(this.width, this.height);

        // 牌桌
        table.setWidthHeight(this.width, this.height);
    }
}
