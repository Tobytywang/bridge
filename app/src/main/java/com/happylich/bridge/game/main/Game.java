package com.happylich.bridge.game.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.happylich.bridge.game.player.AbstractPlayerWithDraw;
import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.player.ProxyPlayer;
import com.happylich.bridge.game.player.RemotePlayer;
import com.happylich.bridge.game.player.Robot;
import com.happylich.bridge.game.res.CardImage;
import com.happylich.bridge.game.scene.Call;
import com.happylich.bridge.game.scene.Count;
import com.happylich.bridge.game.wlan.wifihotspot.transmitdata.GameClientReceiveDataThread;
import com.happylich.bridge.game.wlan.wifihotspot.transmitdata.GameServerReceiveDataThread;
import com.happylich.bridge.game.wlan.wifihotspot.transmitdata.GameTransmitData;
import com.happylich.bridge.game.wlan.wifihotspot.validconnection.FakeSocket;
import com.happylich.bridge.game.wlan.wifihotspot.validconnection.GameClient;
import com.happylich.bridge.game.wlan.wifihotspot.validconnection.GameServer;
import com.happylich.bridge.game.scene.Ready;
import com.happylich.bridge.game.scene.Table;
import com.happylich.bridge.game.player.AbstractPlayer;

import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lich on 2018/3/25.
 * 叫牌阶段要确定庄家和定约，庄家的下一家是首攻，对家是明手
 */

public class Game extends com.happylich.bridge.engine.game.Game{

    public final Context context;
    public Handler mHandler;

    public ArrayList<Socket> sockets = new ArrayList<>();
    public Socket socket;


    // 游戏状态标志
    // 1. 等待玩家加入中
    // 2. 玩家已满
    // 3. 游戏进行中
    protected int gameState = 0;
    public void setGameState(int gameStage) {
        this.gameStage = gameState;
    }
    public int getGameState() {
        return this.gameState;
    }

    // 游戏类型标志
    protected int gameType = -1;
    public void setGameType(int gameType) {
        this.gameType = gameType;
        this.count.setGameType(gameType);
    }
    public int getGameType() {
        return this.gameType;
    }

    // 游戏进程标志
    // 0 玩家未就绪
    // 1 玩家已经就绪
    // 2 准备阶段
    // 3 准备阶段
    // 4 发牌阶段
    // 5 叫牌阶段
    // 6 叫牌过渡阶段
    // 7 打牌阶段
    // 8 打牌过渡阶段
    // 9 结算阶段
    protected int gameStage = 1;
    public void setGameStage(int gameStage) {
        this.gameStage = gameStage;
    }
    public int getGameStage() {
        return gameStage;
    }

    // 本地玩家标号
    protected int playerNumber = 0;
    protected int localPlayerDirection = -1;
    public void setLocalPlayer(AbstractPlayerWithDraw localPlayer) {
        this.playerLocal = localPlayer;
    }
    public void setLocalPlayerDirection(int localPlayerDirection) {
        // TODO:还要做什么额外的工作？
        this.localPlayerDirection = localPlayerDirection;
        this.count.setPlayerDirection(localPlayerDirection);
    }

    // 记录庄家
    protected AbstractPlayer dealerPlayer = null;

    protected Timer timer = new Timer();


    private String serverIP;
    private GameServer gameServer;
    private GameServerReceiveDataThread gameServerReceiveDataThread;
    private GameClient gameClient;
    private GameClientReceiveDataThread gameClientReceiveDataThread;
    public GameTransmitData gameTransmitData;

    protected Count count;
    // 准备
    protected Ready ready;
    // 叫牌
    protected Call call;
    // 打牌
    protected Table table;

    // 玩家
    // TODO:这四个玩家变量应该跟drawPosition绑定还是direction（当然是drawPosition）
    private AbstractPlayerWithDraw playerLocal;
    private AbstractPlayerWithDraw playerBottom;
    private AbstractPlayerWithDraw playerLeft;
    private AbstractPlayerWithDraw playerTop;
    private AbstractPlayerWithDraw playerRight;

    public AbstractPlayer getPlayerTop() {
        return this.playerTop;
    }
    public AbstractPlayer getPlayerLeft() {
        return this.playerLeft;
    }
    public AbstractPlayer getPlayerRight() {
        return this.playerRight;
    }
    public AbstractPlayer getPlayerBottom() {
        return this.playerBottom;
    }
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

        this.count = new Count(context);
        this.ready = new Ready(context);
        this.call = new Call(context);
        this.table = new Table(context);

        this.count.setGame(this);
        this.ready.setGame(this);
        this.call.setGame(this);
        this.table.setGame(this);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        // 表示消费事件（客户端接收事件后）
                        // 表示服务端向客户端发送的需要客户端回复的消息
                        toast(msg.obj.toString());
                        onInformation(0, msg.obj.toString());
                        break;
                    case 1:
                        // 表示发送事件
                        // 表示玩家准备就绪状态
                        // 表示客户端向服务端发送的消息
                        toast(msg.obj.toString());
                        onInformation(1, msg.obj.toString());
                        break;
                    case 2:
                        // 表示客户端消费事件？
                        // 表示服务端向客户端发送的不需要客户端回复的消息
                        toast(msg.obj.toString());
                        onInformation(2, msg.obj.toString());
                        break;
                    case 3:
                        break;
                    case 220:
                        toast220();
                        break;
                    case 221:
                        toast221();
                        break;
                    case 222:
                        toast222();
                        break;
                    case 223:
                        toast223();
                        break;
                    case 233:
                        toast();
//                        Toast.makeText(context, "GameServer启动了", Toast.LENGTH_SHORT).show();
                        break;
                    case 234:
                        toast2();
                        break;
                    case 235:
                        toast235();
                        break;
                    case 236:
                        toast236();
                        break;
                    case 244:
                        toast244();
                        break;
                    case 245:
                        toast245();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public void toast() {
        Toast.makeText(context, "GameServer启动了", Toast.LENGTH_SHORT).show();
    }
    public void toast2() {
        Toast.makeText(context, "GameClient启动了", Toast.LENGTH_SHORT).show();
    }
    public void toast220() {
        Toast.makeText(context, "客户端准备接收消息", Toast.LENGTH_SHORT).show();
    }
    public void toast221() {
        Toast.makeText(context, "客户端接收消息中", Toast.LENGTH_SHORT).show();
    }
    public void toast222() {
        Toast.makeText(context, "客户端接收到了消息", Toast.LENGTH_SHORT).show();
    }
    public void toast223() {
        Toast.makeText(context, "客户端没法儿接收消息", Toast.LENGTH_SHORT).show();
    }
    public void toast235() {
        Toast.makeText(context, "客户端准备连接服务端", Toast.LENGTH_SHORT).show();
    }
    public void toast236() {
        Toast.makeText(context, "客户端连接上服务端了", Toast.LENGTH_SHORT).show();
    }
    public void toast244() {
        Toast.makeText(context, "服务器接收客户端连接", Toast.LENGTH_SHORT).show();
    }
    public void toast245() {
        Toast.makeText(context, "服务器接收到客户端连接了", Toast.LENGTH_SHORT).show();
    }
    public void toast246() {
        Toast.makeText(context, String.valueOf(this.sockets.size()), Toast.LENGTH_SHORT).show();
    }

    public Count getCount() {
        return this.count;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }
    public String getServerIP() {
        return this.serverIP;
    }

    // 启动服务器（开始通信）
    public void setGameServer() {
        if (this.gameServer == null) {
            this.gameServer = new GameServer();
        }
//        if (this.gameServerReceiveDataThread == null) {
//            this.gameServerReceiveDataThread = new GameServerReceiveDataThread();
//        }
        gameServer.setGame(this);
        gameServer.setRunning(true);
//        gameServerReceiveDataThread.setGame(this);
//        gameServerReceiveDataThread.setRunning(true);
        try {
//            gameServerReceiveDataThread.start();
            gameServer.start();
        } catch (Exception e) {
        }
        this.gameTransmitData = new GameTransmitData(this);
    }

    // 启动客户端（开始通信）
    public void setGameClient() {
        if (this.gameClient == null) {
            this.gameClient = new GameClient(serverIP);
        }
        if (this.gameClientReceiveDataThread == null) {
            this.gameClientReceiveDataThread = new GameClientReceiveDataThread();
        }
        gameClient.setGame(this);
        gameClient.setRunning(true);
        gameClientReceiveDataThread.setGame(this);
        gameClientReceiveDataThread.setRunning(true);
        try {
            gameClientReceiveDataThread.start();
            gameClient.start();
        } catch (Exception e) {
        }
        this.gameTransmitData = new GameTransmitData(this);
    }

    public void stopGameThreads() {
        if (this.gameServer != null) {
            this.gameServer.setRunning(false);
            new FakeSocket().start();
        }
        if (this.gameClient != null) {
            try {
                this.socket.close();
                this.gameClient.setRunning(false);
            } catch (Exception e) {
            }
        }
        if (this.gameServerReceiveDataThread != null) {
            try {
                this.gameServerReceiveDataThread.setRunning(false);
            } catch (Exception e) {
            }
        }
        if (this.gameClientReceiveDataThread != null) {
            try {
                this.gameClientReceiveDataThread.setRunning(false);
            } catch (Exception e) {
            }
        }
    }

    public GameClient getGameClient() {
        return this.gameClient;
    }
    public GameServer getGameServer() { return this.gameServer; }

    /**
     * 设置玩家逻辑座位
     * @param player
     */
    public void setGamePlayer(AbstractPlayerWithDraw player) {
        player.setReady(this.ready);
        player.setTable(this.table);
        player.setCall(this.call);

        if (player.direction == localPlayerDirection) {
            player.position = 0;
            playerBottom = player;
        } else if (player.direction == localPlayerDirection + 1
                || player.direction == localPlayerDirection - 3) {
            player.position = 1;
            playerLeft = player;
        } else if (player.direction == localPlayerDirection + 2
                || player.direction == localPlayerDirection - 2) {
            player.position = 2;
            playerTop = player;
        } else if (player.direction == localPlayerDirection + 3
                || player.direction == localPlayerDirection - 1) {
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

        if (gameType == 0 && player instanceof ProxyPlayer) {
            ((ProxyPlayer) player).setRealPlayer(new Robot(context));
        }
        if (gameType == 1 && player instanceof ProxyPlayer) {
            ((ProxyPlayer) player).setRealPlayer(new Robot(context));
        }
        // 服务器模式下专用
        if (gameType == 2 && player instanceof ProxyPlayer) {
            ((ProxyPlayer) player).setRealPlayer(new RemotePlayer(context));
        }
        // 客户端模式下，所有ProxyPlayer都是RemotePlayer
//        if (gameType == 3 && player instanceof ProxyPlayer) {
//            ((ProxyPlayer) player).setRealPlayer(new RemotePlayer(context));
//        }
    }

    public void setGamePlayer(AbstractPlayerWithDraw player, int position) {
        player.setReady(this.ready);
        player.setTable(this.table);
        player.setCall(this.call);

        if (position == 0) {
            player.position = 0;
            playerBottom = player;
        } else if (position == 1) {
            player.position = 1;
            playerLeft = player;
        } else if (position == 2) {
            player.position = 2;
            playerTop = player;
        } else if (position == 3) {
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

//        if (gameType == 0 && player instanceof ProxyPlayer) {
//            ((ProxyPlayer) player).setRealPlayer(new Robot(context));
//        }
//        if (gameType == 1 && player instanceof ProxyPlayer) {
//            ((ProxyPlayer) player).setRealPlayer(new Robot(context));
//        }
//        // 服务器模式下专用
//        if (gameType == 2 && player instanceof ProxyPlayer) {
//            ((ProxyPlayer) player).setRealPlayer(new RemotePlayer(context));
//        }
        // 客户端模式下，所有ProxyPlayer都是RemotePlayer
        if (gameType == 3 && player instanceof ProxyPlayer) {
            ((ProxyPlayer) player).setRealPlayer(new RemotePlayer(context));
        }
    }



    public Ready getReady() {
        return this.ready;
    }
    public Call getCall() {
        return this.call;
    }
    public Table getTable() { return this.table; }
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
            case 0:
                Toast.makeText(context, "阶段 0", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                // 阶段1自动进入阶段2
                //

                // 同步玩家状态
                // 同步玩家状态的时候，还需要同时同步IP和玩家位置
                // 服务端可能需要同步多个
                // 客户端需要同步一个

                // 要考虑谁先进入阶段2
                // 如果是服务端先进入阶段2，先发送消息，客户端就要先处理服务端的玩家状态
                // 如果是客户端先进入阶段2，先发送消息，服务端就要先处理客户端的玩家状态

                // 需要在客户端接入服务器的时候，发送数据包？
                break;
            case 2:                // 在Process里自动切换状态
                // 已准备界面
                // 触摸取消准备按钮，将playerBottom的准备状态切换为未就绪
                ready.onTouch(x, y);
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                // 如果本地玩家是人类玩家 并且 轮到本地玩家叫牌
                switch (call.onTouch(x, y)) {
                    case 0:
                        if (playerBottom instanceof Player && playerNumber == playerBottom.position) {
                            call.setCallStage(0);
                        } else {
                            call.setCallStage(1);
                        }
                        break;
                    case 1:
                        call.setCallStage(1);
                        break;
                    case 2:
                        call.setCallStage(2);
                        break;
                    case 3:
                        call.setCallStage(3);
                        break;
                    case 4:
                        call.setCallStage(1);
                        playerNumber++;
                        break;
                }
                break;
            case 6:
                dealerPlayer = call.getDealer();
                playerNumber = table.getPlayer();
                gameStage = 7;
                break;
            case 7:
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
            case 8:
                // 在这里存储
                Log.v(this.getClass().getName(), "正在存储数据");
                count.saveGame();
                gameStage = 9;
                break;
            case 9:
                switch (count.onTouch(x, y)) {
                    case 1:
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
    // onMessage负责消费事件
    // sendTo负责发送事件
    // 发送事件需要选择state，处理消息只要将去掉state的消息如实转发给onMessage
    public void onInformation(int state, String message) {
        // players=192.168.1.104:0 po 1px 2 zo 3 mo
        String[] msg = message.split("=");
        if (this.gameType == 2) {
            switch (state) {
                case 0:
                    sendTo(0, msg[0]);
                    break;
                case 1:
                    // 消费事件
                    onMessage(msg[0], message);
                    // 发送事件（2）
                    sendTo(2, msg[0]);
                    break;
                case 2:
                    // 发送事件（2）
                    sendTo(2, msg[0]);
                    break;
            }
        } else if (this.gameType == 3) {
            switch (state) {
                case 0:
                    // 消费事件
                    onMessage(msg[0], message);
                    // 发送事件（1）
                    sendTo(1, msg[0]);
                    break;
                case 1:
                    // 发送事件（1）
                    sendTo(1, msg[0]);
                    break;
                case 2:
                    // 接收事件
                    onMessage(msg[0], message);
                    break;
            }
        }
    }

    /**
     *
     * @param messageType 0
     * @param message players
     */
    public void sendTo( int messageType, String message) {
        this.gameTransmitData.sendToClients(messageType, message);
    }

    /**
     * 接收到来自socket的消息
     * @param type 表示消息的类型
     * @param message 消息的内容
     */
    public void onMessage(String type, String message) {
        // 192.168.1.104:0 po 1 px 2 zo 3 mo
        Log.v(this.getClass().getName(), "xxxxxxxxxxxxxxxxxxxxx");
        Log.v(this.getClass().getName(), message);
        Log.v(this.getClass().getName(), "xxxxxxxxxxxxxxxxxxxxx");
        Log.v(this.getClass().getName(), String.valueOf(gameStage));
        String[] messages = message.split(":");
        String ip = messages[0];
        String content = messages[1];
        if (ip != this.serverIP) {
            switch (gameStage) {
                case 0:
                    // 没有连接的状态
                    break;
                case 1:
                case 2:
                    // 连接成功的状态
                    // ready阶段
                    // 如果是服务端：接收来自客户端的消息，还要广播到其他客户端
                    // 如果是客户端：接收来自客户端的消息

                    // 先给玩家从所有状态为x的位置中分配一个位置
                    // 服务器怎么处理这个消息的？
                    // 客户端发送的消息和服务端发送的消息有何不同？
                    // handleMessage时，只同步来自其他客户端的玩家消息（除了Direction）
                    // 处理不同类型的type
                    if (type.equals("players")) {
                        handleMessagePlayer(content);
                    }
                    // 在将所有其他玩家的状态显示到ready中
                    break;
                case 3:
                    // 同步玩家手牌
                    // ready状态
                    break;
                case 4:
                    // ready2阶段
                    break;
                case 5:
                    // 同步叫牌值
                    // 叫牌阶段
                    break;
                case 6:
                    // 叫牌过渡阶段
                    break;
                case 7:
                    // 同步打牌值
                    // 打牌阶段
                    break;
                case 8:
                    // 打牌过渡阶段
                    break;
                case 9:
                    // 结算阶段
                    break;
            }
        }
    }

    public void handleMessagePlayer(String content) {
        // 0 po 1 px 2 zo 3 mo
        Log.v(this.getClass().getName(), "xxxxxxxxxxxxxxxxxxxxx");
        Log.v(this.getClass().getName(), content);
        Log.v(this.getClass().getName(), "xxxxxxxxxxxxxxxxxxxxx");
        String[] message = content.split(" ");
//        setLocalPlayerDirection();
        // 设置所有玩家的direction
        // 设置所有玩家的状态！
        int n = new Random().nextInt(4);
        int state = 0;
        // 如果本机没有设置direction，先占用一个direction
        // 分配Direction
        if (localPlayerDirection == -1) {
            while (state == 0) {
                if (message[n*2].equals("0")) {
                    if (message[n*2+1].equals("px")) {
                        setLocalPlayerDirection(0);
                    }
                } else if (message[n*2].equals("1")) {
                    if (message[n*2+1].equals("px")) {
                        setLocalPlayerDirection(1);
                    }
                } else if (message[n*2].equals("2")) {
                    if (message[n*2+1].equals("px")) {
                        setLocalPlayerDirection(2);
                    }
                } else if (message[n*2].equals("3")) {
                    if (message[n*2+1].equals("px")) {
                        setLocalPlayerDirection(3);
                    }
                }
                state = 1;
            }
            playerBottom.setDirection(localPlayerDirection);
            playerLeft.setDirection(localPlayerDirection + 1);
            playerTop.setDirection(localPlayerDirection + 2);
            playerRight.setDirection(localPlayerDirection + 3);
        }
        // 分配剩下的direction
        // 设置其他玩家的就绪状态
        // 就绪状态可能要区分几种不同的状态
        // 增加一个根据direction找player的函数
        // 只需要设置RemotePlayer
        // 这里只同步了服务器到客户端
//        Log.v(this.getClass().getName(), "位置-----------------------------");
//        Log.v(this.getClass().getName(), "上" + String.valueOf(playerTop.direction));
//        Log.v(this.getClass().getName(), "下" + String.valueOf(playerBottom.direction));
//        Log.v(this.getClass().getName(), "左" + String.valueOf(playerLeft.direction));
//        Log.v(this.getClass().getName(), "右" + String.valueOf(playerRight.direction));
//        Log.v(this.getClass().getName(), "位置-----------------------------");
        AbstractPlayer player = null;
        state = 0;
        n = 0;
        Log.v(this.getClass().getName(), message.toString());
        while (state == 0) {
            Log.v(this.getClass().getName(), message[n]);
            if (message[n].equals("0")) {
                Log.v(this.getClass().getName(), "0号玩家");
                player = getPlayerByDirection(0);
                Log.v(this.getClass().getName(), player.getClass().getName());
                if (player instanceof ProxyPlayer && ((ProxyPlayer) player).getRealPlayer() instanceof RemotePlayer) {
                    if (message[n + 1].equals("mo")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(0);
                    } else if (message[n + 1].equals("zx")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(1);
                    } else if (message[n + 1].equals("zo")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(2);
                    } else if (message[n + 1].equals("po")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(3);
                    } else if (message[n + 1].equals("px")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(4);
                    }
                }
            } else if (message[n].equals("1")) {
                Log.v(this.getClass().getName(), "1号玩家");
                player = getPlayerByDirection(1);
                Log.v(this.getClass().getName(), player.getClass().getName());
                if (player instanceof ProxyPlayer && ((ProxyPlayer) player).getRealPlayer() instanceof RemotePlayer) {
                    if (message[n + 1].equals("mo")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(0);
                    } else if (message[n + 1].equals("zx")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(1);
                    } else if (message[n + 1].equals("zo")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(2);
                    } else if (message[n + 1].equals("po")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(3);
                    } else if (message[n + 1].equals("px")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(4);
                    }
                }
            } else if (message[n].equals("2")) {
                Log.v(this.getClass().getName(), "2号玩家");
                player = getPlayerByDirection(2);
                Log.v(this.getClass().getName(), player.getClass().getName());
                if (player instanceof ProxyPlayer && ((ProxyPlayer) player).getRealPlayer() instanceof RemotePlayer) {
                    if (message[n + 1].equals("mo")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(0);
                    } else if (message[n + 1].equals("zx")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(1);
                    } else if (message[n + 1].equals("zo")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(2);
                    } else if (message[n + 1].equals("po")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(3);
                    } else if (message[n + 1].equals("px")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(4);
                    }
                }
            } else if (message[n].equals("3")) {
                Log.v(this.getClass().getName(), "3号玩家");
                player = getPlayerByDirection(3);
                Log.v(this.getClass().getName(), player.getClass().getName());
                if (player instanceof ProxyPlayer && ((ProxyPlayer) player).getRealPlayer() instanceof RemotePlayer) {
                    if (message[n + 1].equals("mo")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(0);
                    } else if (message[n + 1].equals("zx")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(1);
                    } else if (message[n + 1].equals("zo")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(2);
                    } else if (message[n + 1].equals("po")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(3);
                    } else if (message[n + 1].equals("px")) {
                        ((RemotePlayer) ((ProxyPlayer) player).getRealPlayer()).setState(4);
                    }
                }
            }
            n = n + 2;
            Log.v(this.getClass().getName(), "n = " + String.valueOf(n));
            Log.v(this.getClass().getName(), "状态-----------------------------");
            if (playerTop instanceof ProxyPlayer && ((ProxyPlayer) playerTop).getRealPlayer() instanceof RemotePlayer) {
                Log.v(this.getClass().getName(), "上" + String.valueOf(((RemotePlayer) ((ProxyPlayer) playerTop).getRealPlayer()).getState()));
            }
            if (playerLeft instanceof ProxyPlayer && ((ProxyPlayer) playerLeft).getRealPlayer() instanceof RemotePlayer) {
                Log.v(this.getClass().getName(), "左" + String.valueOf(((RemotePlayer) ((ProxyPlayer) playerLeft).getRealPlayer()).getState()));
            }
            if (playerRight instanceof ProxyPlayer && ((ProxyPlayer) playerRight).getRealPlayer() instanceof RemotePlayer) {
                Log.v(this.getClass().getName(), "右" + String.valueOf(((RemotePlayer) ((ProxyPlayer) playerRight).getRealPlayer()).getState()));
            }
            Log.v(this.getClass().getName(), "状态-----------------------------");
            if (n == 8) {
                state = 1;
            }
        }
    }

    public AbstractPlayer getPlayerByDirection(int direction) {
        if (playerTop.direction == direction) {
            return playerTop;
        } else if (playerBottom.direction == direction) {
            return playerBottom;
        } else if (playerLeft.direction == direction) {
            return playerLeft;
        }
        return playerRight;
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
            case 0:
                // 未连接的状态
                if (this.gameServer != null) {
                    if (this.gameServer.getServerSocket() != null && this.gameServer.getServerSocket().isBound()) {
                        // 如果服务器准备就绪？
                        gameStage = 1;
                    }
                }
                if (this.gameClient != null) {
                    if (this.socket != null && this.socket.isConnected()) {
                        // 如果客户端准备就绪
                        gameStage = 1;
                    }
                }
                break;
            case 1:
                // 连接成功，点击进入下一阶段
                gameStage = 2;
                break;
            case 2:
                if (ready.isFinish()) {
                    // 进入游戏
//                    gameState = 2;
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
                ready.setCards();
                gameStage = 5;
                break;
            case 5:
                // TODO:修改game的stage可以将游戏进程向前推进
                if (call.isFinish()) {
                    gameStage = 6;
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
            case 6:
                // TODO:坐庄提示
                // TODO:没有逻辑处理
                table.setDealerAndContract(this.call.getDealer(),
                        this.call.getLevel(), this.call.getSuits());
                // 同步game-player和table-playerNumber;
//                table.setDirection();
                break;
            case 7:
                // TODO:出牌循环
                // TODO:修改game的stage可以将游戏进程向前推进
                // TODO:要设置table的位置（左，中，右）
                if (table.isFinish()) {
                    gameStage = 8;
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
            case 8:
                break;
            case 9:
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
            case 0:
                drawStage0(canvas, paint, des);
                break;
            case 1:
                drawStage1(canvas, paint, des);
                break;
            case 2:
                // 绘制已经准备界面
                ready.draw(canvas, paint, des);
                break;
            case 3:
                // 绘制已经准备界面
                ready.draw(canvas, paint, des);
                break;
            case 4:
                // 发牌
                ready.draw(canvas, paint, des);
                break;
            case 5:
                playerBottom.draw(canvas, paint, des);
                call.draw(canvas, paint, des);
                break;
            case 6:
                // 过渡阶段，可以什么都不画的
                playerBottom.draw(canvas, paint, des);
                call.setCallStage(11);
                call.draw(canvas, paint, des);
                break;
            case 7:
            case 8:
                drawCount(canvas, paint, des);
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
            case 9:
                count.draw(canvas, paint, des);
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
    public void drawText(Canvas canvas, Paint paint, Rect des) {
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

    public void drawStage0(Canvas canvas, Paint paint, Rect des) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);

        canvas.drawText("阶段" + this.gameStage, 0, 100, paint);

        paint.setTextSize(100);
        try {
            if (this.getGameClient() == null) {
                canvas.drawText("作为服务器", 480, 720, paint);
                canvas.drawText("正在建立连接", 280, 1260, paint);
            } else {
                canvas.drawText("作为客户端", 480, 720, paint);
                canvas.drawText("正在建立连接", 280, 1260, paint);
            }
        } catch (Exception e){
        }
    }
    public void drawStage1(Canvas canvas, Paint paint, Rect des) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);

        canvas.drawText("阶段" + this.gameStage, 0, 100, paint);

        paint.setTextSize(100);
        try {
            if (this.getGameClient() == null) {
                canvas.drawText("作为服务器", 480, 720, paint);
                canvas.drawText("完成连接，点击继续", 280, 1260, paint);
            } else {
                canvas.drawText("作为客户端", 480, 720, paint);
                canvas.drawText("完成连接，点击继续", 280, 1260, paint);
//                canvas.drawText(String.valueOf(this.getGameClient().serverIP), 0, 460, paint);
            }
        } catch (Exception e){
            canvas.drawText(String.valueOf(e.toString()), 0, 460, paint);
        }
    }

    public void drawCount(Canvas canvas, Paint paint, Rect des) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);

        canvas.drawText("南北" + this.table.getTricksNS(), 0, 100, paint);
        canvas.drawText("东西" + this.table.getTricksWE(), 720, 100, paint);
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
        if (playerTop != null) {
            playerTop.setWidthHeight(this.width, this.height);
        }
        if (playerLeft != null) {
            playerLeft.setWidthHeight(this.width, this.height);
        }
        if (playerRight != null) {
            playerRight.setWidthHeight(this.width, this.height);
        }
        if (playerBottom != null) {
            playerBottom.setWidthHeight(this.width, this.height);
        }

        ready.setWidthHeight(this.width, this.height);
        call.setWidthHeight(this.width, this.height);
        table.setWidthHeight(this.width, this.height);
    }
}
