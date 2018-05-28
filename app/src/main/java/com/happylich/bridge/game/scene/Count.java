package com.happylich.bridge.game.scene;

/**
 * Created by lich on 2018/5/27.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.engine.util.Position;
import com.happylich.bridge.game.database.DBHelper;
import com.happylich.bridge.game.res.CardImage;

import java.util.ArrayList;

/**
 * 计分类
 *
 * 0. 游戏类型
 * 1. 玩家所在的位置
 * 2. 各个玩家的手牌
 * 3. 庄家和定约
 * 4. 赢墩数
 * 5. 双方的得分
 */

public class Count extends AbstractScene {

    private DBHelper mDBHelper;

    public  String id;
    private int gameType;
    private int playerDirection;
    private String cardS = "";
    private String cardW = "";
    private String cardN = "";
    private String cardE = "";
    private int banker;
    private int contract;
    private int win;
    private int trickNS;
    private int trickWE;
    private int scoreNS;
    private int scoreWE;
    private int IMPNS;
    private int IMPWE;

    public Count(Context context) {
        this.context = context;
        Log.v(this.getClass().getName(), "新建DBHelper");
        mDBHelper = new DBHelper(context,"history.db",null,1);
    }
    public Count(String id, int gameType, int playerDirection, int banker, int contract) {
        this.id = id;
        this.gameType = gameType;
        this.playerDirection = playerDirection;
        this.banker = banker;
        this.contract = contract;
    }

    public void saveGame() {
        Log.v(this.getClass().getName(), "开始创建数据表");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = db.query("history", null, null, null, null, null, null);

//        if (count >= 100) {
//            cursor.moveToFirst();
//            int id = cursor.getInt();
//            db.delete("");
//        }

        ContentValues values = new ContentValues();
//        values.put("id", null);
        values.put("game_type", this.gameType);
        values.put("player_direction", this.playerDirection);
        values.put("cardsS", this.cardS);
        values.put("cardsW", this.cardW);
        values.put("cardsN", this.cardN);
        values.put("cardsE", this.cardE);
        values.put("banker", this.banker);
        values.put("contract", this.contract);
        values.put("win", 1);
        values.put("trickNS", 12);
        values.put("trickWE", 1);
        values.put("scoreNS", 12);
        values.put("scoreWE", 1);
        values.put("IMPNS", 12);
        values.put("IMPEW", 1);
        Log.v(this.getClass().getName(), "cardS" + String.valueOf(cardS));
        Log.v(this.getClass().getName(), "cardW" + String.valueOf(cardW));
        Log.v(this.getClass().getName(), "cardN" + String.valueOf(cardN));
        Log.v(this.getClass().getName(), "cardE" + String.valueOf(cardE));
        Log.v(this.getClass().getName(), "gameType" + String.valueOf(gameType));
        Log.v(this.getClass().getName(), "playerDirection" + String.valueOf(playerDirection));
        Log.v(this.getClass().getName(), "banker" + String.valueOf(banker));
        Log.v(this.getClass().getName(), "contract" + String.valueOf(contract));

        db.insert("history", null, values);

        cursor = db.query("history", null, null, null, null, null, null);
        Log.v(this.getClass().getName(), "查到" + String.valueOf(cursor.getCount()) + "条数据");
        Log.v(this.getClass().getName(), "添加成功");
    }


    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public int getGameType() {
        return this.gameType;
    }


    public void setPlayerDirection(int playerDirection) {
        this.playerDirection = playerDirection;
    }

    public int getPlayerDirection() {
        return this.playerDirection;
    }


    public void setCard(int direction, ArrayList<Integer> cards) {
        switch (direction) {
            case 0:
                for (Integer card:cards) {
                    if (card < 9) {
                        cardS += "0" + String.valueOf(card);
                    } else {
                        cardS += String.valueOf(card);
                    }
                }
                break;
            case 1:
                for (Integer card:cards) {
                    if (card < 9) {
                        cardW += "0" + String.valueOf(card);
                    } else {
                        cardW += String.valueOf(card);
                    }
                }
                break;
            case 2:
                for (Integer card:cards) {
                    if (card < 9) {
                        cardN += "0" + String.valueOf(card);
                    } else {
                        cardN += String.valueOf(card);
                    }
                }
                break;
            case 3:
                for (Integer card:cards) {
                    if (card < 9) {
                        cardE += "0" + String.valueOf(card);
                    } else {
                        cardE += String.valueOf(card);
                    }
                }
                break;
        }
    }

    public void setBanker(int banker) {
        this.banker = banker;
    }

    public int getBanker() {
        return this.banker;
    }


    public void setContract(int contract) {
        this.contract = contract;
    }

    public int getContract() {
        return this.contract;
    }


    public void setScoreNS(int scoreNS) {
        this.scoreNS = scoreNS;
    }

    public void setScoreWE(int scoreWE) {
        this.scoreWE = scoreWE;
    }

    public void setIMPNS(int IMPNS) {
        this.IMPNS = IMPNS;
    }

    public void setIMPWE(int IMPWE) {
        this.IMPWE = IMPWE;
    }

    public int onTouch(int x, int y) {
        int left = this.left;
        int top = this.top;

        Position position1 = new Position(1550, 150,
                1550 + 152, 250 + 350);
        position1.resieze((float)this.width / (float)1440);
        if (Position.inPosition(x, y, position1)) {
            return 1;
        }

        Position position2 = new Position(1550, 850,
                1550 + 152, 850 + 350);
        position2.resieze((float)this.width / (float)1440);
        if (Position.inPosition(x, y, position2)) {
            return 2;
        }
        return 0;
    }

    public void draw(Canvas canvas, Paint paint, Rect des) {
        // 需要绘制1. 庄家 2. 定约 得分
        Bitmap Image;
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);

        int left = this.left;
        int top = this.top;

        if (banker == 0 || banker == 2) {
            canvas.drawText("庄家: NS", 220, 500, paint);
        } else {

            canvas.drawText("庄家: WE", 220, 500, paint);
        }

        if (contract%5 == 0) {
            canvas.drawText("定约: " + (contract/5+1) + "C", 850, 500, paint);
        } else if (contract%5 == 1) {
            canvas.drawText("定约: " + (contract/5+1) + "D", 850, 500, paint);
        } else if (contract%5 == 2) {
            canvas.drawText("定约: " + (contract/5+1) + "H", 850, 500, paint);
        } else if (contract%5 == 3) {
            canvas.drawText("定约: " + (contract/5+1) + "S", 850, 500, paint);
        } else if (contract%5 == 4) {
            canvas.drawText("定约: " + (contract/5+1) + "NT", 850, 500, paint);
        }

        canvas.drawText("赢墩", 630, 650, paint);
        canvas.drawText("南北: " + this.game.getTable().getTricksNS(), 220, 800, paint);
        canvas.drawText("东西: " + this.game.getTable().getTricksWE(), 850, 800, paint);

//        canvas.drawText("计分", 650, 950, paint);
//        canvas.drawText("南北: " + this.game.getTable().getTricksNS(), 250, 1100, paint);
//        canvas.drawText("东西: " + this.game.getTable().getTricksWE(), 850, 1100, paint);
//
//        canvas.drawText("IMP", 650, 1250, paint);
//        canvas.drawText("南北: " + this.game.getTable().getTricksNS(), 250, 1400, paint);
//        canvas.drawText("东西: " + this.game.getTable().getTricksWE(), 850, 1400, paint);

        Image = CardImage.buttonBitmapImage;
        des.set(150, 1550, 250 + 350, 1550 + 152);
        canvas.drawBitmap(Image, null, des, paint);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("返回菜单", 380, 1550 + 106, paint);

        Image = CardImage.buttonBitmapImage;
        des.set(850 , 1550, 850 + 450, 1550 + 152);
        canvas.drawBitmap(Image, null, des, paint);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("再来一次", 1080, 1550 + 106, paint);
    }
}
