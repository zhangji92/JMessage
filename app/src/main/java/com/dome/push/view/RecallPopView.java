package com.dome.push.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dome.push.R;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.view
 * ClassName:      RecallPopView
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-30 上午9:10
 * UpdateUser:     更新者
 * UpdateDate:     19-12-30 上午9:10
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class RecallPopView extends PopupWindow {

    private TextView recallContent;

    public RecallPopView(Context context) {
        this(context, null);
    }

    private RecallPopView(Context context, AttributeSet attrs) {
        super(context, attrs, R.style.default_dialog_style);
        View view = LayoutInflater.from(context).inflate(R.layout.view_recall, null, false);
        recallContent = view.findViewById(R.id.recall_content);
        //setTouchable(true);
        setOutsideTouchable(true);
        setContentView(view);
    }

    public RecallPopView setContent(String content) {
        recallContent.setText(content);
        return this;
    }

    public int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    public void measure(int width,int height) {
        View contentView = getContentView();
        contentView.measure(makeDropDownMeasureSpec(width),makeDropDownMeasureSpec(height));
    }

    public void click(View.OnClickListener listener) {
        recallContent.setOnClickListener(listener);
    }
}
