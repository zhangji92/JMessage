package com.dome.push.mvp.presenter;

import android.view.View;

import com.dome.push.MyApplication;
import com.dome.push.R;
import com.dome.push.activity.MainActivity;
import com.dome.push.activity.RegisterActivity;
import com.dome.push.base.BasePresenter;
import com.dome.push.base.BaseUrl;
import com.dome.push.mvp.view.ILoginView;
import com.dome.push.util.ActivityTool;
import com.dome.push.util.SPTool;
import com.dome.push.util.ToastTool;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.mvp.presenter
 * ClassName:      LoginPresenter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-13 上午9:22
 * UpdateUser:     更新者
 * UpdateDate:     19-12-13 上午9:22
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class LoginPresenter extends BasePresenter<ILoginView> {
    public LoginPresenter(ILoginView reference) {
        super(reference);
    }

    public void click(View view) {

        String username = getView().name().getText().toString().trim();
        String password = getView().pwd().getText().toString().trim();
        switch (view.getId()) {
            case R.id.login_register:
                if (BaseUrl.IS_GO_REGISTER) {
                    ActivityTool.startActivity(RegisterActivity.class);
                    return;
                }
                register(username, password);
                break;
            case R.id.login_login:
                register(username, password);
                break;
        }
    }

    private void login(String username, String password) {
        JMessageClient.login(username, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (0 == i) {// 801003用户不存在
                    setUserInfo();
                } else {
                    ToastTool.error(s);
                }
            }
        });
    }

    private void register(String username, String password) {
        JMessageClient.register(username, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                // 898001用户已存在
                if (0 == i) {
                    login(username, password);
                } else if (898001 == i) {
                    login(username, password);
                } else {
                    ToastTool.error(s);
                }
            }
        });
    }

    private void setUserInfo() {
        UserInfo myInfo = JMessageClient.getMyInfo();
        String userName = myInfo.getUserName();
        String appKey = myInfo.getAppKey();
        SPTool.getInstance().put(BaseUrl.USER_NAME, userName);
        SPTool.getInstance().put(BaseUrl.APP_KEY, appKey);
        MyApplication.getApp().setUserName(userName);
        MyApplication.getApp().setAppKey(appKey);
        SPTool.getInstance().put(BaseUrl.PUSH_IS_LOGIN, true);
        MyApplication.getApp().setLoginFlag(true);
        ActivityTool.finishAllActivitiesExceptNewest();
        ActivityTool.startActivity(MainActivity.class);
    }


}
