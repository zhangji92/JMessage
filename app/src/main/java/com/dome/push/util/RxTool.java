package com.dome.push.util;


import com.dome.push.base.BaseResponse;
import com.dome.push.enums.ResultExceptionEnum;
import com.dome.push.exception.ExceptionTool;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * ProjectName:    Bicycle
 * Package:        com.chetianxia.yundanche.utils
 * ClassName:      RxTool
 * Description:    描网络请求线程切换数据转化
 * Author:         张继
 * CreateDate:     19-10-19 下午8:47
 * UpdateUser:     更新者
 * UpdateDate:     19-10-19 下午8:47
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public final class RxTool {

    private RxTool() {
        throw new UnsupportedOperationException("your can`t instantiate me");
    }

    /**
     * 切换线程
     *
     * @param <T> 类型
     * @return 返回FlowableTransformer实例对象
     */
    public static <T> FlowableTransformer<T, T> applySchedulers() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 切换线程
     *
     * @param <T> 类型
     * @return 返回FlowableTransformer实例对象
     */
    public static <T> FlowableTransformer<BaseResponse<T>, T> handleResult() {
        return upstream -> upstream.flatMap((Function<BaseResponse<T>, Flowable<T>>) tHttpResponse -> {
            if (tHttpResponse.success()) {
                return createData(tHttpResponse.getData());
            } else {
                String msg = StringTool.getByCode(tHttpResponse.getResult(), ResultExceptionEnum.class);

                // 如果不正确 丢给错误代码统一处理类
                return Flowable.error(new ExceptionTool(msg, tHttpResponse.getResult()));
            }
        });
    }

    private static <T> Flowable<T> createData(T data) {
        return Flowable.create(emitter -> {
            try {
                emitter.onNext(data);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.BUFFER);

    }


}
