package com.happylich.bridge.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.happylich.bridge.R;
import com.happylich.bridge.view.MainActivity;

/**
 * Created by wangt on 2017/11/10.
 * Player维护着玩家当前持有的牌的信息
 */

public class Player {
    private Manager manager;
    private int[] cards;
    private int playerId;
    private int top, left;
    private Context context;
    private Bitmap cardImage;
    private boolean paintDirection;
    private boolean[] cardsFlag;
    private Player last;
    private Player next;
    // 构造函数
    public Player(int[] cards, int left, int top, boolean dir, int id, Manager manager, Context context) {
        //
        this.manager = manager;
        this.playerId = id;
        this.cards = cards;
        this.context = context;
//        cardsFlag = new boolean[cards.length];
        this.left = left;
        this.top = top;
        this.paintDirection = dir;

    }

    public void setLastAndNext(Player last, Player next) {
        this.last = last;
        this.next = next;
    }

    // 绘制
    public void paint(Canvas canvas) {
        Rect des = new Rect();
        if (paintDirection == true) {
            // 关键在于left值
            // left=(720-(i-1)*35-120)/2
            left = (720 - 120 - (cards.length-1)*35)/2;
            for (int i=0; i<cards.length; i++) {
                Log.d("\n\n\n", String.valueOf(cards[i]));
                cardImage = BitmapFactory.decodeResource(context.getResources(),
                        CardImage.cardImages[cards[i]]);
                des.set(left+i*35, top, left+120+i*35, top+160);
                canvas.drawBitmap(cardImage,null, des, new Paint());
            }
//            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_1);
//            Rect rect = new Rect(left, top, left+120, top+160);
//            canvas.drawBitmap(bitmap, null, rect, new Paint());
            // 显示剩余牌数
        } else if (paintDirection == false) {
            top = (790 - 160 - (cards.length-1)*26)/2+10;
            for (int i=0; i<cards.length; i++) {
                cardImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_backface);
                des.set(left, top+i*26, left+120, top+160+i*26);
                canvas.drawBitmap(cardImage, null, des, new Paint());
            }
//            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_backface);
//            Rect rect = new Rect(left, top, left+120, top+160);
//            canvas.drawBitmap(bitmap, null, rect, new Paint());
        }
    }

    // 触摸
    public void onTouch(int x, int y) {
        for (int i=0; i<cards.length; i++) {
            if (i != cards.length - 1) {

            } else {

            }
        }
    }
}
