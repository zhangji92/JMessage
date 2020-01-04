package com.dome.push.mvp.presenter;

import android.view.View;

import com.dome.push.MyApplication;
import com.dome.push.R;
import com.dome.push.activity.LoginActivity;
import com.dome.push.base.BasePresenter;
import com.dome.push.base.BaseUrl;
import com.dome.push.mvp.view.IMyView;
import com.dome.push.util.ActivityTool;
import com.dome.push.util.GlideTool;
import com.dome.push.util.SPTool;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.mvp.presenter
 * ClassName:      MyPresenter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-12 上午10:42
 * UpdateUser:     更新者
 * UpdateDate:     19-12-12 上午10:42
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class MyPresenter extends BasePresenter<IMyView> {
    public MyPresenter(IMyView reference) {
        super(reference);
    }

    public void init() {

        refreshInfo();

    }


    public void refreshInfo() {
        if (MyApplication.getApp().isLoginFlag()) {
            // 加载头像
            GlideTool.load(getView().getBaseActivity(), R.mipmap.icon_head, getView().head());
            getView().name().setText(MyApplication.getApp().getUserName());
            getView().grade().setText("LV0");
            getView().attention().setText("0\n 关注");
            getView().fan().setText("0\n 粉丝");
            getView().dynamic().setText("0\n 动态");
        } else {
            getView().head().setImageResource(R.mipmap.ic_launcher);
            getView().name().setText(getString(R.string.my_login));
            getView().grade().setVisibility(View.GONE);
            getView().attention().setText("--\n 关注");
            getView().fan().setText("--\n 粉丝");
            getView().dynamic().setText("--\n 动态");
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_head:
                break;
            case R.id.my_name:
                if (!BaseUrl.IS_LOGIN_FLAG) {
                    ActivityTool.startActivity(LoginActivity.class);
                    return;
                }

                break;
            case R.id.my_medal:
                break;
            case R.id.my_grade:
                break;
            case R.id.my_attention:
                break;
            case R.id.my_fan:
                break;
            case R.id.my_dynamic:
                break;
        }
    }

    public void cleanUserInfo() {
        SPTool.getInstance().remove(BaseUrl.USER_NAME);
        SPTool.getInstance().remove(BaseUrl.APP_KEY);
        MyApplication.getApp().setUserName("");
        MyApplication.getApp().setAppKey("");
        SPTool.getInstance().remove(BaseUrl.PUSH_IS_LOGIN);
        MyApplication.getApp().setLoginFlag(false);
    }
}
