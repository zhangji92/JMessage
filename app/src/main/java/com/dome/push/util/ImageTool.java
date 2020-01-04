package com.dome.push.util;

import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.util
 * ClassName:      ImageTool
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-25 下午4:31
 * UpdateUser:     更新者
 * UpdateDate:     19-12-25 下午4:31
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ImageTool {
    /**
     * 设置图片最小宽高
     *
     * @param path      图片路径
     * @param imageView 显示图片的View
     */
    public static ImageView setPictureScale(String path, final ImageView imageView) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);


        //计算图片缩放比例
        double imageWidth = opts.outWidth;
        double imageHeight = opts.outHeight;
        return setDensity(imageWidth, imageHeight, imageView);
    }

    private static ImageView setDensity(double imageWidth, double imageHeight, ImageView imageView) {
        if (imageWidth > 350) {
            imageWidth = 550;
            imageHeight = 250;
        } else if (imageHeight > 450) {
            imageWidth = 300;
            imageHeight = 450;
        } else if ((imageWidth < 50 && imageWidth > 20) || (imageHeight < 50 && imageHeight > 20)) {
            imageWidth = 200;
            imageHeight = 300;
        } else if (imageWidth < 20 || imageHeight < 20) {
            imageWidth = 100;
            imageHeight = 150;
        } else {
            imageWidth = 300;
            imageHeight = 450;
        }

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int) imageWidth;
        params.height = (int) imageHeight;
        imageView.setLayoutParams(params);

        return imageView;
    }
}
