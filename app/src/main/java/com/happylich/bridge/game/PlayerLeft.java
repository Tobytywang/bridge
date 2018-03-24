package com.happylich.bridge.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by wangt on 2017/11/16.
 */

public class PlayerLeft extends AbstractPlayer {
    public PlayerLeft(int id, int[] cards, int left, int top, boolean side, Game game, Context context) {
        super(id, cards, left, top, side, game, context);
    }
    @Override
    public void paint0(Canvas canvas) {
        if (calls.size() > 0) {
            paint1(canvas);
        }
    }
    @Override
    public void paint1(Canvas canvas) {
        Bitmap Image;
        Paint paint = new Paint();
        Rect des = new Rect();
        int left = 95;
        int top = 295;
        for (int i=0; i<calls.size(); i++) {
            Image = BitmapFactory.decodeResource(context.getResources(), CardImage.callImage[calls.get(i)]);
            des.set(left , top, left+35, top + 20);
            canvas.drawBitmap(Image, null, des, paint);
            Image = null;
        }
    }
    @Override
    public void paint2(Canvas canvas) {}
    @Override
    public void paint3(Canvas canvas) {}
    @Override
    public void paint4(Canvas canvas) {}
}
