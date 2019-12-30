package com.dome.push.util;

import android.content.Context;
import android.util.SparseArray;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.helper.Logger;

/**
 * 处理tagalias相关的逻辑
 */
public class TagAliasHelper {
    private static final String TAG = "JIGUANG-TagAliasHelper";
    static int sequence = 1;
    /**
     * 覆盖
     */
    static final int ACTION_SET = 2;
    /**
     * 删除部分
     */
    static final int ACTION_DELETE = 3;

    private Context context;

    private static TagAliasHelper mInstance;

    private TagAliasHelper() {
    }

    public static TagAliasHelper getInstance() {
        if (mInstance == null) {
            synchronized (TagAliasHelper.class) {
                if (mInstance == null) {
                    mInstance = new TagAliasHelper();
                }
            }
        }
        return mInstance;
    }

    private void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    private SparseArray<Object> setActionCache = new SparseArray<Object>();

    public Object get(int sequence) {
        return setActionCache.get(sequence);
    }

    public Object remove(int sequence) {
        return setActionCache.get(sequence);
    }

    private void put(int sequence, Object tagAliasBean) {
        setActionCache.put(sequence, tagAliasBean);
    }

    /**
     * 处理设置tag
     */
    void handleAction(Context context, int sequence, TagAliasBean tagAliasBean) {
        init(context);
        if (tagAliasBean == null) {
            Logger.w(TAG, "tagAliasBean was null");
            return;
        }
        put(sequence, tagAliasBean);
        if (tagAliasBean.isAliasAction) {
            switch (tagAliasBean.action) {

                case ACTION_DELETE:
                    JPushInterface.deleteAlias(context, sequence);
                    break;
                case ACTION_SET:
                    JPushInterface.setAlias(context, sequence, tagAliasBean.alias);
                    break;
                default:
                    Logger.w(TAG, "unsupport alias action type");
                    return;
            }
        } else {
            switch (tagAliasBean.action) {

                case ACTION_SET:
                    JPushInterface.setTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteTags(context, sequence, tagAliasBean.tags);
                    break;
                default:
                    Logger.w(TAG, "unsupport tag action type");
                    return;
            }
        }
    }

    private String getActionStr(int actionType) {
        switch (actionType) {
            case ACTION_SET:
                return "set";
            case ACTION_DELETE:
                return "delete";
        }
        return "unkonw operation";
    }

    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Logger.i(TAG, "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags());
        Logger.i(TAG, "tags size:" + jPushMessage.getTags().size());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            //ToastUtil.showToast("获取缓存记录失败");
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            Logger.i(TAG, "action - modify tag Success,sequence:" + sequence);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tags success";
            Logger.i(TAG, logs);
            //ToastUtil.showToast(logs);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags";
            if (jPushMessage.getErrorCode() == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            Logger.e(TAG, logs);
            //if(!RetryActionIfNeeded(jPushMessage.getErrorCode(),tagAliasBean)) {
            //    //ToastUtil.showToast(logs);
            //}
        }
    }

    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Logger.i(TAG, "action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            //ToastUtil.showToast("获取缓存记录失败");
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            Logger.i(TAG, "tagBean:" + tagAliasBean);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
            Logger.i(TAG, logs);
            //ToastUtil.showToast(logs);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags, errorCode:" + jPushMessage.getErrorCode();
            Logger.e(TAG, logs);
            //if(!RetryActionIfNeeded(jPushMessage.getErrorCode(),tagAliasBean)) {
            //    //ToastUtil.showToast(logs);
            //}
        }
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Logger.i(TAG, "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            //ToastUtil.showToast("获取缓存记录失败");
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            Logger.i(TAG, "action - modify alias Success,sequence:" + sequence);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " alias success";
            Logger.i(TAG, logs);
            //ToastUtil.showToast(logs);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " alias, errorCode:" + jPushMessage.getErrorCode();
            Logger.e(TAG, logs);
            //if(!RetryActionIfNeeded(jPushMessage.getErrorCode(),tagAliasBean)) {
            //    //ToastUtil.showToast(logs);
            //}
        }
    }

    //设置手机号码回调
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Logger.i(TAG, "action - onMobileNumberOperatorResult, sequence:" + sequence + ",mobileNumber:" + jPushMessage.getMobileNumber());
        init(context);
        if (jPushMessage.getErrorCode() == 0) {
            Logger.i(TAG, "action - set mobile number Success,sequence:" + sequence);
            setActionCache.remove(sequence);
        } else {
            String logs = "Failed to set mobile number, errorCode:" + jPushMessage.getErrorCode();
            Logger.e(TAG, logs);
            //if(!RetrySetMObileNumberActionIfNeeded(jPushMessage.getErrorCode(),jPushMessage.getMobileNumber())){
            //    //ToastUtil.showToast(logs);
            //}
        }
    }

    static class TagAliasBean {
        int action;
        Set<String> tags;
        String alias;
        boolean isAliasAction;

    }


}
