package com.y7.paint.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/12/19.
 */

public class ResourceUtils {
    private static TypedValue mTmpValue = new TypedValue();

    private ResourceUtils() {
    }

    public static Drawable getDrawable(Context context, String name) {
        return context.getResources().getDrawable(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
    }

    /**
     * 根据资源id获取px值
     * @param context
     * @param id
     * @return
     */
    public static int getXmlDef(Context context, int id) {
        synchronized (mTmpValue) {
            TypedValue value = mTmpValue;
            context.getResources().getValue(id, value, true);
            return (int) TypedValue.complexToFloat(value.data) *3;
        }
    }
    
    /**
     * 根据dimen名字 转换获取px值
     * @param context
     * @param name
     * @return
     */
    public static int getXmlDefByString(Context context,String name){
    	int id = getDimen(context, name);
        synchronized (mTmpValue) {
            TypedValue value = mTmpValue;
            context.getResources().getValue(id, value, true);
            return (int) TypedValue.complexToFloat(value.data) *3;
        }
    }
    
    /**

    * 对于 context.getResources().getIdentifier 无法获取的数据 , 或者数组

    * 资源反射值

    * @paramcontext

    * @param name

    * @param type

    * @return

    */

    private static Object getResourceId(Context context,String name, String type) {

    String className = context.getPackageName() +".R";

    try {

    Class<?> cls = Class.forName(className);

    for (Class<?> childClass : cls.getClasses()) {

    String simple = childClass.getSimpleName();

    if (simple.equals(type)) {

    for (Field field : childClass.getFields()) {

    String fieldName = field.getName();

    if (fieldName.equals(name)) {

    System.out.println(fieldName);

    return field.get(null);

    }

    }

    }

    }

    } catch (Exception e) {

    e.printStackTrace();

    }

    return null;

    }

    /**

    *context.getResources().getIdentifier 无法获取到 styleable 的数据

    * @paramcontext

    * @param name

    * @return

    */

    public static int getStyleable(Context context, String name) {

    return ((Integer)getResourceId(context, name,"styleable")).intValue();

    }
    
    
    public static int getDimen(Context context, String name) {

    return ((Integer)getResourceId(context, name,"dimen")).intValue();

    }

    /**

    * 获取 styleable 的 ID 号数组

    * @paramcontext

    * @param name

    * @return

    */

    public static int[] getStyleableArray(Context context,String name) {

    return (int[])getResourceId(context, name,"styleable");

    }
    
}

