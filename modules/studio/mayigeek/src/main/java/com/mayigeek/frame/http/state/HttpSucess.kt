package com.mayigeek.frame.http.state

/**
 * @author renxiao@zhiwy.com
 * @version V1.0
 * @Description: http成功返回数据
 * @date 16-8-25 下午6:49
 */
interface HttpSucess<T> {
    fun onSucess(data: T)
}
