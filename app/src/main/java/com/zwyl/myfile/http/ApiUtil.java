package com.zwyl.myfile.http;

import android.util.Log;

import com.mayigeek.frame.http.ApiManager;
import com.mayigeek.frame.http.log.LogLevel;
import com.mayigeek.frame.http.state.HttpError;
import com.mayigeek.frame.http.state.HttpFinish;
import com.mayigeek.frame.http.state.HttpSucess;
import com.mayigeek.frame.http.state.RequestHook;
import com.mayigeek.frame.view.state.ViewControl;
import com.zwyl.myfile.App;
import com.zwyl.myfile.BuildConfig;
import com.zwyl.myfile.util.MD5Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;

/**
 * @version V1.0
 */
public class ApiUtil {

    private static final String SIGN = "12345678#";
    private static String TAG;//过滤log的关键词


    private ApiUtil() {
        TAG = getClass().getSimpleName();
    }

    public static String getSign(Map<String, String> data, String timeStamp) {
        String sign = "";
        String key = "mceducationkey";
        Iterator<String> iterator = data.keySet().iterator();
        ArrayList<String> list = new ArrayList<String>();
        while(iterator.hasNext()) {
            list.add(iterator.next());
        }
        Collections.sort(list);
        String md5str = "";
        for(int i = 0; i < list.size(); i++) {
            md5str += data.get(list.get(i));
        }
        md5str += key + timeStamp;
        try {
            sign = MD5Util.md5(md5str);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return sign.toLowerCase();
    }


    /**
     * s上传api
     */
    public static <T> T createUploadApi(final Class<T> service, String token) {
        return createDefaultApi(service, 120, token);
    }

    public static <T> T createDefaultApi(final Class<T> service, String token) {
        return createDefaultApi(service, 15, token);
    }

    public static <T> T createDefaultApi(final Class<T> service, long time, String token) {
        ApiManager apiManager = new ApiManager(BuildConfig.SERVER_HOST);
        if (BuildConfig.DEBUG) {
            apiManager.setLogLevel(LogLevel.BODY);//默认打印log
        }
        apiManager.setRequestHook(new RequestHook() {
            @Override
            public void onHook(Request.Builder builder) {
                Request request = builder.build();
                if ("POST".equals(request.method())) {
                    if (request.body() instanceof FormBody) {
                        FormBody.Builder newForBody = new FormBody.Builder();
                        Map<String, String> map = new HashMap<>();
                        FormBody body = (FormBody) request.body();
                        for (int i = 0; i < body.size(); i++) {
                            newForBody.addEncoded(body.name(i), body.value(i));
                            map.put(body.name(i), body.value(i));
                        }
                        String timeStamp = System.currentTimeMillis() + "";
                        newForBody.addEncoded("timestamp", timeStamp);
                        newForBody.addEncoded("client", "app");
                        map.put("client", "app");
                        newForBody.addEncoded("deviceID", App.mDeviceID);
                        map.put("deviceID", App.mDeviceID);
                        newForBody.addEncoded("appVername", App.mVersionName);
                        map.put("appVername", App.mVersionName);
                        //加token
                        newForBody.addEncoded("cmStudentId", token);
                        map.put("cmStudentId", token);
                        //                        //加的memberid
                        //                        newForBody.addEncoded("memberId", UserManager.getInstance().getUserId());
                        //                        map.put("memberId", UserManager.getInstance().getUserId());
                        newForBody.addEncoded("sign", getSign(map, timeStamp));
                        builder.method(request.method(), newForBody.build());
                    } else if (request.body() instanceof MultipartBody) {
                        MultipartBody.Builder newMultipartBody = new MultipartBody.Builder();
                        newMultipartBody.setType(MediaType.parse("multipart/form-data"));
                        MultipartBody body = (MultipartBody) request.body();
                        List<MultipartBody.Part> parts = body.parts();
                        for (int i = 0; i < parts.size(); i++) {
                            newMultipartBody.addPart(parts.get(i));
                        }
                        Map<String, String> map = new HashMap<>();
                        String timeStamp = System.currentTimeMillis() + "";
                        newMultipartBody.addFormDataPart("timestamp", timeStamp);
                        newMultipartBody.addFormDataPart("client", "app");
                        map.put("client", "app");
                        /////军委教育定制
                        //                        newMultipartBody.addFormDataPart("userId", UserManager.getInstance().getSoldierUserId());
                        //                        map.put("userId", UserManager.getInstance().getSoldierUserId());
                        /////
                        newMultipartBody.addFormDataPart("deviceID", App.mDeviceID);
                        map.put("deviceID", App.mDeviceID);
                        newMultipartBody.addFormDataPart("appVername", App.mVersionName);
                        map.put("appVername", App.mVersionName);
                        //加token
                        newMultipartBody.addFormDataPart("cmStudentId", token);
                        map.put("cmStudentId", token);
                        //                        //加的memberid
                        //                        newMultipartBody.addFormDataPart("memberId", UserManager.getInstance().getUserId());
                        //                        map.put("memberId", UserManager.getInstance().getUserId());
                        newMultipartBody.addFormDataPart("sign", getSign(map, timeStamp));
                        builder.method(request.method(), newMultipartBody.build());
                    }
                } else if ("GET".equals(request.method())) {
                    HttpUrl url = request.url();
                    Map<String, String> map = new HashMap<>();
                    Set<String> names = url.queryParameterNames();
                    Iterator<String> iterator = names.iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        map.put(key, url.queryParameter(key));
                    }
                    String timeStamp = System.currentTimeMillis() + "";
                    map.put("client", "app");
                    map.put("deviceID", App.mDeviceID);
                    map.put("appVername", App.mVersionName);
                    //                    map.put("token", UserManager.getInstance().getToken());
                    //                    //加memberid
                    //                    map.put("memberId", UserManager.getInstance().getUserId());
                    HttpUrl build = url.newBuilder().addQueryParameter("timestamp", timeStamp).addQueryParameter("client", "app").addQueryParameter("deviceID", App.mDeviceID).addQueryParameter("appVername", App.mVersionName).addQueryParameter("token", "token")//("token", UserManager.getInstance().getToken()
                            //加memberid
                            .addQueryParameter("sign", getSign(map, timeStamp)).build();
                    builder.url(build);
                }
            }
        });

        //        apiManager.setResponseHook(null);
        //https证书信息
        String CER = "-----BEGIN CERTIFICATE-----\n" + "MIIDBjCCAe4CCQCnu4M+vpwXrTANBgkqhkiG9w0BAQsFADBFMQswCQYDVQQGEwJB\n" + "VTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0\n" + "cyBQdHkgTHRkMB4XDTE2MTAxMDEwMTUxM1oXDTE3MTAxMDEwMTUxM1owRTELMAkG\n" + "A1UEBhMCQVUxEzARBgNVBAgMClNvbWUtU3RhdGUxITAfBgNVBAoMGEludGVybmV0\n" + "IFdpZGdpdHMgUHR5IEx0ZDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB\n" + "AO46UnvEa1OwEzxzhEhiZ+5O3p3QntxkFFyV1qc9ZZXIM4Qwr97ubjh5lusqM1CA\n" + "3Wl5IGF4Cuz/1hV1F9YIRpjVCNljP8OmlBle9p0Kjd+JWurYV82/iH1FUNVydFt4\n" + "MRmfbxhocaxtunCWRpd/5EWbf6L0h1/M31VoxxfLH2E6rJ3aaoBCzDys99wwlyXA\n" + "tHesqqc0+hOL26sCfYGu6PNo1VUemkkVVrl7l9uNO7eM09gz3DsEt4ddNtGofgOY\n" + "f6pek4ACGVyslzINDsfZCUli+v44JDnc/Hk8Y09IBCR721GRR+Wk/1trWQkubXqX\n" + "vvNwATN+5WRLaKEKQ0r2qiECAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAf220d3f1\n" + "CmbyGha3bevn5/U6nYS5pspKQicFHdcLKvPguZybR73gJHXzh5jVAA8u9LcUSxyw\n" + "vyWbydzKZwIKdjh3gFeHfYYRbB2XpiBagpYQnX3eeCVyddz8oxMUF5hFADZht7Ne\n" + "YHaMIzoqW3phdkCkUYRyQ/C9F4rpU2mBZKb0NZgHH+F0n47LcYc9ETjbbFoYlLRP\n" + "HjVra9T4XtvIK6C1DmbqqhzJkEBT2qEQjIi1H1L1buruv1pUxRVVXNHeeyZlMbXq\n" + "TWfVOVC2uOxYLJYgeJo2rAVu0mPVfYp65uS3UKwSNYJpkcgIOYZDZXz3x8JM6TEn\n" + "JAW8lIpG94aRJQ==\n" + "-----END CERTIFICATE-----";
        apiManager.addCer(CER);
        apiManager.setHostnameVerify(false);
        return apiManager.createApi(service, time);
    }

    /**
     * 执行默认的api
     */
    public static <T> void doDefaultApi(Observable<HttpResult<T>> api, HttpSucess<T> httpSucess) {
        doDefaultApi(api, httpSucess, null, null, null);
    }

    /**
     * 执行默认的api
     */
    public static <T> void doDefaultApi(Observable<HttpResult<T>> api, HttpSucess<T> httpSucess, ViewControl viewControl) {
        doDefaultApi(api, httpSucess, null, null, viewControl);
    }

    /**
     * 执行默认的api
     */
    public static <T> void doDefaultApi(Observable<HttpResult<T>> api, HttpSucess<T> httpSucess, HttpError httpError, ViewControl viewControl) {
        doDefaultApi(api, httpSucess, httpError, null, viewControl);
    }

    /**
     * 执行默认的api
     */
    public static <T extends List> void doDefaultApi(Observable<HttpResult<T>> api, HttpSucess<T> httpSucess, HttpFinish httpFinish) {
        doDefaultApi(api, httpSucess, httpFinish, null);
    }

    /**
     * 执行默认的api
     */
    public static <T> void doDefaultApi(Observable<HttpResult<T>> api, HttpSucess<T> httpSucess, HttpFinish httpFinish, ViewControl viewControl) {
       HttpResultFunc<T> httpResultFunc = new HttpResultFunc<T>();
        doDefaultApi(api, httpSucess, e -> {
            //打印异常
            Log.i(TAG, e.toString());
        }, httpFinish, viewControl);
    }

    /**
     * 执行默认的api
     */
    public static <T> void doDefaultApi(Observable<HttpResult<T>> api, HttpSucess<T> httpSucess, HttpError httpError, HttpFinish httpFinish, ViewControl viewControl) {
        HttpResultFunc<T> httpResultFunc = new HttpResultFunc<T>();
        ApiManager.Companion.doApi(api.map(httpResultFunc), httpSucess, httpError, httpFinish, viewControl);
    }

    /**
     * 执行默认的api返回 HttpResult 全部内容
     */
    public static <T> void doDefaultApiAll(Observable<HttpResult<T>> api, HttpSucess<HttpResult<T>> httpSucess, ViewControl viewControl) {
        ApiManager.Companion.doApi(api, httpSucess, null, null, viewControl);
    }
}
