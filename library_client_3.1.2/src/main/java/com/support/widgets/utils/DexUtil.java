package com.support.widgets.utils;

import android.content.Context;
import android.os.Environment;

import com.support.widgets.api.Constant;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

public class DexUtil extends DexClassLoader {

    //创建一个插件加载器集合，对固定的dex使用固定的加载器可以防止多个加载器同时加载一个dex造成错误
    private static final HashMap<String, DexUtil> pluginLoader = new HashMap<String, DexUtil>();

    public DexUtil(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }

    /**
     * 返回dexPath对应的加载器
     */
    public static DexUtil getClassLoader(String dexPath, Context context, ClassLoader parent) {
        DexUtil SDKClassLoader = pluginLoader.get(dexPath);

        if (SDKClassLoader == null) {
            //获取app启动路径
            final String dexOutputPath = context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
            SDKClassLoader = new DexUtil(dexPath, dexOutputPath, null, parent);
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

        LogUtil.i("jarCode loadPath : " + dexPath.getAbsolutePath());
        LogUtil.i("jarCode cachePath：" + dexOutputPath);

        //通过dexClassLoader类加载器 加载dex代码
        if (dexPath.exists()) {
            dexClassLoader = new DexClassLoader(dexPath.getAbsolutePath(), dexOutputPath, null, context.getClass().getClassLoader().getParent());
        }

        try {

            Class<?> sdkManagerClass = dexClassLoader.loadClass(Constant.SDK_SERVICE_CODE);

            Method initMethod = sdkManagerClass.getDeclaredMethod("appActivityStart", new Class[]{Context.class});

            initMethod.invoke(sdkManagerClass.newInstance(), new Object[]{context});

            LogUtil.i("read jar code is ok");

        } catch (Exception e) {
            e.printStackTrace();

            LogUtil.i("read jar code is Exception" + e.getMessage());
        }

    }

}
