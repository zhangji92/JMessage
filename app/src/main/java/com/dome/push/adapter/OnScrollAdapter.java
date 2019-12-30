package com.dome.push.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.adapter
 * ClassName:      OnScrollAdapter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-27 下午12:09
 * UpdateUser:     更新者
 * UpdateDate:     19-12-27 下午12:09
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public abstract class OnScrollAdapter extends RecyclerView.OnScrollListener {

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }
}
