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
import com.happylich.bridge.game.scene.Ready;
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

    protected int stage = 1;
    protected int dealer = -1;
    protected AbstractPlayer dealerPlayer = null;

    // 这几个变量用来指示玩家的逻辑顺序
    protected int playerNumber = 0;
    protected int localPlayerNumber = -1;

    protected Timer timer = new Timer();

    // 游戏类型，根据游戏类型作出不同的改变
    protected int gameType = -1;

    // 准备
    private Ready ready;
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
    private int[] readyPosition = new int[2];
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
        this.ready = new Ready(context);
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
        player.setReady(this.ready);

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

        if (player.drawPosition == 0) {
            player.setStage(1);
        } else {
            player.setStage(2);
        }

        table.setPlayer(player);
        call.setPlayer(player);
        ready.setPlayer(player);
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
            case 1:
                // 在Process里自动切换状态
                // 已准备界面
                // 触摸取消准备按钮，将playerBottom的准备状态切换为未就绪
                ready.onTouch(x, y);
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
                            stage = 2;
                            break;
                        case 1:
                            stage = 3;
                            break;
                        case 2:
                            stage = 4;
                            break;
                        case 3:
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
                            stage = 2;
                            playerNumber++;
                            break;
                    }
                }
                break;
            case 5:
                dealerPlayer = call.getDealer();
                playerNumber = table.getPlayer();
                stage = 6;
                break;
            case 6:
                // 本地玩家是人类
                if (playerBottom instanceof Player) {
                    Log.v(this.getClass().getName(), "本地玩家是人类玩家");
                    if (playerNumber == playerBottom.drawPosition) {
                        Log.v(this.getClass().getName(), "轮到本地玩家出牌");
                        switch (table.onTouch(x, y)) {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                playerNumber++;
                                break;
                        }
                    } else if(playerNumber == playerTop.drawPosition && playerTop instanceof Robot){
                        Log.v(this.getClass().getName(), "轮到本地玩家对面出牌");
                        if ((playerBottom.drawPosition == dealerPlayer.drawPosition || playerTop.drawPosition == dealerPlayer.drawPosition)) {
                            Log.v(this.getClass().getName(), "刚好本地玩家或者对面玩家是庄家，就可以让人类玩家代替出牌了");
                            switch (table.onTouch(x, y)) {
                                case 0:
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    playerNumber++;
                                    break;
                            }
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
            case 1:
                // 表示本机已经准备好
                // 检测所有玩家的就绪状态
                // 如果所有玩家都已经就绪，则进入下一个阶段
                if (ready.isFinish()) {
                    stage = 2;
                }
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

                    // 设置明手
                    if (playerNumber == dealerPlayer.drawPosition + 2 || playerNumber == dealerPlayer.drawPosition - 2) {
                        // 如果出牌的是明手（庄家的对面）
                        if (playerNumber == 0) {
                            playerBottom.setStage(1);
                        } else if (playerNumber == 1) {
                            playerLeft.setStage(1);
                        } else if (playerNumber == 2) {
                            playerTop.setStage(1);
                        } else if (playerNumber == 3) {
                            playerRight.setStage(1);
                        }
                    }

                    if (playerNumber == playerBottom.drawPosition) {
                        // TODO: 这里是什么意思——监听下方玩家
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
                        table.setDropStage(1);
                        if (playerTop instanceof Robot && playerBottom instanceof Player) {
                            // 将出牌权交给触摸事件
                            // 条件是，top是庄家或者明手
                            if (playerTop.drawPosition == dealerPlayer.drawPosition ||
                                    playerTop.drawPosition == (dealerPlayer.drawPosition + 2) || playerTop.drawPosition == (dealerPlayer.drawPosition - 2)) {
//                                Log.v(this.getClass().getName(), "什么也不做，等待触摸事件");
                                // dropCard还要承担设置明手的作用（setDrop调用
                                // 设置明手的工作应该在出牌之前

                            } else {
                                // 机器人出牌
                                if (playerTop.dropCard()) {
                                    playerNumber = playerRight.drawPosition;
                                }
                            }
                        } else {
                            if (playerTop.dropCard()) {
                                playerNumber = playerRight.drawPosition;
                            }
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
            case 1:
                // 绘制已经准备界面
                ready.draw(canvas, paint, des);
                break;
            case 2:
                playerBottom.draw(canvas, paint, des);
                // 当下面的玩家是人类玩家并且轮到这个玩家时，点亮叫牌区域
                if (playerNumber == playerBottom.drawPosition && playerBottom instanceof Player) {
                    call.setCallStage(10);
                } else {
                    call.setCallStage(11);
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

        readyPosition[0] = 0;
        readyPosition[1] = 360;

        callPosition[0] = 360;
        callPosition[1] = 360 + 80;

        tablePosition[0] = 360;
        tablePosition[1] = 360;

    }

    /**
     * 设置玩家位置
     */
    public void setWidgetPosition() {
        playerTop.setPosition(playerPositionTop);
        playerLeft.setPosition(playerPositionLeft);
        playerRight.setPosition(playerPositionRight);
        playerBottom.setPosition(playerPositionBottom);

        ready.setPosition(readyPosition);
        call.setPosition(callPosition);
        table.setPosition(tablePosition);
    }

    /**
     * 设置宽高
     */
    public void setWidgetWidthHeight() {
        playerTop.setWidthHeight(this.width, this.height);
        playerLeft.setWidthHeight(this.width, this.height);
        playerRight.setWidthHeight(this.width, this.height);
        playerBottom.setWidthHeight(this.width, this.height);

        ready.setWidthHeight(this.width, this.height);
        call.setWidthHeight(this.width, this.height);
        table.setWidthHeight(this.width, this.height);
    }
}
