package com.dome.push;

import android.app.Application;
import android.os.StrictMode;

import com.dome.push.base.BaseUrl;
import com.dome.push.receiver.NotificationClickEventReceiver;
import com.dome.push.util.ApplicationTool;
import com.dome.push.util.SPTool;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push
 * ClassName:      MyApplication
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-19 下午3:35
 * UpdateUser:     更新者
 * UpdateDate:     19-12-19 下午3:35
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class MyApplication extends Application {
    private static MyApplication app;
    private boolean isLoginFlag;
    public static String FILE_DIR = "sdcard/uuj/recvFiles/";
    public static String THUMP_PICTURE_DIR;
    private String appKey = "";
    private String userName = "";
    private String targetAppKey = "";
    private String targetUserName = "";

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        THUMP_PICTURE_DIR = this.getFilesDir().getAbsolutePath() + "/stock";
        ApplicationTool.init(this);
        isLoginFlag = SPTool.getInstance().getBoolean(BaseUrl.PUSH_IS_LOGIN);
        userName = SPTool.getInstance().getString(BaseUrl.USER_NAME);
        appKey = SPTool.getInstance().getString(BaseUrl.APP_KEY);
        // 初始化极光推送
        JPushInterface.init(this);
        JPushInterface.setDebugMode(false);
        // 极光IM聊天初始化
        JMessageClient.init(getApplicationContext(), true);
        JMessageClient.setDebugMode(true);
        //设置Notification的模式 //JMessageClient.FLAG_NOTIFY_WITH_VIBRATE
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_SOUND | JMessageClient.FLAG_NOTIFY_WITH_LED );
        new NotificationClickEventReceiver(this);


        // 获取uri权限
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }


    public static MyApplication getApp() {
        return app;
    }

    public boolean isLoginFlag() {
        return isLoginFlag;
    }

    public void setLoginFlag(boolean loginFlag) {
        isLoginFlag = loginFlag;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTargetAppKey() {
        return targetAppKey;
    }

    public void setTargetAppKey(String targetAppKey) {
        this.targetAppKey = targetAppKey;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }
}
