package com.dome.push.util;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * ProjectName:    Bicycle
 * Package:        com.chetianxia.yundanche.utils
 * ClassName:      RxHandleTool
 * Description:    CompositeDisposable 处理类
 * Author:         张继
 * CreateDate:     19-10-19 下午7:47
 * UpdateUser:     更新者
 * UpdateDate:     19-10-19 下午7:47
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public final class RxHandleTool {

    private Map<String, CompositeDisposable> map;
    private static RxHandleTool util = null;

    public static RxHandleTool getInstance() {
        if (util == null) {
            synchronized (RxHandleTool.class) {
                if (util == null) {
                    util = new RxHandleTool();
                }
            }
        }
        return util;
    }

    private RxHandleTool() {
    }

    /**
     * 添加 Disposable
     * @param tclass 类
     * @param disposable 网络链接
     */
    public void bindDisposable(Class tclass, Disposable disposable) {
        if (map == null) {
            map = new HashMap<>();
        }
        String name = tclass.getName();
        if (map.get(name)!=null) {
            map.get(name).add(disposable);
        }else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            map.put(name, compositeDisposable);
        }

    }

    /**
     * 根据类删除链接请求
     * @param tclass 类
     */
    public void unDisposable(Class tclass) {
        if (map==null) return;
        String name = tclass.getName();
        if (!map.containsKey(name)) return;
        CompositeDisposable disposable = map.get(name);
        if (disposable ==null) return;
        disposable.dispose();
        disposable.clear();
        map.remove(name);
    }



}
