package com.dome.push.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dome.push.R;
import com.dome.push.base.BaseActivity;
import com.dome.push.base.BasePresenter;
import com.dome.push.base.BaseUrl;
import com.dome.push.util.AndroidWorkaroundTool;
import com.dome.push.util.CameraManagerTool;
import com.dome.push.util.FileTool;
import com.dome.push.util.MediaPlayerManager;
import com.dome.push.util.TimeFormatTool;
import com.dome.push.view.CameraProgressBar;
import com.dome.push.view.CameraView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class CameraActivity extends BaseActivity {
    /**
     * 获取相册
     */
    public static final int REQUEST_PHOTO = 1;
    /**
     * 获取视频
     */
    public static final int REQUEST_VIDEO = 2;
    /**
     * 最小录制时间
     */
    private static final int MIN_RECORD_TIME = 1 * 1000;
    /**
     * 最长录制时间
     */
    private static final int MAX_RECORD_TIME = 60 * 1000;
    /**
     * 刷新进度的间隔时间
     */
    private static final int PLUSH_PROGRESS = 100;

    @BindView(R.id.mTextureView)
    TextureView mTextureView;
    @BindView(R.id.layout_status)
    Toolbar layoutStatus;
    @BindView(R.id.mCameraView)
    CameraView mCameraView;
    @BindView(R.id.tv_flash)
    TextView tv_flash;
    @BindView(R.id.iv_facing)
    ImageView iv_facing;
    @BindView(R.id.rl_camera)
    RelativeLayout rl_camera;
    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.iv_choice)
    ImageView iv_choice;
    @BindView(R.id.mProgressbar)
    CameraProgressBar mProgressbar;
    @BindView(R.id.take_photo)
    RelativeLayout takePhoto;
    @BindView(R.id.tv_tack)
    TextView mTv_tack;

    /**
     * camera manager
     */
    private CameraManagerTool cameraManagerTool;
    /**
     * player manager
     */
    private MediaPlayerManager playerManager;
    /**
     * true代表视频录制,否则拍照
     */
    private boolean isSupportRecord;
    /**
     * 视频录制地址
     */
    private String recorderPath;
    /**
     * 录制视频的时间,毫秒
     */
    private int recordSecond;
    /**
     * 获取照片订阅, 进度订阅
     */
    DisposableObserver<Boolean> takePhotoSubscription;
    private DisposableObserver<Long> disposableObserver;

    /**
     * 是否正在录制
     */
    private boolean isRecording;

    /**
     * 是否为点了拍摄状态(没有拍照预览的状态)
     */
    private boolean isPhotoTakingState;

    public static void lanuchForPhoto(Activity context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivityForResult(intent, REQUEST_PHOTO);
    }


    @Override
    protected int layoutResId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initTitle(Bundle savedInstanceState) {
        AndroidWorkaroundTool.isAndroidp(layoutStatus, this);
    }

    @Override
    protected void initData() {
        cameraManagerTool = CameraManagerTool.getInstance(getApplication());
        playerManager = MediaPlayerManager.getInstance(getApplication());
        cameraManagerTool.setCameraType(isSupportRecord ? 1 : 0);

        tv_flash.setVisibility(cameraManagerTool.isSupportFlashCamera() ? View.VISIBLE : View.GONE);
        setCameraFlashState();
        //iv_facing.setVisibility(cameraManagerTool.isSupportFrontCamera() ? View.VISIBLE : View.GONE);
        rl_camera.setVisibility(cameraManagerTool.isSupportFlashCamera()
                || cameraManagerTool.isSupportFrontCamera() ? View.VISIBLE : View.GONE);

        final int max = MAX_RECORD_TIME / PLUSH_PROGRESS;
        mProgressbar.setMaxProgress(max);

        mProgressbar.setOnProgressTouchListener(new CameraProgressBar.OnProgressTouchListener() {
            @Override
            public void onClick(CameraProgressBar progressBar) {
                mTv_tack.setVisibility(View.GONE);
                if (TimeFormatTool.isFastClick(2000)) {
                    return;
                }
                cameraManagerTool.takePhoto(callback);
            }

            @Override
            public void onLongClick(CameraProgressBar progressBar) {
                mTv_tack.setVisibility(View.GONE);

                if (TimeFormatTool.isFastClick(2000)) {
                    return;
                }
                isSupportRecord = true;
                cameraManagerTool.setCameraType(1);
                rl_camera.setVisibility(View.GONE);
                recorderPath = FileTool.getUploadVideoFile(CameraActivity.this);
                cameraManagerTool.startMediaRecord(recorderPath);
                isRecording = true;
                disposableObserver = Observable.interval(100, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .take(max)
                        .subscribeWith(new DisposableObserver<Long>() {


                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                stopRecorder(true);
                            }

                            @Override
                            public void onNext(Long aLong) {
                                mProgressbar.setProgress(mProgressbar.getProgress() + 1);
                            }
                        });
            }

            @Override
            public void onZoom(boolean zoom) {
                cameraManagerTool.handleZoom(zoom);
            }

            @Override
            public void onLongClickUp(CameraProgressBar progressBar) {
                isSupportRecord = false;
                cameraManagerTool.setCameraType(0);
                stopRecorder(true);
                if (disposableObserver != null) {
                    disposableObserver.dispose();
                }
            }

            @Override
            public void onPointerDown(float rawX, float rawY) {
                if (mTextureView != null) {
                    mCameraView.setFoucsPoint(new PointF(rawX, rawY));
                }
            }
        });

        mCameraView.setOnViewTouchListener(new CameraView.OnViewTouchListener() {
            @Override
            public void handleFocus(float x, float y) {
                cameraManagerTool.handleFocusMetering(x, y);
            }

            @Override
            public void handleZoom(boolean zoom) {
                cameraManagerTool.handleZoom(zoom);
            }
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }


    /**
     * 设置闪光状态
     */
    private void setCameraFlashState() {
        int flashState = cameraManagerTool.getCameraFlash();
        switch (flashState) {
            case 0: //自动
                tv_flash.setSelected(true);
                tv_flash.setText("自动");
                break;
            case 1://open
                tv_flash.setSelected(true);
                tv_flash.setText("开启");
                break;
            case 2: //close
                tv_flash.setSelected(false);
                tv_flash.setText("关闭");
                break;
        }
    }

    /**
     * 是否显示录制按钮
     *
     * @param isShow
     */
    private void setTakeButtonShow(boolean isShow) {
        if (isShow) {
            mProgressbar.setVisibility(View.VISIBLE);
            rl_camera.setVisibility(cameraManagerTool.isSupportFlashCamera()
                    || cameraManagerTool.isSupportFrontCamera() ? View.VISIBLE : View.GONE);
        } else {
            mProgressbar.setVisibility(View.GONE);
            rl_camera.setVisibility(View.GONE);
        }
    }

    /**
     * 停止拍摄
     */
    private void stopRecorder(boolean play) {
        isRecording = false;
        cameraManagerTool.stopMediaRecord();
        recordSecond = mProgressbar.getProgress() * PLUSH_PROGRESS;//录制多少毫秒
        mProgressbar.reset();
        if (recordSecond < MIN_RECORD_TIME) {//小于最小录制时间作废
            if (recorderPath != null) {
                FileTool.delteFiles(new File(recorderPath));
                recorderPath = null;
                recordSecond = 0;
            }
            setTakeButtonShow(true);
        } else if (play && mTextureView != null && mTextureView.isAvailable()) {
            setTakeButtonShow(false);
            mProgressbar.setVisibility(View.GONE);
            iv_choice.setVisibility(View.VISIBLE);
            cameraManagerTool.closeCamera();
            playerManager.playMedia(new Surface(mTextureView.getSurfaceTexture()), recorderPath);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTextureView.isAvailable()) {
            if (recorderPath != null) {//优先播放视频
                iv_choice.setVisibility(View.VISIBLE);
                setTakeButtonShow(false);
                playerManager.playMedia(new Surface(mTextureView.getSurfaceTexture()), recorderPath);
            } else {
                iv_choice.setVisibility(View.GONE);
                setTakeButtonShow(true);
                cameraManagerTool.openCamera(this, mTextureView.getSurfaceTexture(),
                        mTextureView.getWidth(), mTextureView.getHeight());
            }
        } else {
            mTextureView.setSurfaceTextureListener(listener);
        }
    }

    @Override
    protected void onPause() {
        if (disposableObserver != null) {
            disposableObserver.dispose();
        }
        if (takePhotoSubscription != null) {
            takePhotoSubscription.dispose();
        }
        if (isRecording) {
            stopRecorder(false);
        }
        cameraManagerTool.closeCamera();
        playerManager.stopMedia();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mCameraView.removeOnZoomListener();
        super.onDestroy();
    }


    /**
     * camera回调监听
     */
    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            if (recorderPath != null) {
                iv_choice.setVisibility(View.VISIBLE);
                setTakeButtonShow(false);
                playerManager.playMedia(new Surface(texture), recorderPath);
            } else {
                setTakeButtonShow(true);
                iv_choice.setVisibility(View.GONE);
                cameraManagerTool.openCamera(CameraActivity.this, texture, width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };
    private String photo;

    private void backPicture() {
        //将图片路径intent回传
        Intent intent = new Intent();
        intent.putExtra("take_photo", photo);
        setResult(BaseUrl.TAKE_PHOTO, intent);
    }

    private Camera.PictureCallback callback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            setTakeButtonShow(false);

            takePhotoSubscription = Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                    if (!emitter.isDisposed()) {
                        String photoPath = FileTool.getUploadPhotoFile(CameraActivity.this);
                        //保存拍摄的图片
                        isPhotoTakingState = FileTool.savePhoto(photoPath, data, cameraManagerTool.isCameraFrontFacing());
                        if (isPhotoTakingState) {
                            photo = photoPath;
                        }
                        emitter.onNext(isPhotoTakingState);
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean o) {
                            if (o != null && o) {
                                iv_choice.setVisibility(View.VISIBLE);
                            } else {
                                setTakeButtonShow(true);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }
    };


    @OnClick({R.id.tv_flash, R.id.iv_facing, R.id.iv_close, R.id.iv_choice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                mTv_tack.setVisibility(View.VISIBLE);
                if (recorderPath != null) {//有拍摄好的正在播放,重新拍摄
                    FileTool.delteFiles(new File(recorderPath));
                    recorderPath = null;
                    recordSecond = 0;
                    playerManager.stopMedia();
                    setTakeButtonShow(true);
                    iv_choice.setVisibility(View.GONE);
                    cameraManagerTool.openCamera(this, mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
                } else if (isPhotoTakingState) {
                    isPhotoTakingState = false;
                    iv_choice.setVisibility(View.GONE);
                    setTakeButtonShow(true);
                    cameraManagerTool.restartPreview();
                } else {
                    finish();
                }
                break;
            case R.id.iv_choice://选择图片或视频
                //将拍摄的视频路径回传
                if (recorderPath != null) {
                    Intent intent = new Intent();
                    intent.putExtra("video", recorderPath);
                    int tempRecord = recordSecond / 1000;
                    intent.putExtra("recordSecond", tempRecord);
                    setResult(BaseUrl.TAKE_VIDEO, intent);
                }
                if (photo != null) {
                    backPicture();
                }
                finish();
                break;
            case R.id.tv_flash:
                cameraManagerTool.changeCameraFlash(mTextureView.getSurfaceTexture(),
                        mTextureView.getWidth(), mTextureView.getHeight());
                setCameraFlashState();
                break;
            case R.id.iv_facing:
                cameraManagerTool.changeCameraFacing(this, mTextureView.getSurfaceTexture(),
                        mTextureView.getWidth(), mTextureView.getHeight());
                break;
        }
    }
}
