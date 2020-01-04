package com.dome.push.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.dome.push.R;
import com.dome.push.base.BaseActivity;
import com.dome.push.base.BasePresenter;
import com.dome.push.util.ImageSource;
import com.dome.push.view.RxScaleImageView;

import butterknife.BindView;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.activity
 * ClassName:      ImageScaleActivity
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-26 下午1:52
 * UpdateUser:     更新者
 * UpdateDate:     19-12-26 下午1:52
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ImageScaleActivity extends BaseActivity {
    @BindView(R.id.scale_image)
    RxScaleImageView scaleImage;

    @Override
    protected int layoutResId() {
        return R.layout.activity_image_scale;
    }

    @Override
    protected void initTitle(Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String path = extras.getString("path");
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            scaleImage.setImage(ImageSource.bitmap(bitmap));
        }
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

}
