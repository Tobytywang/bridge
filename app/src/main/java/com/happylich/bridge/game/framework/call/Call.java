package com.happylich.bridge.game.framework.call;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.R;
import com.happylich.bridge.engine.util.Position;
import com.happylich.bridge.game.framework.res.CardImage;

/**
 * 绘制叫牌矩阵的类
 * 1. 小
 * 2. 大（未选中）
 * 3. 大（选中）
 * Created by lich on 2018/3/26.
 */

public class Call {
    // 资源
    private Context context;

    // 叫牌矩阵标志位
    private int callFlag;

    // 绘制基准
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
     * 设置callflag
     * @param flag
     */
    public void setCallFlag(int flag) {
        this.callFlag = flag;
    }

    /**
     * 检测按键
     * @param x
     * @param y
     * @return 表示事件类型，0表示无效区域，1表示有效区域
     */
    public int onTouch(int x, int y) {
        Log.v(this.getClass().getName(), "CallFlag");
        Log.v(this.getClass().getName(), String.valueOf(callFlag));
        switch(callFlag) {
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
     * 绘制叫牌矩阵
     * @param canvas
     */
    public void draw(Canvas canvas) {
//        drawTest(canvas);
        switch (callFlag) {
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
     * 小矩阵
     * @param x
     * @param y
     * @return
     */
    private int touchSmall(int x, int y) {
        Position position = new Position(this.top, this.left,
                this.top + 1100, this.left + 720);
        position.resieze((float)this.width / (float)1440);
        Log.v(this.getClass().getName(), String.valueOf(Position.inPosition(x, y, position)));
        if (Position.inPosition(x, y, position)) {
            Log.v(this.getClass().getName(), "Bingo");
            return 1;
        }
        return 0;
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
        int top = this.top + 150;

        // 绘制底版
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        canvas.drawRect(this.left + 0 - 130, this.top,
                this.left + 720 + 130, this.top + 1100 + 320, paint);

        // 绘制选择矩阵
        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j*5+i)>lastCallCard) {
                    Image = BitmapFactory.decodeResource(context.getResources(),
                            CardImage.resImages[j * 5 + i]);
                    des.set(left + 9 + 134 * i + 8 * i,
                            top + 134 * j + 4 * j,
                            left + 9 + 134 * (i + 1) + 8 * i,
                            top + 134 * (j + 1) + 4 * j);
                    canvas.drawBitmap(Image, null, des, paint);
                    Image = null;
                }
            }
        }

        // 绘制PASS按钮
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pass);
        des.set(left + 9 + 5, top + 964, left + 702 + 5, top + 1100);
        canvas.drawBitmap(Image, null, des, paint);

        // 绘制线条
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
//        canvas.drawLine(left + 0 - 120, top - 120,
//                left + 360 - 50, top - 120, paint);
//        canvas.drawLine(left + 360 + 50, top - 120,
//                left + 720 + 120, top - 120, paint);
//
//        canvas.drawLine(left + 720 + 120, top - 120,
//                left + 720 + 120, top + 550 - 50, paint);
//        canvas.drawLine(left + 720 + 120, top + 550 + 50 ,
//                left + 720 + 120, top + 1100 + 120, paint);
//
//        canvas.drawLine(left + 720 + 120, top + 1100 + 120,
//                left + 360  + 50, top + 1100 + 120, paint);
//        canvas.drawLine(left + 360 - 50, top + 1100 + 120,
//                left + 0 - 120, top + 1100 + 120, paint);
//
//        canvas.drawLine(left + 0 - 120, top + 1100 + 120,
//                left + 0 - 120, top + 550 + 50, paint);
//        canvas.drawLine(left + 0 - 120, top + 550 - 50,
//                left + 0 - 120, top - 120, paint);

        // 绘制文字
        paint.setStrokeWidth(3);
        paint.setTextSize(80);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText("北", left + 360, top - 20, paint);
//        canvas.drawText("东", left + 720 + 50, top + 550 + 30, paint);
//        canvas.drawText("南", left + 360, top + 1100 + 80, paint);
//        canvas.drawText("西", left + 0 - 50, top + 550 + 30, paint);
        canvas.drawText("北", left + 50, top - 20, paint);
        canvas.drawText("东", left + 720 + 50, top + 80, paint);
        canvas.drawText("南", left + 680, top + 1100 + 90, paint);
        canvas.drawText("西", left + 0 - 50, top + 1090, paint);
    }

    /**
     * 检测大矩阵
     * @param x
     * @param y
     * @return
     */
    private int touchBig(int x, int y) {
        Position position;
        int left = (1440 - 180 * 5 - 20 * 4) / 2;
        int top = this.top - 10;

        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j * 5 + i) > lastCallCard) {
                    position = new Position(top + 180 * j - 2 * j,
                            left + 180 * i + 20 * i,
                            top + 180 * (j + 1) - 2 * j,
                            left + 180 * (i + 1) + 20 * i);
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
     * 绘制大矩阵
     * @param canvas
     */
    private void drawBig(Canvas canvas) {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();

        // 重新修订width的宽度
        int left = (1440 - 180 * 5 - 20 * 4) / 2;
        int top = this.top - 10;

        // 绘制底版
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        canvas.drawRect(this.left + 0 - 130, this.top,
                this.left + 720 + 130, this.top + 1100 + 320, paint);

        // 绘制选择矩阵
        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j * 5 + i)>lastCallCard) {
                    Image = BitmapFactory.decodeResource(context.getResources(),
                            CardImage.resImages[j * 5 + i]);
                    des.set(left + 180 * i + 20 * i,
                            top + 180 * j - 2 * j,
                            left + 180 * (i + 1) + 20 * i,
                            top + 180 * (j + 1) - 2 * j);
                    canvas.drawBitmap(Image, null, des, paint);
                    Image = null;
                }
            }
        }

        // 绘制pass
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pass);
        des.set(left + 5, top + 1250, left + 980 - 5, top + 1250 + 180);
        canvas.drawBitmap(Image, null, des, paint);
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
        int left = (1440 - 180 * 5 - 20 * 4) / 2;
        int top = this.top - 10;

        // 检测是否PASS
        position = new Position(top + 1250,
                left + 5,
                top + 1250 + 180,
                left + 980 - 5);
        position.resieze((float)this.width / (float)1440);
        if (Position.inPosition(x, y, position)) {
           return 0;
        }

        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j * 5 + i) > lastCallCard) {
                    if ((j * 5 + i) == selectFlag) {
                        position = new Position(top + 180 * j - 2 * j - 15,
                                left + 180 * i + 20 * i - 15,
                                top + 180 * (j + 1) - 2 * j + 15,
                                left + 180 * (i + 1) + 20 * i + 15);
                        position.resieze((float)this.width / (float)1440);
                        Log.v(this.getClass().getName(), "触摸本方块");
                        if (Position.inPosition(x, y, position)) {
                            selectFlag = -1;
                            selectFlagX = -1;
                            selectFlagY = -1;

                            Log.v(this.getClass().getName(),"更新lastCallCard");
                            lastCallCard = j * 5 + i;
                            Log.v(this.getClass().getName(), "返回0");
                            return 0;
                        }
                    } else {
                        position = new Position(top + 180 * j - 2 * j,
                                left + 180 * i + 20 * i,
                                top + 180 * (j + 1) - 2 * j,
                                left + 180 * (i + 1) + 20 * i);
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
     * 绘制大矩阵（选中的）
     * @param canvas
     */
    private void drawBigSelected(Canvas canvas) {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();

        // 重新修订width的宽度
        int left = (1440 - 180 * 5 - 20 * 4) / 2;
        int top = this.top - 10;

        // 绘制底版
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        canvas.drawRect(this.left + 0 - 130, this.top,
                this.left + 720 + 130, this.top + 1100 + 320, paint);

        // 绘制选择矩阵
        for(int j = 0; j < 7; j++) {
            for (int i = 0; i < 5; i++) {
                if ((j * 5 + i) > lastCallCard) {
                    Image = BitmapFactory.decodeResource(context.getResources(),
                            CardImage.resImages[j * 5 + i]);
                    des.set(left + 180 * i + 20 * i,
                            top + 180 * j - 2 * j,
                            left + 180 * (i + 1) + 20 * i,
                            top + 180 * (j + 1) - 2 * j);
                    canvas.drawBitmap(Image, null, des, paint);
                    Image = null;
                }
            }
        }

        // 绘制pass
        Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pass);
        des.set(left + 5, top + 1250, left + 980 - 5, top + 1250 + 180);
        canvas.drawBitmap(Image, null, des, paint);

        // 绘制大卡片
        // TODO:这里肯定是要修改的，但在修改这里之前，必须先确认之前的触摸逻辑没有错误。
        // TODO:解决了，是touchBig没有做lastCallCard的校验引起的
        if ((selectFlagX!=-1 && selectFlagY!=-1)) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[selectFlagY * 5 + selectFlagX]);
            des.set(left + 180 * selectFlagX + 20 * selectFlagX - 15,
                    top + 180 * selectFlagY - 2 * selectFlagY- 15,
                    left + 180 * (selectFlagX + 1) + 20 * selectFlagX + 15,
                    top + 180 * (selectFlagY + 1) - 2 * selectFlagY + 15);
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
        int top = this.top + 150;

        if (callHistoryN != -1) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[callHistoryN]);
            des.set(left + 130,
                    top - 100,
                    left + 130 + 100,
                    top - 100 + 100);
            canvas.drawBitmap(Image, null, des, paint);
        }
        if (callHistoryE != -1) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[callHistoryE]);
            des.set(left + 720,
                    top + 120,
                    left + 720 + 100,
                    top + 120 + 100);
            canvas.drawBitmap(Image, null, des, paint);
        }
        if (callHistoryS != -1) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[callHistoryS]);
            des.set(left + 610 - 100,
                    top + 1100 + 15,
                    left + 610 - 100 + 100,
                    top + 1100 + 15 + 100);
            canvas.drawBitmap(Image, null, des, paint);
        }
        if (callHistoryW != -1) {
            Image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.resImages[callHistoryW]);
            des.set(left - 100,
                    top + 1090 - 200,
                    left - 100 + 100,
                    top + 1090 + 100 - 200);
            canvas.drawBitmap(Image, null, des, paint);
        }
    }

}
