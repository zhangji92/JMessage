package com.dome.push.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dome.push.R;
import com.dome.push.base.BaseActivity;
import com.dome.push.base.BasePresenter;
import com.dome.push.view.RxTitle;

import java.io.File;

import butterknife.BindView;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.VideoContent;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.activity
 * ClassName:      DownLoadActivity
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-26 下午4:06
 * UpdateUser:     更新者
 * UpdateDate:     19-12-26 下午4:06
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class DownLoadActivity extends BaseActivity {
    @BindView(R.id.layout_status)
    Toolbar layoutStatus;

    @BindView(R.id.down_toolbar)
    RxTitle downTitle;


    @BindView(R.id.down_file_name)
    TextView fileName;
    @BindView(R.id.btn_down)
    Button btnDown;
    @BindView(R.id.process_num)
    TextView processNum;
    @BindView(R.id.down_process)
    ProgressBar processbar;
    private int mProcessNum;

    @Override
    protected int layoutResId() {
        return R.layout.activity_down;
    }

    @Override
    protected void initTitle(Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        cn.jpush.im.android.api.model.Message msg = (cn.jpush.im.android.api.model.Message) extras.getSerializable("msg");
        VideoContent videoContent = (VideoContent) msg.getContent();
        fileName.setText(videoContent.getFileName());
        downTitle.setTitle(videoContent.getFileName());
        long fileSize = videoContent.getMediaFileSize();
        btnDown.setText("下载(" + byteToMB(fileSize) + ")");
        btnDown.setTextColor(Color.WHITE);


        btnDown.setOnClickListener(v -> {

            btnDown.setVisibility(View.GONE);
            processbar.setVisibility(View.VISIBLE);
            processbar.setVisibility(View.VISIBLE);
            msg.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double percent) {
                    mProcessNum = (int) (percent * 100);
                    processNum.setText(mProcessNum + "%");
                    mHandler.post(progressBar);
                }
            });

            videoContent.downloadVideoFile(msg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int responseCode, String responseMessage, File file) {
                    finish();
                }
            });
        });

        downTitle.setLeftIconOnClickListener(v -> {
            finish();
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            processbar.setProgress(msg.arg1);
            mHandler.post(progressBar);
        }
    };

    Runnable progressBar = new Runnable() {
        @Override
        public void run() {
            Message msg = mHandler.obtainMessage();
            msg.arg1 = mProcessNum;

            mHandler.sendMessage(msg);
            if (mProcessNum == 100) {
                mHandler.removeCallbacks(progressBar);
            }
        }
    };

    private String byteToMB(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size > kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

}
