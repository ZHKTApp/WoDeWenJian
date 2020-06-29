package com.mayigeek.frame.http.log

import android.util.Log

/**
 * @author renxiao@zhiwy.com
 * @version V1.0
 * @Description: 自定义log实现
 * @date 16-8-26 下午6:43
 */
interface Logger {
    fun log(message: String)

    companion object {
        /**
         * A [Logger] defaults output appropriate for the current platform.
         */
        val DEFAULT: Logger = object : Logger {
            override fun log(message: String) {
                Log.i("HttpLoggingInterceptor", message)
            }
        }
    }
}