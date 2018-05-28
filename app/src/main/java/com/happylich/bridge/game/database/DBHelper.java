package com.happylich.bridge.game.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lich on 2018/5/27.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String CREATE_HISTORY = "create table history ( "
            + " id integer primary key autoincrement,"
            + " game_type text,"
            + " player_direction text,"
            + " cardsS text,"
            + " cardsW text,"
            + " cardsN text,"
            + " cardsE text,"
            + " banker integer,"
            + " contract integer,"
            + " win integer,"
            + " trickNS integer,"
            + " trickWE integer,"
            + " scoreNS integer,"
            + " scoreWE integer,"
            + " IMPNS integer,"
            + " IMPEW integer)";
    private Context context;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库后，对数据库的操作
        Log.v(this.getClass().getName(), "正在创建表");
        db.execSQL(CREATE_HISTORY);
    }

    //当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 更改数据库版本的操作
        db.execSQL("drop table if exists history");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        // 每次打开数据库后首先被执行
        super.onOpen(db);
    }
}
