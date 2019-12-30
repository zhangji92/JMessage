package com.dome.push.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.dome.push.R;
import com.dome.push.adapter.EmoticonsAdapter;
import com.dome.push.adapter.PageChangeAdapter;
import com.dome.push.adapter.TextWatcherAdapter;
import com.dome.push.listener.OnBackKeyClickListener;
import com.dome.push.util.EmoticonsKeyboardUtils;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.view
 * ClassName:      EmoticonsKeyBoard
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-23 上午10:50
 * UpdateUser:     更新者
 * UpdateDate:     19-12-23 上午10:50
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class EmoticonsRelative extends AutoHeightLayout implements View.OnClickListener, OnBackKeyClickListener, FuncLayout.OnFuncChangeListener {

    public static final int FUNC_TYPE_EMOTION = -1;
    public static final int FUNC_TYPE_APPPS = -2;

    protected LayoutInflater mInflater;

    protected ImageView mBtnVoiceOrText;
    protected RecordVoiceButton mBtnVoice;
    protected EmoticonsEditText mEtChat;
    protected ImageView mBtnFace;
    protected RelativeLayout mRlInput;
    protected ImageView mBtnMultimedia;
    protected Button mBtnSend;
    protected FuncLayout mLyKvml;


    protected boolean mDispatchKeyEventPreImeLock = false;
    private ViewPager funRecycler;
    private FunPagerIndicator funPagerIndicator;

    public EmoticonsRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateKeyboardBar();
        initView();
        initFuncView();
    }

    /**
     *
     */
    protected void inflateKeyboardBar() {
        mInflater.inflate(R.layout.layout_emoticons_relative, this);
    }

    protected View inflateFunc() {
        return mInflater.inflate(R.layout.layout_fun_emoticons, null);
    }

    protected void initView() {
        mBtnVoiceOrText = findViewById(R.id.btn_voice_or_text);
        mBtnVoice = findViewById(R.id.btn_voice);
        mEtChat = findViewById(R.id.et_chat);
        mBtnFace = findViewById(R.id.btn_face);
        mRlInput = findViewById(R.id.rl_input);
        mBtnMultimedia = findViewById(R.id.btn_multimedia);
        mBtnSend = findViewById(R.id.btn_send);
        mLyKvml = findViewById(R.id.ly_kvml);
        mBtnFace.setOnClickListener(this);
        mBtnMultimedia.setOnClickListener(this);
        mEtChat.setOnBackKeyClickListener(this);
    }

    protected void initFuncView() {
        initEmoticonFuncView();
        initEditView();
    }

    /**
     * 初始化表情
     */
    protected void initEmoticonFuncView() {
        View keyboardView = inflateFunc();
        mLyKvml.addFuncView(FUNC_TYPE_EMOTION, keyboardView);
        funRecycler = keyboardView.findViewById(R.id.fun_view_pager);
        funPagerIndicator = keyboardView.findViewById(R.id.fun_pager_indicator);

        mLyKvml.setOnFuncChangeListener(this);
    }

    protected void initEditView() {
        mEtChat.setOnTouchListener((v, event) -> {
            if (!mEtChat.isFocused()) {
                mEtChat.setFocusable(true);
                mEtChat.setFocusableInTouchMode(true);
            }
            return false;
        });

        mEtChat.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (!TextUtils.isEmpty(s)) {
                    mBtnSend.setVisibility(VISIBLE);
                    mBtnMultimedia.setVisibility(GONE);
                    mBtnSend.setBackgroundResource(R.drawable.solid_25_5);
                } else {
                    mBtnMultimedia.setVisibility(VISIBLE);
                    mBtnSend.setVisibility(GONE);
                }
            }
        });
    }

    /**
     * 设置表情库
     * @param emoticonsAdapter 表情适配器
     */
    public void setAdapter(EmoticonsAdapter emoticonsAdapter) {
        funRecycler.setAdapter(emoticonsAdapter);
        int count = emoticonsAdapter.getCount();
        funPagerIndicator.init(count);
        funRecycler.addOnPageChangeListener(new PageChangeAdapter() {
            int nextPosition = 0;
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                funPagerIndicator.scrollPoint(position, nextPosition);
                nextPosition = position;
            }
        });
    }

    public void addFuncView(View view) {
        mLyKvml.addFuncView(FUNC_TYPE_APPPS, view);
    }

    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(this);
        mLyKvml.hideAllFuncView();
        mBtnFace.setImageResource(R.mipmap.icon_face_nomal);
    }

    protected void showVoice() {
        mRlInput.setVisibility(GONE);
        mBtnVoice.setVisibility(VISIBLE);
        reset();
    }

    protected void checkVoice() {
        if (mBtnVoice.isShown()) {
            mBtnVoiceOrText.setImageResource(R.drawable.voice_or_text_keyboard);
        } else {
            mBtnVoiceOrText.setImageResource(R.drawable.voice_or_text);
        }
    }

    protected void showText() {
        mRlInput.setVisibility(VISIBLE);
        mBtnVoice.setVisibility(GONE);
    }

    protected void toggleFuncView(int key) {
        showText();
        mLyKvml.toggleFuncView(key, isSoftKeyboardPop(), mEtChat);
    }

    @Override
    public void onFuncChange(int key) {
        if (FUNC_TYPE_EMOTION == key) {
            mBtnFace.setImageResource(R.mipmap.icon_face_pop);
        } else {
            mBtnFace.setImageResource(R.mipmap.icon_face_nomal);
        }
        checkVoice();
    }

    protected void setFuncViewHeight(int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLyKvml.getLayoutParams();
        params.height = height;
        mLyKvml.setLayoutParams(params);
    }

    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        mLyKvml.updateHeight(height);
    }

    @Override
    public void onSoftPop(int height) {
        super.onSoftPop(height);
        mLyKvml.setVisibility(true);
        // 软键盘关闭的标志
        mLyKvml.setCurrentFuncKey(mLyKvml.DEF_KEY);
        onFuncChange(mLyKvml.DEF_KEY);
    }

    @Override
    public void onSoftClose() {
        super.onSoftClose();
        if (mLyKvml.isOnlyShowSoftKeyboard()) {
            reset();
        } else {
            onFuncChange(mLyKvml.getCurrentFuncKey());
        }
    }

    public void addOnFuncKeyBoardListener(FuncLayout.OnFuncKeyBoardListener l) {
        mLyKvml.addOnKeyBoardListener(l);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_face) {
            toggleFuncView(FUNC_TYPE_EMOTION);
        } else if (i == R.id.btn_multimedia) {
            toggleFuncView(FUNC_TYPE_APPPS);
        }
    }

    public void setVideoText() {
        if (mRlInput.isShown()) {
            mBtnVoiceOrText.setImageResource(R.drawable.voice_or_text_keyboard);
            showVoice();
        } else {
            showText();
            mBtnVoiceOrText.setImageResource(R.drawable.voice_or_text);
            EmoticonsKeyboardUtils.openSoftKeyboard(mEtChat);
        }
    }

    public ImageView getVoiceOrText() {
        return mBtnVoiceOrText;
    }


    @Override
    public void onBackKeyClick() {
        if (mLyKvml.isShown()) {
            mDispatchKeyEventPreImeLock = true;
            reset();
        }
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (mDispatchKeyEventPreImeLock) {
                    mDispatchKeyEventPreImeLock = false;
                    return true;
                }
                if (mLyKvml.isShown()) {
                    reset();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
            return false;
        }
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
            return;
        }
        super.requestChildFocus(child, focused);
    }

    public boolean dispatchKeyEventInFullScreen(KeyEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext()) && mLyKvml.isShown()) {
                    reset();
                    return true;
                }
            default:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    boolean isFocused;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        isFocused = mEtChat.getShowSoftInputOnFocus();
                    } else {
                        isFocused = mEtChat.isFocused();
                    }
                    if (isFocused) {
                        mEtChat.onKeyDown(event.getKeyCode(), event);
                    }
                }
                return false;
        }
    }

    public EmoticonsEditText getEtChat() {
        return mEtChat;
    }

    public RecordVoiceButton getBtnVoice() {
        return mBtnVoice;
    }

    public Button getBtnSend() {
        return mBtnSend;
    }

}

