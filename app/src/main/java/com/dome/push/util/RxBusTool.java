package com.dome.push.util;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBusTool {

    private final Subject<Object> mSubject;//背压测试

    private static volatile RxBusTool util = null;

    /**
     * 单例模式
     *
     * @return 返回当前实例对象
     */
    public static RxBusTool getInstance() {
        if (util == null) {
            synchronized (RxBusTool.class) {
                if (util == null) {
                    util = new RxBusTool();
                }
            }
        }
        return util;
    }

    private RxBusTool() {
        mSubject = PublishSubject.create().toSerialized();
    }

    public void post(Object obj) {
        if (hasObservers()) {
            mSubject.onNext(obj);
        }
    }

    /**
     * 返回指定类型的带背压的Flowable实例
     *
     * @param type 类型
     * @return 返回 Flowable 实例对象
     */
    private <T> Flowable<T> getObservable(Class<T> type) {
        return mSubject.toFlowable(BackpressureStrategy.BUFFER)
                .ofType(type);
    }

    /**
     * 一个默认的订阅方法
     *
     * @param type  类型
     * @param next  接受信息
     * @param error 错误信息
     * @return 返回 Disposable
     */
    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        return getObservable(type)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    /**
     * 是否已有观察者订阅
     *
     * @return true 有观察者 False无观察者
     */
    private boolean hasObservers() {
        return mSubject.hasObservers();
    }

}
