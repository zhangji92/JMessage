package com.dome.push.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.dome.push.listener.OnResizeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.view
 * ClassName:      KeyBordLayout
 * Description:    键盘监听布局
 * Author:         张继
 * CreateDate:     19-12-23 上午11:35
 * UpdateUser:     更新者
 * UpdateDate:     19-12-23 上午11:35
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class KeyBordLayout extends RelativeLayout {


    private int oldHeight = -1;
    private int nowHeight = -1;
    protected int screenHeight = 0;
    protected boolean softKeyboardPop = false;

    public KeyBordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            if (screenHeight == 0) {
                screenHeight = r.bottom;
            }
            nowHeight = screenHeight - r.bottom;
            if (oldHeight != -1 && nowHeight != oldHeight) {
                if (nowHeight > 0) {
                    softKeyboardPop = true;
                    if (mListenerList != null) {
                        for (OnResizeListener l : mListenerList) {
                            l.onSoftPop(nowHeight);
                        }
                    }
                } else {
                    softKeyboardPop = false;
                    if (mListenerList != null) {
                        for (OnResizeListener l : mListenerList) {
                            l.onSoftClose();
                        }
                    }
                }
            }
            oldHeight = nowHeight;
        });
    }

    public boolean isSoftKeyboardPop() {
        return softKeyboardPop;
    }

    private List<OnResizeListener> mListenerList;

    public void addOnResizeListener(OnResizeListener l) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        mListenerList.add(l);
    }

}
