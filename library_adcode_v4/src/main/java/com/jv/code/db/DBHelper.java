package com.jv.code.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by jv on 2016/9/28.
 */

public class DBHelper extends SQLiteOpenHelper {

    //单列模式数据库对象
    public volatile static DBHelper instance;

    private DBHelper(Context context) {
        //将参数写死  打开数据库, 不开启工厂 , 版本为1
        super(context, "adDateBase", null, 1);
    }

    /**
     * 获取单列模式对象
     */
    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table advertisements(" +
                "noid integer primary key," + //广告id
                "id text," +
                "name text," + //广告名称
                "image text," + //广告图片链接
                "switchMode integer," + //关闭插屏的按钮模式,0为直接关闭，1为直接下载。
                "actionWay integer," + //0表示所有网络形式都可以下载，1表示WiFi时候下载。
                "type text," + //广告类别
                "sendRecordId text," +
                "downloadUrl text," +//记录id 广告状态回报时携带
                "showType integer," +
                "apkName text)");

        db.execSQL("create table apptable(noid integer primary key,"
                + "id text,"
                + "packageName text,"
                + "sendRecordId text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
