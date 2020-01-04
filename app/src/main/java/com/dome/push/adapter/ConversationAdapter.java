package com.dome.push.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dome.push.R;
import com.dome.push.holder.ConversationHolder;
import com.dome.push.listener.IOnClickListener;

import java.util.List;

import cn.jpush.im.android.api.model.Conversation;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.adapter
 * ClassName:      ConversationAdapter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-30 上午11:24
 * UpdateUser:     更新者
 * UpdateDate:     19-12-30 上午11:24
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationHolder> {

    private List<Conversation> conversations;
    private IOnClickListener clickListener;

    public ConversationAdapter(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationHolder(parent, R.layout.adapter_conversation);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationHolder holder, int position) {
        holder.setData(conversations.get(position), position);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    public void setClickListener(IOnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
