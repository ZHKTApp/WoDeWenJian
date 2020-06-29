package com.mayigeek.frame.http.state

import okhttp3.Response

/**
 * @author renxiao@zhiwy.com
 * @version V1.0
 * @Description: Http返回结果hook
 * @date 16-9-1 上午10:37
 */
interface ResponseHook {
    fun onHook(builder: Response.Builder)
}