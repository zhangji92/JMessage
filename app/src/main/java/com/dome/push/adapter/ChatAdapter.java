package com.dome.push.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dome.push.R;
import com.dome.push.holder.ChatHolder;
import com.dome.push.listener.IOnClickListener;
import com.dome.push.util.ToastTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.adapter
 * ClassName:      ChatAdapter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-24 上午11:34
 * UpdateUser:     更新者
 * UpdateDate:     19-12-24 上午11:34
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {
    private Conversation conversation;
    private List<Message> messageList;

    //文本
    private final int TYPE_SEND_TXT = 1;
    private final int TYPE_RECEIVE_TXT = 2;

    // 图片
    private final int TYPE_SEND_IMAGE = 3;
    private final int TYPE_RECEIVE_IMAGE = 4;

    ////文件
    //private final int TYPE_SEND_FILE = 5;
    //private final int TYPE_RECEIVE_FILE = 6;
    // 语音
    private final int TYPE_SEND_VOICE = 7;
    private final int TYPE_RECEIVE_VOICE = 8;
    //视频
    private final int TYPE_SEND_VIDEO = 9;
    private final int TYPE_RECEIVE_VIDEO = 10;
    //群成员变动
    private final int TYPE_GROUP_CHANGE = 11;
    //自定义消息
    private final int TYPE_CUSTOM_TXT = 12;
    // 消息队列
    private Queue<Message> mMsgQueue = new LinkedList<Message>();

    private IOnClickListener listener;
    private final int COUNT = 18;
    private int offSet = COUNT;
    private int start = 0;
    private boolean hasLastPage;
    public ChatAdapter(Conversation conversation, IOnClickListener listener) {
        this.listener = listener;
        this.conversation = conversation;
        if (conversation.getUnReadMsgCnt() > offSet) {
            messageList = conversation.getMessagesFromNewest(start, conversation.getUnReadMsgCnt());
        }else {
            messageList = conversation.getMessagesFromNewest(start, offSet);
        }
        start = offSet;

        reverse(messageList);
        checkSendingImgMsg();
    }



    public void dropDownToRefresh() {
        if (conversation != null) {
            List<Message> msgList = conversation.getMessagesFromNewest(messageList.size(), COUNT);
            if (msgList != null) {
                for (Message msg : msgList) {
                    messageList.add(0, msg);
                }
                if (msgList.size() > 0) {
                    checkSendingImgMsg();
                    offSet = msgList.size();
                    hasLastPage = true;
                } else {
                    offSet = 0;
                    hasLastPage = false;
                }
                notifyDataSetChanged();
            }
        }
    }

    public boolean isHasLastPage() {
        return hasLastPage;
    }

    public int getOffset() {
        return offSet;
    }

    public void refreshStartPosition() {
        start += offSet;
    }

    //当有新消息加到MsgList，自增mStart
    private void incrementStartPosition() {
        ++start;
    }

    private void reverse(List<Message> list) {
        if (list.size() > 0) {
            Collections.reverse(list);
        }
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_SEND_TXT:// 发送文本
                return new ChatHolder(parent, R.layout.adapter_chat_send_text);
            case TYPE_RECEIVE_TXT://接收文本
                return new ChatHolder(parent, R.layout.adapter_chat_receive_text);
            case TYPE_SEND_VOICE:// 发送语音
                return new ChatHolder(parent, R.layout.adapter_chat_send_voice);
            case TYPE_RECEIVE_VOICE:// 接收语音
                return new ChatHolder(parent, R.layout.adapter_chat_receive_voice);
            case TYPE_SEND_IMAGE:// 发送图片
                return new ChatHolder(parent, R.layout.adapter_chat_send_image);
            case TYPE_RECEIVE_IMAGE:// 接受图片
                return new ChatHolder(parent, R.layout.adapter_chat_receive_image);
            case TYPE_SEND_VIDEO:// 发送视频
                return new ChatHolder(parent, R.layout.adapter_chat_send_video);
            case TYPE_RECEIVE_VIDEO:
                return new ChatHolder(parent, R.layout.adapter_chat_receive_video);
            case TYPE_GROUP_CHANGE:
            default:
                return new ChatHolder(parent, R.layout.adapter_chat_custom);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        // 发送 接受文本
        holder.setConversation(conversation);
        holder.setAdapter(this);
        holder.setData(messageList.get(position), position);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v,-1);
            }
        });
        holder.setClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        switch (message.getContentType()) {
            case text:// 文本
                return message.getDirect() == MessageDirect.send ? TYPE_SEND_TXT : TYPE_RECEIVE_TXT;
            case image:// 图片
                return message.getDirect() == MessageDirect.send ? TYPE_SEND_IMAGE : TYPE_RECEIVE_IMAGE;
            case voice:// 语音
                return message.getDirect() == MessageDirect.send ? TYPE_SEND_VOICE : TYPE_RECEIVE_VOICE;
            case video:// 文件
                return message.getDirect() == MessageDirect.send ? TYPE_SEND_VIDEO : TYPE_RECEIVE_VIDEO;
            case eventNotification:
            case prompt:
                return TYPE_GROUP_CHANGE;
            default:
                return TYPE_CUSTOM_TXT;
        }
    }

    public void addMsgToList(Message customMsg) {
        messageList.add(customMsg);
        incrementStartPosition();
        notifyDataSetChanged();

    }

    public void addMsgListToList(List<Message> singleOfflineMsgList) {
        messageList.addAll(singleOfflineMsgList);
        notifyDataSetChanged();
    }

    public Message getLastMsg() {
        if (messageList.size() > 0) {
            return messageList.get(messageList.size() - 1);
        } else {
            return null;
        }
    }

    public Message getMessage(int position) {
        return messageList.get(position);
    }

    public void addMsgFromReceiptToList(Message msg) {
        messageList.add(msg);
        msg.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    incrementStartPosition();
                    notifyDataSetChanged();
                } else {
                    //HandleResponseCode.onHandle(mContext, i, false);
                    ToastTool.error(s);
                    notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 检查图片是否处于创建状态，如果是，则加入发送队列
     */
    private void checkSendingImgMsg() {
        for (Message msg : messageList) {
            if (msg.getStatus() == MessageStatus.created
                    && msg.getContentType() == ContentType.image) {
                mMsgQueue.offer(msg);
            }
        }
        if (mMsgQueue.size() > 0) {
            Message message = mMsgQueue.element();
            sendNextImgMsg(message);
            notifyDataSetChanged();
        }
    }

    public void setSendMsg(Message msg) {
        if (msg != null) {
            messageList.add(msg);
            incrementStartPosition();
            mMsgQueue.offer(msg);
        }

        if (mMsgQueue.size() > 0) {
            Message message = mMsgQueue.element();
            sendNextImgMsg(message);
            notifyDataSetChanged();
        }
    }

    //找到撤回的那一条消息,并且用撤回后event下发的去替换掉这条消息在集合中的原位置
    List<Message> forDel;
    int i;
    public void delMsgRetract(Message msg) {
        forDel = new ArrayList<>();
        i = 0;
        for (Message message : messageList) {
            if (msg.getServerMessageId().equals(message.getServerMessageId())) {
                i = messageList.indexOf(message);
                forDel.add(message);
            }
        }
        messageList.removeAll(forDel);
        messageList.add(i, msg);
        notifyDataSetChanged();
    }

    /**
     * 从发送队列中出列，并发送图片
     *
     * @param msg 图片消息
     */
    private void sendNextImgMsg(Message msg) {
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(msg, options);
        msg.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                //出列
                mMsgQueue.poll();
                //如果队列不为空，则继续发送下一张
                if (!mMsgQueue.isEmpty()) {
                    sendNextImgMsg(mMsgQueue.element());
                }
                notifyDataSetChanged();
            }
        });
    }

    // 已读回执
    public void setUpdateReceiptCount(long serverMsgId, int unReceiptCnt) {
        for (Message message : messageList) {
            if (message.getServerMessageId() == serverMsgId) {
                message.setUnreceiptCnt(unReceiptCnt);
            }
        }
        notifyDataSetChanged();
    }


}
