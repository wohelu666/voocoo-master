package com.voocoo.pet.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SharedPreferencesUtil {
    private static SharedPreferences sharedPreferences;
    private volatile static SharedPreferencesUtil instance;

    public static SharedPreferencesUtil init(Context context){
        if(instance==null){
            synchronized (SharedPreferencesUtil.class){
                if(instance==null){
                    instance = new SharedPreferencesUtil(context);
                }
            }
        }
        return instance;
    }

    private SharedPreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences("juchong", Context.MODE_PRIVATE);
    }

    public static void keepShared(String key, String value) {
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void keepShared(String key, Integer value) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void keepShared(String key, long value) {
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void keepShared(String key, int value) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void keepShared(String key, boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String queryValue(String key, String defvalue) {
        String value = sharedPreferences.getString(key, defvalue);
        // if ("".equals(value)) {
        // return "";
        // }
        return value;
    }

    public static String queryValue(String key) {
        String value = sharedPreferences.getString(key, "");
        if ("".equals(value)) {
            return "";
        }

        return value;
    }

    public static Integer queryIntValue(String key) {
        int value = sharedPreferences.getInt(key, 0);
        return value;
    }

    public static Integer queryIntValue(String key, int defalut) {
        int value = sharedPreferences.getInt(key, defalut);
        return value;
    }

    public static boolean queryBooleanValue(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static long queryLongValue(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    public static boolean deleteAllValue() {

        return sharedPreferences.edit().clear().commit();
    }

    public static void deleteValue(String key) {
        sharedPreferences.edit().remove(key).commit();
    }
}
