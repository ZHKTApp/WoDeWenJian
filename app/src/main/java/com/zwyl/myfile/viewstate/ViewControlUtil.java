package com.zwyl.myfile.viewstate;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.mayigeek.frame.view.state.SimpleToastViewControl;
import com.mayigeek.frame.view.state.SimpleViewControl;
import com.mayigeek.frame.view.state.ViewControl;
import com.mayigeek.frame.view.state.ViewHttpState;
import com.zwyl.myfile.viewstate.RetryHttp;
import com.zwyl.myfile.viewstate.RetryViewHttpState;
import com.zwyl.myfile.viewstate.SimpleViewHttpState;

/**
 * @version V1.0
 * @Description: ViewControl工具
 * @date 16-8-31 下午12:34
 */
public class ViewControlUtil {
    /**
     * 创建默认对话框形式
     */
    public static ViewControl create2Dialog(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context).setMessage("loading...").create();
        return new SimpleToastViewControl(dialog, () -> {
            Toast.makeText(context, "sucess", Toast.LENGTH_SHORT).show();
        }, null, null).build();
    }

    /**
     * 创建默认控制界面形式
     */
    public static ViewControl create2View(View view) {
        return create2View(view, new SimpleViewHttpState());
    }

    /**
     * 创建默认控制界面形式
     */
    public static ViewControl create2View(View view, ViewHttpState viewHttpState) {
        return new SimpleViewControl(view, viewHttpState).build();
    }

    /**
     * 创建默认控制界面形式,可以重试api调用
     */
    public static ViewControl create2View(View view, RetryHttp retryHttp) {
        return create2View(view, new RetryViewHttpState(retryHttp));
    }

}
