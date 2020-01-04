package com.dome.push.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.dome.push.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.view
 * ClassName:      FunPagerIndicator
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-24 上午9:22
 * UpdateUser:     更新者
 * UpdateDate:     19-12-24 上午9:22
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class FunPagerIndicator extends LinearLayout {

    private List<ImageView> imageViews;

    public FunPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        imageViews = new ArrayList<>();
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    public void init(int size) {
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LayoutParams(30, 30);
            params.leftMargin = 40;
            if (i == 0) {
                imageView.setImageResource(R.drawable.solid_oval_a6);
            } else {
                imageView.setImageResource(R.drawable.solid_oval_d8);
            }
            imageView.setLayoutParams(params);
            imageViews.add(imageView);
            this.addView(imageView);
        }
    }

    public void scrollPoint(int start, int next) {
        if (start < 0 || next < 0 || next == start) {
            start = next = 0;
        }
        ImageView startPoint = imageViews.get(start);
        ImageView nextPoint = imageViews.get(next);
        startPoint.setImageResource(R.drawable.solid_oval_a6);
        nextPoint.setImageResource(R.drawable.solid_oval_d8);
    }
}
