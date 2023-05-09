package com.voocoo.pet.common.utils;


import android.text.TextUtils;

/**
 * 类型转换工具类
 * Created by andy on 2017/12/20.
 */
public class TypeParseUtil {

    public static float parseFloat(String strFloat){
        return parseFloat(strFloat,0);
    }

    public static float parseFloat(String strFloat, int def) {
        if (TextUtils.isEmpty(strFloat)) {
            return def;
        }
        try {
            return Float.parseFloat(strFloat);
        } catch (Exception e) {
            LogUtil.e("e=="+e);
            return def;
        }
    }
    public static int parseInt(String strInt) {
        return parseInt(strInt, 0);
    }

    public static int parseInt(String strInt, int def) {
        if (TextUtils.isEmpty(strInt)) {
            return def;
        }
        try {
            return Integer.parseInt(strInt);
        } catch (Exception e) {
            LogUtil.e("e=="+e);
            return def;
        }
    }

    public static long parseLong(String strLong) {
        return parseLong(strLong, 0);
    }

    public static long parseLong(String strLong, int def) {
        if (TextUtils.isEmpty(strLong)) {
            return def;
        }
        try {
            return Long.parseLong(strLong);
        } catch (Exception e) {
            LogUtil.e("e=="+e);
            return def;
        }
    }
}
