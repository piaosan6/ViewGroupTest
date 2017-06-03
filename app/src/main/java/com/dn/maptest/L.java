package com.dn.maptest;

import android.util.Log;

/**
 * Log统一管理类
 *
 */
public class L {

    private L() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    // 是否需要打印bug，可以在application的onCreate函数里面初始化
    public static boolean isDebug = true;
    private static final String TAG = "info";

    // 下面四个是默认tag的函数
    public static <T> void i(T msg){
    	if (isDebug)
            Log.i(TAG, msg.toString());
    }

    public static <T> void d(T msg) {
        if (isDebug)
            Log.d(TAG, msg.toString());
    }

    public static <T> void e(T msg) {
    	 if (isDebug)
             Log.e(TAG, msg.toString());
    }

    public static <T> void v(T msg) {
    	 if (isDebug)
             Log.v(TAG, msg.toString());
    }

    // 下面是传入自定义tag的函数
    public static <T> void i(String tag, T msg) {
        if (isDebug)
            Log.i(tag, msg.toString());
    }
    public static <T> void d(String tag, T msg) {
    	if (isDebug)
    		Log.d(tag, msg.toString());
    }
    public static <T> void e(String tag, T msg) {
    	if (isDebug)
    		Log.e(tag, msg.toString());
    }
    public static <T> void v(String tag, T msg) {
    	if (isDebug)
    		Log.v(tag, msg.toString());
    }

    public static void warnDeprecation(String depreacted, String replacement) {
        if (isDebug)
            Log.w("PullToRefresh", "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
    }
}