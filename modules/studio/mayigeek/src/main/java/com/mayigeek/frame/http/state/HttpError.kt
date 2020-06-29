package com.mayigeek.frame.http.state

/**
 * @author renxiao@zhiwy.com
 * @version V1.0
 * @Description: http返回的错误
 * @date 16-8-25 下午7:04
 */
interface HttpError {
    fun onError(e: Throwable)
}
