package com.zwyl.myfile.http;

import android.widget.Toast;

import com.zwyl.myfile.App;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


/**
 * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
 *
 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */
public class HttpResultFunc<T> implements Function<HttpResult<T>, T> {


    @Override
    public T apply(@NonNull HttpResult<T> httpResult) {
        int resultCode = httpResult.getResultCode();
        if (resultCode == 411) {
            //            App.mHandler.postDelayed(() -> {
            //                Toast.makeText(App.getContext().getApplicationContext(), "登录状态失效, 请重新登录...", Toast.LENGTH_SHORT).show();
            //                ActivityManager.getInstance().exitAll();
            //                Intent intent = new Intent(App.getContext(), LoginActivity.class);
            //                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //                App.getContext().startActivity(intent);
            //            }, 200);
            //            UserManager.getInstance().remove();
            return (T) ("" + resultCode);
        }
        if (resultCode == 410) {
            //            ToastUtils.showShort(httpResult.getResultMsg() + "您的账号可能在其他设备上登录，请您再次进行之前的操作，重新登录账号");
            App.mHandler.post(() -> {
//                Toast.makeText(App.getContext(), httpResult.getResultMsg() + "您的账号可能在其他设备上登录，请您再次进行之前的操作，重新登录账号", Toast.LENGTH_SHORT).show();
//                UserManager.getInstance().remove();
//                ActivityManager.getInstance().exitAll();
//                Intent intent = new Intent(App.getContext(), SimpleLoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                App.getContext().startActivity(intent);
            });
            return (T) ("" + resultCode);
        }
        if (resultCode != 200) {
            App.mHandler.post(() -> Toast.makeText(App.getContext().getApplicationContext(), httpResult.getResultMsg(), Toast.LENGTH_SHORT).show());
            return (T) ("" + resultCode);
        }
        return httpResult.getResultInfo() == null ? (T) ("" + resultCode) : httpResult.getResultInfo();
    }
}