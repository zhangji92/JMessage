package com.dome.push.fragment;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dome.push.MyApplication;
import com.dome.push.R;
import com.dome.push.activity.ChatActivity;
import com.dome.push.adapter.ConversationAdapter;
import com.dome.push.base.BaseFragment;
import com.dome.push.mvp.presenter.HomePresenter;
import com.dome.push.mvp.view.IHomeView;
import com.dome.push.util.ActivityTool;
import com.dome.push.util.AndroidWorkaroundTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.fragment
 * ClassName:      HomeFragment
 * Description:    首页
 * Author:         张继
 * CreateDate:     19-12-12 上午10:08
 * UpdateUser:     更新者
 * UpdateDate:     19-12-12 上午10:08
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class HomeFragment extends BaseFragment<HomePresenter> implements IHomeView {


    @BindView(R.id.layout_status)
    Toolbar layoutStatus;
    @BindView(R.id.home_recycler)
    RecyclerView homeRecycler;
    @BindView(R.id.home_edit)
    EditText homeEdit;
    private ConversationAdapter adapter;
    private List<Conversation> conversationList;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        AndroidWorkaroundTool.isAndroidp(layoutStatus, getActivity());

    }

    @Override
    protected void initData() {
        JMessageClient.registerEventReceiver(this);
        if (!MyApplication.getApp().isLoginFlag()) {
            return;
        }
        conversationList = new ArrayList<>();
        conversationList.addAll(JMessageClient.getConversationList());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        homeRecycler.setLayoutManager(manager);
        adapter = new ConversationAdapter(conversationList);
        homeRecycler.setAdapter(adapter);
        adapter.setClickListener((view, position) -> {
            UserInfo info = (UserInfo) conversationList.get(position).getTargetInfo();
            String appKey = info.getAppKey();
            String userName = info.getUserName();
            MyApplication.getApp().setTargetAppKey(appKey);
            MyApplication.getApp().setTargetUserName(userName);
            ActivityTool.startActivity(ChatActivity.class);
        });
    }

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter(this);
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!MyApplication.getApp().isLoginFlag()) {
                return;
            }
            List<Conversation> conversationList = JMessageClient.getConversationList();
            adapter.setConversations(conversationList);
        }
    }

    @OnClick(R.id.home_chat)
    public void onViewClicked() {
        String s = homeEdit.getText().toString();
        JMessageClient.getUserInfo(s, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                String appKey = userInfo.getAppKey();
                String userName = userInfo.getUserName();
                MyApplication.getApp().setTargetUserName(userName);
                MyApplication.getApp().setTargetAppKey(appKey);
                ActivityTool.startActivity(ChatActivity.class);
            }
        });
    }

    /**
     * 收到新消息
     */
    public void onEventMainThread(MessageEvent event) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!MyApplication.getApp().isLoginFlag()) {
            return;
        }
        List<Conversation> conversationList = JMessageClient.getConversationList();
        adapter.setConversations(conversationList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }
}
