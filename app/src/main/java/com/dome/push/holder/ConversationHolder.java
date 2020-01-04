package com.dome.push.holder;

import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dome.push.R;
import com.dome.push.base.BaseViewHolder;
import com.dome.push.util.IdHelper;
import com.dome.push.util.TimeFormatTool;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.holder
 * ClassName:      ConversationHolder
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-30 上午11:24
 * UpdateUser:     更新者
 * UpdateDate:     19-12-30 上午11:24
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ConversationHolder extends BaseViewHolder<Conversation> {

    private final ImageView conversationHeader;
    private final TextView conversationName;
    private final TextView conversationNum;
    private final TextView conversationContent;
    private final TextView conversationTime;

    public ConversationHolder(ViewGroup parent, int itemView) {
        super(parent, itemView);
        conversationHeader = findViewById(R.id.conversation_header);
        conversationName = findViewById(R.id.conversation_name);
        conversationNum = findViewById(R.id.conversation_un_num);
        conversationContent = findViewById(R.id.conversation_content);
        conversationTime = findViewById(R.id.conversation_time);
    }

    @Override
    public void setData(Conversation data, int position) {
        UserInfo targetInfo = (UserInfo) data.getTargetInfo();
        if (targetInfo != null) {
            targetInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int status, String desc, Bitmap bitmap) {
                    if (status == 0) {
                        conversationHeader.setImageBitmap(bitmap);
                    } else {
                        conversationHeader.setImageResource(R.mipmap.icon_head);
                    }
                }
            });
            conversationName.setText(targetInfo.getUserName());

        } else {
            conversationHeader.setImageResource(R.mipmap.icon_head);
        }

        Message latestMessage = data.getLatestMessage();
        if (latestMessage != null) {
            //会话界面时间
            conversationTime.setText(TimeFormatTool.getDetailTime(latestMessage.getCreateTime()));
            String contentStr = "";
            switch (latestMessage.getContentType()) {
                case image:
                    contentStr = IdHelper.getString(R.string.type_picture);
                    break;
                case voice:
                    contentStr = IdHelper.getString(R.string.type_voice);
                    break;
                case video:
                    contentStr = IdHelper.getString(R.string.type_video);
                    break;
                case custom:
                    contentStr = IdHelper.getString(R.string.type_custom);
                    break;
                case prompt:
                    contentStr = ((PromptContent) latestMessage.getContent()).getPromptText();
                    break;
                default:
                    contentStr = ((TextContent) latestMessage.getContent()).getText();
                    break;
            }

            int allUnReadMsgCount = JMessageClient.getAllUnReadMsgCount();
            if (allUnReadMsgCount == 0) {
                contentStr = "[已读] " + contentStr;
                conversationContent.setText(contentStr);
            } else {
                contentStr = "[未读] " + contentStr;
                conversationContent.setText(contentStr);
                SpannableStringBuilder builder = new SpannableStringBuilder(contentStr);
                builder.setSpan(new ForegroundColorSpan(IdHelper.getColor(R.color.color_2d)),
                        0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                conversationContent.setText(builder);
            }

            if (allUnReadMsgCount == 0) {
                conversationNum.setVisibility(View.GONE);
            } else {
                conversationNum.setVisibility(View.VISIBLE);
                if (data.getUnReadMsgCnt() > 100) {
                    conversationNum.setText("99");
                } else {
                    conversationNum.setText(String.valueOf(allUnReadMsgCount));
                }
            }
        }
    }


    public TextView getConversationNum() {
        return conversationNum;
    }
}
