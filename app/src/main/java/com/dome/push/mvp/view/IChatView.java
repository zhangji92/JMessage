package com.dome.push.mvp.view;

import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.dome.push.base.IBaseView;
import com.dome.push.view.EmoticonsRelative;
import com.dome.push.view.RxTitle;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.mvp.view
 * ClassName:      IChatView
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-13 下午3:46
 * UpdateUser:     更新者
 * UpdateDate:     19-12-13 下午3:46
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public interface IChatView extends IBaseView {
    EmoticonsRelative getRelative();

    RecyclerView getChatRecord();

    RxTitle getRxTitle();
    RelativeLayout getChatRoot();
}
