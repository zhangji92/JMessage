package com.dome.push.mvp.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dome.push.MyApplication;
import com.dome.push.R;
import com.dome.push.activity.CameraActivity;
import com.dome.push.adapter.ChatAdapter;
import com.dome.push.adapter.EmoticonsAdapter;
import com.dome.push.adapter.OnScrollAdapter;
import com.dome.push.base.BasePresenter;
import com.dome.push.base.BaseUrl;
import com.dome.push.bean.EmojiBean;
import com.dome.push.mvp.view.IChatView;
import com.dome.push.util.ActivityTool;
import com.dome.push.util.FileTool;
import com.dome.push.util.PermissionHelper;
import com.dome.push.util.ToastTool;
import com.dome.push.view.FuncLayout;
import com.dome.push.view.MultimediaLayout;
import com.dome.push.view.RecallPopView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VideoContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.mvp.presenter
 * ClassName:      ChatPresenter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-13 下午3:46
 * UpdateUser:     更新者
 * UpdateDate:     19-12-13 下午3:46
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ChatPresenter extends BasePresenter<IChatView> implements FuncLayout.OnFuncKeyBoardListener, View.OnClickListener {

    private ChatAdapter adapter;
    private Conversation conversation;
    private boolean onSoftPop;
    private String targetUserName;
    private String targetAppKey;
    RecallPopView popView = null;

    public ChatPresenter(IChatView reference) {
        super(reference);
    }

    public void init() {
        // 事件注册器
        JMessageClient.registerEventReceiver(this);
        // 功能布局
        MultimediaLayout layout = new MultimediaLayout(getView().getBaseActivity());
        layout.listener(this);
        getView().getRelative().addFuncView(layout);

        // 软键盘弹起监听
        getView().getRelative().addOnFuncKeyBoardListener(this);
        // 表情适配器
        EmoticonsAdapter emoticonsAdapter = new EmoticonsAdapter(getView().getBaseActivity(), (position, count, bean) -> {
            if (position == count - 1) {
                // 如果点击了最后一个回退按钮,则调用删除键事件
                getView().getRelative().getEtChat().dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                // 如果点击了表情,则添加到输入框中
                // 获取当前光标位置,在指定位置上添加表情图片文本
                int curPosition = getView().getRelative().getEtChat().getSelectionStart();


                StringBuilder sb = new StringBuilder(getView().getRelative().getEtChat().getText().toString());

                sb.insert(curPosition, ((EmojiBean) bean).emoji);

                getView().getRelative().getEtChat().setText(sb);
                //// 将光标设置到新增完表情的右侧
                getView().getRelative().getEtChat().setSelection(curPosition + ((EmojiBean) bean).emoji.length());
            }
        });
        // 表情设置适配器
        getView().getRelative().setAdapter(emoticonsAdapter);

        // 目标名称
        targetUserName = MyApplication.getApp().getTargetUserName();
        getView().getRxTitle().setTitle(targetUserName);
        // 目标appkey
        targetAppKey = MyApplication.getApp().getTargetAppKey();
        JMessageClient.enterSingleConversation(targetUserName, targetAppKey);
        // 获取单聊信息
        conversation = JMessageClient.getSingleConversation(targetUserName, targetAppKey);
        if (conversation == null) {
            // 创建单聊信息
            conversation = Conversation.createSingleConversation(targetUserName, targetAppKey);
        }
        // 初始化布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getView().getBaseActivity());
        // 设置布局管理器
        getView().getChatRecord().setLayoutManager(layoutManager);
        // 初始化适配器
        adapter = new ChatAdapter(conversation, (view, position) -> {
            if (isOnSoftPop() && position < 0) {
                getView().getRelative().onBackKeyClick();
            } else if (position >= 0) {
                Message message = adapter.getMessage(position);
                if (message.getDirect() == MessageDirect.send) {
                    if (popView == null) {
                        popView = new RecallPopView(getView().getBaseActivity());
                        popView.setContent("撤回");
                    } else {
                        popView.dismiss();
                    }
                    popView.measure(popView.getWidth(), popView.getHeight());
                    view.measure(0, 0);
                    int xOff = (view.getWidth() - popView.getContentView().getMeasuredWidth()) / 2;
                    int yOff = (view.getHeight() + popView.getContentView().getMeasuredHeight());
                    popView.showAsDropDown(view, xOff, -yOff);
                    popView.click(v -> {
                        popView.dismiss();
                        //撤回
                        conversation.retractMessage(message, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 855001) {
                                    ToastTool.error("发送时间过长，不能撤回");
                                } else if (i == 0) {
                                    adapter.delMsgRetract(message);
                                }
                            }
                        });
                    });
                }
            }
        });
        // 设置适配器
        getView().getChatRecord().setAdapter(adapter);
        getView().getChatRecord().addOnScrollListener(new OnScrollAdapter() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        getView().getRelative().reset();
                        break;
                }
            }
        });
        //切换语音输入
        getView().getRelative().getVoiceOrText().setOnClickListener(v -> {
            if (v.getId() == R.id.btn_voice_or_text) {
                getView().getRelative().setVideoText();
                getView().getRelative().getBtnVoice().initConversation(conversation, adapter, getView().getChatRecord());
            }
        });


        getView().getRelative().getEtChat().setOnFocusChangeListener((v, hasFocus) -> {
            String content;
            if (hasFocus) {
                content = "{\"type\": \"input\",\"content\": {\"message\": \"对方正在输入...\"}}";
            } else {
                content = "{\"type\": \"input\",\"content\": {\"message\": \"\"}}";
            }
            JMessageClient.sendSingleTransCommand(targetUserName, targetAppKey, content, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {

                }
            });
        });


        // 滚动到列表最底部
        setScrollBottom();
        // 发送
        getView().getRelative().getBtnSend().setOnClickListener(v -> {
            String mcgContent = getView().getRelative().getEtChat().getText().toString();
            if (TextUtils.isEmpty(mcgContent)) {
                return;
            }
            TextContent content = new TextContent(mcgContent);
            // 创建消息
            Message sendMessage = conversation.createSendMessage(content);
            //设置需要已读回执
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);
            // 发送消息
            JMessageClient.sendMessage(sendMessage, options);
            adapter.addMsgFromReceiptToList(sendMessage);
            // 滚动到列表最底部
            setScrollBottom();
            // 清空输入框
            getView().getRelative().getEtChat().setText("");
        });
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与View的中心点对齐
     *
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    private int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        final int anchorWidth = anchorView.getWidth();
        final int screenHeight = anchorView.getContext().getResources().getDisplayMetrics().heightPixels;
        final int screenWidth = anchorView.getContext().getResources().getDisplayMetrics().widthPixels;
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        //偏移，否则会弹出在屏幕外
        int offset = windowWidth < anchorWidth ? (windowWidth - anchorWidth) : 0;
        //实际坐标中心点为触发view的中间
        windowPos[0] = (anchorLoc[0] + anchorWidth / 2) - (windowWidth / 2) + offset;
        windowPos[1] = isNeedShowUp ? anchorLoc[1] - windowHeight : anchorLoc[1] + anchorHeight;
        return windowPos;
    }

    @Override
    public void OnFuncPop(int height) {
        onSoftPop = true;
        setScrollBottom();
    }

    public void setScrollBottom() {
        getView().getChatRecord().requestLayout();
        getView().getChatRecord().postDelayed(() -> {
            int position = adapter.getItemCount() - 1;
            if (position > 0) {
                getView().getChatRecord().smoothScrollToPosition(position);
            }
        }, 100);
    }

    @Override
    public void OnFuncClose() {
        onSoftPop = false;
        //setScrollBottom();
    }

    /**
     * 消息已读事件
     */
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        List<MessageReceiptStatusChangeEvent.MessageReceiptMeta> messageReceiptMetas = event.getMessageReceiptMetas();
        for (MessageReceiptStatusChangeEvent.MessageReceiptMeta meta : messageReceiptMetas) {
            long serverMsgId = meta.getServerMsgId();
            int unReceiptCnt = meta.getUnReceiptCnt();
            adapter.setUpdateReceiptCount(serverMsgId, unReceiptCnt);
        }
    }

    public void onEventMainThread(MessageRetractEvent event) {
        Message retractedMessage = event.getRetractedMessage();
        adapter.delMsgRetract(retractedMessage);
    }

    /**
     * 当在聊天界面断网再次连接时收离线事件刷新
     */
    public void onEvent(OfflineMessageEvent event) {
        String targetAppKey = MyApplication.getApp().getTargetAppKey();
        String targetUserName = MyApplication.getApp().getTargetUserName();
        Conversation conv = event.getConversation();
        UserInfo userInfo = (UserInfo) conv.getTargetInfo();
        String targetId = userInfo.getUserName();
        String appKey = userInfo.getAppKey();
        if (targetId.equals(targetUserName) && appKey.equals(targetAppKey)) {
            List<Message> singleOfflineMsgList = event.getOfflineMessageList();
            if (singleOfflineMsgList != null && singleOfflineMsgList.size() > 0) {
                adapter.addMsgListToList(singleOfflineMsgList);
                setScrollBottom();
            }
        }
    }

    public void onEventMainThread(MessageEvent messageEvent) {
        String targetAppKey = MyApplication.getApp().getTargetAppKey();
        String targetUserName = MyApplication.getApp().getTargetUserName();
        Message message = messageEvent.getMessage();
        if (message.getTargetType() == ConversationType.single) {
            UserInfo userInfo = (UserInfo) message.getTargetInfo();
            String targetId = userInfo.getUserName();
            String appKey = userInfo.getAppKey();
            if (targetId.equals(targetUserName) && appKey.equals(targetAppKey)) {
                Message lastMsg = adapter.getLastMsg();
                if (lastMsg == null || message.getId() != lastMsg.getId()) {
                    adapter.addMsgToList(message);
                } else {
                    adapter.notifyDataSetChanged();
                }
                setScrollBottom();
            }
        }
    }

    public void onEvent(CommandNotificationEvent event) {
        if (event.getType().equals(CommandNotificationEvent.Type.single)) {
            String msg = event.getMsg();
            if (msg.contains("content")) {
                try {
                    JSONObject object = new JSONObject(msg);
                    JSONObject jsonContent = object.getJSONObject("content");
                    String messageString = jsonContent.getString("message");
                    if (TextUtils.isEmpty(messageString)) {
                        getView().getRxTitle().setTitle(conversation.getTitle());
                    } else {
                        getView().getRxTitle().setTitle(messageString);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                getView().getRxTitle().setTitle(msg);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            // 设置商品主图返回的图片路径
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList.size() > 0) {
                String compressPath = selectList.get(0).getCompressPath();
                //所有图片都在这里拿到
                ImageContent.createImageContentAsync(new File(compressPath), new ImageContent.CreateImageContentCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                        if (responseCode == 0) {
                            Message msg = conversation.createSendMessage(imageContent);
                            adapter.setSendMsg(msg);
                            setScrollBottom();
                        }
                    }
                });
            }
        }
        switch (resultCode) {
            case BaseUrl.TAKE_PHOTO:
                if (data != null) {
                    String name = data.getStringExtra("take_photo");
                    if (name != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(name);
                        ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                                if (responseCode == 0) {
                                    Message msg = conversation.createSendMessage(imageContent);
                                    adapter.setSendMsg(msg);
                                    setScrollBottom();
                                }
                            }
                        });
                    }
                }
                break;
            case BaseUrl.TAKE_VIDEO:
                if (data != null) {
                    String path = data.getStringExtra("video");
                    int recordSecond = data.getIntExtra("recordSecond", 0);
                    try {
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND);
                        VideoContent videoContent = new VideoContent(bitmap, ".jpg", new File(path),
                                FileTool.getTimeString() + ".mp4", recordSecond);
                        Message msg = conversation.createSendMessage(videoContent);
                        adapter.setSendMsg(msg);
                        setScrollBottom();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.multimedia_picture:
                PermissionHelper.requestStorage(v.getContext(), () -> {
                    PictureSelector.create(getView().getBaseActivity())
                            .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                            .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                            .isCamera(true)// 是否显示拍照按钮 true or false
                            .previewImage(true)// 是否可预览图片 true or false
                            .selectionMode(PictureConfig.SINGLE)
                            .compress(true)// 是否压缩 true or false
                            .cropCompressQuality(40)// 裁剪压缩质量 默认90 int
                            .minimumCompressSize(100)// 小于100kb的图片不压缩
                            .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                            .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                            .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                }, shouldRequest -> shouldRequest.again(true));
                break;
            case R.id.multimedia_camera:
                PermissionHelper.requestCamera(v.getContext(), () -> {
                    Intent intent = new Intent(getView().getBaseActivity(), CameraActivity.class);
                    ActivityTool.startActivityForResult(getView().getBaseActivity(), intent, BaseUrl.TAKE_PHOTO);
                }, shouldRequest -> shouldRequest.again(true));


                break;
            case R.id.multimedia_file:
                break;
        }
    }

    public boolean isOnSoftPop() {
        return onSoftPop;
    }

    @Override
    protected void detachView() {
        super.detachView();
        JMessageClient.unRegisterEventReceiver(this);
    }

    public ChatAdapter getAdapter() {
        return adapter;
    }
}
