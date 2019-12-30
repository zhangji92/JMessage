package com.dome.push.mvp.view;

import android.widget.TextView;

import com.dome.push.base.IBaseView;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.mvp.view
 * ClassName:      IMyView
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-12 上午10:41
 * UpdateUser:     更新者
 * UpdateDate:     19-12-12 上午10:41
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public interface IMyView extends IBaseView {

    /** 头像 */
    RoundedImageView head();

    TextView name();
    TextView medal();
    TextView grade();
    TextView attention();
    TextView fan();
    TextView dynamic();

}
