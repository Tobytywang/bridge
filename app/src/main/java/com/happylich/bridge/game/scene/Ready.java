package com.happylich.bridge.game.scene;

/**
 * Created by lich on 2018/4/24.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.engine.util.Position;
import com.happylich.bridge.game.main.Cards;
import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.player.AbstractPlayerWithDraw;
import com.happylich.bridge.game.player.Player;
import com.happylich.bridge.game.player.ProxyPlayer;
import com.happylich.bridge.game.player.RemotePlayer;
import com.happylich.bridge.game.player.Robot;
import com.happylich.bridge.game.res.CardImage;

/**
 * 玩家准备界面
 */
public class Ready extends AbstractScene {

    private int readyStage = 0;

    private boolean finish = false;

    private int a;
    private int b;
    private int c;
    private int d;


    /**
     * 构造函数
     * @param context
     */
    public Ready(Context context) {
        this.context = context;
    }

    /**
     * 检测所有玩家是否就绪
     */
    public boolean isFinish() {
        if (playerTop.isInOrder() &&
                playerLeft.isInOrder() &&
                playerRight.isInOrder() &&
                playerBottom.isInOrder()) {
            // 在这里执行发牌操作？
            Cards cards = new Cards(52);
            playerLeft.setCards(cards.getCards(0));
            playerRight.setCards(cards.getCards(1));
            playerBottom.setCards(cards.getCards(2));
            playerTop.setCards(cards.getCards(3));
            return true;
        }
        return false;
    }

    public boolean hasFreePlace() {
        if (!playerTop.isInOrder() ||
                !playerLeft.isInOrder() ||
                !playerRight.isInOrder()) {
            return true;
        }
        return false;
    }

    public void setRealPlayer(RemotePlayer remotePlayer) {
        if (!playerTop.isInOrder()) {
            ((ProxyPlayer) playerTop).setRealPlayer(remotePlayer);
        } else if (!playerLeft.isInOrder()) {
            ((ProxyPlayer) playerLeft).setRealPlayer(remotePlayer);
        } else if (!playerRight.isInOrder()) {
            ((ProxyPlayer) playerRight).setRealPlayer(remotePlayer);
        }
    }

    //
    public void removeRealPlayer(int direction) {
        // 应该传入一个参数，传什么呢？
        if (playerTop.direction == direction) {
            playerTop = null;
        } else if (playerLeft.direction == direction) {
            playerLeft = null;
        } else if (playerRight.direction == direction) {
            playerRight = null;
        }
    }

    // 默认为ProxyPlayer，如果没有就绪（没有被代理的玩家）
    // 有几种类型
    // 1. 对于电脑玩家
    //   1. 在主机端，显示为"电脑"
    //   2. 在其他端，显示为"电脑就绪"
    // 2. 对于主机玩家
    //   1. 还没有就绪的
    //     1. 在主机端，显示为"准备"
    //     2. 在其他段，显示为"主机未就绪"
    //   2. 已经就绪的
    //     1. 在主机端，显示为"就绪"
    //     1. 在主机端，显示为"主机就绪"
    // 3. 对于非主机玩家
    //   1. 还没有就绪的
    //     1. 在主机端，显示为"玩家未就绪"
    //     2. 在其他段，显示为"玩家未就绪"
    //   2. 已经就绪的
    //     1. 在主机端，显示为"玩家就绪"
    //     2. 在其他段，显示为"玩家就绪"
    // 4. 对于空玩家
    //   1. 在主机端，显示为"玩家"
    //   2. 在其他段，显示为"空玩家"

    /**
     * 检测按键（这个只有本地玩家有）
     * 在特定阶段被game.call调用
     * @param x
     * @param y
     * @return 表示事件类型，0表示无效区域，1表示有效区域
     */
    public int onTouch(int x, int y) {
        switch(readyStage) {
            case 0:
                // 监听所有四个按钮
                switch (touchReady(x, y)) {
                    case 0:
                        readyStage = 1;
                        break;
                    case 3:
                        readyStage = 2;
                        break;
                    case 6:
                        readyStage = 3;
                        break;
                    case 9:
                        readyStage = 0;
                        if (playerBottom instanceof Player && !playerBottom.isInOrder()) {
                            ((Player) playerBottom).setInOrder(true);
                            if (this.game.getGameClient() != null) {
                                Log.v(this.getClass().getName(), "准备就绪");
                                game.getGameClient().sendToServer(this.game.getServerIP() + " ready");
                            }
                        } else {
                            ((Player) playerBottom).setInOrder(false);
                            if (this.game.getGameClient() != null) {
                                Log.v(this.getClass().getName(), "取消准备");
                                game.getGameClient().sendToServer(this.game.getServerIP() + " unready");
                            }
                        }
                        break;
                    default:
                        readyStage = 0;
                        break;
                }
                return 0;
            case 1:
                switch (touchReady(x, y)) {
                    case 0:
                        readyStage = 0;
                        break;
                    case 1:
                        // 设置上边位置为ProxyPlayer（
                        a = 0;
                        if (playerTop instanceof  ProxyPlayer && !playerTop.isInOrder()) {
//                            ((ProxyPlayer) playerTop).removeRealPlayer();
                        }
                        readyStage = 0;
                        break;
                    case 2:
                        // 设置上边位置为Robot
                        a = 1;
                        if (playerTop instanceof ProxyPlayer) {
                            ((ProxyPlayer) playerTop).setRealPlayer(new Robot(context));
                        }
                        readyStage = 0;
                        break;
                    case 3:
                        readyStage = 2;
                        break;
                    case 6:
                        readyStage = 3;
                        break;
                    case 9:
                        readyStage = 0;
                        if (playerBottom instanceof Player && !playerBottom.isInOrder()) {
                            ((Player) playerBottom).setInOrder(true);
                            if (this.game.getGameClient() != null) {
                                Log.v(this.getClass().getName(), "准备就绪");
                                game.getGameClient().sendToServer(this.game.getServerIP() + " ready");
                            }
                        } else {
                            ((Player) playerBottom).setInOrder(false);
                            if (this.game.getGameClient() != null) {
                                Log.v(this.getClass().getName(), "取消准备");
                                game.getGameClient().sendToServer(this.game.getServerIP() + " unready");
                            }
                        }
                        break;
                    default:
                        readyStage = 0;
                        break;
                }
                // 监听上面的两个弹出按钮
                return 1;
            case 2:
                switch (touchReady(x, y)) {
                    case 0:
                        readyStage = 1;
                        break;
                    case 3:
                        readyStage = 0;
                        break;
                    case 4:
                        // 设置左边位置为ProxyPlayer
                        b = 0;
                        if (playerLeft instanceof ProxyPlayer && !playerLeft.isInOrder()) {
//                            ((ProxyPlayer) playerLeft).removeRealPlayer();
                        }
                        readyStage = 0;
                        break;
                    case 5:
                        // 设置左边位置为Robot
                        b = 1;
                        readyStage = 0;
                        if (playerLeft instanceof ProxyPlayer) {
                            ((ProxyPlayer) playerLeft).setRealPlayer(new Robot(context));
                        }
                        break;
                    case 6:
                        readyStage = 3;
                        break;
                    case 9:
                        readyStage = 0;
                        if (playerBottom instanceof Player && !playerBottom.isInOrder()) {
                            ((Player) playerBottom).setInOrder(true);
                            if (this.game.getGameClient() != null) {
                                Log.v(this.getClass().getName(), "准备就绪");
                                game.getGameClient().sendToServer(this.game.getServerIP() + " ready");
                            }
                        } else {
                            ((Player) playerBottom).setInOrder(false);
                            if (this.game.getGameClient() != null) {
                                Log.v(this.getClass().getName(), "取消准备");
                                game.getGameClient().sendToServer(this.game.getServerIP() + " unready");
                            }
                        }
                        break;
                    default:
                        readyStage = 0;
                        break;
                }
                // 监听左边的两个弹出按钮
                return 2;
            case 3:
                switch (touchReady(x, y)) {
                    case 0:
                        readyStage = 1;
                        break;
                    case 3:
                        readyStage = 2;
                        break;
                    case 6:
                        readyStage = 0;
                        break;
                    case 7:
                        // 设置右边位置为PorxyPlayer
                        c = 0;
                        readyStage = 0;
                        if (playerRight instanceof ProxyPlayer && !playerRight.isInOrder()) {
                            ((ProxyPlayer) playerRight).removeRealPlayer();
                        }
                        break;
                    case 8:
                        // 设置右边位置为Robot
                        c = 1;
                        readyStage = 0;
                        if (playerRight instanceof ProxyPlayer) {
                            ((ProxyPlayer) playerRight).setRealPlayer(new Robot(context));
                        }
                        break;
                    case 9:
                        readyStage = 0;
                        if (playerBottom instanceof Player && !playerBottom.isInOrder()) {
                            ((Player) playerBottom).setInOrder(true);
                            if (this.game.getGameClient() != null) {
                                Log.v(this.getClass().getName(), "准备就绪");
                                game.getGameClient().sendToServer(this.game.getServerIP() + " ready");
                            }
                        } else {
                            ((Player) playerBottom).setInOrder(false);
                            if (this.game.getGameClient() != null) {
                                Log.v(this.getClass().getName(), "取消准备");
                                game.getGameClient().sendToServer(this.game.getServerIP() + " unready");
                            }
                        }
                        break;
                    default:
                        readyStage = 0;
                        break;
                }
                // 监听右边的两个弹出按钮
                return 3;
            default:
                return 0;
        }
    }

    public void setPlayer(AbstractPlayerWithDraw player) {
        if (player.position == 0) {
            playerBottom = player;
        } else if (player.position == 1) {
            playerLeft = player;
        } else if (player.position == 2) {
            playerTop = player;
        } else if (player.position == 3) {
            playerRight = player;
        }
    }

    public int touchReady(int x, int y) {
        int left = this.left;
        int top = this.top;

        left = this.left + ((1440 - 300) / 2) - 250;
        top = this.top + 120;
        Position positionTop = new Position(top, left,
                top + 152, left + 300);
        positionTop.resieze((float)this.width / (float)1440);

        left = this.left + ((1440 - 300) / 2) - 250 + 300;
        top = this.top + 120 - 12;
        Position positionTop1 = new Position(top + 12, left + 30,
                top + 152 + 12, left + 300 + 30);
        positionTop1.resieze((float)this.width / (float)1440);

        left = this.left + ((1440 - 300) / 2) - 250 + 300;
        top = this.top + 120 - 12;
        Position positionTop2 = new Position(top + 152 + 12 * 3, left + 30,
                top + 152 * 2 + 12 * 3, left + 300 + 30);
        positionTop2.resieze((float)this.width / (float)1440);

        left = this.left + 40;
        top = this.top + ((1800 - 152) / 2) + 200 - 100;
        Position positionLeft = new Position(top, left,
                top + 152, left + 300);
        positionLeft.resieze((float)this.width / (float)1440);

        left = this.left + 40 + 300;
        top = this.top + ((1800 - 152) / 2) + 200 - 12 - 100;
        Position positionLeft1 = new Position(top + 12, left + 30,
                top + 152 + 12, left + 300 + 30);
        positionLeft1.resieze((float)this.width / (float)1440);

        left = this.left + 40 + 300;
        top = this.top + ((1800 - 152) / 2) + 200 - 12 - 100;
        Position positionLeft2 = new Position(top + 152 + 12 * 3, left + 30,
                top + 152 * 2 + 12 * 3, left + 300 + 30);
        positionLeft2.resieze((float)this.width / (float)1440);

        left = 1440 - 40 - 300;
        top = this.top + ((1800 - 152) / 2) - 200 - 100;
        Position positionRight = new Position(top, left,
                top + 152, left + 300);
        positionRight.resieze((float)this.width / (float)1440);

        left = 1440 - 40 - 300 - 360;
        top = this.top + ((1800 - 152) / 2) - 200 - 12 - 100;
        Position positionRight1 = new Position(top + 12, left + 30,
                top + 152 + 12, left + 300 + 30);
        positionRight1.resieze((float)this.width / (float)1440);

        left = 1440 - 40 - 300 - 360;
        top = this.top + ((1800 - 152) / 2) - 200 - 12 - 100;
        Position positionRight2 = new Position(top + 152 + 12 * 3, left + 30,
                top + 152 * 2 + 12 * 3, left + 300 + 30);
        positionRight2.resieze((float)this.width / (float)1440);

        left = this.left + ((1440 - 300) / 2) + 250;
        top = this.top + 1800 - 320 - 152;
        Position positionBottom = new Position(top, left,
                top + 152, left + 300);
        positionBottom.resieze((float)this.width / (float)1440);

        if (Position.inPosition(x, y, positionTop)) {
            return 0;
        } else if (Position.inPosition(x, y, positionTop1)) {
            return 1;
        } else if (Position.inPosition(x, y, positionTop2)) {
            return 2;
        } else if (Position.inPosition(x, y, positionLeft)) {
            return 3;
        } else if (Position.inPosition(x, y, positionLeft1)) {
            return 4;
        } else if (Position.inPosition(x, y, positionLeft2)) {
            return 5;
        } else if (Position.inPosition(x, y, positionRight)) {
            return 6;
        } else if (Position.inPosition(x, y, positionRight1)) {
            return 7;
        } else if (Position.inPosition(x, y, positionRight2)) {
            Log.v(this.getClass().getName(), "触摸8");
            return 8;
        } else if (Position.inPosition(x, y, positionBottom)) {
            return 9;
        }
        Log.v(this.getClass().getName(), "完毕");
        return 10;
    }

    /**
     * 绘制叫牌矩阵
     * @param canvas
     */
    public void draw(Canvas canvas, Paint paint, Rect des) {
        drawBasicButton(canvas, paint, des);
        switch (readyStage) {
            case 0:
                // 不显示任何菜单

                break;
            case 1:
                // 显示上面的菜单
                drawTopMenu(canvas, paint, des);
                break;
            case 2:
                // 显示左边的菜单
                drawLeftMenu(canvas, paint, des);
                break;
            case 3:
                // 显示右边的菜单
                drawRightMenu(canvas, paint, des);
                break;
        }
    }

    public void drawBasicButton(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;
        int left = this.left;
        int top = this.top;


        // 绘制本地玩家的未准备状态
        // 根据其他玩家的状态绘制其他玩家的状态
        Image = CardImage.buttonBitmapImage;

        // 上面的按钮
        left = this.left + ((1440 - 300) / 2) - 250;
        top = this.top + 120;
        des.set(left, top, left+ 300, top + 152);
        canvas.drawBitmap(Image, null, des, paint);

        // 按钮上的字
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
//        if (a == 0) {
//            canvas.drawText("玩家", left + 150, top + 100, paint);
//        } else {
//            canvas.drawText("电脑", left + 150, top + 100, paint);
//        }
        if (playerTop instanceof ProxyPlayer && !((ProxyPlayer) playerTop).isInOrder()) {
            canvas.drawText("玩家", left + 150, top + 100, paint);
        } else if (playerTop instanceof ProxyPlayer && ((ProxyPlayer) playerTop).getRealPlayer() instanceof RemotePlayer) {
            canvas.drawText("就绪", left + 150, top + 100, paint);
        } else if (playerTop instanceof ProxyPlayer && ((ProxyPlayer) playerTop).getRealPlayer() instanceof Robot) {
            canvas.drawText("电脑", left + 150, top + 100, paint);
        }

        // 下面的按钮
        left = this.left + ((1440 - 300) / 2) + 250;
        top = this.top + 1800 - 320 - 152;
        des.set(left, top, left+ 300, top + 152);
        canvas.drawBitmap(Image, null, des, paint);
        // 按钮上的字
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        if (playerBottom.isInOrder()) {
            canvas.drawText("就绪", left + 150, top + 100, paint);
        } else {
            canvas.drawText("准备", left + 150, top + 100, paint);
        }

        // 左边的按钮
        left = this.left + 40;
        top = this.top + ((1800 - 152) / 2) + 200 - 100;
        des.set(left, top, left+ 300, top + 152);
        canvas.drawBitmap(Image, null, des, paint);
        // 按钮上的字
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        if (playerLeft instanceof ProxyPlayer && !((ProxyPlayer) playerLeft).isInOrder()) {
            canvas.drawText("玩家", left + 150, top + 100, paint);
        } else if (playerLeft instanceof ProxyPlayer && ((ProxyPlayer) playerLeft).getRealPlayer() instanceof RemotePlayer) {
            canvas.drawText("就绪", left + 150, top + 100, paint);
        } else if (playerLeft instanceof ProxyPlayer && ((ProxyPlayer) playerLeft).getRealPlayer() instanceof Robot) {
            canvas.drawText("电脑", left + 150, top + 100, paint);
        }
//        if (b == 0) {
//            canvas.drawText("玩家", left + 150, top + 100, paint);
//        } else {
//            canvas.drawText("电脑", left + 150, top + 100, paint);
//        }

        // 右边的按钮
        left = 1440 - 40 - 300;
        top = this.top + ((1800 - 152) / 2) - 200 - 100;
        des.set(left, top, left+ 300, top + 152);
        canvas.drawBitmap(Image, null, des, paint);
        // 按钮上的字
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        if (playerRight instanceof ProxyPlayer && !((ProxyPlayer) playerRight).isInOrder()) {
            canvas.drawText("玩家", left + 150, top + 100, paint);
        } else if (playerRight instanceof ProxyPlayer && ((ProxyPlayer) playerRight).getRealPlayer() instanceof RemotePlayer) {
            canvas.drawText("就绪", left + 150, top + 100, paint);
        } else if (playerRight instanceof ProxyPlayer && ((ProxyPlayer) playerRight).getRealPlayer() instanceof Robot) {
            canvas.drawText("电脑", left + 150, top + 100, paint);
        }
//        if (c == 0) {
//            canvas.drawText("玩家", left + 150, top + 100, paint);
//        } else {
//            canvas.drawText("电脑", left + 150, top + 100, paint);
//        }
        Image = null;
    }
    public void drawTopMenu(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;
        int left = this.left;
        int top = this.top;

        left = this.left + ((1440 - 300) / 2) - 250 + 300;
        top = this.top + 120 - 12;

        paint.setColor(Color.parseColor("#99444444"));
        canvas.drawRect(left, top, left + 360, top + 352, paint);

        paint.setColor(Color.WHITE);

        Image = CardImage.buttonBitmapImage;
        des.set(left + 30, top + 12 * 1, left+ 300 + 30, top + 152 + 12 * 1);
        canvas.drawBitmap(Image, null, des, paint);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("玩家", left + 150 + 30, top + 12 * 1 + 100, paint);

        Image = CardImage.buttonBitmapImage;
        des.set(left + 30 , top + 152 +  12 * 3, left+ 300 + 30, top + 152 * 2 + 12 * 3);
        canvas.drawBitmap(Image, null, des, paint);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("电脑", left + 150 + 30, top + 152 + 12 * 3 + 100, paint);

    }
    public void drawLeftMenu(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;
        int left = this.left;
        int top = this.top;

        left = this.left + 40 + 300;
        top = this.top + ((1800 - 152) / 2) + 200 - 12 - 100;

        paint.setColor(Color.parseColor("#99444444"));
        canvas.drawRect(left, top, left + 360, top + 352, paint);

        paint.setColor(Color.WHITE);

        Image = CardImage.buttonBitmapImage;
        des.set(left + 30, top + 12 * 1, left+ 300 + 30, top + 152 + 12 * 1);
        canvas.drawBitmap(Image, null, des, paint);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("玩家", left + 150 + 30, top + 12 * 1 + 100, paint);

        Image = CardImage.buttonBitmapImage;
        des.set(left + 30 , top + 152 +  12 * 3, left+ 300 + 30, top + 152 * 2 + 12 * 3);
        canvas.drawBitmap(Image, null, des, paint);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("电脑", left + 150 + 30, top + 152 + 12 * 3 + 100, paint);
    }
    public void drawRightMenu(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;
        int left = this.left;
        int top = this.top;

        left = 1440 - 40 - 300 - 360;
        top = this.top + ((1800 - 152) / 2) - 200 - 12 - 100;

        paint.setColor(Color.parseColor("#99444444"));
        canvas.drawRect(left, top, left + 360, top + 352, paint);

        paint.setColor(Color.WHITE);

        Image = CardImage.buttonBitmapImage;
        des.set(left + 30, top + 12 * 1, left+ 300 + 30, top + 152 + 12 * 1);
        canvas.drawBitmap(Image, null, des, paint);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("玩家", left + 150 + 30, top + 12 * 1 + 100, paint);

        Image = CardImage.buttonBitmapImage;
        des.set(left + 30 , top + 152 +  12 * 3, left+ 300 + 30, top + 152 * 2 + 12 * 3);
        canvas.drawBitmap(Image, null, des, paint);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("电脑", left + 150 + 30, top + 152 + 12 * 3 + 100, paint);
    }
}
