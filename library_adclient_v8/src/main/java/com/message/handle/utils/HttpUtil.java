package com.message.handle.utils;


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

}
