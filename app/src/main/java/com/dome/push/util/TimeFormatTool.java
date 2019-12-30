package com.dome.push.util;

import com.dome.push.R;

import java.sql.Date;
import java.text.SimpleDateFormat;

import cn.jiguang.api.JCoreInterface;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.util
 * ClassName:      TimeFormatTool
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-24 下午3:09
 * UpdateUser:     更新者
 * UpdateDate:     19-12-24 下午3:09
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class TimeFormatTool {
    private static long lastClickTime;

    /**
     * 判断控件是否被快速点击
     *
     * @param millisecond 间隔时间
     * @return true连续点击 false未连续点击
     */
    public static boolean isFastClick(int millisecond) {
        long curClickTime = System.currentTimeMillis();
        long interval = (curClickTime - lastClickTime);

        if (0 < interval && interval < millisecond) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            return true;
        }
        lastClickTime = curClickTime;
        return false;
    }

    //用于显示消息具体时间
    public static String getDetailTime(long timeStamp) {
        //最后一条消息的 年 月 日 时 分
        //yyyy-MM-dd HH:mm:ss
        Date date = new Date(timeStamp);
        String dateStr = format(date, IdHelper.getString(R.string.time_format_accuracy));
        String oldYear = dateStr.substring(0, 4);
        int oldMonth = Integer.parseInt(dateStr.substring(5, 7));
        int oldDay = Integer.parseInt(dateStr.substring(8, 10));
        String oldHour = dateStr.substring(11, 13);
        String oldMinute = dateStr.substring(14, 16);

        //当前时间
        long today = JCoreInterface.getReportTime();//当前时间
        Date now = new Date(today * 1000);//当前时间
        String nowStr = format(now, IdHelper.getString(R.string.time_format_accuracy));

        String newYear = nowStr.substring(0, 4);
        int newMonth = Integer.parseInt(nowStr.substring(5, 7));
        int newDay = Integer.parseInt(nowStr.substring(8, 10));//当前 日
        String result = "";
        //String newHour = nowStr.substring(11, 13);
        //String newMinute = nowStr.substring(14, 16);
        //long l = today * 1000 - timeStamp;
        //long days = l / (24 * 60 * 60 * 1000);
        //long hours = (l / (60 * 60 * 1000) - days * 24);
        //long min = ((l / (60 * 1000)) - days * 24 * 60 - hours * 60);
        //long s = (l / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - min * 60);

        if (!oldYear.equals(newYear)) {
            //往年
            result = oldYear + "-" + oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
        } else {
            //今年
            //同月
            if (oldMonth == newMonth) {
                //同天
                if (oldDay == newDay) {
                    result = oldHour + ":" + oldMinute;
                } else {
                    //不同天
                    int day = newDay - oldDay;
                    if (day == 1) {
                        result = IdHelper.getString(R.string.yesterday) + oldHour + ":" + oldMinute;
                    } else if (day == 2) {
                        result = IdHelper.getString(R.string.day_before_yesterday) + oldHour + ":" + oldMinute;
                    } else if (day > 2 && day < 8) {
                        int week = date.getDay();
                        if (week == 1) {
                            result = IdHelper.getString(R.string.monday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 2) {
                            result = IdHelper.getString(R.string.tuesday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 3) {
                            result = IdHelper.getString(R.string.wednesday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 4) {
                            result = IdHelper.getString(R.string.thursday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 5) {
                            result = IdHelper.getString(R.string.friday) + " " + oldHour + ":" + oldMinute;
                        } else if (week == 6) {
                            result = IdHelper.getString(R.string.saturday) + " " + oldHour + ":" + oldMinute;
                        } else {
                            result = IdHelper.getString(R.string.sunday) + " " + oldHour + ":" + oldMinute;
                        }
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                }
            } else {
                if (oldMonth == 1 || oldMonth == 3 || oldMonth == 5 || oldMonth == 7 || oldMonth == 8 || oldMonth == 10 || oldMonth == 12) {
                    if (newDay == 1 && oldDay == 30) {
                        result = IdHelper.getString(R.string.day_before_yesterday) + oldHour + ":" + oldMinute;
                    } else if (newDay == 1 && oldDay == 31) {
                        result = IdHelper.getString(R.string.yesterday) + oldHour + ":" + oldMinute;
                    } else if (newDay == 2 && oldDay == 31) {
                        result = IdHelper.getString(R.string.day_before_yesterday) + oldHour + ":" + oldMinute;
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                } else if (oldMonth == 2) {
                    if (newDay == 1 && oldDay == 27 || newDay == 2 && oldDay == 28) {
                        result = IdHelper.getString(R.string.day_before_yesterday) + oldHour + ":" + oldMinute;
                    } else if (newDay == 1 && oldDay == 28) {
                        result = IdHelper.getString(R.string.yesterday) + oldHour + ":" + oldMinute;
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                } else if (oldMonth == 4 || oldMonth == 6 || oldMonth == 9 || oldMonth == 11) {
                    if (newDay == 1 && oldDay == 29) {
                        result = IdHelper.getString(R.string.day_before_yesterday) + oldHour + ":" + oldMinute;
                    } else if (newDay == 1 && oldDay == 30) {
                        result = IdHelper.getString(R.string.yesterday) + oldHour + ":" + oldMinute;
                    } else if (newDay == 2 && oldDay == 30) {
                        result = IdHelper.getString(R.string.day_before_yesterday) + oldHour + ":" + oldMinute;
                    } else {
                        result = oldMonth + "-" + oldDay + " " + oldHour + ":" + oldMinute;
                    }
                }
            }
        }
        return result;
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
