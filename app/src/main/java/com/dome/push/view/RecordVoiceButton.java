package com.dome.push.view;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.dome.push.R;
import com.dome.push.adapter.ChatAdapter;
import com.dome.push.util.FileTool;
import com.dome.push.util.IdHelper;
import com.dome.push.util.ToastTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.view
 * ClassName:      RecordVoiceButton
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-19 下午5:03
 * UpdateUser:     更新者
 * UpdateDate:     19-12-19 下午5:03
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class RecordVoiceButton extends AppCompatButton {

    private File myRecAudioFile;

    private static final int MIN_INTERVAL_TIME = 1000;// 1s
    private final static int CANCEL_RECORD = 5;
    private final static int START_RECORD = 7;
    //依次为按下录音键坐标、手指离开屏幕坐标、手指移动坐标
    float pressVoiceY, upVoiceY, moveVoiceY;
    //依次为开始录音时刻，按下录音时刻
    private long startTime;
    private long pressTime;
    // 麦克风弹窗
    private Dialog recordIndicator;
    // 时间太短弹窗
    private Dialog mTimeShort;
    // 麦克风图片
    private ImageView ivVolume;
    // 提示信息
    private TextView tvVoiceContent;
    private Chronometer mVoiceTime;
    private TextView mTimeDown;
    private LinearLayout mMicShow;

    // 录音实例对象
    private MediaRecorder recorder;

    private ObtainDecibelThread mThread;
    private Handler mVolumeHandler;
    private final MyHandler myHandler = new MyHandler(this);

    public static boolean mIsPressed = false;
    private Conversation conversation;
    private Timer timer = new Timer();
    private Timer mCountTimer;
    private boolean isTimerCanceled = false;
    private boolean mTimeUp = false;
    private static int[] res;

    private String userName;
    private String appKey;
    private ChatAdapter adapter;
    private RecyclerView recyclerView;

    public RecordVoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mVolumeHandler = new ShowVolumeHandler(this);
        //如果需要跳动的麦克图 将五张相同的图片替换即可
        res = new int[]{R.mipmap.icon_mic, R.mipmap.icon_mic, R.mipmap.icon_mic, R.mipmap.icon_mic, R.mipmap.icon_mic, R.mipmap.cancel_record};
    }

    public void initConversation(Conversation conv, ChatAdapter recordAdapter, RecyclerView recyclerView) {
        this.conversation = conv;
        this.adapter = recordAdapter;
        this.recyclerView = recyclerView;
        if (conv.getType() == ConversationType.single) {
            UserInfo userInfo = (UserInfo) conv.getTargetInfo();
            userName = userInfo.getUserName();
            appKey = userInfo.getAppKey();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.setPressed(true);
        int action = event.getAction();
        mTimeShort = new Dialog(getContext(), R.style.RecordVoiceDialog);
        mTimeShort.setContentView(R.layout.record_voice_short);
        float MIN_CANCEL_DISTANCE = 300f;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                sendSingleTransCommand("{\"type\": \"input\",\"content\": {\"message\": \""+IdHelper.getString(R.string.record_party_talking)+"\"}}");
                //文字 松开结束
                this.setText(IdHelper.getString(R.string.record_release_end));
                mIsPressed = true;
                pressTime = System.currentTimeMillis();
                pressVoiceY = event.getY();
                //检查sd卡是否存在
                if (FileTool.isSDCardExist()) {
                    if (isTimerCanceled) {
                        timer = createTimer();
                    }
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            android.os.Message msg = myHandler.obtainMessage();
                            msg.what = START_RECORD;
                            msg.sendToTarget();
                        }
                    }, 300);
                } else {
                    ToastTool.error(IdHelper.getString(R.string.record_create_file_fail));
                    this.setPressed(false);
                    //文字 按住说话
                    this.setText(IdHelper.getString(R.string.record_press_speak));
                    mIsPressed = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                sendSingleTransCommand(conversation.getTitle());
                //文字 按住说话
                this.setText(IdHelper.getString(R.string.record_press_speak));
                mIsPressed = false;
                this.setPressed(false);
                upVoiceY = event.getY();
                //松开录音按钮时刻
                long upVoiceTime = System.currentTimeMillis();
                if (upVoiceTime - pressTime < 300) {
                    showCancelDialog();
                    return true;
                } else if (upVoiceTime - pressTime < 1000) {
                    showCancelDialog();
                    cancelRecord();
                } else if (pressVoiceY - upVoiceY > MIN_CANCEL_DISTANCE) {
                    cancelRecord();
                } else if (upVoiceTime - pressTime < 60000) {
                    finishRecord();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                moveVoiceY = event.getY();
                //手指上滑到超出限定后，显示松开取消发送提示
                if (pressVoiceY - moveVoiceY > MIN_CANCEL_DISTANCE) {
                    //文字  松开手指取消发送
                    this.setText(IdHelper.getString(R.string.record_release_finger_cancel_send));
                    mVolumeHandler.sendEmptyMessage(CANCEL_RECORD);
                    if (mThread != null) {
                        mThread.exit();
                    }
                    mThread = null;
                } else {
                    //文字 送开结束
                    this.setText(IdHelper.getString(R.string.record_release_end));
                    if (mThread == null) {
                        mThread = new ObtainDecibelThread();
                        mThread.start();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel
                //文字 按住说话
                this.setText(IdHelper.getString(R.string.record_press_speak));
                cancelRecord();
                break;
        }

        return true;
    }

    private void showCancelDialog() {
        mTimeShort.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTimeShort.dismiss();
            }
        }, 1000);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            isTimerCanceled = true;
        }
        if (mCountTimer != null) {
            mCountTimer.cancel();
            mCountTimer.purge();
        }
    }

    private Timer createTimer() {
        timer = new Timer();
        isTimerCanceled = false;
        return timer;
    }

    private void initDialogAndStartRecord() {
        //存放录音文件目录
        File rootDir = getContext().getFilesDir();
        String fileDir = rootDir.getAbsolutePath() + "/voice";
        File destDir = new File(fileDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        //录音文件的命名格式
        myRecAudioFile = new File(fileDir, new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".amr");
        if (myRecAudioFile == null) {
            cancelTimer();
            stopRecording();
            ToastTool.error(IdHelper.getString(R.string.record_create_file_fail));
        }
        recordIndicator = new Dialog(getContext(), R.style.RecordVoiceDialog);
        recordIndicator.setContentView(R.layout.dialog_record_indicator);

        ivVolume = recordIndicator.findViewById(R.id.record_mic);
        tvVoiceContent = recordIndicator.findViewById(R.id.record_content);
        mVoiceTime = recordIndicator.findViewById(R.id.record_time);

        mTimeDown = recordIndicator.findViewById(R.id.record_time_down);
        mMicShow = recordIndicator.findViewById(R.id.record_mic_show);

        tvVoiceContent.setText(IdHelper.getString(R.string.record_release_end));
        startRecording();
        recordIndicator.show();
    }

    //录音完毕加载 ListView item
    private void finishRecord() {
        cancelTimer();
        stopRecording();

        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            mMicShow.setVisibility(GONE);
            myRecAudioFile.delete();
        } else {
            mMicShow.setVisibility(VISIBLE);
            if (myRecAudioFile != null && myRecAudioFile.exists()) {
                MediaPlayer mp = new MediaPlayer();
                try {
                    FileInputStream fis = new FileInputStream(myRecAudioFile);
                    mp.setDataSource(fis.getFD());
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //某些手机会限制录音，如果用户拒接使用录音，则需判断mp是否存在
                if (mp != null) {
                    int duration = mp.getDuration() / 1000;//即为时长 是s
                    if (duration < 1) {
                        duration = 1;
                    } else if (duration > 60) {
                        duration = 60;
                    }
                    try {
                        VoiceContent content = new VoiceContent(myRecAudioFile, duration);
                        cn.jpush.im.android.api.model.Message msg = conversation.createSendMessage(content);
                        adapter.addMsgFromReceiptToList(msg);
                        if (conversation.getType() == ConversationType.single) {
                            MessageSendingOptions options = new MessageSendingOptions();
                            options.setNeedReadReceipt(true);
                            JMessageClient.sendMessage(msg, options);
                        }
                        recyclerView.requestLayout();
                        recyclerView.postDelayed(() -> {
                            int position = adapter.getItemCount() - 1;
                            if (position > 0) {
                                recyclerView.smoothScrollToPosition(position);
                            }
                        }, 100);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastTool.error(IdHelper.getString(R.string.record_voice_permission_request));
                }
            }
        }
    }

    //取消录音，清除计时
    private void cancelRecord() {
        //可能在消息队列中还存在HandlerMessage，移除剩余消息
        mVolumeHandler.removeMessages(56, null);
        mVolumeHandler.removeMessages(57, null);
        mVolumeHandler.removeMessages(58, null);
        mVolumeHandler.removeMessages(59, null);
        mTimeUp = false;
        cancelTimer();
        stopRecording();
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }
        if (myRecAudioFile != null) {
            myRecAudioFile.delete();
        }
    }

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(myRecAudioFile.getAbsolutePath());
            myRecAudioFile.createNewFile();
            recorder.prepare();
            recorder.setOnErrorListener((mediaRecorder, i, i2) -> Log.i("RecordVoiceController", "recorder prepare failed!"));
            recorder.start();
            startTime = System.currentTimeMillis();

            mVoiceTime.setBase(SystemClock.elapsedRealtime());
            mVoiceTime.start();

            mCountTimer = new Timer();
            mCountTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimeUp = true;
                    android.os.Message msg = mVolumeHandler.obtainMessage();
                    msg.what = 50;
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", 10);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    mCountTimer.cancel();
                }
            }, 51000);

        } catch (IOException e) {
            e.printStackTrace();
            //HandleResponseCode.onHandle(mContext, 1003, false);
            ToastTool.error(IdHelper.getString(R.string.record_error_status));
            cancelTimer();
            dismissDialog();
            if (mThread != null) {
                mThread.exit();
                mThread = null;
            }
            if (myRecAudioFile != null) {
                myRecAudioFile.delete();
            }
            recorder.release();
            recorder = null;
        } catch (RuntimeException e) {
            ToastTool.error(IdHelper.getString(R.string.record_voice_permission_denied));
            cancelTimer();
            dismissDialog();
            if (mThread != null) {
                mThread.exit();
                mThread = null;
            }
            if (myRecAudioFile != null) {
                myRecAudioFile.delete();
            }
            recorder.release();
            recorder = null;
        }


        mThread = new ObtainDecibelThread();
        mThread.start();

    }

    //停止录音，隐藏录音动画
    private void stopRecording() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
        releaseRecorder();
    }

    public void releaseRecorder() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (Exception e) {
                Log.d("RecordVoice", "Catch exception: stop recorder failed!");
            } finally {
                recorder.release();
                recorder = null;
            }
        }
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                try {
                    int x = recorder.getMaxAmplitude();
                    if (x != 0) {
                        int f = (int) (10 * Math.log(x) / Math.log(10));
                        if (f < 20) {
                            mVolumeHandler.sendEmptyMessage(0);
                        } else if (f < 26) {
                            mVolumeHandler.sendEmptyMessage(1);
                        } else if (f < 32) {
                            mVolumeHandler.sendEmptyMessage(2);
                        } else if (f < 38) {
                            mVolumeHandler.sendEmptyMessage(3);
                        } else {
                            mVolumeHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void dismissDialog() {
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }
        this.setText(IdHelper.getString(R.string.record_press_speak));
    }

    /**
     * 录音动画控制
     */
    private static class ShowVolumeHandler extends Handler {

        private final WeakReference<RecordVoiceButton> lButton;

        ShowVolumeHandler(RecordVoiceButton button) {
            lButton = new WeakReference<>(button);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            RecordVoiceButton controller = lButton.get();
            if (controller != null) {
                int restTime = msg.getData().getInt("restTime", -1);
                // 若restTime>0, 进入倒计时
                if (restTime > 0) {
                    controller.mTimeUp = true;
                    android.os.Message msg1 = controller.mVolumeHandler.obtainMessage();
                    msg1.what = 60 - restTime + 1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", restTime - 1);
                    msg1.setData(bundle);
                    //创建一个延迟一秒执行的HandlerMessage，用于倒计时
                    controller.mVolumeHandler.sendMessageDelayed(msg1, 1000);
                    controller.mMicShow.setVisibility(GONE);
                    controller.mTimeDown.setVisibility(VISIBLE);
                    controller.mTimeDown.setText(restTime + "");

                    // 倒计时结束，发送语音, 重置状态
                } else if (restTime == 0) {
                    controller.finishRecord();
                    controller.setPressed(false);
                    controller.mTimeUp = false;
                    // restTime = -1, 一般情况
                } else {
                    // 没有进入倒计时状态
                    if (!controller.mTimeUp) {
                        if (msg.what < CANCEL_RECORD) {
                            controller.tvVoiceContent.setText(R.string.record_finger_up_cancel_send);
                            controller.tvVoiceContent.setBackgroundColor(IdHelper.getColor(android.R.color.transparent));
                        } else {
                            controller.tvVoiceContent.setText(R.string.record_release_finger_cancel_send);
                            controller.tvVoiceContent.setBackgroundColor(IdHelper.getColor(R.color.color_7e));
                        }
                        // 进入倒计时
                    } else {
                        if (msg.what == CANCEL_RECORD) {
                            controller.tvVoiceContent.setText(R.string.record_release_finger_cancel_send);
                            controller.tvVoiceContent.setBackgroundColor(IdHelper.getColor(R.color.color_7e));
                            if (!mIsPressed) {
                                controller.cancelRecord();
                            }
                        }
                    }
                    controller.ivVolume.setImageResource(res[msg.what]);
                }
            }
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<RecordVoiceButton> lButton;

        MyHandler(RecordVoiceButton button) {
            lButton = new WeakReference<>(button);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            RecordVoiceButton controller = lButton.get();
            if (controller != null) {
                if (msg.what == START_RECORD) {
                    if (mIsPressed) {
                        controller.initDialogAndStartRecord();
                    }
                }
            }
        }
    }

    private void sendSingleTransCommand(String content) {

        JMessageClient.sendSingleTransCommand(userName, appKey, content, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {

            }
        });
    }
}
