package com.dome.push.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dome.push.R;

/**
 * ProjectName:    stock
 * Package:        cn.com.yundanche.stock.utils
 * ClassName:      DialogHelper
 * Description:    弹窗工具类
 * Author:         张继
 * CreateDate:     2019/5/21 17:37
 * UpdateUser:     更新者
 * UpdateDate:     2019/5/21 17:37
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public final class DialogHelper {

    private DialogHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    //            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
    //    @Override
    //    public void onClick(DialogInterface dialog, int which) {
    //        shouldRequest.again(false);
    //    }
    //})
    static void showRationaleDialog(Context context, final PermissionTool.OnRationaleListener.ShouldRequest shouldRequest) {
        new AlertDialog.Builder(context)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage("您拒绝了我们申请授权，请同意授权，否则该功能不能正常使用！")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shouldRequest.again(true);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }


    static void showOpenAppSettingDialog(Context context) {

        new AlertDialog.Builder(context)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage("我们需要一些您拒绝的权限或系统无法应用的失败，请手动设置到页面授权，否则该函数不能正常使用！")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionTool.launchAppDetailsSettings();
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    public static Dialog createResendDialog(Context context, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, R.style.default_dialog_style);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_base_with_button, null, false);
        dialog.setContentView(view);
        Button cancelBtn = view.findViewById(R.id.cancel);
        Button resendBtn = view.findViewById(R.id.resend_commit);
        cancelBtn.setOnClickListener(listener);
        resendBtn.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_view, null);
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dialog_view);
        ImageView mLoadImg = (ImageView) v.findViewById(R.id.loading_img);
        TextView mLoadText = (TextView) v.findViewById(R.id.loading_txt);
        AnimationDrawable mDrawable = (AnimationDrawable) mLoadImg.getDrawable();
        mDrawable.start();
        mLoadText.setText(msg);
        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }

}
