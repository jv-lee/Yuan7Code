package com.jv.code.db.dao;


import com.jv.code.bean.AdBean;

import java.util.List;


/**
 * Created by jv on 2016/9/28.
 */

public interface IAdDao {

    void save(List<AdBean> datas);

    void delete(int onid);

    AdBean findByCurr(int type);

    int getCount(int type);

}
