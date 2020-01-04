package com.dome.push.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dome.push.R;
import com.dome.push.base.BaseFragment;
import com.dome.push.mvp.presenter.MyPresenter;
import com.dome.push.mvp.view.IMyView;
import com.dome.push.util.AndroidWorkaroundTool;
import com.dome.push.view.RxTitle;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.fragment
 * ClassName:      MyFragment
 * Description:    我的
 * Author:         张继
 * CreateDate:     19-12-12 上午10:41
 * UpdateUser:     更新者
 * UpdateDate:     19-12-12 上午10:41
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class MyFragment extends BaseFragment<MyPresenter> implements IMyView, View.OnClickListener {

    @BindView(R.id.layout_status)
    Toolbar layoutStatus;
     @BindView(R.id.my_toolbar)
     RxTitle myToolbar;

    @BindView(R.id.my_head)
    RoundedImageView myHead;
    @BindView(R.id.my_name)
    TextView myName;
    @BindView(R.id.my_medal)
    TextView myMedal;
    @BindView(R.id.my_grade)
    TextView myGrade;
    @BindView(R.id.my_attention)
    TextView myAttention;
    @BindView(R.id.my_fan)
    TextView myFan;
    @BindView(R.id.my_dynamic)
    TextView myDynamic;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        AndroidWorkaroundTool.isAndroidp(layoutStatus, getActivity());
        myToolbar.setLeftIconOnClickListener(this);

    }

    @Override
    protected void initData() {
        presenter.init();
    }

    @Override
    protected MyPresenter createPresenter() {
        return new MyPresenter(this);
    }

    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    public RoundedImageView head() {
        return myHead;
    }

    @Override
    public TextView name() {
        return myName;
    }

    @Override
    public TextView medal() {
        return myMedal;
    }

    @Override
    public TextView grade() {
        return myGrade;
    }

    @Override
    public TextView attention() {
        return myAttention;
    }

    @Override
    public TextView fan() {
        return myFan;
    }

    @Override
    public TextView dynamic() {
        return myDynamic;
    }

    @OnClick({R.id.my_head, R.id.my_name, R.id.my_medal, R.id.my_grade, R.id.my_attention, R.id.my_fan, R.id.my_dynamic})
    public void onViewClicked(View view) {
        presenter.onClick(view);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            presenter.init();
        }
    }

    @Override
    public void onClick(View v) {
        JMessageClient.logout();
        presenter.cleanUserInfo();
        presenter.refreshInfo();
    }

}
