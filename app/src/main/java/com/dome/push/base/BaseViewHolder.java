package com.dome.push.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {


    public BaseViewHolder(ViewGroup parent, @LayoutRes int itemView) {
        super(LayoutInflater.from(parent.getContext()).inflate(itemView, parent, false));
    }

    public BaseViewHolder(View view) {
        super(view);
    }

    /**
     * 获取布局中的View
     *
     * @param viewId view的Id
     * @param <T>    View的类型
     * @return view
     */
    protected <T extends View> T findViewById(@IdRes int viewId) {
        return itemView.findViewById(viewId);
    }

    /**
     * 获取Context实例
     *
     * @return context
     */
    protected Context getContext() {
        return itemView.getContext();
    }

    /**
     * 设置数据
     *
     * @param data 要显示的数据对象
     */
    public abstract void setData(T data, int position);


}
