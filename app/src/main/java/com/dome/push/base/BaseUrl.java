package com.dome.push.base;

import com.dome.push.util.SPTool;

/**
 * ProjectName:    Bicycle
 * Package:        com.chetianxia.yundanche.base
 * ClassName:      BaseUrl
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-10-19 下午8:42
 * UpdateUser:     更新者
 * UpdateDate:     19-10-19 下午8:42
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public final class BaseUrl {


    public static final String SHARE = "bicycle_share";
    public static final String UNLOCK_MILLIS = "localMillis";
    public static final String ORDER_FLAG = "ORDER_FLAG";
    public static final String TARGET_APP_KEY = "push_target_app_key";
    public static final String TARGET_USER_NAME = "push_target_user_name";
    public static final int TAKE_VIDEO = 88;
    public static final int TAKE_PHOTO = 99;

    private BaseUrl() {
        throw new UnsupportedOperationException("you can t instantiate me");
    }

    //public static final String BASE_URL = "http://ydclocation.tpddns.cn:9898";
    public static final String BASE_URL = "http://app.yundanche.com.cn:9002";//正式库
    //private static final String BASE_URL = "http://ydclocation.tpddns.cn:9899";


    public static final String TOKEN = "push_token";
    public static final String USER_ID = "push_id";
    public static final String S_KEY = "push_key";
    public static final String CPY_ID = "push_cpy_id";
    public static final String PHONE = "push_phone";
    public static final String PUSH_IS_LOGIN = "push_is_login";
    public static final int TYPE_DATA = 1;
    public static final int TYPE_EMPTY = 2;

    public static String MILLIS = "millis";
    // 是否登录
    public static boolean IS_LOGIN_FLAG = SPTool.getInstance().getBoolean(PUSH_IS_LOGIN);
    public static boolean IS_GO_REGISTER = false;// 是否去注册
    public static final String USER_NAME = "push_user_name";// 登录的用户名
    public static final String APP_KEY = "push_app_key";// 登录的appkey


}
