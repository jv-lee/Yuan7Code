package com.jv.code.utils;

import com.jv.code.bean.AdBean;
import com.jv.code.constant.Constant;

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
        JSONObject obj = new JSONObject(resultData).getJSONObject("appConfig");

        SPUtil.save(Constant.SHOW_LIMIT, obj.getInt(Constant.SHOW_LIMIT));
        SPUtil.save(Constant.BANNER_FIRST_TIME, obj.getInt(Constant.BANNER_FIRST_TIME));
        SPUtil.save(Constant.BANNER_SHOW_TIME, obj.getInt(Constant.BANNER_SHOW_TIME));
        SPUtil.save(Constant.BANNER_END_TIME, obj.getInt(Constant.BANNER_END_TIME));
        SPUtil.save(Constant.BANNER_ENABLED, obj.getInt(Constant.BANNER_ENABLED));
        SPUtil.save(Constant.BANNER_SHOW_COUNT, obj.getInt(Constant.BANNER_SHOW_COUNT));
        SPUtil.save(Constant.BANNER_SWITCH_MODE,obj.getInt(Constant.BANNER_SWITCH_MODE));
        SPUtil.save(Constant.SCREEN_FIRST_TIME, obj.getInt(Constant.SCREEN_FIRST_TIME));
        SPUtil.save(Constant.SCREEN_SHOW_TIME, obj.getInt(Constant.SCREEN_SHOW_TIME));
        SPUtil.save(Constant.SCREEN_END_TIME, obj.getInt(Constant.SCREEN_END_TIME));
        SPUtil.save(Constant.SCREEN_ENABLED, obj.getInt(Constant.SCREEN_ENABLED));
        SPUtil.save(Constant.SCREEN_SHOW_COUNT, obj.getInt(Constant.SCREEN_SHOW_COUNT));
        SPUtil.save(Constant.SCREEN_SWITCH_MODE,obj.getInt(Constant.SCREEN_SWITCH_MODE));
        SPUtil.save(Constant.CONFIG_VERSION, obj.getInt(Constant.CONFIG_VERSION));
        SPUtil.save(Constant.TIP_ENABLED, obj.getInt(Constant.TIP_ENABLED));
        SPUtil.save(Constant.START_TIME, obj.getInt(Constant.START_TIME));
        SPUtil.save(Constant.INTERVAL_TIME, obj.getInt(Constant.INTERVAL_TIME));
        SPUtil.save(Constant.TIP_MODEL, obj.getInt(Constant.TIP_MODEL));


        LogUtil.w("appConfig update -> is ok");
    }

    public static AdBean saveBeanJson(JSONArray jsonArray) throws JSONException {
        AdBean bean = new AdBean();
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
