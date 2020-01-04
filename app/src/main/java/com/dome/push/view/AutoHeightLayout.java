package com.dome.push.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dome.push.R;
import com.dome.push.listener.OnResizeListener;
import com.dome.push.util.EmoticonsKeyboardUtils;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.view
 * ClassName:      AutoHeightLayout
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-23 下午1:58
 * UpdateUser:     更新者
 * UpdateDate:     19-12-23 下午1:58
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public abstract class AutoHeightLayout extends KeyBordLayout implements OnResizeListener {

    private static final int ID_CHILD = R.id.id_autolayout;

    protected int mMaxParentHeight;
    protected int mSoftKeyboardHeight;
    protected boolean mConfigurationChangedFlag = false;

    public AutoHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSoftKeyboardHeight = EmoticonsKeyboardUtils.getDefKeyboardHeight();
        addOnResizeListener(this);
    }

    @SuppressLint("ResourceType")
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int childSum = getChildCount();
        if (childSum > 1) {
            throw new IllegalStateException("can host only one direct child");
        }
        super.addView(child, index, params);
        if (childSum == 0) {
            if (child.getId() < 0) {
                child.setId(ID_CHILD);
            }
            LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(ALIGN_PARENT_BOTTOM);
            child.setLayoutParams(paramsChild);
        } else if (childSum == 1) {
            LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(ABOVE, ID_CHILD);
            child.setLayoutParams(paramsChild);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        onSoftKeyboardHeightChanged(mSoftKeyboardHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mMaxParentHeight == 0) {
            mMaxParentHeight = h;
        }
    }

    public void updateMaxParentHeight(int maxParentHeight) {
        this.mMaxParentHeight = maxParentHeight;
        if(maxParentHeightChangeListener != null){
            maxParentHeightChangeListener.onMaxParentHeightChange(maxParentHeight);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mConfigurationChangedFlag = true;
        screenHeight = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mConfigurationChangedFlag){
            mConfigurationChangedFlag = false;
            Rect r = new Rect();
            ((Activity) getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            if (screenHeight == 0) {
                screenHeight = r.bottom;
            }
            int nowHeight = screenHeight - r.bottom;
            mMaxParentHeight = nowHeight;
        }

        if (mMaxParentHeight != 0) {
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int expandSpec = MeasureSpec.makeMeasureSpec(mMaxParentHeight, heightMode);
            super.onMeasure(widthMeasureSpec, expandSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onSoftPop(final int height) {
        if (mSoftKeyboardHeight != height) {
            mSoftKeyboardHeight = height;
            EmoticonsKeyboardUtils.setDefKeyboardHeight(mSoftKeyboardHeight);
            onSoftKeyboardHeightChanged(mSoftKeyboardHeight);
        }
    }

    @Override
    public void onSoftClose() { }

    public abstract void onSoftKeyboardHeightChanged(int height);

    private OnMaxParentHeightChangeListener maxParentHeightChangeListener;


    public interface OnMaxParentHeightChangeListener {
        void onMaxParentHeightChange(int height);
    }

    public void setOnMaxParentHeightChangeListener(OnMaxParentHeightChangeListener listener) {
        this.maxParentHeightChangeListener = listener;
    }
}
