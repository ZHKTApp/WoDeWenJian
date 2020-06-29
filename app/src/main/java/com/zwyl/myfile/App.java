package com.zwyl.myfile;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.zwyl.myfile.util.DeviceUtil;


public class App extends MultiDexApplication {
    public static Context mContext;
    public static String mDeviceID;
    public static String mVersionName;
    public static int statusHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        mDeviceID = DeviceUtil.getDeviceID(this);
        mVersionName = DeviceUtil.getVersionName(this);
        //statusHeight = TopStatusBar.statusHeight(this);


        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }

        initBugly();
        saveServiceState(false);

    }


    private void initBugly() {
            /* Bugly SDK初始化
             * 参数1：上下文对象
	    	 * 参数2：APPID，平台注册时得到,注意替换成你的appId
	    	 * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
	    	 */
       // CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_APP_ID, false);
    }

    public static Context getContext() {
        return mContext;
    }

    public static Handler mHandler = new Handler();

    private void saveServiceState(boolean isAlive){
       // SpUtils.getInstance().putBoolean("downService",isAlive);
    }

}
