package com.dome.push.adapter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.adapter
 * ClassName:      TextWatcherAdapter
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-19 下午4:46
 * UpdateUser:     更新者
 * UpdateDate:     19-12-19 下午4:46
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public abstract class TextWatcherAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
