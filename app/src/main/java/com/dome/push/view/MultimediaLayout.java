package com.dome.push.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dome.push.R;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.view
 * ClassName:      MultimediaLayout
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-24 上午11:09
 * UpdateUser:     更新者
 * UpdateDate:     19-12-24 上午11:09
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class MultimediaLayout extends LinearLayout {

    private TextView multimediaPicture;
    private TextView multimediaCamera;
    private TextView multimediaFile;

    public MultimediaLayout(Context context) {
        this(context, null);
    }

    public MultimediaLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_multimedia, this);
        initView();
    }

    private void initView() {
        multimediaPicture = findViewById(R.id.multimedia_picture);
        multimediaCamera = findViewById(R.id.multimedia_camera);
        multimediaFile = findViewById(R.id.multimedia_file);
    }

    public void listener(OnClickListener listener) {
        multimediaPicture.setOnClickListener(listener);
        multimediaCamera.setOnClickListener(listener);
        multimediaFile.setOnClickListener(listener);
    }


}
