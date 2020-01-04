package com.dome.push.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.dome.push.exception.ExceptionTool;
import com.dome.push.util.ToastTool;
import com.dome.push.view.LoadDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * ProjectName:    Ok_RxJava_Retrofit_Mvp
 * Package:        com.ok.java.retrofit.base
 * ClassName:      BaseFragment
 * Description:    java类作用描述
 * Author:         张继
 * CreateDate:     2019/4/24 23:00
 * UpdateUser:     更新者
 * UpdateDate:     2019/4/24 23:00
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IBaseView {

    public P presenter;
    private LoadDialog mLoad;
    private Context mContext;
    private Unbinder mBind;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutId() != 0) {
            return inflater.inflate(getLayoutId(), container, false);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mBind == null) {
            mBind = ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = createPresenter();
        this.initToolbar(savedInstanceState);
        this.initData();
    }


    /**
     * 获取布局ID
     *
     * @return 返回布局id
     */
    protected abstract int getLayoutId();

    /**
     * 处理顶部title
     *
     * @param savedInstanceState 上一个Fragment销毁时保存的状态
     */
    protected abstract void initToolbar(Bundle savedInstanceState);


    /**
     * 数据初始化操作
     */
    protected abstract void initData();
    /**
     * 创建Presenter对象
     *
     * @return 返回Presenter对象
     */
    protected abstract P createPresenter();


    @Override
    public void showDialog() {
        if (mLoad == null) {
            mLoad = new LoadDialog(mContext);
        }
        mLoad.show();
    }

    @Override
    public void onError(ExceptionTool exception) {
        //if (exception.getCode() == -7) {
        //    if (BaseUrl.JUST_LOGIN) {
        //        BaseUrl.JUST_LOGIN = false;
        //        //ToolUtil.clean();
        //        //ActivityUtils.startActivity(LoginActivity.class);
        //        //ActivityUtils.finishAllActivitiesExceptNewest();
        //    }
        //    return;
        //}
        ToastTool.error(exception.getMessage());
        hideDialog();
    }

    @Override
    public void hideDialog() {
        if (mLoad != null && mLoad.isShowing()) {
            mLoad.dismiss();
            mLoad.cancel();
        }
    }

    @Override
    public FragmentActivity getBaseActivity() {
        return getActivity();
    }

    @Override
    public void showMsg(String msg, int shortOrLong) {
        if (shortOrLong == 0) {
            ToastTool.error(getActivity(), msg, Toast.LENGTH_SHORT, true).show();
        } else {
            ToastTool.error(getActivity(), msg, Toast.LENGTH_LONG, true).show();
        }
    }


    @Nullable
    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
        if (mBind != null) {
            mBind.unbind();
            mBind = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
