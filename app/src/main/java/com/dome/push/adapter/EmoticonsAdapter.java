package com.dome.push.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.dome.push.bean.EmojiBean;
import com.dome.push.listener.EmoticonsListener;
import com.dome.push.util.DefEmoticonsTool;
import com.dome.push.util.ScreenTool;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.adapter
 * ClassName:      EmoticonsAdapter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-23 下午3:17
 * UpdateUser:     更新者
 * UpdateDate:     19-12-23 下午3:17
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class EmoticonsAdapter extends PagerAdapter {

    private List<GridView> gridViewList;
    private List<EmojiBean> emotionNames;
    private int count;
    private EmoticonsListener listener;

    public EmoticonsAdapter(Context context, EmoticonsListener listener) {
        this.listener = listener;
        gridViewList = new ArrayList<>();
        emotionNames = new ArrayList<>();
        int height = ScreenTool.getDisplayHeight();
        if (height > 1920) {
            count = 27;
        } else {
            count = 20;
        }
        // 获取屏幕宽度
        int screenWidth = ScreenTool.getDisplayWidth();
        // item的间距
        int spacing = ScreenTool.dp2px(12);
        // 动态计算item的宽度和高度
        int itemWidth = (screenWidth - spacing * 8) / 7;
        //动态计算gridview的总高度
        int gvHeight = itemWidth * 3 + spacing * 6;
        // 遍历所有的表情的key
        for (EmojiBean emojiName : DefEmoticonsTool.sEmojiArray) {
            emotionNames.add(emojiName);
            // 每20个表情作为一组,同时添加到ViewPager对应的view集合中
            if (emotionNames.size() == count) {
                GridView gv = createEmotionGridView(context, emotionNames, screenWidth, spacing, itemWidth, gvHeight);
                gridViewList.add(gv);
                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<>();
            }
        }
        // 判断最后是否有不足20个表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(context, emotionNames, screenWidth, spacing, itemWidth, gvHeight);
            gridViewList.add(gv);
        }

    }

    @Override
    public int getCount() {
        return gridViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = gridViewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(gridViewList.get(position));
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(Context context, List<EmojiBean> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
        // 创建GridView
        GridView gv = new GridView(context);
        //设置点击背景透明
        gv.setSelector(android.R.color.transparent);
        //设置7列
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding * 2);
        //设置GridView的宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionGridViewAdapter adapter = new EmotionGridViewAdapter(emotionNames, itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener((parent, view, position, id) -> {
            if (listener != null) {
                if (position == adapter.getCount() - 1) {
                    listener.click(position, adapter.getCount(), null);
                } else {
                    Object item = adapter.getItem(position);
                    listener.click(position, adapter.getCount(), item);
                }
            }
        });
        return gv;
    }
}
