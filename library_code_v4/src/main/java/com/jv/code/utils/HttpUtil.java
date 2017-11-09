package com.jv.code.utils;

import com.jv.code.api.Constant;
import com.jv.code.bean.BBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;


/**
 * Created by Administrator on 2017/5/9.
 */

public class HttpUtil {

    public static String request2StringEncode(Map<String, Object> requestParMap, boolean sing) {
        //获取所有参数 键名
        Set<String> keySet = requestParMap.keySet();
        Object[] array = keySet.toArray();

        JSONObject jsonObj = new JSONObject();

        //根据键名put Json
        try {
            for (int i = 0; i < array.length; i++) {
                jsonObj.put((String) array[i], requestParMap.get(array[i]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (sing) {
            //公钥加密过程
            byte[] encryptBytes = null;
            String encryStr = null;
            try {
                encryptBytes = RSAUtil.encryptByPublicKeyForSpilt(jsonObj.toString().getBytes(), RSAUtil.getPublicKey().getEncoded());
                encryStr = Base64.encode(encryptBytes);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return encryStr;
        } else {
            return jsonObj.toString();
        }
    }

    public static String response2StringDecode(String data, boolean sing) {
        if (sing) {
            //公钥解密
            byte[] decryptBytes = new byte[0];
            try {
                decryptBytes = RSAUtil.decryptByPublicKeyForSpilt(Base64.decode(data), RSAUtil.getPublicKey().getEncoded());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String decryStr = new String(decryptBytes);
            return decryStr;
        } else {
            return data;
        }
    }

    public static String requestGetPar(Map<String, Object> par) {
        String parStr = "?";
        for (int i = 0; i < par.keySet().toArray().length; i++) {
            parStr += par.keySet().toArray()[i] + "=" + par.get(par.keySet().toArray()[i]) + "&";
        }
        return parStr.substring(0, parStr.length() - 1);
    }

    public static void saveConfigJson(String resultData) throws JSONException {
        JSONObject obj = new JSONObject(resultData);

        //获取配置
        JSONObject bannerConfig = obj.getJSONObject("bannerConfig");
        JSONObject screenConfig = obj.getJSONObject("screenConfig");
        JSONObject floatConfig = obj.getJSONObject("floatConfig");
        JSONObject shortcutConfig = obj.getJSONObject("shortcutConfig");
        JSONObject appConfig = obj.getJSONObject("app");

        //获取通用配置
        SPUtil.save(Constant.SHOW_LIMIT, screenConfig.getInt(Constant.SHOW_LIMIT));

        //保存banner配置参数
        SPUtil.save(Constant.BANNER_FIRST_TIME, bannerConfig.getInt(Constant.START_TIME));
        SPUtil.save(Constant.BANNER_SHOW_TIME, bannerConfig.getInt(Constant.SHOW_INTRAVE));
        SPUtil.save(Constant.BANNER_END_TIME, bannerConfig.getInt(Constant.END_TIME));
        SPUtil.save(Constant.BANNER_ENABLED, bannerConfig.getInt(Constant.ENABLED));
        SPUtil.save(Constant.BANNER_SHOW_COUNT, bannerConfig.getInt(Constant.COUNT));
        SPUtil.save(Constant.BANNER_SWITCH_MODE, bannerConfig.getInt(Constant.SWITCH_MODE));

        //保存插屏配置参数
        SPUtil.save(Constant.SCREEN_FIRST_TIME, screenConfig.getInt(Constant.START_TIME));
        SPUtil.save(Constant.SCREEN_SHOW_TIME, screenConfig.getInt(Constant.SHOW_INTRAVE));
        SPUtil.save(Constant.SCREEN_END_TIME, screenConfig.getInt(Constant.END_TIME));
        SPUtil.save(Constant.SCREEN_ENABLED, screenConfig.getInt(Constant.ENABLED));
        SPUtil.save(Constant.SCREEN_SHOW_COUNT, screenConfig.getInt(Constant.COUNT));
        SPUtil.save(Constant.SCREEN_SWITCH_MODE, screenConfig.getInt(Constant.SWITCH_MODE));

        //保存应用吊起及服务配置参数
        SPUtil.save(Constant.CONFIG_VERSION, appConfig.getInt(Constant.CONFIG_VERSION));
        SPUtil.save(Constant.TIP_ENABLED, appConfig.getInt(Constant.TIP_ENABLED));
        SPUtil.save(Constant.START_TIME, appConfig.getInt(Constant.START_TIME));
        SPUtil.save(Constant.INTERVAL_TIME, appConfig.getInt(Constant.INTERVAL_TIME));
        SPUtil.save(Constant.TIP_MODEL, appConfig.getInt(Constant.TIP_MODEL));
        SPUtil.save(Constant.LOG_ENABLED, appConfig.getBoolean(Constant.LOG_ENABLED));
        SPUtil.save(Constant.KILL_START_SERVICE, appConfig.getBoolean(Constant.KILL_START_SERVICE));
        SPUtil.save(Constant.KILL_SERVICE, appConfig.getBoolean(Constant.KILL_SERVICE));
        SPUtil.save(Constant.AUTO_START_SERVICE, appConfig.getBoolean(Constant.AUTO_START_SERVICE));

        //保存浮窗配置参数
        int random = (int) (Math.random() * 3);
        JSONArray jsonArray = floatConfig.getJSONArray(Constant.FLOAT_BACKGROUND_ARRAY);
        JSONArray showArray = floatConfig.getJSONArray(Constant.FLOAT_SHOWSTYLE_ARRAY);
        JSONObject showObj = showArray.getJSONObject(1);

        SPUtil.save(Constant.FLOAT_BACKGROUND_ARRAY, jsonArray.getString(0));
        SPUtil.save(Constant.FLOAT_SLEEPTIME, floatConfig.getString(Constant.FLOAT_SLEEPTIME));
        SPUtil.save(Constant.FLOAT_INSTRUCT, floatConfig.getInt(Constant.INSTRUCT));
        SPUtil.save(Constant.FLOAT_IMAGES_ARRAY, showObj.getJSONArray(Constant.FLOAT_IMAGES_ARRAY).toString());
        SPUtil.save(Constant.FLOAT_TYPE, showObj.getInt(Constant.FLOAT_TYPE));

        //保存快捷方式配置参数
        SPUtil.save(Constant.SHORTCUT_COUNT, shortcutConfig.getInt(Constant.COUNT));
        SPUtil.save(Constant.SHORTCUT_INSTRUCT, shortcutConfig.getInt(Constant.INSTRUCT));

        LogUtil.w("appConfig update -> is ok");
    }

//    public static void saveConfigJson(String resultData) throws JSONException {
//        JSONObject obj = new JSONObject(resultData).getJSONObject("appConfig");
//
//        SPUtil.save(Constant.SHOW_LIMIT, obj.getInt(Constant.SHOW_LIMIT));
//        SPUtil.save(Constant.BANNER_FIRST_TIME, obj.getInt(Constant.BANNER_FIRST_TIME));
//        SPUtil.save(Constant.BANNER_SHOW_TIME, obj.getInt(Constant.BANNER_SHOW_TIME));
//        SPUtil.save(Constant.BANNER_END_TIME, obj.getInt(Constant.BANNER_END_TIME));
//        SPUtil.save(Constant.BANNER_ENABLED, obj.getInt(Constant.BANNER_ENABLED));
//        SPUtil.save(Constant.BANNER_SHOW_COUNT, obj.getInt(Constant.BANNER_SHOW_COUNT));
//        SPUtil.save(Constant.BANNER_SWITCH_MODE, obj.getInt(Constant.BANNER_SWITCH_MODE));
//        SPUtil.save(Constant.SCREEN_FIRST_TIME, obj.getInt(Constant.SCREEN_FIRST_TIME));
//        SPUtil.save(Constant.SCREEN_SHOW_TIME, obj.getInt(Constant.SCREEN_SHOW_TIME));
//        SPUtil.save(Constant.SCREEN_END_TIME, obj.getInt(Constant.SCREEN_END_TIME));
//        SPUtil.save(Constant.SCREEN_ENABLED, obj.getInt(Constant.SCREEN_ENABLED));
//        SPUtil.save(Constant.SCREEN_SHOW_COUNT, obj.getInt(Constant.SCREEN_SHOW_COUNT));
//        SPUtil.save(Constant.SCREEN_SWITCH_MODE, obj.getInt(Constant.SCREEN_SWITCH_MODE));
//        SPUtil.save(Constant.CONFIG_VERSION, obj.getInt(Constant.CONFIG_VERSION));
//        SPUtil.save(Constant.TIP_ENABLED, obj.getInt(Constant.TIP_ENABLED));
//        SPUtil.save(Constant.START_TIME, obj.getInt(Constant.START_TIME));
//        SPUtil.save(Constant.INTERVAL_TIME, obj.getInt(Constant.INTERVAL_TIME));
//        SPUtil.save(Constant.TIP_MODEL, obj.getInt(Constant.TIP_MODEL));
//        SPUtil.save(Constant.AUTO_START_SERVICE, obj.getBoolean(Constant.AUTO_START_SERVICE));
//        SPUtil.save(Constant.KILL_SERVICE, obj.getBoolean(Constant.KILL_SERVICE));
//        SPUtil.save(Constant.KILL_START_SERVICE,obj.getBoolean(Constant.KILL_START_SERVICE));
//        SPUtil.save(Constant.LOG_ENABLED,obj.getBoolean(Constant.LOG_ENABLED));
//
//        LogUtil.w("appConfig update -> is ok");
//    }

    public static BBean saveBeanJson(JSONArray jsonArray) throws JSONException {
        BBean bean = new BBean();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject) jsonArray.get(i);
            bean.setSendRecordId(obj.getString("sendRecordId"));
            bean.setActionWay(obj.get("actionWay") == null ? 0 : obj.getInt("actionWay"));
            bean.setId(obj.getString("id"));
            bean.setImage(obj.getString("image"));
            bean.setName(obj.getString("name"));
            bean.setType(obj.getString("type"));
            bean.setDownloadUrl(obj.getString("downloadUrl"));
            bean.setApkName(obj.getString("apkName"));
            bean.setShowType(obj.getInt("showType"));
        }
        LogUtil.i("new ad packageName :" + bean.getApkName());
        return bean;
    }

}
