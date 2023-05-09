package com.voocoo.pet.common.utils;

import android.text.TextUtils;

/**格式化工具
 * Created by andy on 2017/12/2/002.
 */

public class FormatUtil {
    public static String formatPhone(String phone){
        if(!TextUtils.isEmpty(phone)){
            String formatPhone = phone.substring(3,phone.length()-4);
            return phone.replace(formatPhone,"****");
        }
        return "";
    }
}
