package com.dome.push.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.util
 * ClassName:      EmoticonsKeyboardUtils
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-23 上午11:01
 * UpdateUser:     更新者
 * UpdateDate:     19-12-23 上午11:01
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class EmoticonsKeyboardUtils {

    private static int sDefKeyboardHeight=-1;
    private static String EXTRA_DEF_KEYBOARD_HEIGHT="DEF_KEYBOARD_HEIGHT";

    /**
     * 是否全屏.
     */
    public static boolean isFullScreen(Activity context) {
        return (context.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }

    /**
     * 关闭软键盘.
     */
    public static void closeSoftKeyboard(EditText view) {
        if (view == null || view.getWindowToken() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 关闭软键盘.
     */
    public static void closeSoftKeyboard(Context context) {
        if (context == null || !(context instanceof Activity) || ((Activity) context).getCurrentFocus() == null) {
            return;
        }
        try {
            View view = ((Activity) context).getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openSoftKeyboard(EditText et) {
        if (et != null) {
            et.setFocusable(true);
            et.setFocusableInTouchMode(true);
            et.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(et, 0);
        }
    }

    public static int getDefKeyboardHeight() {
        if (sDefKeyboardHeight < 0) {
            sDefKeyboardHeight = ScreenTool.dip2px(300);
        }
        int height = SPTool.getInstance().getInt(EXTRA_DEF_KEYBOARD_HEIGHT, 0);
        return sDefKeyboardHeight = height > 0 && sDefKeyboardHeight != height ? height : sDefKeyboardHeight;
    }

    public static void setDefKeyboardHeight(int height) {
        if (sDefKeyboardHeight != height) {
            SPTool.getInstance().put(EXTRA_DEF_KEYBOARD_HEIGHT,height);
            EmoticonsKeyboardUtils.sDefKeyboardHeight = height;
        }
    }

    /**
     * 关闭软键盘
     * 当使用全屏主题的时候,XhsEmoticonsKeyBoard屏蔽了焦点.关闭软键盘时,直接指定 closeSoftKeyboard(EditView)
     * @param view
     */
    public static void closeSoftKeyboard(View view) {
        if (view == null || view.getWindowToken() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
