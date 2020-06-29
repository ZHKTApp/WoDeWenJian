package com.zwyl.myfile.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.mayigeek.frame.http.state.HttpSucess;
import com.zwyl.myfile.base.ComFlag;
import com.zwyl.myfile.http.ApiUtil;
import com.zwyl.myfile.service.UserService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdataManager {
    private Context mContext;
    private String TAG = "http";
    //关于进度显示
    private ProgressDialog progressDialog;
    private UserService api;
    private String appId;
    private int versionNo;

    public UpdataManager(Context mContext, String token, String appId) {
        this.mContext = mContext;
        this.appId = appId;
        api = ApiUtil.createDefaultApi(UserService.class, token);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("正在下载...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
    }

    public void getVersion() {
        ApiUtil.doDefaultApi(api.selectAppUpdate(appId), new HttpSucess<UpdateBean>() {
            @Override
            public void onSucess(UpdateBean data) {
                int currentVersion = getLocalVersion(mContext);
                int version = Integer.parseInt(data.getAppVersionName().replace(".",""));
                Log.e(TAG, "currentVersion: " + currentVersion + " version: " + data.getAppVersionId() + "versionName : " + data.getAppVersionName() + " version1 : " +version);
                if (currentVersion < version) {
//                    versionNo = data.getAppVersionName();
                    versionNo=version;
                    alter(data.getFileUrl(), data.getAppRemark(), "更新版本");
                }
            }
        });
    }

    private void alter(String apkUrl, String message, String title) {
        AlertDialog mDialog = new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "更新执行");
//                        if (Build.VERSION.SDK_INT >= 26) {
//                            boolean b = mContext.getPackageManager().canRequestPackageInstalls();
//                            if (!b) {
//                                openSettings();
//                            }
//                        }

                        onUpdate(apkUrl);

                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        mDialog.show();
    }

    /**
     * 开启权限界面
     */
//    private void openSettings() {
//        ShowToast(mContext, mContext.getString(R.string.notifySetting));
//        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + mContext.getPackageName()));
////        ((Activity)mContext).startActivityForResult(intent,1);
//        mContext.startActivity(intent);
//    }
    private void onUpdate(String url) {
        DownloadTask downloadTask = new DownloadTask(mContext);
        downloadTask.execute(url);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            final String fileName = ComFlag.PACKAGE_NAME + "_V" + versionNo + ".apk";

            File file = FileUtils.getFilePath(ComFlag.PACKAGE_NAME, fileName);
            try {
                URL urll = new URL(url[0]);
                Log.d("upgrade", "url1:" + urll + "////url:" + url);
                connection = (HttpURLConnection) urll.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;


                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                progressDialog.setCancelable(true);
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    progressDialog.setCancelable(true);
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            progressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();

            final String fileName = ComFlag.PACKAGE_NAME + "_V" + versionNo + ".apk";

            File file = FileUtils.getFilePath(ComFlag.PACKAGE_NAME, fileName);
            installApk(file);
        }

    }

    //打开APK程序代码
    private void installApk(File file) {
        Log.e("installApk", file.getName());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) { //适配安卓7.0以上
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            Uri apkFileUri = FileProvider.getUriForFile(mContext.getApplicationContext(),
                    mContext.getPackageName() + ".FileProvider", file);
            intent.setDataAndType(apkFileUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }

    /**
     * 获取本地软件版本号
     */
    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            Log.e("TAG", "本软件的版本号 : " + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
//            Log.e("TAG", "本软件的版本号 : " + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }


}
