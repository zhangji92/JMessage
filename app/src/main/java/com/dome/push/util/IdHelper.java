package com.dome.push.util;

import java.lang.reflect.Field;

public class IdHelper {

    public static int getLayout(String layoutName) {
        return ApplicationTool.getApp().getResources().getIdentifier(layoutName, "layout",
                ApplicationTool.getApp().getApplicationContext().getPackageName());
    }

    public static int getViewID(String IDName) {
        return ApplicationTool.getApp().getResources().getIdentifier(IDName, "id",
                ApplicationTool.getApp().getApplicationContext().getPackageName());
    }

    public static int getDrawable(String drawableName) {
        return ApplicationTool.getApp().getResources().getIdentifier(drawableName, "drawable",
                ApplicationTool.getApp().getApplicationContext().getPackageName());
    }

    public static int getMipmap(String drawableName) {
        return ApplicationTool.getApp().getResources().getIdentifier(drawableName, "mipmap",
                ApplicationTool.getApp().getApplicationContext().getPackageName());
    }

    public static int getAttr(String attrName) {
        return ApplicationTool.getApp().getResources().getIdentifier(attrName, "attr",
                ApplicationTool.getApp().getApplicationContext().getPackageName());
    }

    public static int getString(String stringName) {
        return ApplicationTool.getApp().getResources().getIdentifier(stringName, "string",
                ApplicationTool.getApp().getApplicationContext().getPackageName());
    }

    public static String getString(int stringName) {
        return ApplicationTool.getApp().getString(stringName);
    }

    public static int getColor(int color) {
        return ApplicationTool.getApp().getResources().getColor(color);
    }

    public static int getStyle(String styleName) {
        return ApplicationTool.getApp().getResources().getIdentifier(styleName, "style",
                ApplicationTool.getApp().getApplicationContext().getPackageName());
    }

    public static int[] getResourceDeclareStyleableIntArray(String name) {
        try {
            //反射拿到包名.因为本应用manifest中包名和gradle中不一样,这里手动填上了
            Field[] fields2 = Class.forName("jiguang.chat" + ".R$styleable").getFields();

            //browse all fields
            for (Field f : fields2) {
                //pick matching field
                if (f.getName().equals(name)) {
                    //return as int array
                    return (int[]) f.get(null);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static int getAnim(String animName) {
        return ApplicationTool.getApp().getResources().getIdentifier(animName, "anim",
                ApplicationTool.getApp().getApplicationContext().getPackageName());
    }
}
