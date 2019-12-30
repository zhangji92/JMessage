package com.dome.push.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.dome.push.R;
import com.dome.push.base.BaseActivity;
import com.dome.push.mvp.presenter.LoginPresenter;
import com.dome.push.mvp.view.ILoginView;
import com.dome.push.util.AndroidWorkaroundTool;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.activity
 * ClassName:      LoginActivity
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-19 下午3:58
 * UpdateUser:     更新者
 * UpdateDate:     19-12-19 下午3:58
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginView {
    @BindView(R.id.layout_status)
    Toolbar layoutStatus;
    @BindView(R.id.login_name)
    EditText loginName;
    @BindView(R.id.login_pwd)
    EditText loginPwd;

    @Override
    protected int layoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initTitle(Bundle savedInstanceState) {
        AndroidWorkaroundTool.isAndroidp(layoutStatus, this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public EditText name() {
        return loginName;
    }

    @Override
    public EditText pwd() {
        return loginPwd;
    }

    @OnClick({R.id.login_register, R.id.login_login})
    public void onViewClicked(View view) {
      presenter.click(view);
    }
}
