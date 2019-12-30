package com.dome.push.base;

/**
 * ProjectName:    Bicycle
 * Package:        com.chetianxia.yundanche.base
 * ClassName:      BaseResponse
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-10-19 下午9:00
 * UpdateUser:     更新者
 * UpdateDate:     19-10-19 下午9:00
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class BaseResponse<T> {
    private int result;
    private String msg;
    private T data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean success() {
        return result == 0;
    }
}
