package com.happylich.bridge.game.framework.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.game.framework.res.CardImage;

import java.util.ArrayList;

/**
 * 抽象玩家
 * 1. 机器人
 * 2. 真人玩家
 * 3. 远程玩家
 * Created by wangt on 2017/11/16.
 */

public abstract class AbstractPlayer {

    // 玩家ID:0-3（出牌顺序？）
    protected int playerId;
    // 玩家持有的牌
    protected int[] cards;
    // 玩家的叫牌列表
    protected ArrayList<Integer> calls = new ArrayList<>();
    // 当前选中的牌
    protected int selectCard = -1;

    // 玩家状态（根据状态采取不同的绘制策略）
    // 000表示叫牌阶段：玩家+叫牌
    // 100表示调整座位：
    // 200：玩家+桌面
    // 200：不绘制
    // 201：下方，向上
    // 202：下方，向下
    // 211：上方，向上
    // 212：上方，向下
    // 221：左方，向上
    // 222：左方，向下
    // 231：右方，向上
    // 232：右方，向下
    // 3表示结算：结算
    protected int stage;
    protected int width, height;
    protected int top, left;
    protected Context context;

    protected AbstractPlayer last;
    protected AbstractPlayer next;

    /**
     * 构造函数
     */
    public AbstractPlayer() {
    }

    /**
     * 构造函数（实际上并没有用，要被覆盖掉的）
     * @param id
     * @param cards
     */
//    public AbstractPlayer(int id,
//                          int[] cards) {
//        this.stage = 200;
//        this.playerId = id;
//        this.cards = cards;
//    }

    /**
     * 设置上家和下家
     * @param last
     * @param next
     */
    public void setLastAndNext(AbstractPlayer last, AbstractPlayer next) {
        this.last = last;
        this.next = next;
    }

    /**
     * 设置绘图模式
     * @param stage
     */
    public void setStage(int stage) {
        this.stage = stage;
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
     * 绘图函数
     * @param canvas
     */
    public void draw(Canvas canvas) {
        switch(stage) {
            case 200:
                // do-nothing
            case 201:
//                Log.v(this.getClass().getName(), "201");
                paintBottomUp(canvas);
//                Log.v(this.getClass().getName(), "---");
            case 202:
                paintBottomDown(canvas);
            case 211:
                paintTopUp(canvas);
            case 212:
                paintTopDown(canvas);
            case 221:
                paintLeftUp(canvas);
            case 222:
                paintLeftDown(canvas);
            case 231:
                paintRightUp(canvas);
            case 232:
                paintRightDown(canvas);
            default:
                // do-nothing
        }
    }
    private void paintBottomUp(Canvas canvas) {
        Bitmap Image;
        Paint paint = new Paint();
        Rect des = new Rect();

        // 虽然规定了left，但是并不采用，实际情况下还是根据width重新绘制
        left = (1440 - (cards.length - 1) * 90 - 180) / 2;

        // 绘制纸牌（底部玩家）
        for (int i=0; i<cards.length; i++) {
            Image = BitmapFactory.decodeResource(context.getResources(), CardImage.cardImages[cards[i]]);
            if ((selectCard != -1) && (cards[i]/13 == cards[selectCard]/13)) {
                des.set(left + i * 90, top - 120, left + 180 + i * 90, top + 120);
            } else {
                des.set(left + i * 90, top, left + 180 + i * 90, top + 240);
            }
            canvas.drawBitmap(Image,null, des, paint);
            Image = null;
        }

//        paint.setColor(Color.BLUE);
//        paint.setTextSize(100);
//        canvas.drawText(String.valueOf(this.left), 0, 100, paint);
//        canvas.drawText(String.valueOf(this.top), 0, 200, paint);
//        canvas.drawText(String.valueOf(this.width), 0, 100, paint);
//        canvas.drawText(String.valueOf(this.height), 0, 200, paint);
//        canvas.drawLine(0, 2160, 1440, 2160, paint);
    }
    private void paintBottomDown(Canvas canvas) {

    }
    private void paintTopUp(Canvas canvas) {

    }
    private void paintTopDown(Canvas canvas) {

    }
    private void paintLeftUp(Canvas canvas) {

    }
    private void paintLeftDown(Canvas canvas) {

    }
    private void paintRightUp(Canvas canvas) {

    }
    private void paintRightDown(Canvas canvas) {

    }

    /**
     * 键盘输入
     * 键盘输入只有对于本地人类玩家有效
     * 当玩家是机器人或者远程玩家时不再有效
     * @param x
     * @param y
     */
    // TODO：选牌，重选
//    public void onTouch(int x, int y) {
//        Log.d("时间调优：", "Players.onTouch");
//        for (int i=0; i<cards.length; i++) {
//            if (i != cards.length - 1) {
//                // 判定区域
//                if (inRect(x, y, left+i*35, top,35, 160)) {
//                    // 重绘
//                    Log.d("坐标", String.valueOf(i));
//                    selectCard = i;
//                    break;
//                } selectCard = -1;
//            } else {
//                // 判定区域
//                if (inRect(x, y, left+i*35, top,120, 160)) {
//                    // 重绘
//                    selectCard = i;
//                    break;
//                } else {
//                    selectCard = -1;
//                }
//            }
//        }
//    }


}
