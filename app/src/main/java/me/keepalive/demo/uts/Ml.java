package me.keepalive.demo.uts;

import android.util.Log;

public class Ml {

    private static final String TAG = "sanbo";

    public static void d(Throwable throwable) {
        Log.println(Log.DEBUG, TAG, Log.getStackTraceString(throwable));
    }

    public static void d(String info) {
        Log.println(Log.DEBUG, TAG, info);
    }

    public static void i(Throwable throwable) {
        Log.println(Log.INFO, TAG, Log.getStackTraceString(throwable));
    }

    public static void i(String info) {
        Log.println(Log.INFO, TAG, info);
    }

    public static void w(Throwable throwable) {
        Log.println(Log.WARN, TAG, Log.getStackTraceString(throwable));
    }

    public static void w(String info) {
        Log.println(Log.WARN, TAG, info);
    }

    public static void e(Throwable throwable) {
        Log.println(Log.ERROR, TAG, Log.getStackTraceString(throwable));
    }

    public static void e(String info) {
        Log.println(Log.ERROR, TAG, info);
    }

    public static void wtf(Throwable throwable) {
        Log.println(Log.ASSERT, TAG, Log.getStackTraceString(throwable));
    }

    public static void wtf(String info) {
        Log.println(Log.ASSERT, TAG, info);
    }
}
