package com.hash.pre.utils;

import android.content.Context;
import android.os.Environment;

import com.hash.pre.api.Constant;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

public class DexUtils extends DexClassLoader {

    //创建一个插件加载器集合，对固定的dex使用固定的加载器可以防止多个加载器同时加载一个dex造成错误
    private static final HashMap<String, DexUtils> pluginLoader = new HashMap<String, DexUtils>();

    public DexUtils(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }

    /**
     * 返回dexPath对应的加载器
     */
    public static DexUtils getClassLoader(String dexPath, Context context, ClassLoader parent) {
        DexUtils SDKClassLoader = pluginLoader.get(dexPath);

        if (SDKClassLoader == null) {
            //获取app启动路径
            final String dexOutputPath = context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
            SDKClassLoader = new DexUtils(dexPath, dexOutputPath, null, parent);
            pluginLoader.put(dexPath, SDKClassLoader);

        }

        return SDKClassLoader;
    }


    public static void appStart(Context context) {
        DexClassLoader dexClassLoader = null;

        //dexPath 为获取当前包下dex类文件
        final File dexPath = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "patch.jar");
        //dexOutputPatch 获取dex读取后存放路径
        final String dexOutputPath = context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();

        LogUtils.i("jarCode loadPath : " + dexPath.getAbsolutePath());
        LogUtils.i("jarCode cachePath：" + dexOutputPath);

        //通过dexClassLoader类加载器 加载dex代码
        if (dexPath.exists()) {
            dexClassLoader = new DexClassLoader(dexPath.getAbsolutePath(), dexOutputPath, null, context.getClass().getClassLoader().getParent());
        }

        try {

            Class<?> sdkManagerClass = dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);

            Method initMethod = sdkManagerClass.getDeclaredMethod("appActivityStart", new Class[]{Context.class});

            initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{context});

            LogUtils.i("read jar code is ok");

        } catch (Exception e) {
            e.printStackTrace();

            LogUtils.i("read jar code is Exception" + e.getMessage());
        }

    }

}