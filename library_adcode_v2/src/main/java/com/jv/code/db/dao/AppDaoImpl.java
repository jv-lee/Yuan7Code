package com.jv.code.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.jv.code.bean.AppBean;
import com.jv.code.db.DBHelper;

import android.content.Context;
import android.database.Cursor;

public class AppDaoImpl implements IAppDao{
	
	private DBHelper db;
	
    public AppDaoImpl(Context context) {
        this.db = DBHelper.getInstance(context);
    }
	
	@Override
	public List<AppBean> findAll() {
		List<AppBean> appBeans = new ArrayList<>();
		
		Cursor cursor = db.getReadableDatabase().rawQuery("select * from apptable", null);
		
		while(cursor.moveToNext()){
			
			AppBean bean = new AppBean();
			bean.setNoid(cursor.getInt(cursor.getColumnIndex("noid")));
			bean.setId(cursor.getString(cursor.getColumnIndex("id")));
			bean.setPackageName(cursor.getString(cursor.getColumnIndex("packageName")));
			bean.setSendRecordId(cursor.getString(cursor.getColumnIndex("sendRecordId")));
			appBeans.add(bean);
		}
		
		return appBeans;
	}

	@Override
	public void delete(int noId) {
		db.getReadableDatabase().execSQL("delete from apptable where noid = ?",new Object[]{noId});
	}

	
	@Override
	public void deleteByPackageName(String packageName) {
		db.getReadableDatabase().execSQL("delete from apptable where packageName = ?",new Object[]{packageName});
	}
	
	@Override
	public void insert(AppBean appBean) {
		db.getReadableDatabase().execSQL("insert into apptable values(null,?,?,?)", new Object[]{appBean.getId(),appBean.getPackageName(),appBean.getSendRecordId()});
	}

}
