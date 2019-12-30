package com.dome.push.activity;

import android.os.Bundle;

import com.dome.push.R;
import com.dome.push.base.BaseActivity;
import com.dome.push.mvp.presenter.MainPresenter;
import com.dome.push.mvp.view.IMainView;
import com.dome.push.util.AndroidWorkaroundTool;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView {


    @BindView(R.id.main_tab)
    TabLayout mainTab;

    @Override
    protected int layoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initTitle(Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {

        presenter.initData();
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public TabLayout getTab() {
        return mainTab;
    }


    @Override
    protected void setStateBar() {
        AndroidWorkaroundTool.setAndroidNativeLightStatusBar(this,true);
    }
}
