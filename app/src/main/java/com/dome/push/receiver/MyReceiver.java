package com.dome.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dome.push.util.ToastTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.helper.Logger;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";
    //private int badgeCount = SPUtils.getInstance().getInt(BaseUrl.BIKE_BADGER);

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();

            if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                processCustomMessage(context, bundle);
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

            }
        } catch (Exception e) {
            ToastTool.showToast(e.getMessage());
        }

    }

    private void processCustomMessage(Context context, Bundle bundle) {
        //int count = MyApplication.getApp().getCount();
        //String extraMessage = bundle.getString(JPushInterface.EXTRA_EXTRA);
        ////String extraMessage = "{\"afterPay\":\"0\",\"handleType\":\"2\",\"oid\":\"388887603610729381\"}";
        //if (StringUtil.isEmpty(extraMessage)) return;
        //String uid = SPUtils.getInstance().getString(BaseUrl.USER_ID);
        //if (StringUtil.isEmpty(uid)) {
        //    return;
        //}
        //ReceiverBean message = GsonUtils.fromJson(extraMessage, ReceiverBean.class);
        //message.setMsgContent(bundle.getString(JPushInterface.EXTRA_MESSAGE));
        //if (message.getAction().equals("2")) {
        //    MyApplication.getApp().setId("");
        //    MyApplication.getApp().setUrl("");
        //    MyApplication.getApp().setBikeId("");
        //    MyApplication.getApp().setBikeNo("");
        //    MyApplication.getApp().setBalance("");
        //    MyApplication.getApp().setOrderBean(null);
        //    MainPresenter.endOrder(message.getOrderId());// 结束订单
        //    MyApplication.getApp().setOldOid(message.getOrderId());
        //    RxBusUtil.getInstance().post(new EndOrderBean(message.getOrderId(), 2, null));
        //} else if (message.getAction().equals("10") || message.getAction().equals("11")) {
        //    RxBusUtil.getInstance().post(new EndOrderBean(message.getOrderId(), 10, null));
        //    if (count < 99) {
        //        ++count;
        //    }
        //    MyApplication.getApp().setCount(count);
        //    SPUtils.getInstance().put(BaseUrl.BIKE_BADGER, count);
        //    ShortcutBadger.applyCount(context, count);
        //}
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

}
