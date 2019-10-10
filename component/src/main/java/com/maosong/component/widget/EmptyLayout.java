package com.maosong.component.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maosong.component.R;

/**
 * @author zhouhao
 * @since 2019/04/18
 */
public class EmptyLayout extends FrameLayout {

    public static final int STATE_HIDE = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_NO_DATA = 2;
    public static final int STATE_NETWORK_ERROR = 3;

    @IntDef({STATE_HIDE, STATE_LOADING, STATE_NO_DATA, STATE_NETWORK_ERROR})
    public @interface STATE {

    }

    @STATE
    private int currentState = STATE_HIDE;

    private ProgressBar progressBar;

    private TextView tvState;

    private OnRetryListener onRetryListener;

    public EmptyLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.empty_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        progressBar = findViewById(R.id.progress);
        tvState = findViewById(R.id.tv_state);
        /*
         * 重试
         */
        setOnClickListener(v -> {
            if (onRetryListener != null) {
                onRetryListener.onRetry();
            }
        });
    }

    public void setState(@STATE int state) {
        currentState = state;
        switch (state) {
            case STATE_HIDE:
                setVisibility(GONE);
                setClickable(false);
                break;
            case STATE_LOADING:
                setVisibility(VISIBLE);
                progressBar.setVisibility(VISIBLE);
                tvState.setVisibility(GONE);
                setClickable(false);
                break;
            case STATE_NO_DATA:
                setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
                tvState.setVisibility(VISIBLE);
                tvState.setText(getResources().getString(R.string.no_data));
                setClickable(true);
                break;
            case STATE_NETWORK_ERROR:
                setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
                tvState.setVisibility(VISIBLE);
                tvState.setText(getResources().getString(R.string.network_error));
                setClickable(true);
                break;
            default:
        }
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setOnRetryListener(OnRetryListener onRetryListener) {
        this.onRetryListener = onRetryListener;
    }

    public interface OnRetryListener {
        /**
         * 重新请求
         */
        void onRetry();
    }
}
