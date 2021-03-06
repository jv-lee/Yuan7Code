package com.jv.code.db.dao;

import android.content.Context;
import android.database.Cursor;

import com.jv.code.bean.BBean;
import com.jv.code.db.DBHelper;

import java.util.List;


/**
 * Created by jv on 2016/9/28.
 */

public class BeanDaoImpl implements IBeanDao {

    private DBHelper db;

    public BeanDaoImpl(Context context) {
        this.db = DBHelper.getInstance(context);
    }

    /**
     * 添加至數據庫
     *
     * @param datas
     */
    @Override
    public void save(List<BBean> datas) {

        if (datas != null) {

            for (BBean bean : datas) {
                db.getReadableDatabase().execSQL("insert into advertisements values(null,?,?,?,?,?,?,?,?,?,?)", new Object[]{bean.getId(), bean.getName(), bean.getImage(), bean.getSwitchMode(), bean.getActionWay(), bean.getType(), bean.getSendRecordId(), bean.getDownloadUrl(), bean.getShowType(), bean.getApkName()});
            }

        }

    }

    /**
     * 展示完進行刪除操作
     *
     * @param noid
     */
    @Override
    public void delete(int noid) {
        db.getReadableDatabase().execSQL("delete from advertisements where noid = ?", new Object[]{noid});
    }

    /**
     * 查詢當前第一條內容
     *
     * @return
     */
    @Override
    public BBean findByCurr(int type) {//where showType = ?
        Cursor cursor = db.getReadableDatabase().rawQuery("select * from advertisements where showType = ? limit 1", new String[]{type + ""});

        BBean bean = null;

        if (cursor != null) {

            while (cursor.moveToNext()) {
                bean = new BBean();
                bean.setNoid(cursor.getInt(cursor.getColumnIndex("noid")));
                bean.setId(cursor.getString(cursor.getColumnIndex("id")));
                bean.setName(cursor.getString(cursor.getColumnIndex("name")));
                bean.setImage(cursor.getString(cursor.getColumnIndex("image")));
                bean.setSwitchMode(cursor.getInt(cursor.getColumnIndex("switchMode")));
                bean.setActionWay(cursor.getInt(cursor.getColumnIndex("actionWay")));
                bean.setDownloadUrl(cursor.getString(cursor.getColumnIndex("downloadUrl")));
                bean.setType(cursor.getString(cursor.getColumnIndex("type")));
                bean.setSendRecordId(cursor.getString(cursor.getColumnIndex("sendRecordId")));
                bean.setApkName(cursor.getString(cursor.getColumnIndex("apkName")));
                bean.setShowType(cursor.getInt(cursor.getColumnIndex("showType")));
            }

        }
        cursor.close();

        return bean;
    }


    /**
     * 获取广告总记录数
     *
     * @return
     */
    @Override
    public int getCount(int type) {

        Cursor cursor = db.getReadableDatabase().rawQuery("select count(*) from advertisements where showType = ?", new String[]{String.valueOf(type)});
        int count = 0;

        if (cursor != null) {

            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }

        }

        return count;
    }

}
