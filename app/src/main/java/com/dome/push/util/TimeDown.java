package com.dome.push.util;

import android.os.CountDownTimer;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.util
 * ClassName:      TimeDown
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-11 上午9:25
 * UpdateUser:     更新者
 * UpdateDate:     19-12-11 上午9:25
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public abstract class TimeDown extends CountDownTimer {
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public TimeDown(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {

    }
}
