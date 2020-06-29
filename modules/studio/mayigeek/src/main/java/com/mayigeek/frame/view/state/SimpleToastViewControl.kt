package com.mayigeek.frame.view.state

import android.app.Dialog

/**
 * @author renxiao@zhiwy.com
 * @version V1.0
 * @Description: 简单的Toast实现的ViewControl
 * @date 16-8-31 上午11:53
 */
class SimpleToastViewControl(val waiteDialog: Dialog, val viewComplete: ViewComplete?, val viewError: ViewError?, val viewEmpty: ViewEmpty?) {


    fun build(): ViewControl {
        return object : ViewControl {
            override fun onEmpty() {
                viewEmpty?.onEmpty()
            }

            override fun onStart() {
                if (!waiteDialog.isShowing) {
                    waiteDialog.show()
                }
            }

            override fun onComplete() {
                viewComplete?.onComplete()
            }

            override fun onError() {
                viewError?.onError()
            }

            override fun onFinish() {
                waiteDialog.cancel()
            }

        }
    }
}