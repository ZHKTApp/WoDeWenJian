package com.mayigeek.frame.http.state

/**
 * @version V1.0
 * @Description: Http请求hook
 * @date 16-9-1 上午10:34
 */
interface HttpHook {
    fun onHook(builder: okhttp3.OkHttpClient.Builder)
}