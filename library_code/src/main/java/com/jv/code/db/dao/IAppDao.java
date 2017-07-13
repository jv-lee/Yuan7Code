package com.jv.code.db.dao;

import com.jv.code.bean.AppBean;

import java.util.List;

public interface IAppDao {
	
	public List<AppBean> findAll();
	
	public void delete(int noId);
	
	public void insert(AppBean appBean);

	public void deleteByPackageName(String packageName);
	
}
