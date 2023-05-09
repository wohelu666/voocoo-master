package com.voocoo.pet.common.utils;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * 文本工具类
 */

public class StringUtil {


    /**
     * 转为人民币格式
     *
     * @param num
     * @return
     */
    public static String formatRMB(double num) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CHINA);

        return currency.format(num);
    }

    /**
     * 补充日 月到两位数
     */
    public static String getSupplementStr(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return value + "";
    }
}
