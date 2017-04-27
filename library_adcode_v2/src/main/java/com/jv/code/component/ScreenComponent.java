package com.jv.code.component;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AdDaoImpl;
import com.jv.code.db.dao.IAdDao;
import com.jv.code.net.HttpAdvertisment;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SPHelper;
import com.jv.code.utils.Util;

/**
 * Created by Administrator on 2017/4/9.
 */

public class ScreenComponent {


    private static volatile ScreenComponent mInstance;

    private ScreenComponent() {
    }

    public static ScreenComponent getInstance() {
        if (mInstance == null) {
            synchronized (ScreenComponent.class) {
                if (mInstance == null) {
                    mInstance = new ScreenComponent();
                }
            }
        }
        return mInstance;
    }

    //广告数据库实列声明
    public IAdDao mDao;
    //当前日期
    public String currentDate;
    //当前Time换算值
    public final int TIME_MS = 1000;

    public boolean SCREEN_FLAG = true;

    public void sendScreen() {

        SDKService.screenEnable2 = false;

        int time = (int) SPHelper.get(Constant.SCREEN_FIRST_TIME, 30);
        if (SCREEN_FLAG) {
            SCREEN_FLAG = false;
        } else {
            //获取当前间隔时间 为空的话为第一次初始化该时间 获取正常间隔间隔时间做替补
            time = (int) SPHelper.get(Constant.SCREEN_TIME, SPHelper.get(Constant.SCREEN_SHOW_TIME, 30));
        }

        LogUtil.w("Screen Window is " + time + "秒 -> get request http ad  send window ");

        SDKService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //实列化广告持久层DAO
                mDao = new AdDaoImpl(SDKService.mContext);

                //app每日首次启动，获取最近存储时间
                currentDate = (String) SPHelper.get(Constant.CURRENT_DATE, "FIRST");

                //如果当前日期不是最近日期,就重新保存 当天日期
                if (!currentDate.equals(Util.getDate()) || currentDate.equals("FIRST")) {

                    //保存当前时间
                    SPHelper.save(Constant.CURRENT_DATE, Util.getDate());

                    LogUtil.i("save this DATE :" + Util.getDate());

                }


                //获取插屏广告 当前条数 Constant.SCREEN_AD  =  1
                int screenAdCount = mDao.getCount(0);//获取当前广告长度
                int showLimit = (Integer) SPHelper.get(Constant.SHOW_LIMIT, 5);//获取每天最大显示量
                int timeCount = (Integer) SPHelper.get(Util.getAdShowDate(), 0);//当天已显示的次数

                LogUtil.w("** screen count :" + screenAdCount + " -> this day 已显示:" + timeCount + " -> this day showLimit 总数:" + showLimit + "  **");

                //当天显示 广告总数 小于 每日规定显示次数   &&  当前广告长度小于 0 进行广告请求
                if (timeCount < showLimit && screenAdCount <= 0) {
                    LogUtil.w("timeCount < showLimit && screenAdCount <=0  -> sendMessage AD_LIST_RE");

                    //重新发起广告申请
                    new HttpAdvertisment(SDKService.mContext, SDKService.mHandler, Constant.SCREEN_AD).start();

                } else {//当前数据库还有广告 直接推送

                    //继续发送
                    if (timeCount < showLimit) {
                        LogUtil.i("timeCount < showLimit  -> sendAdWindowByTime()");
                        //直接启动广告
                        Message message = new Message();
                        message.what = SDKService.AD_LIST;
                        message.obj = Constant.SCREEN_AD;
                        SDKService.mHandler.sendMessage(message);

                    } else {
                        LogUtil.i("当天广告全部发送完毕 ：timeCount == showLimit -> CLOSE AD SERVICE");
                        SDKService.mHandler.sendEmptyMessage(SDKService.CLOSE_SDK_SERVICE);

                    }
                }


            }
        }, time * TIME_MS);

    }

}
