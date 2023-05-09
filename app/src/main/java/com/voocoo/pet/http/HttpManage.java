package com.voocoo.pet.http;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.voocoo.pet.common.manager.PetsManager;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.utils.SystemUtil;
import com.voocoo.pet.common.utils.ToastUtil;
import com.voocoo.pet.entity.BindDevFeedPlan;
import com.voocoo.pet.entity.Diet;
import com.voocoo.pet.entity.UpdateDiet;
import com.voocoo.pet.modules.PetApp;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.voocoo.pet.modules.user.LoginByCodeActivity;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;


public class HttpManage {

    public static HttpManage instance;
    public static String host = "http://106.52.132.14:8081";
//    test
//    public static String host = "http://129.204.199.6:8081";
//    localhost
//    public static String host = "http://192.168.31.147:8080";

    //获取设备版本号
    public final String selectFeederVersionUrl = "/ownerApp/device_version/selectFeederVersion";

    //设备升级
    public final String feederVersionUpdate = "/ownerApp/device_version/feederVersionUpdate";

    //获取手机验证码
    public final String getVerifyCodeUrl = "/auth/sms";
    //登录通过验证码
    public final String loginByCodeUrl = "/ownerApp/owner-user/login";
    public final String loginByWechat = "/ownerApp/owner-user/wxLogin";

    public final String getVerifyCodeFromEmailUrl = "/api/mail/getVerifyCode";
    //注册
    public final String registerUrl = "/api/login/registerByMobileAndPassword";
    public final String registerFromEmailUrl = "/api/login/registerByEmailAndPassword";

    //登录
    public final String loginUrl = "/api/login/mobileAndPassword";
    public final String loginFromEmailUrl = "/api/login/emailAndPassword";

    public final String loginByCodeFromEmailUrl = "/api/login/emailAndVerifyCode";

    //忘记密码
    public final String forgetPwdUrl = "/api/login/resetPassword";
    public final String forgetPwdFromEmailUrl = "/api/login/resetPasswordByEmail";

    //修改密码
    public final String modityPwdUrl = "/api/user/modifyPassword";

    //首页轮播图
    public final String homeBannerUrl = "/api/content/banner";

    //首页动态
    public final String homeMomentUrl = "/api/moment/main/list";

    //动态详情
    public final String homeMomentDetileUrl = "/api/moment/detail";

    //养宠文章列表
    public final String contentListPageUrl = "/api/content/listPage";

    //养宠文章详情
    public final String contentDetitleUrl = "/api/content/detail";

    //修改密码
    public final String modifyPasswordUrl = "/api/user/modifyPassword";

    //个人用户信息
    public final String myInfoUrl = "/api/user/myInfo";
    //其他用户信息
    public final String userInfoUrl = "/ownerApp/owner-user/selectUser";
    //更新用户信息
    public final String updateInfoUrl = "/ownerApp/owner-user";

    //动态列表
    public final String newsListUrl = "/api/moment/listPage";

    //阿里云sts信息接口
    public final String aliyunStsUrl = "/api/upload/aliyun/app/sts";

    //添加设备接口
    public final String addDevUrl = "/ownerApp/binding/userBindingDevice";
    public final String reAddDevUrl = "/ownerApp/binding/ackUserBindingDevice";
    //删除设备接口
    public final String delDevUrl = "/ownerApp/owner-device/deleteDeviceById";

    public final String unbindDevUrl = "/ownerApp/owner-user/unbindingWx/{userId}";
    //清除数据
    public final String clearDataUrl = "/api/userDevice/clearData";

    //设备列表接口
    public final String devListUrl = "/ownerApp/owner-device/selectDeviceListByUser";
    //设备详情接口
    public final String devDetailUrl = "/ownerApp/owner-device/selectDeviceById";
    //修改设备名称
    public final String updataDevUrl = "/ownerApp/owner-deviceWater/updateDeviceName?deviceId={deviceId}&" +
            "deviceName={deviceName}&userId={userId}";

    public final String updataFeedDevUrl = "/ownerApp/owner-deviceFeeder/updateDeviceName?deviceId={deviceId}&" +
            "deviceName={deviceName}&userId={userId}";

    public final String waterDevLog = "/ownerApp/owner-deviceWater/waterLogList";
    public final String feedDevLog = "/ownerApp/owner-deviceFeeder/feederLogList";

    //查询设备属性
    public final String queryDevPropUrl = "/api/productDevice/listAll";
    //控制设备属性
    public final String controlDevPropUrl = "/api/productDevice/setProp";

    //宠物列表接口
    public final String petListUrl = "/ownerApp/pets/list";
    //宠物详情接口
    public final String petDetailUrl = "/api/pet/detail";
    //删除宠物接口
    public final String deletePetUrl = "/ownerApp/pets/deletePetById";
    //新增宠物接口
    public final String addPetUrl = "/ownerApp/pets";
    //更新宠物接口
    public final String updatePetUrl = "/ownerApp/pets";
    public final String smartFeedUrl = "/ownerApp/plan/generateSmartPlan";

    //反馈接口
    public final String feedbackUrl = "/ownerApp/suggest";

    //关于我们
    public final String aboutUrl = "/api/appAboutUs/detail";

    //订阅设备消息
    public final String subscribeDevUrl = "/api/userDevice/subscribeTopic";

    //取消订阅设备消息
    public final String unSubscribeDevUrl = "/api/userDevice/unsubscribeTopic";

    //发布动态
    public final String publishUrl = "/api/moment/publish";

    //商城列表
    public final String shopListUrl = "/api/productGoods/listPage";

    //猫砂盆使用记录
    public final String userRecordUrl = "/api/petCatLitterBoxLog/listPage";

    //推送开关设置
    public final String pushSettingUrl = "/api/userDevice/propPushSwitch";

    //检测升级
    public final String checkUpdateUrl = "/api/appVersion/getLatestVersion";

    //设备分享记录
    public final String devShareListUrl = "/api/userDevice/shareList";
    public final String shareUrl = "/api/userDevice/share";
    public final String cancelShareUrl = "/api/userDevice/cancelShare";

    public final String updateTimezoneUrl = "/api/productDevice/setTimeZone";

    public final String bindPositionerUrl = "/api/locatorDevice/bind";
    public final String unBindPositionerUrl = "/api/locatorDevice/unbind";
    public final String positionerListUrl = "/api/locatorDevice/listAll";
    public final String bindPetUrl = "/api/locatorDevice/bindPet";
    public final String positionerDetail = "/api/locatorDevice/detail";
    public final String updatePositioner = "/api/locatorDevice/update";
    public final String positionerPetInfo = "/device/locator/petInfo";

    public final String positionerLog = "/api/locatorDevice/listLog";

    //获取直播流地址
    public final String queryLiveStream = "/api/productDevice/queryLiveStreaming";

    public final String setWaterDevInductionMode = "/ownerApp/owner-deviceWater/sendInductionMode";
    public final String setWaterDevSmartMode = "/ownerApp/owner-deviceWater/sendSmartMode";
    public final String setWaterDevNightMode = "/ownerApp/owner-deviceWater/sendNightMode";
    public final String setFeedDevNightMode = "/ownerApp/owner-deviceFeeder/sendNightMode";
    public final String setFeedDevChildLock = "/ownerApp/owner-deviceFeeder/sendChildLock";

    public final String setHomeMode = "/ownerApp/owner-deviceWater/sendFamilyMode";

    public final String feedSetHomeMode = "/ownerApp/owner-deviceFeeder/sendFamilyMode";
    public final String refreshDesiccanElement = "/ownerApp/owner-deviceFeeder/selectDesiccan";

    public final String refreshFilterElement = "/ownerApp/owner-deviceWater/selectFilterElement";
    public final String waterDrink = "/ownerApp/owner-deviceWater/statisticsDrinking";
    public final String sendFeedingByHand = "/ownerApp/owner-deviceFeeder/sendFeedingByHand";
    public final String getDevFeedPlan = "/ownerApp/plan/selectPetFeederPlanByDevice";
    public final String newFeedPlan = "/ownerApp/plan";
    public final String waitToDo = "/ownerApp/owner-device/selectToDoList";

    public final String beforeCancelAccount = "/ownerApp/owner-user/beforeCancelAccount/{userId}";
    public final String cancelAccount = "/ownerApp/owner-user/cancelAccount";
    public final String unbindWechat = "/ownerApp/owner-user/unbindingWx/{userId}";

    public static final String uploadHead = "/ownerApp/owner-user/uploadUserPhoto";
    public static final String uploadSuggest = "/ownerApp/suggest/uploadSuggestImg";
    public static final String uploadPetHead = "/ownerApp/pets/uploadPetImg";
    public final String modifyPhone = "/ownerApp/owner-user/updateOwnerPhone";
    public final String feedComapreData = "/ownerApp/owner-deviceFeeder/selectEatingCompare";
    public final String feedData = "/ownerApp/owner-deviceFeeder/statisticsFeeding";
    public final String devShareList = "/ownerApp/share/selectShareByManageByDevice";
    public final String cancelShare = "/ownerApp/share/cancelShare";
    public final String bindWechat = "/ownerApp/owner-user/bindWxWithoutPhone";

    public final String checkLatestVersion = "/ownerApp/app_version/selectAndroidLatestVersion";


    /**
     * 全局的http代理
     */
    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    HttpClient httpClient = new DefaultHttpClient();

    private static final Context context;

    static {
        // 设置网络超时时间
        client.setTimeout(10000);
        client.setConnectTimeout(9000);
        client.setResponseTimeout(10000);
        context = PetApp.getInstance();
    }

    public static HttpManage getInstance() {
        if (instance == null) {
            instance = new HttpManage();
        }
        return instance;
    }


    /**
     * 获取OTA版本号
     *
     * @param deviceId
     * @param callback
     */
    public void selectFeederVersion(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        get(host + selectFeederVersionUrl, head, params, callback);
    }

    public void feederVersionUpdate(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        get(host + feederVersionUpdate, head, params, callback);
    }

    /**
     * 获取验证码
     *
     * @param callback
     */
    public void getVerifyCode(String phoneNum, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("phone", phoneNum);
        params.put("type", 0);
        params.put("region", SharedPreferencesUtil.queryValue("area_code", "+86").equals("+86") ? 0 :
                SharedPreferencesUtil.queryValue("area_code", "+86").equals("+852") ? 1 :
                        SharedPreferencesUtil.queryValue("area_code", "+86").equals("+853") ? 2 : 3);
        get(host + getVerifyCodeUrl, head, params, callback);
    }

    /**
     * 获取验证码
     *
     * @param callback
     */
    public void getVerifyCode(String phoneNum, int type, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("phone", phoneNum);
        params.put("type", type);
        params.put("region", SharedPreferencesUtil.queryValue("area_code", "+86").equals("+86") ? 0 :
                SharedPreferencesUtil.queryValue("area_code", "+86").equals("+852") ? 1 :
                        SharedPreferencesUtil.queryValue("area_code", "+86").equals("+853") ? 2 : 3);
        get(host + getVerifyCodeUrl, head, params, callback);
    }

    /**
     * 注销前检测
     *
     * @param callback
     */
    public void cancelAccountBefore(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        String url = host + beforeCancelAccount.replace("{userId}", SharedPreferencesUtil.queryIntValue("userId") + "");
        get(url, head, params, callback);
    }

    /**
     * 检查新版本
     *
     * @param callback
     */
    public void checkLastVersion(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        String url = host + checkLatestVersion.replace("{userId}", SharedPreferencesUtil.queryIntValue("userId") + "");
        get(url, head, params, callback);
    }

    /**
     * 注销账号
     *
     * @param callback
     */
    public void cancelAccount(String phone, String vCode, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("vCode", vCode);
        String url = host + cancelAccount;
        post(url, head, params, callback);
    }

    public void modifyPhone(String phone, String vCode, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("newPhone", phone);
        params.put("vCode", vCode);
        params.put("region", SharedPreferencesUtil.queryValue("area_code", "+86").equals("+86") ? 0 :
                SharedPreferencesUtil.queryValue("area_code", "+86").equals("+852") ? 1 :
                        SharedPreferencesUtil.queryValue("area_code", "+86").equals("+853") ? 2 : 3);
        String url = host + modifyPhone;
        post(url, head, params, callback);
    }

    public void unbindWechat(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        String url = host + unbindDevUrl.replace("{userId}", SharedPreferencesUtil.queryIntValue("userId") + "");
        get(url, head, params, callback);
    }


    /**
     * 获取验证码
     *
     * @param callback
     */
    public void getVerifyCodeFromEmail(String email, String source, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("source", source);
        post(host + getVerifyCodeFromEmailUrl, head, params, callback);
    }

    /**
     * 注册
     *
     * @param callback
     */
    public void register(String phoneNum, String password, String verifyCode, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("mobile", phoneNum);
        params.put("password", password);
        params.put("verifyCode", verifyCode);
        params.put("virtualDeviceName", SystemUtil.getNewMac());
        post(host + registerUrl, head, params, callback);
    }

    /**
     * 注册
     *
     * @param callback
     */
    public void registerFromEmail(String phoneNum, String password, String verifyCode, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("email", phoneNum);
        params.put("password", password);
        params.put("verifyCode", verifyCode);
        params.put("virtualDeviceName", SystemUtil.getNewMac());
        post(host + registerFromEmailUrl, head, params, callback);
    }


    /**
     * 忘记密码
     *
     * @param callback
     */
    public void forgetPwd(String phoneNum, String password, String verifyCode, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("mobile", phoneNum);
        params.put("newPassword", password);
        params.put("verifyCode", verifyCode);
        post(host + forgetPwdUrl, head, params, callback);
    }

    /**
     * 忘记密码
     *
     * @param callback
     */
    public void forgetPwdFromEmail(String phoneNum, String password, String verifyCode, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("email", phoneNum);
        params.put("newPassword", password);
        params.put("verifyCode", verifyCode);
        post(host + forgetPwdFromEmailUrl, head, params, callback);
    }

    /**
     * 修改密码
     *
     * @param callback
     */
    public void modityPwd(String oldPassword, String password, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("oldPassword", oldPassword);
        params.put("newPassword", password);
        post(host + modityPwdUrl, head, params, callback);
    }

    /**
     * 登录
     *
     * @param callback
     */
    public void login(String phoneNum, String password, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("mobile", phoneNum);
        params.put("password", password);
        params.put("virtualDeviceName", SystemUtil.getNewMac());
        post(host + loginUrl, head, params, callback);
    }

    /**
     * 登录
     *
     * @param callback
     */
    public void loginFromEmail(String phoneNum, String password, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("email", phoneNum);
        params.put("password", password);
        params.put("virtualDeviceName", SystemUtil.getNewMac());
        post(host + loginFromEmailUrl, head, params, callback);
    }

    /**
     * 验证码登录
     *
     * @param callback
     */
    public void loginByCode(String phone, String vCode, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("vCode", vCode);
        params.put("region", SharedPreferencesUtil.queryValue("area_code", "+86").equals("+86") ? 0 :
                SharedPreferencesUtil.queryValue("area_code", "+86").equals("+852") ? 1 :
                        SharedPreferencesUtil.queryValue("area_code", "+86").equals("+853") ? 2 : 3);
        post(host + loginByCodeUrl, head, params, callback);
    }

    public void loginByWechat(String wxJson, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("wxJson", wxJson);
        post(host + loginByWechat, head, params, callback);
    }

    public void loginByWechat(String phone, String wxJson, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("wxJson", wxJson);
        post(host + bindWechat, head, params, callback);
    }


    public void loginWechat(String phone, String vCode, String json, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("vCode", vCode);
        params.put("wxJson", json);
        params.put("region", SharedPreferencesUtil.queryValue("area_code", "+86").equals("+86") ? 0 :
                SharedPreferencesUtil.queryValue("area_code", "+86").equals("+852") ? 1 :
                        SharedPreferencesUtil.queryValue("area_code", "+86").equals("+853") ? 2 : 3);
        post(host + loginByCodeUrl, head, params, callback);
    }

    public void bindWechat(String phone, String json, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("wxJson", json);
        post(host + bindWechat, head, params, callback);
    }

    /**
     * 验证码登录
     *
     * @param callback
     */
    public void loginByCodeFromEmail(String phoneNum, String verifyCode, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");

        RequestParams params = new RequestParams();
        params.put("email", phoneNum);
        params.put("verifyCode", verifyCode);
        post(host + loginByCodeFromEmailUrl, head, params, callback);
    }


    /**
     * 首页轮播图
     *
     * @param callback
     */
    public void getHomeBanner(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        post(host + homeBannerUrl, head, params, callback);
    }

    /**
     * 首页动态列表
     *
     * @param callback
     */
    public void getHomeMomentList(int page, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("needOfficial", true);
        post(host + homeMomentUrl, head, params, callback);
    }

    /**
     * 首页动态详情
     *
     * @param callback
     */
    public void getHomeMomentDetile(String id, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", id);
        post(host + homeMomentDetileUrl, head, params, callback);
    }


    /**
     * 养宠文章列表
     *
     * @param callback
     */
    public void getComentPageList(int page, int pageSize, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("pageSize", pageSize);
        post(host + contentListPageUrl, head, params, callback);
    }

    /**
     * 养宠文章详情
     *
     * @param callback
     */
    public void getComentDetile(String contentId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", contentId);
        post(host + contentDetitleUrl, head, params, callback);
    }

    /**
     * 修改密码
     *
     * @param callback
     */
    public void modifyPassword(String oldPassword, String newPassword, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("oldPassword", oldPassword);
        params.put("newPassword", newPassword);
        post(host + modifyPasswordUrl, head, params, callback);
    }

    /**
     * 获取个人用户信息
     *
     * @param callback
     */
    public void getMyInfo(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        post(host + myInfoUrl, head, params, callback);
    }

    /**
     * @param callback
     */
    public void getUserInfo(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        get(host + userInfoUrl, head, params, callback);
    }

    /**
     * 更新用户信息
     *
     * @param callback
     */
    public void updateInfo(String email, String nickName, String avatarUrl, String birthday, int sex,
                           int provinceId, String province, int cityId, String city, int regionId, String region, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("nickName", nickName);
        params.put("avatarUrl", avatarUrl);
        params.put("birthday", birthday);
        params.put("sex", sex);
        params.put("provinceId", provinceId);
        params.put("province", province);
        params.put("cityId", cityId);
        params.put("city", city);
        params.put("regionId", regionId);
        params.put("region", region);
        post(host + updateInfoUrl, head, params, callback);
    }

    public void updateInfo(String name, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("nickName", name);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        put(host + updateInfoUrl, head, params, callback);
    }

    public void updateInfoForSex(String gender, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("gender", gender);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        put(host + updateInfoUrl, head, params, callback);
    }

    public void updateInfoForWork(String work, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("job", work);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        put(host + updateInfoUrl, head, params, callback);
    }

    public void updateInfoForBirth(String birthday, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("birthdayDate", birthday);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        put(host + updateInfoUrl, head, params, callback);
    }

    public void updateInfoForCity(String city, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("city", city);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        put(host + updateInfoUrl, head, params, callback);
    }

    public void updateInfoForHead(String headD, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("photo", headD);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        put(host + updateInfoUrl, head, params, callback);
    }


    /**
     * 获取动态列表
     *
     * @param callback
     */
    public void getNewsList(int page, int pageSize, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("pageSize", pageSize);
        post(host + newsListUrl, head, params, callback);
    }

    /**
     * 获取ali sts
     *
     * @param callback
     */
    public void getAliSts(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        post(host + aliyunStsUrl, head, params, callback);
    }

    /**
     * 添加设备
     */
    public void addDev(String deviceName, String familyModel, String mac, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("deviceName", deviceName);
        params.put("familyModel", familyModel);
        params.put("mac", mac);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        post(host + addDevUrl, head, params, callback);
    }

    /**
     * 添加设备
     */
    public void addDev(String deviceName, String familyModel, BindDevFeedPlan feederPlan, String mac, String wifiName, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("deviceName", deviceName);
        params.put("familyModel", familyModel);
        params.put("feederPlan", feederPlan);
        params.put("wifiName", wifiName);
        params.put("mac", mac);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        post(host + addDevUrl, head, params, callback);
    }

    /**
     * 添加设备
     */
    public void reAddDev(String deviceName, String familyModel, BindDevFeedPlan feederPlan, String mac, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("deviceName", deviceName);
        params.put("familyModel", familyModel);
        params.put("feederPlan", feederPlan);
        params.put("mac", mac);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        post(host + reAddDevUrl, head, params, callback);
    }

    /**
     * 添加设备
     */
    public void addDev(String deviceName, String familyModel, String mac, int water, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("deviceName", deviceName);
        params.put("familyModel", familyModel);
        params.put("mac", mac);
        params.put("water", water);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        post(host + addDevUrl, head, params, callback);
    }

    /**
     * 添加设备
     */
    public void reAddDev(String deviceName, String familyModel, String mac, int water, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("deviceName", deviceName);
        params.put("familyModel", familyModel);
        params.put("mac", mac);
        params.put("water", water);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        post(host + reAddDevUrl, head, params, callback);
    }

    /**
     * 已经被绑定过的设备调用这个接口，不传计划列表
     *
     * @param deviceName
     * @param familyModel
     * @param mac
     * @param callback
     */
    public void reAddDev(String deviceName, String familyModel, String mac, String wifiName, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("deviceName", deviceName);
        params.put("familyModel", familyModel);
        params.put("mac", mac);
        params.put("wifiName", wifiName);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        post(host + reAddDevUrl, head, params, callback);
    }

    /**
     * 删除设备
     */
    public void delDev(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + delDevUrl, head, params, callback);
    }


    /**
     * 解绑设备
     */
    public void unbindDev(String id, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("virtualDeviceName", SystemUtil.getNewMac());
        post(host + unbindDevUrl, head, params, callback);
    }

    /**
     * 清除设备数据
     */
    public void clearDevData(String productKey, String deviceName, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("productKey", productKey);
        params.put("deviceName", deviceName);
        post(host + clearDataUrl, head, params, callback);
    }

    /**
     * 设备列表
     */
    public void devList(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        get(host + devListUrl, head, params, callback);
    }

    /**
     * 设备详情
     */
    public void devDetail(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + devDetailUrl, head, params, callback);
    }

    public void waterDevDetail(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + waterDevLog, head, params, callback);
    }

    public void feedDevDetail(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + feedDevLog, head, params, callback);
    }

    public void sendInductionMode(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        post(host + setWaterDevInductionMode, head, params, callback);
    }

    public void sendNightMode(int deviceId, String enable, String endTime, String startTime, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("enable", enable);
        params.put("endTime", endTime);
        params.put("startTime", startTime);
        post(host + setWaterDevNightMode, head, params, callback);
    }

    public void sendNightMode(int deviceId, String enable, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("enable", enable);
        post(host + setWaterDevNightMode, head, params, callback);
    }

    public void sendFeedNightMode(int deviceId, String enable, String endTime, String startTime, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("enable", enable);
        params.put("endTime", endTime);
        params.put("startTime", startTime);
        post(host + setFeedDevNightMode, head, params, callback);
    }

    public void sendFeedNightMode(int deviceId, String enable, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("enable", enable);
        post(host + setFeedDevNightMode, head, params, callback);
    }

    public void sendFeedChildLock(int deviceId, String enable, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("enable", enable);
        post(host + setFeedDevChildLock, head, params, callback);
    }

    public void sendSmartMode(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        post(host + setWaterDevSmartMode, head, params, callback);
    }

    public void sendHomeMode(int deviceId, String familyModel, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("familyModel", familyModel);
        post(host + setHomeMode, head, params, callback);
    }

    public void sendHomeMode(int deviceId, String familyModel, int water, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("familyModel", familyModel);
        params.put("water", water);
        post(host + setHomeMode, head, params, callback);
    }

    public void feedSendHomeMode(int deviceId, String familyModel, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("familyModel", familyModel);
        post(host + feedSetHomeMode, head, params, callback);
    }

    public void refreshFilterElement(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + refreshFilterElement, head, params, callback);
    }

    public void refreshDesiccan(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + refreshDesiccanElement, head, params, callback);
    }

    public void waterDrinkData(int deviceId, int queryType, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        params.put("queryType", queryType);
        get(host + waterDrink, head, params, callback);
    }

    public void feederData(int deviceId, int queryType, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        params.put("queryType", queryType);
        get(host + feedData, head, params, callback);
    }

    public void devShareList(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + devShareList, head, params, callback);
    }

    public void cancelShare(int deviceShareId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceShareId", deviceShareId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + cancelShare, head, params, callback);
    }

    public void getWaitTodo(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        get(host + waitToDo, head, params, callback);
    }

    public void getFeedCompareData(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + feedComapreData, head, params, callback);
    }

    public void getDevFeedPlan(int deviceId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + getDevFeedPlan, head, params, callback);
    }

    public void sendFeedingByHand(int deviceId, int feedingAmount, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("deviceId", deviceId);
        params.put("feedingAmount", feedingAmount);
        post(host + sendFeedingByHand, head, params, callback);
    }

    public void newFeedPlan(int feederPlanId, int deviceId, List<Integer> feederPlanCycle, List<Integer> delDietList,
                            List<Diet> insertDietList, List<UpdateDiet> updateDietList, String status, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("feederPlanDevice", deviceId);
        params.put("feederPlanUser", SharedPreferencesUtil.queryIntValue("userId"));
        params.put("feederPlanCycle", feederPlanCycle);
        params.put("feederPlanId", feederPlanId);
        params.put("delDietList", delDietList);
        params.put("insertDietList", insertDietList);
        params.put("updateDietList", updateDietList);
        params.put("status", status);
        put(host + newFeedPlan, head, params, callback);
    }


    /**
     * 设备详情
     */
    public void updatePositionerDevDetail(long id, String deviceAlias, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("deviceAlias", deviceAlias);
        post(host + updatePositioner, head, params, callback);
    }

    /**
     * 设备详情
     */
    public void updateDevDetail(int deviceId, String deviceAlias, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", deviceId);
        params.put("deviceName", deviceAlias);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        String url = host + updataDevUrl.replace("{deviceId}", deviceId + "");
        url = url.replace("{deviceName}", deviceAlias);
        url = url.replace("{userId}", SharedPreferencesUtil.queryIntValue("userId") + "");
        put(url, head, params, callback);
    }

    public void updateFeedDevDetail(int deviceId, String deviceAlias, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", deviceId);
        params.put("deviceName", deviceAlias);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        String url = host + updataFeedDevUrl.replace("{deviceId}", deviceId + "");
        url = url.replace("{deviceName}", deviceAlias);
        url = url.replace("{userId}", SharedPreferencesUtil.queryIntValue("userId") + "");
        put(url, head, params, callback);
    }

    /**
     * 查询设备属性
     */
    public void queryDevProp(String iotId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("iotId", iotId);
        post(host + queryDevPropUrl, head, params, callback);
    }

    /**
     * 查询直播流地址
     */
    public void queryLiveStream(String iotId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("iotId", iotId);
        post(host + queryLiveStream, head, params, callback);
    }


    /**
     * 设置设备属性
     */
    public void controlDevProp(String iotId, String items, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("iotId", iotId);
        params.put("items", items);
        post(host + controlDevPropUrl, head, params, callback);
    }

    /**
     * 宠物列表
     */
    public void petList(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("petUser", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + petListUrl, head, params, callback);
    }

    public void generateSmartPlan(String petId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("petIds", petId);
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
        get(host + smartFeedUrl, head, params, callback);
    }


    /**
     * 添加宠物
     */
    public void addPet(String petImg, String petNickname, String petBreed,
                       String petType, String petGender, String petAge, float petWeight, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("petImg", petImg);
        params.put("petNickname", petNickname);
        params.put("petBreed", petBreed);
        params.put("petType", petType);
        params.put("petGender", petGender);
        params.put("petAge", petAge);
        params.put("petWeight", petWeight);
        params.put("petUser", SharedPreferencesUtil.queryIntValue("userId") + "");
        post(host + addPetUrl, head, params, callback);
    }

    /**
     * 更新宠物
     */
    public void updatePet(Map<String, Object> params, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        put(host + updatePetUrl, head, params, callback);
    }

    /**
     * 删除宠物
     */
    public void delPet(int petId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("petId", petId + "");
        params.put("userId", SharedPreferencesUtil.queryIntValue("userId") + "");
        String url = host + deletePetUrl;
        url = url + "?petId=" + petId;
        url = url + "&userId=" + SharedPreferencesUtil.queryIntValue("userId") + "";
        delete(url, head, params, callback);
    }

    /**
     * 反馈
     */
    public void feedback(String feedback, List<String> imgUrl, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/json");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        Map<String, Object> params = new HashMap<>();
        params.put("suggestDetails", feedback);
        params.put("imgUrlList", imgUrl);
        params.put("suggestUser", SharedPreferencesUtil.queryIntValue("userId") + "");

        post(host + feedbackUrl, head, params, callback);
    }

    /**
     * 关于我们
     */
    public void aboutUs(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        post(host + aboutUrl, head, params, callback);
    }

    /**
     * 订阅设备
     */
    public void subscribeDev(long id, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("virtualDeviceName", SystemUtil.getNewMac());
        LogUtil.d("virtualDeviceName" + SystemUtil.getNewMac());
        post(host + subscribeDevUrl, head, params, callback);
    }

    /**
     * 取消订阅设备
     */
    public void unSubscribeDev(long id, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("virtualDeviceName", SystemUtil.getNewMac());
        post(host + unSubscribeDevUrl, head, params, callback);
    }

    /**
     * 发布动态
     */
    public void publish(int momentType, String moment, String sourceUrl, int status, String petIds, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("momentType", momentType);
        params.put("moment", moment);
        params.put("sourceUrl", sourceUrl);
        params.put("status", status);
        params.put("petIds", petIds);
        post(host + publishUrl, head, params, callback);
    }

    /**
     * 商城列表
     */
    public void shopList(int page, int pageSize, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("pageSize", pageSize);
        post(host + shopListUrl, head, params, callback);
    }

    /**
     * 猫砂盆使用记录
     */
    public void catUseRecord(String productKey, String deviceName, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("productKey", productKey);
        params.put("deviceName", deviceName);
        post(host + userRecordUrl, head, params, callback);
    }

    /**
     * 猫砂盆分享记录
     */
    public void devShareRecord(String devId, String deviceType, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", devId);
        params.put("deviceType", deviceType);
        post(host + devShareListUrl, head, params, callback);
    }

    public void devShare(String devId, String deviceType, String userMobile, String deviceName, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", devId);
        params.put("deviceType", deviceType);
        params.put("userMobile", userMobile);
        params.put("deviceName", deviceName);
        post(host + shareUrl, head, params, callback);
    }

    public void updateTimeZone(String iotId, int timezone, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("iotId", iotId);
        params.put("timeZone", timezone);
        post(host + updateTimezoneUrl, head, params, callback);
    }

    public void devCancelShare(long devId, long userId, String deviceType, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", devId);
        params.put("deviceType", deviceType);
        params.put("userId", userId);
        post(host + cancelShareUrl, head, params, callback);
    }


    /**
     * 检测app升级
     */
    public void checkUpdate(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        post(host + checkUpdateUrl, head, params, callback);
    }

    /**
     * 推送设置
     */
    public void pushSetting(String deviceId, String propName, String value, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", deviceId);
        params.put("propName", propName);
        params.put("value", value);
        post(host + pushSettingUrl, head, params, callback);
    }

    /**
     * 绑定定位器
     *
     * @param callback
     */
    public void bindPositioner(String id, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("locatorKey", id);
        post(host + bindPositionerUrl, head, params, callback);
    }

    /**
     * 绑定定位器
     *
     * @param callback
     */
    public void unbindPositioner(String imei, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("imei", imei);
        post(host + unBindPositionerUrl, head, params, callback);
    }

    public void getPositionerList(final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        post(host + positionerListUrl, head, params, callback);
    }

    public void bindPet(String id, long petId, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("petId", petId);
        post(host + bindPetUrl, head, params, callback);
    }

    public void positionerDetail(String imei, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("imei", imei);
        post(host + positionerDetail, head, params, callback);
    }

    public void positionerInfo(String imei, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("imei", imei);
        post(host + positionerPetInfo, head, params, callback);
    }

    public void positionerLog(String imei, String startTime, String endTime, final ResultCallback<String> callback) {
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("x-terminal-type", "Android");
        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));

        RequestParams params = new RequestParams();
        params.put("imei", imei);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        post(host + positionerLog, head, params, callback);
    }

    //=========================================================================================

    public AsyncHttpClient post(String url, Map<String, String> params, ResultCallback callback) {
        // 请求entity
        try {
            StringEntity entity = null;
            entity = new StringEntity(new Gson().toJson(params), "UTF-8");
            client.post(context, url, entity, "application/json", callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public AsyncHttpClient post(String url, RequestParams params, ResultCallback callback) {
        // 请求entity
        try {
            client.post(context, url, params, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AsyncHttpClient post(String url, Map<String, String> headers, RequestParams params, ResultCallback callback) {
        //请求entity
        Header[] headersdata = map2Header(headers);
        client.post(context, url, headersdata, params, "application/json", callback);
        return client;
    }

    public AsyncHttpClient get(String url, Map<String, String> headers, RequestParams params, ResultCallback callback) {
        //请求entity
        Header[] headersdata = map2Header(headers);
        client.get(context, url, headersdata, params, callback);
        return client;
    }

    private void get(String url, Map<String, String> headers, ResultCallback callback) {
        Header[] headersdata = new Header[headers.size()];
        int i = 0;
        for (String key : headers.keySet()) {
            headersdata[i] = new XHeader(key, headers.get(key));
            i++;
        }
        client.get(context, url, headersdata, null, callback);
    }

    private void delete(String url, Map<String, String> headers, ResultCallback callback) {
        Header[] headersdata = new Header[headers.size()];
        int i = 0;
        for (String key : headers.keySet()) {
            headersdata[i] = new XHeader(key, headers.get(key));
            i++;
        }
        client.delete(context, url, headersdata, null, callback);
    }

    private void delete(String url, Map<String, String> headers, Map<String, Object> params, final ResultCallback callback) {
        final HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);

        StringEntity entity = null;
        try {
            entity = new StringEntity(new Gson().toJson(params), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // json 处理

        httpDelete.setHeader("Content-Type", "application/json; charset=UTF-8");//or addHeader();

        httpDelete.setHeader("X-Requested-With", "XMLHttpRequest");

        for (String key : headers.keySet()) {
            Header header = new XHeader(key, headers.get(key));
            httpDelete.addHeader(header);
        }

        //设置HttpDelete的请求参数

        httpDelete.setEntity(entity);

        httpDelete.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);

        httpDelete.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpResponse response = null;
                    try {
                        response = httpClient.execute(httpDelete);
                        /**请求发送成功，并得到响应**/
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            /**读取服务器返回过来的json字符串数据**/
                            String strResult = EntityUtils.toString(response.getEntity());

                            callback.onSuccess(response.getStatusLine().getStatusCode(), strResult);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            callback.onError(null, null);
            e.printStackTrace();
        }
    }


    public AsyncHttpClient post(String url, Map<String, String> headers, Map<String, Object> params, ResultCallback callback) {
        // 请求entity
        StringEntity entity = null;
        try {
            entity = new StringEntity(new Gson().toJson(params), "UTF-8");
            Log.e("hello", "post entity:" + new Gson().toJson(params));
        } catch (Exception e) {
            e.printStackTrace();
            return client;
        }
        Header[] headersdata = new Header[headers.size()];
        int i = 0;
        for (String key : headers.keySet()) {
            headersdata[i] = new XHeader(key, headers.get(key));
            i++;
        }
        client.post(context, url, headersdata, entity, "application/json", callback);
        return client;
    }


    private void put(String url, Map<String, String> headers, Map<String, Object> params, ResultCallback callback) {
        // 请求entity
        StringEntity entity = null;
        try {
            entity = new StringEntity(new Gson().toJson(params), "UTF-8");
            LogUtil.d(new Gson().toJson(params));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Header[] headersdata = new Header[headers.size()];
        int i = 0;
        for (String key : headers.keySet()) {
            headersdata[i] = new XHeader(key, headers.get(key));
            i++;
        }
        client.put(context, url, headersdata, entity, "application/json", callback);
    }

    public Header[] map2Header(Map<String, String> headers) {
        if (headers == null) {
            return null;
        }
        Header[] headersdata = new Header[headers.size()];
        int i = 0;
        for (String key : headers.keySet()) {
            headersdata[i] = new XHeader(key, headers.get(key));
            i++;
        }
        return headersdata;
    }

    public static abstract class ResultCallback<T> extends TextHttpResponseHandler {
        Type mType;
        private Gson mGson;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
            mGson = Public.getGsonDetTime();
        }

        @Override
        public void onFailure(int code, Header[] headers, String msg, Throwable throwable) {
            try {
                if (code > 0 && !TextUtils.isEmpty(msg)) {
                    ErrorEntity errorEntity = new ErrorEntity();
                    errorEntity.error = new Error();
                    errorEntity.error.setMsg(code + msg);
                    onError(headers, errorEntity.error);
                } else {

                    ErrorEntity errorEntity = new ErrorEntity();
                    errorEntity.error = new Error();
                    if (throwable != null) {
                        errorEntity.error.setMsg(code + msg);//throwable.getMessage()
                    }
                    errorEntity.error.setCode(201);
                    onError(headers, errorEntity.error);
                }
            } catch (Exception e) {
                ErrorEntity errorEntity = new ErrorEntity();
                errorEntity.error = new Error();
                errorEntity.error.setMsg("服务器异常");
                onError(headers, errorEntity.error);
            }
        }

        @Override
        public void onSuccess(int code, Header[] headers, String msg) {
            if (msg.contains("登录用户无操作该设备权限") || msg.contains("登录状态已过期")) {
                ToastUtil.getInstance().shortToast("登录用户无操作该设备权限");
                JPushInterface.stopPush(PetApp.getInstance());
                JPushInterface.deleteAlias(PetApp.getInstance(), 1);
                //反注册
                PetsManager.getInstance().deletePet();
                SharedPreferencesUtil.deleteAllValue();
                Intent intent = new Intent(PetApp.getInstance(), LoginByCodeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PetApp.getInstance().startActivity(intent);
            }

            if (mType == String.class) {
                onSuccess(code, (T) msg);
            } else {
                T o = mGson.fromJson(msg, mType);
                onSuccess(code, o);
            }
        }


        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            System.out.println(superclass);
            if (superclass instanceof Class) {
                System.out.println(superclass);
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Header[] headers, Error error);

        public abstract void onSuccess(int code, T response);
    }


    public static class Error {
        private int code;
        private String msg;

        public void setCode(int code) {
            this.code = code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }

    public static class ErrorEntity {
        public Error error;
    }


}
