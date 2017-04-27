package com.jv.code.component;


import com.jv.code.constant.Constant;
import com.jv.code.db.dao.AdDaoImpl;
import com.jv.code.db.dao.IAdDao;
import com.jv.code.service.SDKService;
import com.jv.code.utils.LogUtil;
import com.jv.code.utils.SDKUtil;
import com.jv.code.utils.SPUtil;
import com.jv.code.view.ScreenWindowView;

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


        int time = (int) SPUtil.get(Constant.SCREEN_FIRST_TIME, 30);
        if (SCREEN_FLAG) {
            SCREEN_FLAG = false;
        } else {
            //获取当前间隔时间 为空的话为第一次初始化该时间 获取正常间隔间隔时间做替补
            time = (int) SPUtil.get(Constant.SCREEN_TIME, SPUtil.get(Constant.SCREEN_SHOW_TIME, 30));
        }

        LogUtil.w("插屏 窗体 " + time + "秒 -> 发送广告请求\n ");
        SDKService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mDao = new AdDaoImpl(SDKService.mContext);//实列化广告持久层DAO
                currentDate = (String) SPUtil.get(Constant.CURRENT_DATE, "FIRST");//app每日首次启动，获取最近存储时间

                //如果当前日期不是最近日期,就重新保存 当天日期
                if (!currentDate.equals(SDKUtil.getDate()) || currentDate.equals("FIRST")) {
                    SPUtil.save(Constant.CURRENT_DATE, SDKUtil.getDate());//保存当前时间
                    LogUtil.i("save this DATE :" + SDKUtil.getDate());

                }

                //获取插屏广告 当前条数 Constant.SCREEN_AD  =  1
                int screenAdCount = mDao.getCount(Constant.SCREEN_ID);//获取当前广告长度
                int showLimit = (Integer) SPUtil.get(Constant.SHOW_LIMIT, 5);//获取每天最大显示量
                int timeCount = (Integer) SPUtil.get(SDKUtil.getAdShowDate(), 0);//当天已显示的次数

                LogUtil.w("** 插屏 count :" + screenAdCount + " -> 已显示:" + timeCount + "/" + showLimit + "  **");

                //继续发送
                if (timeCount < showLimit) {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            LogUtil.w("new ScreenWindowView() ->");
                            new ScreenWindowView(SDKService.mContext);
                        }
                    }.start();
                } else {
                    LogUtil.w("当天广告全部发送完毕 ：showLimit >=timeCount -> ");
                    SDKService.mHandler.sendEmptyMessage(SDKService.CLOSE_SDK_SERVICE);
                }

            }
        }, time * TIME_MS);

    }

}
