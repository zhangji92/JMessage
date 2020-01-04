package com.dome.push.util;

import com.dome.push.enums.ResultExceptionEnum;

import java.util.UUID;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.util
 * ClassName:      StringTool
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-10 下午3:36
 * UpdateUser:     更新者
 * UpdateDate:     19-12-10 下午3:36
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class StringTool {
    /**
     * 根据code获取msg
     *
     * @param code code
     * @param t    枚举类
     * @return 返回字符串
     */
    public static <T extends ResultExceptionEnum> String getByCode(Integer code, Class<T> t) {
        for (T item : t.getEnumConstants()) {
            if (item.getCode() == code) {
                return item.getMsg();
            }
        }
        return "操作异常";
    }
    /**
     * 生成唯一号
     *
     * @return
     */
    public static String get36UUID() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        return uniqueId;
    }
}
