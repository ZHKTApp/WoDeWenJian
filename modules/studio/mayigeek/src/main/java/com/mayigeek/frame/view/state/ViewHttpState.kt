package com.mayigeek.frame.view.state

import android.view.View
import android.view.ViewGroup

/**
 * @author renxiao@zhiwy.com
 * @version V1.0
 * @Description: Http加载,空数据和失败情况
 * @date 16-8-31 下午4:41
 */
interface ViewHttpState {


    /**
     * 加载时显示的布局
     * */
    fun loadingView(parentView: ViewGroup): View

    /**
     * 空数据显示的布局
     * */
    fun emptyView(parentView: ViewGroup): View

    /**
     * 失败时显示的布局
     * */
    fun errorView(parentView: ViewGroup): View
}