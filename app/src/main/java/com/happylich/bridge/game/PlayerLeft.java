package com.happylich.bridge.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.happylich.bridge.R;

/**
 * Created by wangt on 2017/11/16.
 */

public class PlayerLeft extends AbstractPlayer {
    public PlayerLeft(int id, int[] cards, int left, int top, boolean side, Manager manager, Context context) {
        super(id, cards, left, top, side, manager, context);
    }
    @Override
    public void paint0(Canvas canvas) {
        Bitmap Image;
        Paint paint = new Paint();
        Rect des = new Rect();
        // 绘制纸牌
        if (side == true) {
            top = (1100 - 160 - (cards.length-1)*35)/2+10;
            for (int i=0; i<cards.length; i++) {
                Image = BitmapFactory.decodeResource(context.getResources(), CardImage.cardImages[cards[i]]);
                des.set(left, top+i*35, left+120, top+160+i*35);
                canvas.drawBitmap(Image, null, des, paint);
                Image = null;
            }
        } else {
            Image = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_backface);
            top = (1100 - 160 - (cards.length-1)*35)/2+10;
            for (int i=0; i<cards.length; i++) {
                des.set(left, top+i*35, left+120, top+160+i*35);
                canvas.drawBitmap(Image, null, des, paint);
            }
            Image = null;
        }
    }
    @Override
    public void paint1(Canvas canvas) {}
    @Override
    public void paint2(Canvas canvas) {}
    @Override
    public void paint3(Canvas canvas) {}
    @Override
    public void paint4(Canvas canvas) {}
}
