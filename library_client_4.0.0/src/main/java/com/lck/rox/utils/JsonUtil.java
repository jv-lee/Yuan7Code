package com.lck.rox.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JsonUtil
 *
 * @author Administrator
 */
public class JsonUtil {
    public static Map<String, Class> maps = new HashMap<String, Class>();

    static {
        maps.put("Z", boolean.class);
        maps.put("B", byte.class);
        maps.put("C", char.class);
        maps.put("D", double.class);
        maps.put("F", float.class);
        maps.put("I", int.class);
        maps.put("J", long.class);
        maps.put("S", short.class);
    }

    /**
     * 将一个实体类转换成json格式字符串
     *
     * @param obj
     * @return
     */
    public static String buildJson(Object obj) {
        Class<? extends Object> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            JSONObject json = new JSONObject();
            for (Field f : fields) {
                f.setAccessible(true);
                int modifiers = f.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    continue;
                }
                if (!f.getType().isPrimitive() && f.get(obj) == null) {
                    continue;
                }
                json.put(f.getName(), f.get(obj));
            }
            return json.toString();
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
        return null;
    }

    public static String buildJsonArray(Object obj) {
        if (obj == null) {
            return null;
        }
        JSONArray ary = new JSONArray();
        Class<? extends Object> clz = obj.getClass();
        if (clz.isArray()) {
            int len = Array.getLength(obj);
            String clzName = clz.getName();
            try {
                if (clzName.startsWith("[L") && !String[].class.getName().equals(clzName)) {
                    // 引用类型数组
                    for (int i = 0; i < len; i++) {
                        String tmp = buildJson(Array.get(obj, i));
                        ary.put(i, new JSONObject(tmp));
                    }
                } else {
                    // 基本类型数组
                    for (int i = 0; i < len; i++) {
                        ary.put(i, Array.get(obj, i));
                    }
                }
            } catch (Exception e) {
                Log.getStackTraceString(e);
            }
        } else {
            try {
                if (clz.isPrimitive() || clz == String.class) {
                    ary.put(0, obj);
                } else {
                    String json = buildJson(obj);
                    ary.put(0, json);
                }
            } catch (JSONException e) {
                Log.getStackTraceString(e);
            }
        }
        return ary.toString();
    }

    public static Object parseJsonObject(String className, String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(s);
            Class clz = Class.forName(className);
            Object obj = clz.newInstance();
            Field[] fields = clz.getDeclaredFields();
            for (Field f : fields) {
                int mod = f.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                String name = f.getName();
                if (json.isNull(name)) {
                    continue;
                }
                f.setAccessible(true);
                Object val = json.get(name);
                f.set(obj, val);
            }
            return obj;
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
        return null;
    }

    /**
     * 将json解释成clzStr类的json数组
     *
     * @param jsonArray
     * @return 解析出错时返回null
     */
    public static Object parseJSONArray(Class clz, String jsonArray) {
        if (clz == null || jsonArray == null) {
            return null;
        }
        try {
            JSONArray jsonAry = new JSONArray(jsonArray);
            int len = jsonAry.length();
            Object result = Array.newInstance(clz, len);
            if (clz.isPrimitive() || clz == String.class) {
                for (int i = 0; i < len; i++) {
                    Array.set(result, i, jsonAry.get(i));
                }
            } else {
                for (int i = 0; i < len; i++) {
                    String tmp = jsonAry.getString(i);
                    Object obj = parseJsonObject(clz.getName(), tmp);
                    Array.set(result, i, obj);
                }
            }
            return result;
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
        return null;
    }

    public static Map<String, Object> parseJsonObject(String json, Map<String, Class> content) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (json == null || content == null) {
            return result;
        }
        try {
            JSONObject obj = new JSONObject(json);
            Set<String> keys = content.keySet();
            for (String key : keys) {
                if (obj.isNull(key)) {
                    continue;
                }
                Class clz = content.get(key);
                Object val = null;
                if (clz.isPrimitive() || clz == String.class) {
                    val = obj.get(key);
                } else if (clz.isArray()) {
                    String name = clz.getName();
                    String tmp = obj.getString(key);
                    if (name.startsWith("[L")) {
                        String clzName = clz.getName().substring(2, clz.getName().length() - 1);
                        val = parseJSONArray(Class.forName(clzName), tmp);
                    } else {
                        Class c = maps.get(name.substring(1));
                        val = parseJSONArray(c, tmp);
                    }
                } else {
                    String tmp = obj.getString(key);
                    val = parseJsonObject(clz.getName(), tmp);
                }
                result.put(key, val);
            }
            return result;
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
        return result;
    }

    public static JsonInterface parseJSonObject(Class clz, String jsonString) {
        if (jsonString == null) {
            return null;
        }
        try {
            JSONObject jo = new JSONObject(jsonString);
            JsonInterface jInterface = (JsonInterface) clz.newInstance();
            if (!jo.isNull(jInterface.getShortName())) {
                jInterface.parseJson(jo.getJSONObject(jInterface.getShortName()));
                return jInterface;
            } else {
                jInterface.parseJson(jo);
                return jInterface;
            }
        } catch (JSONException e) {
            Log.getStackTraceString(e);
        } catch (IllegalAccessException e) {
            Log.getStackTraceString(e);
        } catch (InstantiationException e) {
            Log.getStackTraceString(e);
        }
        return null;
    }

    public static JsonInterface[] parseJSonArray(Class clz, String jsonString) {
        if (jsonString == null) {
            return null;
        }
        try {
            JSONObject jo = new JSONObject(jsonString);
            JsonInterface ji = (JsonInterface) clz.newInstance();
            if (!jo.isNull(ji.getShortName())) {
                JSONArray ja = jo.getJSONArray(ji.getShortName());
                if (ja != null) {
                    JsonInterface[] interfaces = (JsonInterface[]) Array.newInstance(clz, ja.length());
                    for (int i = 0; i < ja.length(); i++) {
                        ji = (JsonInterface) clz.newInstance();
                        ji.parseJson(ja.getJSONObject(i));
                        interfaces[i] = ji;
                    }
                    return interfaces;
                }
            }
        } catch (JSONException e) {
            Log.getStackTraceString(e);
        } catch (IllegalAccessException e) {
            Log.getStackTraceString(e);
        } catch (InstantiationException e) {
            Log.getStackTraceString(e);
        }
        return null;
    }

}
