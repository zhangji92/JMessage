package com.dome.push.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.dome.push.R;
import com.dome.push.base.BaseActivity;
import com.dome.push.holder.ChatHolder;
import com.dome.push.mvp.presenter.ChatPresenter;
import com.dome.push.mvp.view.IChatView;
import com.dome.push.util.AndroidWorkaroundTool;
import com.dome.push.view.EmoticonsRelative;
import com.dome.push.view.RxTitle;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import butterknife.BindView;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.activity
 * ClassName:      ChatActivity
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-19 下午4:18
 * UpdateUser:     更新者
 * UpdateDate:     19-12-19 下午4:18
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ChatActivity extends BaseActivity<ChatPresenter> implements IChatView, View.OnClickListener {
    @BindView(R.id.layout_status)
    Toolbar layoutStatus;
    @BindView(R.id.chat_toolbar)
    RxTitle chatToolbar;
    @BindView(R.id.chat_emoticons)
    EmoticonsRelative chatEmoticons;
    @BindView(R.id.chat_record)
    RecyclerView chatRecord;
    @BindView(R.id.chat_root)
    RelativeLayout chatRoot;
    @BindView(R.id.chat_smart)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.chat_list_header)
    ClassicsHeader chatListHeader;


    @Override
    protected int layoutResId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initTitle(Bundle savedInstanceState) {
        AndroidWorkaroundTool.isAndroidp(layoutStatus, this);
        chatToolbar.setLeftIconOnClickListener(this);
    }

    @Override
    protected void initData() {
        presenter.init();

        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            smartRefreshLayout.finishRefresh();
            presenter.getAdapter().dropDownToRefresh();
            if (presenter.getAdapter().isHasLastPage()) {
                chatRecord.smoothScrollToPosition(0);
                presenter.getAdapter().refreshStartPosition();
            } else {
                chatListHeader.setLastUpdateText("没有更多数据了");
                chatRecord.smoothScrollToPosition(0);
            }

        });

    }

    @Override
    protected ChatPresenter createPresenter() {
        return new ChatPresenter(this);
    }

    @Override
    public void onClick(View v) {
        // 停止播放语音
        ChatHolder.stopMediaPlayer();
        finish();
    }

    @Override
    public EmoticonsRelative getRelative() {
        return chatEmoticons;
    }

    @Override
    public RecyclerView getChatRecord() {
        return chatRecord;
    }

    @Override
    public RxTitle getRxTitle() {
        return chatToolbar;
    }

    @Override
    public RelativeLayout getChatRoot() {
        return chatRoot;
    }


    @Override
    public void onBackPressed() {
        if (presenter.isOnSoftPop()) {
            chatEmoticons.onBackKeyClick();
            return;
        }
        // 停止播放语音
        ChatHolder.stopMediaPlayer();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }


}
