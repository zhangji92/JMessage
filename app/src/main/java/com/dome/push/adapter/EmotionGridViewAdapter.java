package com.dome.push.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dome.push.R;
import com.dome.push.bean.EmojiBean;

import java.util.List;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.adapter
 * ClassName:      EmotionGridViewAdapter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-23 下午5:06
 * UpdateUser:     更新者
 * UpdateDate:     19-12-23 下午5:06
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class EmotionGridViewAdapter extends BaseAdapter {

    private List<EmojiBean> emojiBeans;
    private int itemWidth;
    public EmotionGridViewAdapter(List<EmojiBean> emotionNames, int itemWidth) {
        this.emojiBeans = emotionNames;
        this.itemWidth = itemWidth;
    }

    @Override
    public int getCount() {
        return emojiBeans.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return emojiBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv_emotion = new ImageView(parent.getContext());
        // 设置内边距
        iv_emotion.setPadding(itemWidth/8, itemWidth/8, itemWidth/8, itemWidth/8);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(itemWidth, itemWidth);
        iv_emotion.setLayoutParams(params);

        //判断是否为最后一个item
        if(position == getCount() - 1) {
            iv_emotion.setImageResource(R.mipmap.icon_del);
        } else {
            int emotionName = emojiBeans.get(position).icon;
            iv_emotion.setImageResource(emotionName);
        }

        return iv_emotion;
    }
}
