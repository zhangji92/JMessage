package com.dome.push.enums;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.util
 * ClassName:      ResultExceptionEnum
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-10 下午2:25
 * UpdateUser:     更新者
 * UpdateDate:     19-12-10 下午2:25
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public enum ResultExceptionEnum {
    ;

    private int code;
    private String msg;

    ResultExceptionEnum() {
    }

    ResultExceptionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
