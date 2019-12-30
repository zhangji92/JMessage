package com.dome.push.exception;

import com.dome.push.enums.ResultExceptionEnum;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.util
 * ClassName:      ExceptionTool
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-10 下午2:23
 * UpdateUser:     更新者
 * UpdateDate:     19-12-10 下午2:23
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ExceptionTool extends Exception {
    private int code;

    public ExceptionTool(ResultExceptionEnum result) {
        super(result.getMsg());
    }

    public ExceptionTool(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
