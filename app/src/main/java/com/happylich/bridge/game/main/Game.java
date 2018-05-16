package com.happylich.bridge.game.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.game.player.AbstractPlayerWithDraw;
import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.player.ProxyPlayer;
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

    // 游戏类型标志
    protected int gameType = -1;
    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    // 游戏进程标志
    // 0 玩家未就绪
    // 1 玩家已经就绪
    // 2 叫牌1
    // 3 叫牌2
    // 4 叫牌3
    // 5 过渡
    // 6 打牌
    // 7
    protected int gameStage = 1;
    public void setGameStage(int gameStage) {
        this.gameStage = gameStage;
    }
    public int getGameStage() {
        return gameStage;
    }

    // 本地玩家标号
    protected int playerNumber = 0;
    protected int localPlayerNumber = -1;
    public void setLocalPlayerNumber(int localPlayerNumber) {
        this.localPlayerNumber = localPlayerNumber;
    }

    // 记录庄家
    protected AbstractPlayer dealerPlayer = null;



    protected Timer timer = new Timer();


    // 准备
    private Ready ready;
    // 叫牌
    private Call call;
    // 打牌
    private Table table;

    // 玩家
    // TODO:这四个玩家变量应该跟drawPosition绑定还是direction（当然是drawPosition）
    private AbstractPlayerWithDraw playerBottom;
    private AbstractPlayerWithDraw playerLeft;
    private AbstractPlayerWithDraw playerTop;
    private AbstractPlayerWithDraw playerRight;

    // 赢墩（界面上显示当前玩家的）
//    private int nsContract = -1;
//    private int weContract = -1;
//    private int nsNow = -1;
//    private int weNow = -1;


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
        this.ready = new Ready(context);
        this.call = new Call(context);
        this.table = new Table(context);

        this.ready.setGame(this);
        this.call.setGame(this);
        this.table.setGame(this);
    }

    /**
     * 设置玩家逻辑座位
     * @param player
     */
    public void setGamePlayer(AbstractPlayerWithDraw player) {
        player.setReady(this.ready);
        player.setTable(this.table);
        player.setCall(this.call);

        if (player.direction == localPlayerNumber) {
            player.position = 0;
            playerBottom = player;
        } else if (player.direction == localPlayerNumber + 1
                || player.direction == localPlayerNumber - 3) {
            player.position = 1;
            playerLeft = player;
        } else if (player.direction == localPlayerNumber + 2
                || player.direction == localPlayerNumber - 2) {
            player.position = 2;
            playerTop = player;
        } else if (player.direction == localPlayerNumber + 3
                || player.direction == localPlayerNumber - 1) {
            player.position = 3;
            playerRight = player;
        }

        if (player.position == 0) {
            player.setPlayerStage(1);
        } else {
            player.setPlayerStage(2);
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
        switch (gameStage) {
            case 2:                // 在Process里自动切换状态
                // 已准备界面
                // 触摸取消准备按钮，将playerBottom的准备状态切换为未就绪
                ready.onTouch(x, y);
                break;
            case 3:
                break;
            case 4:
                // 如果本地玩家是人类玩家 并且 轮到本地玩家叫牌
                switch (call.onTouch(x, y)) {
                    case 0:
                        if (playerBottom instanceof Player && playerNumber == playerBottom.position) {
                            call.setCallStage(0);
                        } else {
                            call.setCallStage(1);
                        }
                        Log.v(this.getClass().getName(), "进入Small");
                        break;
                    case 1:
                        call.setCallStage(1);
                        break;
                    case 2:
                        call.setCallStage(2);
                        Log.v(this.getClass().getName(), "进入Big");
                        break;
                    case 3:
                        call.setCallStage(3);
                        Log.v(this.getClass().getName(), "进入Selected");
                        break;
                    case 4:
                        call.setCallStage(1);
                        playerNumber++;
                        break;
                }
                break;
            case 5:
                dealerPlayer = call.getDealer();
                playerNumber = table.getPlayer();
                gameStage = 6;
                break;
            case 6:
                // 本地玩家是人类
                if (playerBottom instanceof Player) {
                    if (playerNumber == playerBottom.position) {
                        switch (table.onTouch(x, y)) {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                playerNumber++;
                                break;
                        }
                    } else if(playerNumber == playerTop.position &&
                            playerTop instanceof ProxyPlayer &&
                            ((ProxyPlayer) playerTop).getRealPlayer() instanceof Robot){
                        if ((playerBottom.position == dealerPlayer.position || playerTop.position == dealerPlayer.position)) {
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
        switch (gameStage) {
            case 2:
                if (ready.isFinish()) {
                    gameStage = 4;
                }
                break;
            case 3:
                // 表示本机已经准备好
                // 检测所有玩家的就绪状态
                // 如果所有玩家都已经就绪，则进入下一个阶段
                // 要在这里帮忙建立人机
                ((ProxyPlayer) playerTop).setRealPlayer(new Robot(context));
                ((ProxyPlayer) playerLeft).setRealPlayer(new Robot(context));
                ((ProxyPlayer) playerRight).setRealPlayer(new Robot(context));
                if (ready.isFinish()) {
                    gameStage = 4;
                }
                break;
            case 4:
                // TODO:修改game的stage可以将游戏进程向前推进
                if (call.isFinish()) {
                    gameStage = 5;
                } else {
                    if (playerNumber == playerBottom.position) {
                        if (playerBottom instanceof Player && call.getCallStage() == 1) {
                            call.setCallStage(0);
                        }
                        if (playerBottom.callCard()) {
                            playerNumber = playerLeft.position;
                        }
                    } else if (playerNumber == playerLeft.position) {
                        if (playerLeft.callCard()) {
                            playerNumber = playerTop.position;
                        }
                    } else if (playerNumber == playerTop.position) {
                        if (playerTop.callCard()) {
                            playerNumber = playerRight.position;
                        }
                    } else if (playerNumber == playerRight.position) {
//                        Log.v(this.getClass().getName(), "Right叫牌");
//                            if (hasSetRightPlayerCallCard == 0) {
//                                timer.schedule(new rightPlayerCallCard(), 3000);
//                                hasSetRightPlayerCallCard = 1;
//                            }
                        if (playerRight.callCard()) {
                            playerNumber = playerBottom.position;
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
                    gameStage = 7;
                } else {
                    // 有更新playerNumber的必要
                    playerNumber = table.getPlayer();

                    // 设置明手
                    if (playerNumber == dealerPlayer.position + 2 || playerNumber == dealerPlayer.position - 2) {
                        // 如果出牌的是明手（庄家的对面）
                        if (playerNumber == 0) {
                            playerBottom.setPlayerStage(1);
                        } else if (playerNumber == 1) {
                            playerLeft.setPlayerStage(1);
                        } else if (playerNumber == 2) {
                            playerTop.setPlayerStage(1);
                        } else if (playerNumber == 3) {
                            playerRight.setPlayerStage(1);
                        }
                    }

                    if (playerNumber == playerBottom.position) {
                        // TODO: 这里是什么意思——监听下方玩家
                        table.setDropStage(0);
                        if (playerBottom.dropCard()) {
                            playerNumber = playerLeft.position;
                        }
                    } else if (playerNumber == playerLeft.position) {
                        // TODO:为什么这里没有设置player
                        if (playerLeft.dropCard()) {
                            playerNumber = playerTop.position;
                        }
                    } else if (playerNumber == playerTop.position) {
                        table.setDropStage(1);
                        if (playerTop instanceof ProxyPlayer &&
                                ((ProxyPlayer) playerTop).getRealPlayer() instanceof Robot &&
                                playerBottom instanceof Player) {
                            // 将出牌权交给触摸事件
                            // 条件是，top是庄家或者明手
                            if (playerTop.position == dealerPlayer.position ||
                                    playerTop.position == (dealerPlayer.position + 2) || playerTop.position == (dealerPlayer.position - 2)) {
                            } else {
                                // 机器人出牌
                                if (playerTop.dropCard()) {
                                    playerNumber = playerRight.position;
                                }
                            }
                        } else {
                            if (playerTop.dropCard()) {
                                playerNumber = playerRight.position;
                            }
                        }
                    } else if (playerNumber == playerRight.position) {
                        if (playerRight.dropCard()) {
                            playerNumber = playerBottom.position;
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
        switch(gameStage) {
            case 2:
                // 绘制已经准备界面
                ready.draw(canvas, paint, des);
                break;
            case 3:
                // 过渡界面
                break;
            case 4:
                playerBottom.draw(canvas, paint, des);
                call.draw(canvas, paint, des);
                break;
            case 5:
                // 过渡阶段，可以什么都不画的
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
                if (this.call.getDealer().position == 0 || this.call.getDealer().position == 2) {
                    playerTop.setPlayerStage(1);
                    playerTop.draw(canvas, paint, des);
                } else if (this.call.getDealer().position == 1) {
                    playerTop.setPlayerStage(2);
                    playerTop.draw(canvas, paint, des);
                    playerRight.draw(canvas, paint, des);
                } else if (this.call.getDealer().position == 3) {
                    playerTop.setPlayerStage(2);
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
        if (this.call.getDealer().position == 0) {
            return 1;
        } else if (this.call.getDealer().position == 1) {
            return 0;
        } else if (this.call.getDealer().position == 2) {
            return 1;
        } else if (this.call.getDealer().position == 3) {
            return 2;
        }
        return 1;
    }

    /**
     * 初始化宽高（这个函数中涉及到玩家的宽高设置，但是在真正的玩家确定之前，宽高是无法确定的
     * 可以这样，先设置所有的玩家（可以除了本地玩家）都是ProxyPlayer，ProxyPlayer可以绘制，但无法确定进行出牌等
     * 抽象操作，需要借助真正的机器人玩家或者远程玩家完成出牌操作
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
        if (playerTop != null) {
            playerTop.setPosition(playerPositionTop);
        }
        if (playerLeft != null) {
            playerLeft.setPosition(playerPositionLeft);
        }
        if (playerRight != null) {
            playerRight.setPosition(playerPositionRight);
        }
        if (playerBottom != null) {
            playerBottom.setPosition(playerPositionBottom);
        }

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
