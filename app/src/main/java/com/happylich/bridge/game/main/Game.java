package com.happylich.bridge.game.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.res.CardImage;
import com.happylich.bridge.game.scene.Call;
import com.happylich.bridge.game.scene.Table;
import com.happylich.bridge.game.player.AbstractPlayer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lich on 2018/3/25.
 * 叫牌阶段要确定庄家和定约，庄家的下一家是首攻，对家是明手
 */

public class Game extends com.happylich.bridge.engine.game.Game{

    private Context context;

    protected int stage = 2;
    protected int dealer = -1;
    protected AbstractPlayer dealerPlayer = null;

    // 这几个变量用来指示玩家的逻辑顺序
    protected int playerNumber = 0;
    protected int localPlayerNumber = -1;

    protected Timer timer = new Timer();

    // 游戏类型，根据游戏类型作出不同的改变
    protected int gameType = -1;

    // 叫牌
    private Call call;
    // 打牌
    private Table table;

    // 玩家
    // TODO:这四个玩家变量应该跟drawPosition绑定还是direction（当然是drawPosition）
    private AbstractPlayer playerBottom;
    private AbstractPlayer playerLeft;
    private AbstractPlayer playerTop;
    private AbstractPlayer playerRight;

    // 赢墩（界面上显示当前玩家的）
    private int nsContract = -1;
    private int weContract = -1;
    private int nsNow = -1;
    private int weNow = -1;

    //
    private int hasSetLocalPlayerCallCard = 0;
    private int hasSetLeftPlayerCallCard = 0;
    private int hasSetTopPlayerCallCard = 0;
    private int hasSetRightPlayerCallCard = 0;

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
        CardImage.getResource(context);
        this.call = new Call(context);
        this.table = new Table(context);
        // TODO:关键在初始化玩家的时候没有将宽高进行正确的初始化
        // TODO:但是宽高是在setWidthHeight之后才执行的操作
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public void setLocalPlayerNumber(int localPlayerNumber) {
        this.localPlayerNumber = localPlayerNumber;
    }

    /**
     * 设置玩家逻辑座位
     */
    public void setGamePlayer(AbstractPlayer player) {
        player.setTable(this.table);
        player.setCall(this.call);

        if (player.direction == localPlayerNumber) {
            player.drawPosition = 0;
            playerBottom = player;
        } else if (player.direction == localPlayerNumber + 1
                || player.direction == localPlayerNumber - 3) {
            player.drawPosition = 1;
            playerLeft = player;
        } else if (player.direction == localPlayerNumber + 2
                || player.direction == localPlayerNumber - 2) {
            player.drawPosition = 2;
            playerTop = player;
        } else if (player.direction == localPlayerNumber + 3
                || player.direction == localPlayerNumber - 1) {
            player.drawPosition = 3;
            playerRight = player;
        }

        table.setPlayer(player);
        call.setPlayer(player);
    }


    /**
     * 这个类用来在若干秒后给出机器人的叫牌结果
     */
    class localPlayerCallCard extends TimerTask {
        @Override
        public void run() {
            playerBottom.callCard();
        }
    }
    /**
     * 这个类用来在若干秒后给出机器人的叫牌结果
     */
    class leftPlayerCallCard extends TimerTask {
        @Override
        public void run() {
            if (playerLeft.callCard()) {
                playerNumber = 2;
                hasSetLeftPlayerCallCard = 0;
            }
        }
    }
    /**
     * 这个类用来在若干秒后给出机器人的叫牌结果
     */
    class topPlayerCallCard extends TimerTask {
        @Override
        public void run() {
            if (playerTop.callCard()) {
                playerNumber = 3;
                hasSetTopPlayerCallCard = 0;
            }
        }
    }
    /**
     * 这个类用来在若干秒后给出机器人的叫牌结果
     */
    class rightPlayerCallCard extends TimerTask {
        @Override
        public void run() {
            if (playerRight.callCard()) {
                playerNumber = 0;
                hasSetRightPlayerCallCard = 0;
            }
        }
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
                if (playerBottom instanceof Player && playerNumber == playerBottom.drawPosition)
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
                            playerNumber++;
                            break;
                    }
                }
                break;
            case 3:
                // 如果本地玩家是人类玩家 并且 轮到本地玩家叫牌
                if (playerBottom instanceof Player && playerNumber == playerBottom.drawPosition)
                {
                    switch (call.onTouch(x, y)) {
                        case 0:
                            // TODO:这个地方可以有吗？不可以，会引起big的情况跳过玩家的bug
                            // TODO:玩家按PASS后player不会++
//                            playerNumber++;
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
                            playerNumber++;
                            break;
                    }
                }
                break;
            case 4:
                // 如果本地玩家是人类玩家 并且 轮到本地玩家叫牌
                if (playerBottom instanceof Player && playerNumber == playerBottom.drawPosition)
                {
                    switch (call.onTouch(x, y)) {
                        case 0:
                            playerNumber++;
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
                            playerNumber++;
                            break;
                    }
                }
                break;
            case 5:
                // 确定首攻：庄家的下家
                // BUG:之后game:player就再也没变过了
                // 这里要重新设置player的值为首攻玩家
                playerNumber = table.getPlayer();
                stage = 6;
                break;
            case 6:
                // 出牌检测（为什么是player0）
                // 0表示当前阶段——什么都不按
                // 1表示显示选中
                // 2表示出牌
                // 应该有一个标志位表示本地玩家是哪个？

//                Log.v(this.getClass().getName(), "Player: " + String.valueOf(playerNumber));
                // TODO:还需要检测玩家的类型是否是机器人
                // 如果当前玩家时本地玩家——需要检测触摸事件
                // 如果本地玩家是庄家并且当前玩家是本地玩家（庄家）的对家——需要检测触摸事件

                // 如果本地玩家是人类玩家
                //     轮到本地玩家时需要检测触摸事件
                // 如果本地玩家是人类玩家
                //     本地玩家或者对家是庄家
                //     轮到本地玩家时需要检测触摸事件
                if (playerBottom instanceof Player) {
                    // 如果本地玩家是人类玩家
                    dealerPlayer = call.getDealer();
                    if ((playerNumber == playerBottom.drawPosition || (playerNumber == (playerBottom.drawPosition + 2) || playerNumber == (playerBottom.drawPosition - 2) ) &&
                            (playerBottom.drawPosition == dealerPlayer.drawPosition || (playerBottom.drawPosition + 2) == dealerPlayer.drawPosition || (playerBottom.drawPosition - 2) == dealerPlayer.drawPosition))) {
                        // 如果轮到本地玩家
                        // 如果轮到对家 并且 本地玩家或对家是是庄家
                        switch (table.onTouch(x, y)) {
                            case 0:
                                // 触摸无效部分
    //                            stage = 6;
                                break;
                            case 1:
                                // 选中牌的状态
    //                            stage = 6;
                                break;
                            case 2:
                                // 出牌的状态
                                playerNumber++;
//                                stage = 6;
                                break;
                        }
                    }
                }
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
                    if (playerNumber == playerBottom.drawPosition) {
//                        Log.v(this.getClass().getName(), "Bottom叫牌");
                        // TODO:怎么获得人类玩家的叫牌值
                        // TODO:怎么在获得机器人玩家的叫牌值得同时，不阻碍界面绘制
                        // TODO:不够抽象？
                        // 这个逻辑仅仅适用于人机模式
                        // 机器人玩家在（自己是人类玩家的对面 并且 自己或者人类玩家是庄家） 的情况下，需要将出牌和叫牌的权利移交给人类玩家
                        // 具体操作是：
                        //     如果localPlayer是机器人
                        if (playerBottom.callCard()) {
                            playerNumber = playerLeft.drawPosition;
                        }
                    } else if (playerNumber == playerLeft.drawPosition) {
//                        Log.v(this.getClass().getName(), "Left叫牌");
//                            if (hasSetLeftPlayerCallCard == 0) {
//                                timer.schedule(new leftPlayerCallCard(), 3000);
//                                hasSetLeftPlayerCallCard = 1;
//                            }
                        if (playerLeft.callCard()) {
                            playerNumber = playerTop.drawPosition;
                        }
                    } else if (playerNumber == playerTop.drawPosition) {
//                        Log.v(this.getClass().getName(), "Top叫牌");
//                            if (hasSetTopPlayerCallCard == 0) {
//                                timer.schedule(new topPlayerCallCard(), 3000);
//                                hasSetTopPlayerCallCard = 1;
//                            }
                        if (playerTop.callCard()) {
                            playerNumber = playerRight.drawPosition;
                        }
                    } else if (playerNumber == playerRight.drawPosition) {
//                        Log.v(this.getClass().getName(), "Right叫牌");
//                            if (hasSetRightPlayerCallCard == 0) {
//                                timer.schedule(new rightPlayerCallCard(), 3000);
//                                hasSetRightPlayerCallCard = 1;
//                            }
                        if (playerRight.callCard()) {
                            playerNumber = playerBottom.drawPosition;
                        }
                    }
                }
                break;
            case 5:
                // TODO:坐庄提示
                // TODO:没有逻辑处理
                table.setDealerAndContract(this.call.getDealer(),
                        this.call.getLevel(), this.call.getSuits());
                // 同步game-player和table-playerNumber;
//                table.setDirection();
                break;
            case 6:
                // TODO:出牌循环
                // TODO:修改game的stage可以将游戏进程向前推进
                // TODO:要设置table的位置（左，中，右）
                if (table.isFinish()) {
                    stage = 7;
                } else {
                    // 有更新playerNumber的必要
                    playerNumber = table.getPlayer();
                    if (playerNumber == playerBottom.drawPosition) {
                        // TODO: 这里是什么意思
                        table.setDropStage(0);
                        if (playerBottom.dropCard()) {
                            playerNumber = playerLeft.drawPosition;
                        }
                    } else if (playerNumber == playerLeft.drawPosition) {
                        // TODO:为什么这里没有设置player
                        if (playerLeft.dropCard()) {
                            playerNumber = playerTop.drawPosition;
                        }
                    } else if (playerNumber == playerTop.drawPosition) {
                        if (playerTop.dropCard()) {
                            playerNumber = playerRight.drawPosition;
                        }
                    } else if (playerNumber == playerRight.drawPosition) {
                        if (playerRight.dropCard()) {
                            playerNumber = playerBottom.drawPosition;
                        }
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

        initCanvas(canvas, paint);
        switch(stage) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                playerBottom.draw(canvas, paint, des);
                if (playerNumber != playerBottom.drawPosition) {
                    call.setCallStage(11);
                } else {
                    call.setCallStage(10);
                }
                call.draw(canvas, paint, des);
                break;
            case 3:
                playerBottom.draw(canvas, paint, des);
                call.setCallStage(20);
                call.draw(canvas, paint, des);
                break;
            case 4:
                playerBottom.draw(canvas, paint, des);
                call.setCallStage(30);
                call.draw(canvas, paint, des);
                break;
            case 5:
                playerBottom.draw(canvas, paint, des);
                call.setCallStage(11);
                call.draw(canvas, paint, des);
                break;
            case 6:
                // TODO:出牌循环
                // 根据叫牌情况选择不同的绘制形态
                table.setModifier(getModifier());
                table.draw(canvas, paint, des);
                playerBottom.draw(canvas, paint, des);
                if (this.call.getDealer().drawPosition == 0 || this.call.getDealer().drawPosition == 2) {
                    playerTop.draw(canvas, paint, des);
                } else if (this.call.getDealer().drawPosition == 1) {
                    playerTop.setStage(222);
                    playerTop.draw(canvas, paint, des);
                    playerRight.draw(canvas, paint, des);
                } else if (this.call.getDealer().drawPosition == 3) {
                    playerTop.setStage(222);
                    playerTop.draw(canvas, paint, des);
                    playerLeft.draw(canvas, paint, des);
                }
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
        paint.setColor(Color.parseColor("#408030"));
        canvas.drawRect(0, 0, this.width, this.height, paint);

        canvas.scale( (float)this.width / (float)1440 ,(float)this.width / (float)1440);

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
        if (this.call.getDealer().drawPosition == 0) {
            return 1;
        } else if (this.call.getDealer().drawPosition == 1) {
            return 0;
        } else if (this.call.getDealer().drawPosition == 2) {
            return 1;
        } else if (this.call.getDealer().drawPosition == 3) {
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
        playerTop.setPosition(playerPositionTop);
        playerLeft.setPosition(playerPositionLeft);
        playerRight.setPosition(playerPositionRight);
        playerBottom.setPosition(playerPositionBottom);

        // 叫牌矩阵
        call.setPosition(callPosition);

        table.setPosition(tablePosition);
    }

    /**
     * 设置宽高
     */
    public void setWidgetWidthHeight() {
        // 玩家
        playerTop.setWidthHeight(this.width, this.height);
        playerLeft.setWidthHeight(this.width, this.height);
        playerRight.setWidthHeight(this.width, this.height);
        playerBottom.setWidthHeight(this.width, this.height);

        // 叫牌矩阵
        call.setWidthHeight(this.width, this.height);

        // 牌桌
        table.setWidthHeight(this.width, this.height);
    }
}
