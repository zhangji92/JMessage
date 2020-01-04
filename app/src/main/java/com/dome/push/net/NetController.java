package com.dome.push.net;


import com.dome.push.base.BaseResponse;
import com.dome.push.util.RxTool;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.net
 * ClassName:      NetController
 * Description:    网络处理类
 * Author:         张继
 * CreateDate:     19-10-19 下午9:11
 * UpdateUser:     更新者
 * UpdateDate:     19-10-19 下午9:11
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public final class NetController {
    private static NetController controller;

    public static NetController getInstance() {
        if (controller == null) {
            synchronized (NetController.class) {
                if (controller == null) {
                    controller = new NetController();
                }
            }
        }
        return controller;
    }

    private NetController() {
    }

    /**
     * 普通网络请求
     *
     * @param flowable   接口
     * @param subscriber 数据返回
     * @return 返回 Disposable
     */
    public <T> Disposable subscriber(Flowable<BaseResponse<T>> flowable, ResourceSubscriber subscriber) {
        return flowable.compose(RxTool.applySchedulers())
                .compose(RxTool.handleResult())
                .subscribeWith(subscriber);
    }

    ///**
    // * 普通网络请求
    // *
    // * @param flowable 接口
    // * @param callBack 数据返回
    // * @return 返回Disposable
    // */
    //public <T> Disposable subscriberDelay(Flowable<BaseResponse<T>> flowable, INetCallBack callBack) {
    //    int delay = 5;
    //    long nowMillis = System.currentTimeMillis();
    //    return flowable.doOnSubscribe(subscription -> {
    //        if (System.currentTimeMillis() - nowMillis > 45 * 1000) {
    //            subscription.cancel();
    //        }
    //    }).subscribeOn(Schedulers.io())
    //            .observeOn(AndroidSchedulers.mainThread())
    //            .doOnNext(response -> {
    //                if (response.success()) {
    //                    callBack.success(response.getData());
    //                } else {
    //                    String msg = ToolUtil.getByCode(response.getResult(), EnumResponseCode.class);
    //                    callBack.error(new HandleExceptionUtil(response.getResult(), msg));
    //                }
    //            })
    //            .doOnError(throwable -> callBack.error(new HandleExceptionUtil(throwable)))
    //            .delay(delay, TimeUnit.SECONDS, true)// 设置delayError为true，表示出现错误的时候也需要延迟5s进行通知，达到无论是请求正常还是请求失败，都是5s后重新订阅，即重新请求。
    //            .repeat()
    //            .retry()
    //            .subscribe();
    //
    //}

}
