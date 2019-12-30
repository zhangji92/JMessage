package com.dome.push.base;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.dome.push.R;
import com.dome.push.exception.ExceptionTool;
import com.dome.push.util.AndroidWorkaroundTool;
import com.dome.push.util.ToastTool;
import com.dome.push.view.LoadDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ProjectName:    Bicycle
 * Package:        com.chetianxia.yundanche
 * ClassName:      BaseActivity
 * Description:    activity基础类，所有Activity必须集成该类
 * Author:         张继
 * CreateDate:     2019/7/5 21:16
 * UpdateUser:     更新者
 * UpdateDate:     2019/7/5 21:16
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public abstract class BaseActivity<P extends BasePresenter> extends FragmentActivity implements IBaseView {

    private Unbinder bind = null;
    protected P presenter = null;
    private LoadDialog loadDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //home键之后还是原来界面
        //home键之后还是原来界面
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        if (layoutResId() != 0) {
            setContentView(layoutResId());
        }
        if (bind == null) {
            bind = ButterKnife.bind(this);
        }
        presenter = createPresenter();
        setStateBar();
        initTitle(savedInstanceState);
        initData();
    }

    /**
     * 布局 id
     *
     * @return 返回布局id
     */
    protected abstract int layoutResId();

    /**
     * 设置标题信息
     *
     * @param savedInstanceState activity意外挂掉时保存的临时信息
     */
    protected abstract void initTitle(Bundle savedInstanceState);

    /**
     * 初始化逻辑
     */
    protected abstract void initData();

    /**
     * 初始化逻辑类
     *
     * @return 返回逻辑引用
     */
    protected abstract P createPresenter();

    protected void setStateBar() {
        if (AndroidWorkaroundTool.checkDeviceHasNavigationBar(this)) {
            AndroidWorkaroundTool.assistActivity(findViewById(android.R.id.content));
            getWindow().setNavigationBarColor(this.getResources().getColor(android.R.color.white));
        }
        // Android 状态栏背景适配
        AndroidWorkaroundTool.setAndroidNativeLightStatusBar(this, true);
    }


    @Override
    protected void onStop() {
        super.onStop();
        hideDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
            bind = null;
        }
        if (presenter != null) {
            presenter.detachView();
            presenter = null;
        }
    }


    @Override
    public void showDialog() {
        if (loadDialog == null) {
            loadDialog = new LoadDialog(this);
        }
        loadDialog.show();
    }

    @Override
    public void onError(ExceptionTool exception) {

        ToastTool.error(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        hideDialog();
    }

    @Override
    public void hideDialog() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
            loadDialog.cancel();
        }

    }



    @Override
    public void showMsg(String msg, int shortOrLong) {
        if (shortOrLong == 0) {
            ToastTool.error(this, msg, Toast.LENGTH_SHORT, true).show();
        } else {
            ToastTool.error(this, msg, Toast.LENGTH_LONG, true).show();
        }
    }

    @Override
    public FragmentActivity getBaseActivity() {
        return this;
    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        setOverridePendingTransition(true);
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        setOverridePendingTransition(true);
    }

    @Override
    public void finish() {
        super.finish();
        setOverridePendingTransition(false);
    }

    /**
     * 左侧进入右侧返回必须重写这个方法 当按下返回键后
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setOverridePendingTransition(false);
    }

    private void setOverridePendingTransition(boolean leftOrRight) {
        if (leftOrRight) {
            // 新页面从右边进入
            overridePendingTransition(R.anim.push_right_in,
                    R.anim.push_right_out);
        } else {
            // 上一个页面从左边进入
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
        }
    }
}
