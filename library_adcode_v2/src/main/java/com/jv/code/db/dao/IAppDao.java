package com.jv.code.db.dao;

import java.util.List;

import com.jv.code.bean.AppBean;

public interface IAppDao {
	
	public List<AppBean> findAll();
	
	public void delete(int noId);
	
	public void insert(AppBean appBean);

	public void deleteByPackageName(String packageName);
	
}
