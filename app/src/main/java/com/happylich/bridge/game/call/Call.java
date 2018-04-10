package com.happylich.bridge.game.call;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.happylich.bridge.R;
import com.happylich.bridge.engine.util.Position;
import com.happylich.bridge.game.res.CardImage;

/**
 * Created by lich on 2018/3/26.
 * 绘制叫牌矩阵的类
 * 1. 小
 * 2. 大（未选中）
 * 3. 大（选中）
 *
 * Call是Game的一个子类，但也要和玩家类一起工作，返回玩家的叫牌值？
 * Call的作用
 * 1. 绘制
 * 2. 处理叫牌按键
 * 3. 获得叫牌按键的结果
 *    call和玩家要有交互（将call的touch结果通知给player）
 *    call的触摸事件只需要和人类玩家交互
 *    call的状态需要和所有玩家交互
 *    1. call.setPlayerE(player) playerE.setLastCall:人类玩家 playerE.getLastCall
 *    2. player.setCall(call) call.getLastCall
 */

public class Call {
    private Context context;

    // 叫牌阶段标志位
    private int callStage;

    // 叫牌阶段完成
    private boolean finish;

    // 绘制尺寸
    private int width, height;
    private int left, top;

    // 选牌标志
    private int selectFlag = -1;
    private int selectFlagX = -1;
    private int selectFlagY = -1;

    // 叫牌历史
    private int lastCallCard = -1;
    private int[] callHistory = new int[35];
    private int callHistoryN = -1;
    private int callHistoryE = -1;
    private int callHistoryS = -1;
    private int callHistoryW = -1;

    // 叫牌矩阵（0表示有,1表示空）
    private int[][] calls = new int[5][7];

    /**
     * 构造函数
     */
    public Call(Context context) {
        this.context = context;
    }

    /**
     * 每次在玩家监听的时候置为false
     * @param finish
     */
    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    /**
     * 获得人类玩家的叫牌值？
     * 0-34表示有效叫牌值，35表示pass
     */
    public boolean callCard() {
        return finish;
    }

    /**
     * 设置绘图基准点
     * @param position
     */
    public void setPosition(int[] position) {
        this.left = position[0];
        this.top = position[1];
    }

    /**
     * 设置宽高
     */
    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 设置callstage
     * @param stage
     */
    public void setCallStage(int stage) {
        this.callStage = stage;
    }

    /**
     * 获得上一次的叫牌点
     */
    public int getLastCallCard() {
        return lastCallCard;
    }

    /**
     * 机器人玩家专用的
     */
    public void setCallW(int callCard) {
        this.lastCallCard = callCard;
        this.callHistoryW = callCard;
    }

    /**
     * 机器人玩家专用的
     */
    public void setCallN(int callCard) {
        this.lastCallCard = callCard;
        this.callHistoryN = callCard;
    }

    /**
     * 机器人玩家专用的
     */
    public void setCallE(int callCard) {
        this.lastCallCard = callCard;
        this.callHistoryE = callCard;
    }

    /**
     * 机器人玩家专用的
     */
    public void setCallS(int callCard) {
        this.lastCallCard = callCard;
        this.callHistoryS = callCard;
    }

    /**
     * 检测按键（这个只有本地玩家有）
     * 在特定阶段被game.call调用
     * @param x
     * @param y
     * @return 表示事件类型，0表示无效区域，1表示有效区域
     */
    public int onTouch(int x, int y) {
        switch(callStage) {
            case 0:
                if (touchSmall(x, y) == 1) {
                    return 1;
                } else {
                    return 0;
                }
            case 1:
                if (touchBig(x, y) == 1) {
                    return 2;
                } else {
                    return 0;
                }
            case 2:
                int touchBig = touchBigSelected(x, y);
                if (touchBig == 0) {
                    Log.v(this.getClass().getName(), "触摸到本方格，返回阶段0");
                    this.callHistoryS = lastCallCard;
                    this.finish = true;
                    return 0;
                } else if (touchBig == 2) {
                    Log.v(this.getClass().getName(), "触摸到其他方格，返回阶段2");
                    return 2;
                } else {
                    Log.v(this.getClass().getName(), "触摸无效区域，返回阶段1");
                    return 1;
                }
            default:
                return 0;
        }
    }

    /**
     * 小矩阵
     * @param x
     * @param y
     * @return
     */
    private int touchSmall(int x, int y) {
        int left = this.left;
        int top = this.top + 100;
        Position position = new Position(top, left,
                top + 1144, left + 720);
        position.resieze((float)this.width / (float)1440);
        Log.v(this.getClass().getName(), String.valueOf(Position.inPosition(x, y, position)));
        if (Position.inPosition(x, y, position)) {
            Log.v(this.getClass().getName(), "Bingo");
            return 1;
        }
        return 0;
    }

    /**
     * 检测大矩阵
     * @param x
     * @param y
     * @return
     */
    private int touchBig(int x, int y) {
        Position position;
        int left = this.left - 100;
        int top = this.top;

        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j * 5 + i) > lastCallCard) {
                    position = new Position(top + 5 + 165 * j + 2 * j,
                            left + 1 + 170 * i + 17 * i,
                            top + 5 + 165 * (j + 1) + 2 * j,
                            left + 1 + 170 * (i + 1) + 17 * i);
                    position.resieze((float) this.width / (float) 1440);
                    if (Position.inPosition(x, y, position)) {
                        selectFlag = j * 5 + i;
                        selectFlagX = i;
                        selectFlagY = j;
                        return 1;
                    }
                }
            }
        }

        Log.v(this.getClass().getName(), "大键盘返回0");
        return 0;
    }

    /**
     * 检测大矩阵
     * 0. 按到本方格时，进入阶段0
     * 1. 按到其他方格时，进入阶段2，重新绘制bigselected
     * 2. 按到其他区域，进入阶段1
     * @param x
     * @param y
     * @return
     */
    private int touchBigSelected(int x, int y) {
        Position position;
        int left = this.left - 100;
        int top = this.top;

        // 检测是否PASS
        position = new Position(top + 1177,
                left + 1,
                top + 1177 + 163,
                left + 1 + 920);
        position.resieze((float)this.width / (float)1440);
        if (Position.inPosition(x, y, position)) {
            return 0;
        }

        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j * 5 + i) > lastCallCard) {
                    if ((j * 5 + i) == selectFlag) {
                        position = new Position(top + 5 + 165 * j + 2 * j - 15,
                                left + 1 + 170 * i + 17 * i - 15,
                                top + 5 + 165 * (j + 1) + 2 * j + 15,
                                left + 1 + 170 * (i + 1) + 17 * i + 15);
                        position.resieze((float)this.width / (float)1440);
                        Log.v(this.getClass().getName(), "触摸本方块");
                        if (Position.inPosition(x, y, position)) {
                            selectFlag = -1;
                            selectFlagX = -1;
                            selectFlagY = -1;

                            Log.v(this.getClass().getName(),"更新lastCallCard");
                            // 这里是更新lastCallCard的操作
                            // 如何识别lastCallCard更新了
                            lastCallCard = j * 5 + i;
                            Log.v(this.getClass().getName(), "返回0");
                            return 0;
                        }
                    } else {
                        position = new Position(top + 5 + 165 * j + 2 * j,
                                left + 1 + 170 * i + 17 * i,
                                top + 5 + 165 * (j + 1) + 2 * j,
                                left + 1 + 170 * (i + 1) + 17 * i);
                        position.resieze((float)this.width / (float)1440);
                        if (Position.inPosition(x, y, position)) {
                            selectFlag = j * 5 + i;
                            selectFlagX = i;
                            selectFlagY = j;
                            Log.v(this.getClass().getName(), "返回2");
                            return 2;
                        }
                    }
                }
            }
        }
        Log.v(this.getClass().getName(), "返回1");
        return 1;
    }

    /**
     * 测试
     * @param canvas
     */
    public void drawTest(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        canvas.drawRect(this.left, this.top,
                this.left+720, this.top+720, paint);
    }

    /**
     * 绘制叫牌矩阵
     * @param canvas
     */
    public void draw(Canvas canvas) {
//        drawTest(canvas);
        switch (callStage) {
            case 0:
                drawSmall(canvas);
                drawHistory(canvas);
                break;
            case 1:
                drawBig(canvas);
                break;
            case 2:
                drawBigSelected(canvas);
                break;
            default:
                drawSmall(canvas);
        }
    }

    /**
     * 绘制小矩阵（带边框）
     * @param canvas
     */
    private void drawSmall(Canvas canvas) {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();

        int left = this.left;
        int top = this.top;

        paint.setColor(Color.WHITE);
        canvas.drawLine(0, 0, 1440, 0, paint);
        canvas.drawLine(0, 360, 1440, 360, paint);
        canvas.drawLine(0, 1800, 1440, 1800, paint);
        canvas.drawLine(0, 2160, 1440, 2160, paint);

        // 绘制底版
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        RectF round = new RectF();
        round.left = left - 150;
        round.right = left + 720 + 150;
        round.top = top;
        // 1144 = 134*7 + 8*6 + 134 + 8*3
        round.bottom = top + 1144 + 200;
        canvas.drawRoundRect(round, 20, 20, paint);

        // 绘制线条
        paint.setColor(Color.parseColor("#408030"));
        paint.setStrokeWidth(8);
        canvas.drawLine(left - 150, top,
                left, top + 100, paint);
        canvas.drawLine(left + 720 + 150, top,
                left + 720, top + 100, paint);
        canvas.drawLine(left - 150, top + 1144 + 200,
                left, top + 1144 + 100, paint);
        canvas.drawLine(left + 720 + 150, top + 1144 + 200,
                left + 720, top + 1144 + 100, paint);

        paint.setStrokeWidth(8);
        canvas.drawLine(left, top + 100,
                left + 720, top + 100, paint);
        canvas.drawLine(left + 720, top + 100,
                left + 720, top + 1144 + 100, paint);
        canvas.drawLine(left + 720, top + 1144 + 100,
                left, top + 1144 + 100, paint);
        canvas.drawLine(left, top + 1144 + 100,
                left, top + 100, paint);

        // 绘制文字
        paint.setTextSize(80);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("北", left + 50, top + 80, paint);
        canvas.drawText("东", left + 720 + 50, top + 180, paint);
        canvas.drawText("南", left + 680, top + 1160 + 200 - 35, paint);
        canvas.drawText("西", left + 0 - 50, top + 1160 + 70, paint);

        // 重新设置left和top
        left = this.left;
        top = this.top + 100;

        // 绘制选择矩阵
        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j*5+i)>lastCallCard) {
                    Image = BitmapFactory.decodeResource(context.getResources(),
                            CardImage.resImages[j * 5 + i]);
                    des.set(left + 9 + 134 * i + 8 * i,
                            top + 8 + 134 * j + 8 * j,
                            left + 9 + 134 * (i + 1) + 8 * i,
                            top + 8 + 134 * (j + 1) + 8 * j);
                    canvas.drawBitmap(Image, null, des, paint);
                    Image = null;
                }
            }
        }

        // 绘制PASS按钮
        // 134*7 + 8*6 + 16 = 1002
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pass);
        des.set(left + 9, top + 8 + 8 + 986,
                left + 720 - 9, top + 8 + 8 + 986 + 134);
        canvas.drawBitmap(Image, null, des, paint);
    }

    /**
     * 绘制大矩阵
     * @param canvas
     */
    private void drawBig(Canvas canvas) {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();

        int left = this.left;
        int top = this.top;

        paint.setColor(Color.WHITE);
        canvas.drawLine(0, 0, 1440, 0, paint);
        canvas.drawLine(0, 360, 1440, 360, paint);
        canvas.drawLine(0, 1800, 1440, 1800, paint);
        canvas.drawLine(0, 2160, 1440, 2160, paint);

        // 绘制底版
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        RectF round = new RectF();
        round.left = left - 150;
        round.right = left + 720 + 150;
        round.top = top;
        // 1144 = 134*7 + 8*6 + 134 + 8*3
        round.bottom = top + 1144 + 200;
        canvas.drawRoundRect(round, 20, 20, paint);

        // 重新修订width的宽度
        left = this.left - 100;
        top = this.top;

        // 绘制选择矩阵
        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j * 5 + i)>lastCallCard) {
                    Image = BitmapFactory.decodeResource(context.getResources(),
                            CardImage.resImages[j * 5 + i]);
                    des.set(left + 1 + 170 * i + 17 * i,
                            top + 5 + 165 * j + 2 * j,
                            left + 1 + 170 * (i + 1) + 17 * i,
                            top + 5 + 165 * (j + 1) + 2 * j);
                    canvas.drawBitmap(Image, null, des, paint);
                    Image = null;
                }
            }
        }

        // 绘制pass
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pass);
        des.set(left + 1, top + 1177, left + 1 + 920, top + 1177 + 163);
        canvas.drawBitmap(Image, null, des, paint);
    }

    /**
     * 绘制大矩阵（选中的）
     * @param canvas
     */
    private void drawBigSelected(Canvas canvas) {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();

        int left = this.left;
        int top = this.top;

        paint.setColor(Color.WHITE);
        canvas.drawLine(0, 0, 1440, 0, paint);
        canvas.drawLine(0, 360, 1440, 360, paint);
        canvas.drawLine(0, 1800, 1440, 1800, paint);
        canvas.drawLine(0, 2160, 1440, 2160, paint);

        // 绘制底版
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        RectF round = new RectF();
        round.left = left - 150;
        round.right = left + 720 + 150;
        round.top = top;
        // 1144 = 134*7 + 8*6 + 134 + 8*3
        round.bottom = top + 1144 + 200;
        canvas.drawRoundRect(round, 20, 20, paint);

        // 重新修订width的宽度
        left = this.left - 100;
        top = this.top;

        // 绘制选择矩阵
        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j * 5 + i)>lastCallCard) {
                    Image = BitmapFactory.decodeResource(context.getResources(),
                            CardImage.resImages[j * 5 + i]);
                    des.set(left + 1 + 170 * i + 17 * i,
                            top + 5 + 165 * j + 2 * j,
                            left + 1 + 170 * (i + 1) + 17 * i,
                            top + 5 + 165 * (j + 1) + 2 * j);
                    canvas.drawBitmap(Image, null, des, paint);
                    Image = null;
                }
            }
        }

        // 绘制pass
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pass);
        des.set(left + 1, top + 1177, left + 1 + 920, top + 1177 + 163);
        canvas.drawBitmap(Image, null, des, paint);

        // 绘制大卡片
        // TODO:这里肯定是要修改的，但在修改这里之前，必须先确认之前的触摸逻辑没有错误。
        // TODO:解决了，是touchBig没有做lastCallCard的校验引起的
        if ((selectFlagX!=-1 && selectFlagY!=-1)) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[selectFlagY * 5 + selectFlagX]);
            des.set(left + 1 + 170 * selectFlagX + 17 * selectFlagX - 15,
                    top + 5 + 165 * selectFlagY + 2 * selectFlagY - 15,
                    left + 1 + 170 * (selectFlagX + 1) + 17 * selectFlagX + 15,
                    top + 5 + 165 * (selectFlagY + 1) + 2 * selectFlagY + 15);
            canvas.drawBitmap(Image, null, des, paint);
        }
    }

    /**
     * 绘制叫牌历史
     * 每个玩家保留一项
     * @param canvas
     */
    public void drawHistory(Canvas canvas) {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();

        int left = this.left;
        int top = this.top;

        Log.v(this.getClass().getName(), "绘制历史");
        Log.v(this.getClass().getName(), String.valueOf(callHistoryN));
        if (callHistoryN != -1) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[callHistoryN]);
            des.set(left + 130,
                    top,
                    left + 130 + 100,
                    top + 100);
            canvas.drawBitmap(Image, null, des, paint);
        }
        if (callHistoryE != -1) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[callHistoryE]);
            des.set(left + 730,
                    top + 220,
                    left + 730 + 100,
                    top + 220 + 100);
            canvas.drawBitmap(Image, null, des, paint);
        }
        if (callHistoryS != -1) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[callHistoryS]);
            des.set(left + 610 - 100,
                    top + 1144 + 100,
                    left + 610 - 100 + 100,
                    top + 1144 + 200);
            canvas.drawBitmap(Image, null, des, paint);
        }
        if (callHistoryW != -1) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[callHistoryW]);
            des.set(left - 110,
                    top + 1144 - 100,
                    left - 110 + 100,
                    top + 1144);
            canvas.drawBitmap(Image, null, des, paint);
        }
    }

}
