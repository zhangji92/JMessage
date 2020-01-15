package com.dome.push.fragment;

import android.os.Bundle;

import com.dome.push.R;
import com.dome.push.base.BaseFragment;
import com.dome.push.mvp.presenter.PicturePresenter;
import com.dome.push.mvp.view.IPictureView;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.fragment
 * ClassName:      PictureFragment
 * Description:
 * Author:         张继
 * CreateDate:     2020/1/15 17:06
 * UpdateUser:     更新者
 * UpdateDate:     2020/1/15 17:06
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class PictureFragment extends BaseFragment<PicturePresenter> implements IPictureView {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_picture;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected PicturePresenter createPresenter() {
        return new PicturePresenter(this);
    }

    public static PictureFragment newInstance() {
        return new PictureFragment();
    }

}
