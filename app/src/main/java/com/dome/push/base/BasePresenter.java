package com.dome.push.base;

import android.graphics.drawable.Drawable;

import com.dome.push.net.NetController;
import com.dome.push.net.NetRequest;
import com.dome.push.util.RxHandleTool;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * ProjectName:    Bicycle
 * Package:        com.chetianxia.yundanche.base
 * ClassName:      BasePresenter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-10-19 下午7:36
 * UpdateUser:     更新者
 * UpdateDate:     19-10-19 下午7:36
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class BasePresenter<V extends IBaseView> {

    private Reference<V> reference;

    public BasePresenter(V reference) {
        this.reference = new WeakReference<>(reference);
    }

    public V getView() {
        if (isAttachView()) {
            return reference.get();
        }
        return null;
    }

    /**
     * 是否存在
     *
     * @return true存在 false不存在
     */
    private boolean isAttachView() {
        return reference != null && reference.get() != null;
    }

    protected <T> void subsricber(Class tclass, Flowable<BaseResponse<T>> flowable, ResourceSubscriber subscriber) {
        Disposable disposable = NetController.getInstance().subscriber(flowable, subscriber);
        RxHandleTool.getInstance().bindDisposable(tclass, disposable);

    }

    protected IApiService getService() {
        return NetRequest.getInstance().getService();
    }

    protected void detachView() {
        if (isAttachView()) {
            reference.clear();
            reference = null;
        }
    }

    public String getString(int res) {
        return getView().getBaseActivity().getString(res);
    }

    public int getColor(int colorRes) {
        return getView().getBaseActivity().getResources().getColor(colorRes);
    }

    public Drawable getDrawable(int colorRes) {
        return getView().getBaseActivity().getResources().getDrawable(colorRes);
    }

    public int getDimens(int colorRes) {
        return getView().getBaseActivity().getResources().getDimensionPixelOffset(colorRes);
    }


}
