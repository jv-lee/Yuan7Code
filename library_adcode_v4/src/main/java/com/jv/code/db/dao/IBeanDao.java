package com.jv.code.db.dao;


import com.jv.code.bean.BBean;

import java.util.List;


/**
 * Created by jv on 2016/9/28.
 */

public interface IBeanDao {

    void save(List<BBean> datas);

    void delete(int onid);

    BBean findByCurr(int type);

    int getCount(int type);

}
