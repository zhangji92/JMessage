package com.dome.push.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.util
 * ClassName:      GlideTool
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-12 下午2:45
 * UpdateUser:     更新者
 * UpdateDate:     19-12-12 下午2:45
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class GlideTool {
    private volatile static Pattern NUMBER_PATTERN = Pattern.compile("[0-9]*");

    private GlideTool() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 加载图片
     *
     * @param context 上下文
     * @param url     加载图片路径
     * @param view    图片控件
     */
    public static void load(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }


    /**
     * 加载图片
     *
     * @param context 上下文
     * @param url     加载图片资源文件
     * @param view    图片控件
     */
    public static void load(Context context, int url, ImageView view) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    /**
     * 加载模糊图片
     *
     * @param context 上下文
     * @param url     加载图片路径
     * @param view    图片控件
     */
    public static void loadBlur(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 4)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    /**
     * 加载模糊图片
     *
     * @param context 上下文
     * @param url     加载图片资源文件
     * @param view    图片控件
     */
    public static void loadBlur(Context context, int url, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 4)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    public static void load(Context activity, File file, ImageView imageView) {
        Glide.with(activity)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }


}
