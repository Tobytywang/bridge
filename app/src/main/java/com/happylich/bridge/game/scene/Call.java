package com.happylich.bridge.game.scene;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.happylich.bridge.engine.util.Position;
import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.res.CardImage;

import java.util.ArrayList;

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
 *    1. call.setPlayerE(playerNumber) playerE.setLastCall:人类玩家 playerE.getLastCall
 *    2. playerNumber.setCall(call) call.getLastCall
 */

public class Call extends AbstractScene {
    // 叫牌阶段标志位
    private int callStage;

    // 叫牌阶段完成标志位
    private boolean finish;

    // 庄家
    private int dealer = -1;
    private AbstractPlayer dealerPlayer = null;

    // 领牌人（本轮首次出牌的）
    private int lead = -1;
    // 定约阶
    private int level = -1;
    private int suits = -1;

    // 选牌标志
    private int selectFlag = -1;
    private int selectFlagX = -1;
    private int selectFlagY = -1;

    // 叫牌历史
    private int lastCallCard = -1;
    private ArrayList<Integer> callHistory  = new ArrayList<>();
    private ArrayList<Integer> callHistoryN = new ArrayList<>();
    private ArrayList<Integer> callHistoryE = new ArrayList<>();
    private ArrayList<Integer> callHistoryS = new ArrayList<>();
    private ArrayList<Integer> callHistoryW = new ArrayList<>();

    private ArrayList<Integer> callHistoryLocal = callHistoryS;

    // 叫牌矩阵（0表示有,1表示空）
    private int[][] calls = new int[5][7];


    /**
     * 构造函数
     */
    public Call(Context context) {
        this.context = context;
    }


    /**
     * 设置叫牌的结束标志为真
     * @return
     */
    public void finish() {
        // 设置庄家（需要获得最后叫牌的玩家）

        // 设置定约（需要获得最后的非pass牌）

        finish = true;
    }

    /**
     * 判断是否满足结束叫牌条件
     */
    public boolean isFinish() {
        if (getCallHistory().size() >=3) {
            if (getCallHistory().get(getCallHistory().size() - 1) == 35 &&
                    getCallHistory().get(getCallHistory().size() - 2) == 35 &&
                    getCallHistory().get(getCallHistory().size() - 3) == 35) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回庄家
     * @return
     */
    public AbstractPlayer getDealer() {
//        if (this.dealer == 0 || this.dealer == 1 ||
//                this.dealer == 2 || this.dealer == 3) {
//            return this.dealer;
//        }
//        return -1;
        return dealerPlayer;
    }

    /**
     * 获取定约level
     * @return
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * 获得定约花色
     * @return
     */
    public int getSuits() {
        return this.suits;
    }

    /**
     * 设置callstage
     * @param stage
     */
    public void setCallStage(int stage) {
        this.callStage = stage;
    }


    /**
     * 获取叫牌历史（全局）
     */
    public ArrayList<Integer> getCallHistory() {
        return callHistory;
    }

    /**
     * 获得上一次的叫牌点
     */
    public int getLastCallCard() {
        return lastCallCard;
    }

    /**
     * 设置叫牌值
     * @param drawPosition
     * @param callCard
     */
    public void setCall(int drawPosition, int callCard) {
        if (callCard < 35) {
            this.lastCallCard = callCard;
            this.suits = callCard % 5;
            this.level = callCard / 5;
        }
        this.callHistory.add(callCard);
        switch (drawPosition) {
            case 0:
                this.callHistoryS.add(callCard);
                if (callCard < 35) {
                    this.dealer = drawPosition;
                    this.dealerPlayer = playerBottom;
                }
                break;
            case 1:
                this.callHistoryW.add(callCard);
                if (callCard < 35) {
                    this.dealer = drawPosition;
                    this.dealerPlayer = playerLeft;
                }
                break;
            case 2:
                this.callHistoryN.add(callCard);
                if (callCard < 35) {
                    this.dealer = drawPosition;
                    this.dealerPlayer = playerTop;
                }
                break;
            case 3:
                this.callHistoryE.add(callCard);
                if (callCard < 35) {
                    this.dealer = drawPosition;
                    this.dealerPlayer = playerRight;
                }
                break;
        }
    }


    /**
     * 检测按键（这个只有本地玩家有）
     * 在特定阶段被game.call调用
     * @param x
     * @param y
     * @return 表示事件类型，0表示无效区域，1表示有效区域
     */
    public int onTouch(int x, int y) {
        int touch;
        switch(callStage) {
            case 00:
            case 10:
                touch = touchSmall(x, y);
                if (touch == 1) {
                    return 1;
                } else {
                    return 0;
                }
            case 11:
                return 0;
            case 20:
                touch = touchBig(x, y);
                if (touch == 2) {
                    return 2;
                } else if(touch == 3){
                    return 3;
                } else {
                    return 0;
                }
            case 30:
                touch = touchBigSelected(x, y);
                if (touch == 0) {
                    return 0;
                } else if (touch == 2) {
                    return 2;
                } else if (touch == 3) {
                    return 3;
                } else {
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
        if (Position.inPosition(x, y, position)) {
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

        // 检测是否PASS（PASS进入阶段0）
        position = new Position(top + 1177,
                left + 1,
                top + 1177 + 163,
                left + 1 + 920);
        position.resieze((float)this.width / (float)1440);
        if (Position.inPosition(x, y, position)) {
            setCall(0,35);
            return 3;
        }

        // 检测是否触摸一个方块（进入阶段2）
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
                        return 2;
                    }
                }
            }
        }

        // 触摸其他区域进入阶段0
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

        // 检测是否PASS（PASS进入阶段0）
        position = new Position(top + 1177,
                left + 1,
                top + 1177 + 163,
                left + 1 + 920);
        position.resieze((float)this.width / (float)1440);
        if (Position.inPosition(x, y, position)) {
            setCall(0,35);
            return 3;
        }

        // 检测是否触摸方块（有效方块进入阶段0， 无效方块重复阶段2）
        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j * 5 + i) > lastCallCard) {
                    if ((j * 5 + i) == selectFlag) {
                        position = new Position(top + 5 + 165 * j + 2 * j - 15,
                                left + 1 + 170 * i + 17 * i - 15,
                                top + 5 + 165 * (j + 1) + 2 * j + 15,
                                left + 1 + 170 * (i + 1) + 17 * i + 15);
                        position.resieze((float)this.width / (float)1440);
                        if (Position.inPosition(x, y, position)) {
                            selectFlag = -1;
                            selectFlagX = -1;
                            selectFlagY = -1;
                            setCall(0, j * 5 + i);
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
                            return 2;
                        }
                    }
                }
            }
        }

        // 否则返回阶段1
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
     * 当轮到非本地玩家是时，画灰色
     * @param canvas
     */
    public void draw(Canvas canvas, Paint paint, Rect rect) {
//        drawTest(canvas);
        switch (callStage) {
            case 0:
            case 10:
                drawSmall(canvas, paint, rect);
                drawHistory(canvas, paint, rect);
                break;
            case 11:
                drawSmall(canvas, paint, rect);
                drawHistory(canvas, paint, rect);
                drawCover(canvas, paint, rect);
                break;
            case 20:
                drawBig(canvas, paint, rect);
                break;
            case 30:
                drawBigSelected(canvas, paint, rect);
                break;
            default:
                drawSmall(canvas, paint, rect);
                drawHistory(canvas, paint, rect);
                break;
        }
    }

    /**
     * 绘制小矩阵（带边框）
     * @param canvas
     */
    private void drawSmall(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;

        int left = this.left;
        int top = this.top;

        paint.setColor(Color.WHITE);

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

        if (playerTop.direction == 0) {
            canvas.drawText("南", left + 50, top + 80, paint);
            canvas.drawText("西", left + 720 + 50, top + 180, paint);
            canvas.drawText("北", left + 680, top + 1160 + 200 - 35, paint);
            canvas.drawText("东", left + 0 - 50, top + 1160 + 70, paint);
        } else if (playerTop.direction == 1) {
            canvas.drawText("西", left + 50, top + 80, paint);
            canvas.drawText("北", left + 720 + 50, top + 180, paint);
            canvas.drawText("东", left + 680, top + 1160 + 200 - 35, paint);
            canvas.drawText("南", left + 0 - 50, top + 1160 + 70, paint);
        } else if (playerTop.direction == 2) {
            canvas.drawText("北", left + 50, top + 80, paint);
            canvas.drawText("东", left + 720 + 50, top + 180, paint);
            canvas.drawText("南", left + 680, top + 1160 + 200 - 35, paint);
            canvas.drawText("西", left + 0 - 50, top + 1160 + 70, paint);
        } else if (playerTop.direction == 3) {
            canvas.drawText("东", left + 50, top + 80, paint);
            canvas.drawText("南", left + 720 + 50, top + 180, paint);
            canvas.drawText("西", left + 680, top + 1160 + 200 - 35, paint);
            canvas.drawText("北", left + 0 - 50, top + 1160 + 70, paint);
        }

        // 重新设置left和top
        left = this.left;
        top = this.top + 100;

        // 绘制选择矩阵
        for(int j=0; j<7; j++) {
            for (int i=0; i<5; i++) {
                if ((j*5+i)>lastCallCard) {
                    Image = CardImage.callBitmapImages.get(j * 5 + i);
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
        Image = CardImage.passBitmapImage;
        des.set(left + 9, top + 8 + 8 + 986,
                left + 720 - 9, top + 8 + 8 + 986 + 134);
        canvas.drawBitmap(Image, null, des, paint);
    }

    /**
     * 绘制大矩阵
     * @param canvas
     */
    private void drawBig(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;

        int left = this.left;
        int top = this.top;

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
                    Image = CardImage.callBitmapImages.get(j * 5 + i);
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
        Image = CardImage.passBitmapImage;
        des.set(left + 1, top + 1177, left + 1 + 920, top + 1177 + 163);
        canvas.drawBitmap(Image, null, des, paint);
    }

    /**
     * 绘制大矩阵（选中的）
     * @param canvas
     */
    private void drawBigSelected(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;

        int left = this.left;
        int top = this.top;

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
                    Image = CardImage.callBitmapImages.get(j * 5 + i);
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
        Image = CardImage.passBitmapImage;
        des.set(left + 1, top + 1177, left + 1 + 920, top + 1177 + 163);
        canvas.drawBitmap(Image, null, des, paint);

        // 绘制大卡片
        if ((selectFlagX!=-1 && selectFlagY!=-1)) {
            Image = CardImage.callBitmapImages.get(selectFlagY * 5 + selectFlagX);
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
    public void drawHistory(Canvas canvas, Paint paint, Rect des) {
        Bitmap Image;

        int left = this.left;
        int top = this.top;

        for (int i = 0; i < callHistoryS.size(); i++) {
            Image = CardImage.callBitmapImages.get(callHistoryS.get(i));
            des.set(left + 610 - 100 * (i + 1),
                    top + 1144 + 100,
                    left + 610 - 100 * (i + 1) + 100,
                    top + 1144 + 200);
            canvas.drawBitmap(Image, null, des, paint);
        }
        for (int i = 0; i < callHistoryW.size(); i++) {
            Image = CardImage.callBitmapImages.get(callHistoryW.get(i));
            des.set(left - 110,
                    top + 1144 - 100 * (i + 1),
                    left - 110 + 100,
                    top + 1144 - 100 * i);
            canvas.drawBitmap(Image, null, des, paint);
        }
        for (int i = 0; i < callHistoryN.size(); i++) {
            Image = CardImage.callBitmapImages.get(callHistoryN.get(i));
            des.set(left + 110 + 100 * i,
                    top,
                    left + 110 + 100 * (i + 1),
                    top + 100);
            canvas.drawBitmap(Image, null, des, paint);
        }
        for (int i = 0; i < callHistoryE.size(); i++) {
            Image = CardImage.callBitmapImages.get(callHistoryE.get(i));
            des.set(left + 730,
                    top + 220 + 100 * i,
                    left + 730 + 100,
                    top + 220 + 100 * (i + 1));
            canvas.drawBitmap(Image, null, des, paint);
        }
    }

    /**
     * 绘制遮盖
     * @param canvas
     */
    public void drawCover(Canvas canvas, Paint paint, Rect des) {

        int left = this.left;
        int top = this.top;

        paint.setColor(Color.parseColor("#44000000"));
        canvas.drawRect(left, top + 100, left + 720, top + 100 + 1144, paint);
    }
}
