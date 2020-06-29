package com.mayigeek.frame.http.state

import okhttp3.Request.Builder

/**
 * @author renxiao@zhiwy.com
 * @version V1.0
 * @Description: Http请求hook
 * @date 16-9-1 上午10:34
 */
interface RequestHook {
    fun onHook(builder: Builder)
}