package com.dome.push.holder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dome.push.MyApplication;
import com.dome.push.R;
import com.dome.push.activity.DownLoadActivity;
import com.dome.push.activity.ImageScaleActivity;
import com.dome.push.adapter.ChatAdapter;
import com.dome.push.base.BaseViewHolder;
import com.dome.push.listener.IOnClickListener;
import com.dome.push.util.ActivityTool;
import com.dome.push.util.DialogHelper;
import com.dome.push.util.FileTool;
import com.dome.push.util.GlideTool;
import com.dome.push.util.IdHelper;
import com.dome.push.util.ImageTool;
import com.dome.push.util.ScreenTool;
import com.dome.push.util.TimeFormatTool;
import com.dome.push.util.ToastTool;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VideoContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.holder
 * ClassName:      ChatHolder
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-24 下午2:10
 * UpdateUser:     更新者
 * UpdateDate:     19-12-24 下午2:10
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ChatHolder extends BaseViewHolder<Message> {

    private TextView chatTime;// 时间
    private ImageButton chatFailResend;// 发送失败标志
    private ImageView chatSending;// 发送中
    private TextView chatReceipt;// 一读未读标志
    private TextView chatContent;// 内容
    private ImageView chatAvatar;// 头像
    private TextView chatVoiceLength;// 语音长度
    private ImageView chatVoice;// 语音背景
    private ImageView chatReadStatus;// 语音读取状态
    private ImageView chatPicture;// 图片
    private TextView chatProgress;// 进度
    private LinearLayout chatVideoPlay;// 播放图片

    private Dialog resendDialog;// 重发弹窗

    private int mPosition;
    private AnimationDrawable mVoiceAnimation;
    private static final MediaPlayer mp = new MediaPlayer();
    private FileInputStream mFIS;
    private boolean mSetData = false;
    private int nextPlayPosition;
    private List<Integer> mIndexList = new ArrayList<>();//
    private boolean autoPlay;


    private final Animation mSendingAnim;// 发送动画
    private static long lastTime;// 上一条记录时间

    private IOnClickListener clickListener;
    private Conversation conversation;
    private ChatAdapter adapter;
    private final Context context;
    private final FrameLayout chatImgGroup;

    public ChatHolder(ViewGroup parent, int adapter_chat_record) {
        super(parent, adapter_chat_record);
        context = parent.getContext();
        chatTime = findViewById(R.id.chat_time);
        chatFailResend = findViewById(R.id.chat_fail_resend);
        chatSending = findViewById(R.id.chat_sending);
        chatReceipt = findViewById(R.id.chat_receipt);
        chatContent = findViewById(R.id.chat_content);
        chatAvatar = findViewById(R.id.chat_avatar);
        chatVoiceLength = findViewById(R.id.chat_voice_length);
        chatVoice = findViewById(R.id.chat_voice);
        chatReadStatus = findViewById(R.id.chat_read_status);
        chatPicture = findViewById(R.id.chat_picture);
        chatImgGroup = findViewById(R.id.chat_img_group);
        chatProgress = findViewById(R.id.chat_progress);
        chatVideoPlay = findViewById(R.id.chat_video_play);
        mSendingAnim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        mSendingAnim.setInterpolator(lin);
    }


    @Override
    public void setData(Message data, int position) {
        // 时间 消息与上一条消息距离五分钟显示时间
        long createTime = data.getCreateTime();
        if (createTime - lastTime > 300000 || position == 0) {
            String time = TimeFormatTool.getDetailTime(createTime);
            chatTime.setText(time);
            chatTime.setVisibility(View.VISIBLE);
        } else {
            chatTime.setVisibility(View.GONE);
        }
        lastTime = createTime;

        ContentType contentType = data.getContentType();
        switch (contentType) {
            case text:
                handleMessage(data, position);
                // 内容
                String strContent = ((TextContent) data.getContent()).getText();
                if (!TextUtils.isEmpty(strContent)) {
                    chatContent.setText(strContent);
                    chatContent.setVisibility(View.VISIBLE);
                } else {
                    chatContent.setVisibility(View.GONE);
                }
                chatContent.setOnLongClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onClick(chatContent, position);
                    }
                    return true;
                });
                break;
            case voice:
                VoiceContent voiceContent = (VoiceContent) data.getContent();
                int length = voiceContent.getDuration();
                String lengthStr = length + IdHelper.getString(R.string.symbol_second);
                chatVoiceLength.setText(lengthStr);
                // 发送方
                chatVoice.setImageResource(R.mipmap.send_3);
                //控制语音长度显示，长度增幅随语音长度逐渐缩小
                int width = (int) (-0.04 * length * length + 4.526 * length + 75.214);
                chatContent.setWidth((int) (width * ScreenTool.getDensity()));
                handleMessage(data, position);
                chatContent.setOnLongClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onClick(chatContent, position);
                    }
                    return true;
                });
                break;
            case image:
                handleMessage(data, position);
                ImageContent imgContent = (ImageContent) data.getContent();
                // 先拿本地缩略图
                String path = imgContent.getLocalThumbnailPath();
                if (path == null) {
                    //从服务器上拿缩略图
                    imgContent.downloadThumbnailImage(data, new DownloadCompletionCallback() {
                        @Override
                        public void onComplete(int status, String desc, File file) {
                            if (status == 0) {
                                ImageView imageView = ImageTool.setPictureScale(path, chatPicture);
                                GlideTool.load(context, new File(path), imageView);
                            }
                        }
                    });
                } else {
                    ImageView imageView = ImageTool.setPictureScale(path, chatPicture);
                    GlideTool.load(context, new File(path), imageView);
                }
                chatPicture.setOnLongClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onClick(chatImgGroup, position);
                    }
                    return true;
                });
                break;
            case video:
                handleMessage(data, position);
                VideoContent videoContent = (VideoContent) data.getContent();
                String thumbLocalPath = videoContent.getThumbLocalPath();
                if (thumbLocalPath != null) {
                    ImageView imageView = ImageTool.setPictureScale(thumbLocalPath, chatPicture);
                    GlideTool.load(context, new File(thumbLocalPath), imageView);
                } else {
                    GlideTool.load(context, R.mipmap.video_not_found, chatPicture);
                }
                chatPicture.setOnLongClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onClick(chatImgGroup, position);
                    }
                    return true;
                });
                break;
            case eventNotification:
                String content = ((EventNotificationContent) data.getContent()).getEventText();
                EventNotificationContent.EventNotificationType type = ((EventNotificationContent) data
                        .getContent()).getEventNotificationType();
                switch (type) {
                    case group_member_added:
                    case group_member_exit:
                    case group_member_removed:
                    case group_info_updated:
                    case group_member_keep_silence:
                    case group_member_keep_silence_cancel:
                        chatContent.setText(content);
                        chatContent.setVisibility(View.VISIBLE);
                        chatTime.setVisibility(View.GONE);
                        break;
                }
                break;
            case prompt:
                String promptText = ((PromptContent) data.getContent()).getPromptText();
                chatContent.setText(promptText);
                chatContent.setVisibility(View.VISIBLE);
                chatTime.setVisibility(View.GONE);
                break;
            case custom:
                CustomContent customContent = (CustomContent) data.getContent();
                Boolean isBlackListHint = customContent.getBooleanValue("blackList");
                //Boolean notFriendFlag = customContent.getBooleanValue("notFriend");
                // 会话列表滑动时自定义消息这里groupChange会出现null的情况
                if (chatContent != null) {
                    if (isBlackListHint != null && isBlackListHint) {
                        chatContent.setText(R.string.server_803008);
                        chatContent.setVisibility(View.VISIBLE);
                    } else {
                        chatContent.setVisibility(View.GONE);
                    }
                    chatContent.setVisibility(View.GONE);
                }
                break;
        }


        if (contentType.equals(ContentType.image) || contentType.equals(ContentType.video)) {
            chatPicture.setOnClickListener(v -> {
                if (contentType.equals(ContentType.image)) {

                    ImageContent content = (ImageContent) data.getContent();
                    content.downloadOriginImage(data, new DownloadCompletionCallback() {
                        @Override
                        public void onComplete(int i, String s, File file) {
                            if (i == 0) {
                                Bundle bundle = new Bundle();
                                bundle.putString("path", file.getAbsolutePath());
                                ActivityTool.startActivity(bundle, ImageScaleActivity.class);
                            } else {
                                ToastTool.error(s);
                            }
                        }
                    });
                } else if (contentType.equals(ContentType.video)) {
                    VideoContent videoContent = (VideoContent) data.getContent();
                    String videoLocalPath = videoContent.getVideoLocalPath();
                    String fileName = videoContent.getFileName();
                    if (videoLocalPath != null && new File(videoLocalPath).exists()) {
                        final String newPath = MyApplication.FILE_DIR + fileName;
                        File file = new File(newPath);
                        if (file.exists() && file.isFile()) {
                            browseDocument(fileName, newPath);
                        } else {
                            final String finalFileName = fileName;
                            FileTool.getInstance().copyFile(fileName, videoLocalPath, (Activity) context, uri -> browseDocument(finalFileName, newPath));
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("msg", data);
                        ActivityTool.startActivity(bundle, DownLoadActivity.class);
                    }
                }
            });
            return;
        }
        chatContent.setOnClickListener(v ->

        {
            if (data.getContentType() == ContentType.voice) {
                if (!FileTool.isSDCardExist()) {
                    ToastTool.error(IdHelper.getString(R.string.record_no_storage));
                    return;
                }
                // 如果之前存在播放动画，无论这次点击触发的是暂停还是播放，停止上次播放的动画
                if (mVoiceAnimation != null) {
                    mVoiceAnimation.stop();
                }
                // 播放中点击了正在播放的Item 则暂停播放
                if (mp.isPlaying() && mPosition == position) {
                    if (data.getDirect() == MessageDirect.send) {
                        chatVoice.setImageResource(R.drawable.voice_send);
                    } else {
                        chatVoice.setImageResource(R.drawable.voice_receive);
                    }
                    mVoiceAnimation = (AnimationDrawable) chatVoice.getDrawable();
                    pauseVoice(data.getDirect(), chatVoice);
                    // 开始播放录音
                } else if (data.getDirect() == MessageDirect.send) {
                    chatVoice.setImageResource(R.drawable.voice_send);
                    mVoiceAnimation = (AnimationDrawable) chatVoice.getDrawable();
                    // 继续播放之前暂停的录音
                    if (mSetData && mPosition == position) {
                        mVoiceAnimation.start();
                        mp.start();
                        // 否则重新播放该录音或者其他录音
                    } else {
                        playVoice(position, data, true);
                    }
                    // 语音接收方特殊处理，自动连续播放未读语音
                } else {
                    try {
                        // 继续播放之前暂停的录音
                        if (mSetData && mPosition == position) {
                            if (mVoiceAnimation != null) {
                                mVoiceAnimation.start();
                            }
                            mp.start();
                            // 否则开始播放另一条录音
                        } else {
                            // 选中的录音是否已经播放过，如果未播放，自动连续播放这条语音之后未播放的语音
                            if (data.getContent().getBooleanExtra("isRead") == null
                                    || !data.getContent().getBooleanExtra("isRead")) {
                                autoPlay = true;
                                playVoice(position, data, false);
                                // 否则直接播放选中的语音
                            } else {
                                chatVoice.setImageResource(R.drawable.voice_receive);
                                mVoiceAnimation = (AnimationDrawable) chatVoice.getDrawable();
                                playVoice(position, data, false);
                            }
                        }
                    } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleMessage(Message data, int position) {
        //获取消息发送者的UserInfo
        UserInfo fromUser = data.getFromUser();
        ContentType contentType = data.getContentType();
        // 是否发送完成
        if (data.getDirect() == MessageDirect.send) {
            switch (data.getStatus()) {
                case send_success:
                    chatFailResend.setVisibility(View.GONE);
                    chatReceipt.setVisibility(View.VISIBLE);
                    chatSending.clearAnimation();
                    chatSending.setVisibility(View.GONE);
                    // 图片
                    if (contentType.equals(ContentType.image)) {
                        chatPicture.setAlpha(1.0f);
                        chatProgress.setVisibility(View.GONE);
                        chatPicture.setClickable(true);
                    }
                    if (contentType.equals(ContentType.video)) {
                        chatPicture.setAlpha(1.0f);
                        chatPicture.setClickable(true);
                        chatProgress.setVisibility(View.GONE);
                        chatVideoPlay.setVisibility(View.VISIBLE);
                    }
                    break;
                case send_fail:
                    chatFailResend.setVisibility(View.VISIBLE);
                    chatReceipt.setVisibility(View.GONE);
                    chatSending.clearAnimation();
                    chatSending.setVisibility(View.GONE);

                    if (contentType.equals(ContentType.image)) {
                        chatPicture.setAlpha(1.0f);
                        chatProgress.setVisibility(View.GONE);
                    }
                    if (contentType.equals(ContentType.video)) {
                        chatPicture.setAlpha(1.0f);
                        chatProgress.setVisibility(View.GONE);
                        chatVideoPlay.setVisibility(View.VISIBLE);
                    }
                    break;
                case send_going:
                    chatFailResend.setVisibility(View.GONE);
                    chatReceipt.setVisibility(View.GONE);
                    chatSending.setVisibility(View.VISIBLE);
                    chatSending.startAnimation(mSendingAnim);
                    if (contentType.equals(ContentType.image)) {
                        sendingImage(data);
                        chatPicture.setClickable(false);
                    } else if (contentType.equals(ContentType.video)) {
                        chatPicture.setClickable(false);
                        chatVideoPlay.setVisibility(View.GONE);
                        sendingImage(data);
                    } else {
                        sendingTextOrVoice(data);
                    }
                    break;
            }
            // 已读未读标志
            if (data.getUnreceiptCnt() == 0) {
                chatReceipt.setText("已读");
                chatReceipt.setTextColor(IdHelper.getColor(R.color.color_99));
            } else {
                chatReceipt.setText("未读");
                chatReceipt.setTextColor(IdHelper.getColor(R.color.color_2d));
            }
            // 头像
            JMessageClient.getMyInfo().getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int status, String s, Bitmap bitmap) {
                    if (status == 0) {
                        chatAvatar.setImageBitmap(bitmap);
                    } else {
                        chatAvatar.setImageResource(R.mipmap.icon_head);
                    }
                }
            });
            chatFailResend.setOnClickListener(v -> {
                showResendDialog(data);
            });
        } else if (data.getDirect() == MessageDirect.receive) {// 接收方消息
            // 接收方头像
            fromUser.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int status, String desc, Bitmap bitmap) {
                    if (status == 0) {
                        chatAvatar.setImageBitmap(bitmap);
                    } else {
                        chatAvatar.setImageResource(R.mipmap.icon_head);
                    }
                }
            });
            // 已读的回执
            if (!data.haveRead()) {
                // 消息接收方已读回执
                data.setHaveRead(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {

                    }
                });
            }
            if (data.getStatus() == MessageStatus.receive_fail) {
                if (contentType.equals(ContentType.image)) {
                    ImageContent imgContent = (ImageContent) data.getContent();
                    chatPicture.setImageResource(R.mipmap.fetch_failed);
                    chatFailResend.setVisibility(View.VISIBLE);
                    chatFailResend.setOnClickListener(v -> imgContent.downloadOriginImage(data, new DownloadCompletionCallback() {
                        @Override
                        public void onComplete(int i, String s, File file) {
                            if (i == 0) {
                                ToastTool.success("下载成功");
                                adapter.notifyDataSetChanged();
                            } else {
                                ToastTool.error("下载失败" + s);
                            }
                        }
                    }));
                } else if (contentType.equals(ContentType.voice)) {
                    VoiceContent content = (VoiceContent) data.getContent();
                    // 接收方
                    chatVoice.setImageResource(R.mipmap.receive_3);
                    content.downloadVoiceFile(data, new DownloadCompletionCallback() {
                        @Override
                        public void onComplete(int i, String s, File file) {

                        }
                    });
                }
            }
            if (contentType.equals(ContentType.video)) {
                chatProgress.setVisibility(View.VISIBLE);
                chatVideoPlay.setVisibility(View.GONE);
                data.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {

                    @Override
                    public void onProgressUpdate(double percent) {
                        int progressNum = (int) (percent * 100);
                        chatProgress.setText(progressNum + "%");
                    }
                });
                VideoContent videoContent = (VideoContent) data.getContent();
                videoContent.downloadVideoFile(data, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int responseCode, String responseMessage, File file) {
                        if (responseCode == 0) {
                            chatProgress.setVisibility(View.GONE);
                            chatVideoPlay.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            if (contentType.equals(ContentType.voice)) {
                // 接收方
                chatVoice.setImageResource(R.mipmap.receive_3);
                // 收到语音，设置未读
                if (data.getContent().getBooleanExtra("isRead") == null
                        || !data.getContent().getBooleanExtra("isRead")) {
                    conversation.updateMessageExtra(data, "isRead", false);
                    chatReadStatus.setVisibility(View.VISIBLE);
                    if (mIndexList.size() > 0) {
                        if (!mIndexList.contains(position)) {
                            addToListAndSort(position);
                        }
                    } else {
                        addToListAndSort(position);
                    }
                    if (nextPlayPosition == position && autoPlay) {
                        playVoice(position, data, false);
                    }
                } else if (data.getContent().getBooleanExtra("isRead")) {
                    chatReadStatus.setVisibility(View.GONE);
                }
            }
        }
    }


    private void sendingImage(final Message msg) {
        chatPicture.setAlpha(0.75f);
        chatProgress.setVisibility(View.VISIBLE);
        chatProgress.setText("0%");
        //如果图片正在发送，重新注册上传进度Callback
        if (!msg.isContentUploadProgressCallbackExists()) {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double v) {
                    String progressStr = (int) (v * 100) + "%";
                    chatProgress.setText(progressStr);
                }
            });
        }
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, String desc) {
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = conversation.createSendMessage(customContent);
                        adapter.addMsgToList(customMsg);
                    } else if (status != 0) {
                        ToastTool.error(desc);
                    }
                }
            });

        }
    }

    //正在发送文字或语音
    private void sendingTextOrVoice(Message msg) {
        //消息正在发送，重新注册一个监听消息发送完成的Callback
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = conversation.createSendMessage(customContent);
                        adapter.addMsgToList(customMsg);
                    } else if (status != 0) {
                        ToastTool.error(desc);
                    }
                }
            });
        }
    }


    //重发对话框
    private void showResendDialog(final Message msg) {
        View.OnClickListener listener = view -> {
            if (view.getId() == R.id.cancel) {
                resendDialog.dismiss();
            } else {
                resendDialog.dismiss();
                switch (msg.getContentType()) {
                    case text:
                    case voice:
                        resendTextOrVoice(msg);
                        break;
                    case image:
                        resendImage(msg);
                        break;
                    case video:
                        resendVideo(msg);
                        break;
                }
            }
        };
        resendDialog = DialogHelper.createResendDialog(context, listener);
        resendDialog.getWindow().setLayout((int) (0.8 * ScreenTool.getDisplayWidth()), WindowManager.LayoutParams.WRAP_CONTENT);
        resendDialog.show();
    }

    private void resendVideo(Message msg) {
        chatFailResend.setVisibility(View.GONE);
        chatProgress.setVisibility(View.VISIBLE);
        chatSending.setVisibility(View.VISIBLE);
        chatSending.startAnimation(mSendingAnim);
        chatVideoPlay.setVisibility(View.GONE);
        chatPicture.setAlpha(0.75f);
        chatProgress.setText("0%");
        try {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(final double progress) {
                    String progressStr = (int) (progress * 100) + "%";
                    chatProgress.setText(progressStr);
                }
            });
            if (!msg.isSendCompleteCallbackExists()) {
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        chatSending.clearAnimation();
                        chatSending.setVisibility(View.GONE);
                        chatProgress.setVisibility(View.GONE);
                        chatPicture.setAlpha(1.0f);
                        chatVideoPlay.setVisibility(View.VISIBLE);
                        if (status != 0) {
                            ToastTool.error(desc);
                            chatFailResend.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);
            JMessageClient.sendMessage(msg, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void resendImage(Message msg) {
        chatSending.setVisibility(View.VISIBLE);
        chatSending.startAnimation(mSendingAnim);
        chatPicture.setAlpha(0.75f);
        chatFailResend.setVisibility(View.GONE);
        chatProgress.setVisibility(View.VISIBLE);
        chatProgress.setText("0%");
        try {
            // 显示上传进度
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(final double progress) {
                    String progressStr = (int) (progress * 100) + "%";
                    chatProgress.setText(progressStr);
                }
            });
            if (!msg.isSendCompleteCallbackExists()) {
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        chatSending.clearAnimation();
                        chatSending.setVisibility(View.GONE);
                        chatProgress.setVisibility(View.GONE);
                        chatPicture.setAlpha(1.0f);
                        if (status != 0) {
                            chatFailResend.setVisibility(View.VISIBLE);
                            ToastTool.error(desc);
                        }
                    }
                });
            }
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);
            JMessageClient.sendMessage(msg, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新发送语音或文本
     *
     * @param msg 消息
     */
    private void resendTextOrVoice(Message msg) {
        chatFailResend.setVisibility(View.GONE);
        chatSending.setVisibility(View.VISIBLE);
        chatSending.startAnimation(mSendingAnim);
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, String desc) {
                    chatSending.clearAnimation();
                    chatSending.setVisibility(View.GONE);
                    if (status != 0) {
                        chatFailResend.setVisibility(View.VISIBLE);
                        ToastTool.error(desc);
                    }
                }
            });
        }
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(msg, options);
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public void setAdapter(ChatAdapter adapter) {
        this.adapter = adapter;
    }


    private void playVoice(final int position, Message message, boolean isSender) {
        // 记录播放录音的位置
        mPosition = position;
        if (autoPlay) {
            conversation.updateMessageExtra(message, "isRead", true);
            chatReadStatus.setVisibility(View.GONE);
            if (mVoiceAnimation != null) {
                mVoiceAnimation.stop();
                mVoiceAnimation = null;
            }
            chatVoice.setImageResource(R.drawable.voice_receive);
            mVoiceAnimation = (AnimationDrawable) chatVoice.getDrawable();
        }
        try {
            mp.reset();
            VoiceContent vc = (VoiceContent) message.getContent();
            mFIS = new FileInputStream(vc.getLocalPath());
            FileDescriptor mFD = mFIS.getFD();
            mp.setDataSource(mFD);

            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare();
            mp.setOnPreparedListener(mp -> {
                mVoiceAnimation.start();
                mp.start();
            });
            mp.setOnCompletionListener(mp -> {
                mVoiceAnimation.stop();
                mp.reset();
                mSetData = false;
                if (isSender) {
                    chatVoice.setImageResource(R.mipmap.send_3);
                } else {
                    chatVoice.setImageResource(R.mipmap.receive_3);
                }
                if (autoPlay) {
                    int curCount = mIndexList.indexOf(position);
                    if (curCount + 1 >= mIndexList.size()) {
                        nextPlayPosition = -1;
                        autoPlay = false;
                    } else {
                        nextPlayPosition = mIndexList.get(curCount + 1);
                        adapter.notifyDataSetChanged();
                    }
                    mIndexList.remove(curCount);
                }
            });
        } catch (Exception e) {
            ToastTool.error(IdHelper.getString(R.string.file_not_found));

            VoiceContent vc = (VoiceContent) message.getContent();
            vc.downloadVoiceFile(message, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int status, String desc, File file) {
                    if (status == 0) {
                        ToastTool.error(IdHelper.getString(R.string.download_completed));
                    } else {
                        ToastTool.error(IdHelper.getString(R.string.file_fetch_failed));
                    }
                }
            });
        } finally {
            try {
                if (mFIS != null) {
                    mFIS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void pauseVoice(MessageDirect msgDirect, ImageView voice) {
        if (msgDirect == MessageDirect.send) {
            voice.setImageResource(R.mipmap.send_3);
        } else {
            voice.setImageResource(R.mipmap.receive_3);
        }
        mp.pause();
        mSetData = true;
    }

    private void addToListAndSort(int position) {
        mIndexList.add(position);
        Collections.sort(mIndexList);
    }

    private void browseDocument(String fileName, String path) {
        try {
            String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mime = mimeTypeMap.getMimeTypeFromExtension(ext);
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), mime);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastTool.error(IdHelper.getString(R.string.file_not_support_hint));
        }
    }


    public static void stopMediaPlayer() {
        if (mp.isPlaying())
            mp.stop();
    }

    public void setClickListener(IOnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
