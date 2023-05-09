package com.voocoo.pet.common.utils;




import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 工具类用于各种格式校验
 */
public class Validations {
    /**
     * 校验失败异常
     */
    public static class ValidationException extends Exception {
        public final String target;
        public final String reason;

        public ValidationException(String reason) {
            this("", reason);
        }

        public ValidationException(String target, String reason) {
            super(target + reason);
            this.target = target;
            this.reason = reason;
        }

        public ValidationException withTarget(String target) {
            return new ValidationException(target, reason);
        }

        public boolean isTargetIn(String... targets) {
            for (String t : targets) {
                if (target.equals(t)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 手机号码模式。
     * <ul>
     * <li>2G/3G：13x,15x,18x</li>
     * <li>上网卡：14x</li>
     * <li>4G：17x</li>
     * <li>虚拟运营商号码：170x</li>
     * <li>卫星通信：1349（不考虑）</li>
     * </ul>
     */
    private static final Pattern PHONE_NUMBER = Pattern.compile("(^1(?:3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$)");

    private static final Pattern PHONE_NUMBER_V2015_0229 = Pattern.compile("^[1-9][0-9]{10}$");

    /**
     * 匹配手机号码的格式
     *
     * @param phoneNumber 手机号码
     * @return 是否匹配
     */
    public static boolean matchesPhoneNumber(CharSequence phoneNumber) {
        return true;
    }
    public static boolean checkEmail(@Nullable String input) {
        if (input == null) {
            return false;
        }
        return Pattern.compile("^[A-Za-z0-9]+([-_.]*[A-Za-z0-9])*@([A-Za-z0-9]+[-A-Za-z0-9]*[A-Za-z0-9]+\\.){1,63}[A-Za-z0-9]+$").matcher(input).matches();
    }
    /**
     * 大陆地区固话及小灵通
     */
    private static final Pattern CN_TEL_NUMBER = Pattern
            .compile("^0(10|2[0-5789]|[3-9]\\d{2})[-]?\\d{7,8}$");

    /**
     * 可以输固话和手机号码的地方：3-13位数字加符号组合，符号包括"-""/"
     */
    private static final Pattern CN_TEL_NUMBER_V2015_0229 = Pattern
            .compile("^[-/0-9]{3,13}$");


    /**
     * 匹配固定电话格式
     *
     * @param telNumber 固定电话
     * @return 是否匹配
     */
    public static boolean matchesTelNumber(CharSequence telNumber) {
        return CN_TEL_NUMBER.matcher(telNumber).matches();
    }

    /**
     * 匹配固定电话或手机号码的格式
     *
     * @param number 号码
     * @return 是否匹配
     */
    public static boolean matchesTelOrPhoneNumber(CharSequence number) {
        return matchesTelNumber(number) || matchesPhoneNumber(number);
    }

    /**
     * 密码必须是6～18位数字或字母组成 <br/>
     * 拷贝自 <code>LoginActivity.checkInput</code>
     */
    private static final Pattern PASSWORD = Pattern.compile("^[A-Za-z0-9!@#$%^&*-/:;()_?,.]{6,16}$");

    /**
     * 匹配 6～18位数字或字母 的密码
     *
     * @param password 密码
     * @return 是否匹配
     */
    public static boolean matchesPassword(CharSequence password) {
        return PASSWORD.matcher(password).matches();
    }

    /**
     * 手机验证码是4位数字或字母组成 <br/>
     * 拷贝自 <code>LoginActivity.checkInput</code>
     */
    private static final Pattern VALIDATECODE = Pattern.compile("^[A-Za-z0-9]{4}$");

    /**
     * 特殊符号
     */
    private static final Pattern SPECIAL_CODE = Pattern.compile("^.*[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？].*$");

    /**
     * 匹配 6～18位数字或字母 的密码
     *
     * @param validateCode 密码
     * @return 是否匹配
     */
    public static boolean matchesValidateCode(CharSequence validateCode) {
        return VALIDATECODE.matcher(validateCode).matches();
    }

    private Validations() {
    }


    private static final Pattern CHECK_LENGTH = Pattern.compile("^.{1,20}$");

    public static boolean checkLength(CharSequence content) {
        return CHECK_LENGTH.matcher(content).matches();
    }

    private static final Pattern CHECK_URL = Pattern.compile("[a-zA-z]+://[^\\s]*");

    /**
     * 匹配url
     *
     * @param content
     * @return
     */
    public static boolean checkUrl(CharSequence content) {
        return CHECK_URL.matcher(content).matches();
    }

    /**
     * 是否包含特殊字符
     * @param content 内容
     * @return
     */
    public static boolean checkSpecialCode(CharSequence content){
        return SPECIAL_CODE.matcher(content).matches();
    }

    /**
     * 判断文本是否全部为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 验证密码格式
     */
    public static boolean checkPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }

        if (password.length() > 16) {
            return false;
        }

        boolean flag;
        try {
            String check = "^[\\x21-\\x7e]{6,16}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(password);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


}
