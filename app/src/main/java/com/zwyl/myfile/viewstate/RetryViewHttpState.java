package com.zwyl.myfile.viewstate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mayigeek.frame.view.state.ViewHttpState;
import com.zwyl.myfile.R;

import org.jetbrains.annotations.NotNull;


public class RetryViewHttpState implements ViewHttpState {

    com.zwyl.myfile.viewstate.RetryHttp retryHttp;

    public RetryViewHttpState(RetryHttp retryHttp) {
        this.retryHttp = retryHttp;
    }

    private View createView(ViewGroup parentView, int layoutId) {
        return LayoutInflater.from(parentView.getContext()).inflate(layoutId, parentView, false);
    }

    private void onClickRetry() {
        if (retryHttp != null) {
            retryHttp.run();
        }
    }

    @NotNull
    @Override
    public View loadingView(@NotNull ViewGroup parentView) {
        return LayoutInflater.from(parentView.getContext()).inflate(R.layout.view_loading, parentView, false);
    }

    @NotNull
    @Override
    public View emptyView(@NotNull ViewGroup parentView) {
        View view = createView(parentView, R.layout.view_empty);
        view.findViewById(R.id.btnEmpty).setOnClickListener(v -> onClickRetry());
        return view;

    }

    @NotNull
    @Override
    public View errorView(@NotNull ViewGroup parentView) {
        View view = createView(parentView, R.layout.view_error);
        view.findViewById(R.id.btnError).setOnClickListener(v -> onClickRetry());
        return view;
    }
}
