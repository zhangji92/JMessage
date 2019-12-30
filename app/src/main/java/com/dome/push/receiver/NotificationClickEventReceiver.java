package com.dome.push.receiver;


import android.content.Context;
import android.content.Intent;

import com.dome.push.MyApplication;
import com.dome.push.activity.ChatActivity;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class NotificationClickEventReceiver {
    private Context mContext;

    public NotificationClickEventReceiver(Context context) {
        mContext = context;
        //注册接收消息事件
        JMessageClient.registerEventReceiver(this);
    }

    /**
     * 收到消息处理
     *
     * @param notificationClickEvent 通知点击事件
     */
    public void onEvent(NotificationClickEvent notificationClickEvent) {
        if (null == notificationClickEvent) {
            return;
        }
        Message msg = notificationClickEvent.getMessage();
        if (msg != null) {
            UserInfo info = (UserInfo) msg.getTargetInfo();
            Intent notificationIntent = new Intent(mContext, ChatActivity.class);
            Conversation conv = JMessageClient.getSingleConversation(info.getUserName(), info.getAppKey());
            MyApplication.getApp().setTargetAppKey(info.getAppKey());
            MyApplication.getApp().setTargetUserName(info.getUserName());
            conv.resetUnreadCount();
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(notificationIntent);
        }
    }

}
