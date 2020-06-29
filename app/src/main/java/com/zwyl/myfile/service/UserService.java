package com.zwyl.myfile.service;

import com.zwyl.myfile.dialog.bean.BeanAllYear;
import com.zwyl.myfile.http.HttpResult;
import com.zwyl.myfile.main.BeanHomeGrid;
import com.zwyl.myfile.util.UpdateBean;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {
    /**
     * 接口名：/app/guidebook/selectAppUpdate
     * <p>
     * 参数：cmStudentId,appId(应用id)
     * <p>
     * 返回参数：
     * appVersionId:版本号id
     * appVersionName：版本名称
     * fileUrl：文件url
     */
    @FormUrlEncoded
    @POST("guidebook/selectAppUpdate")
    Observable<HttpResult<UpdateBean>> selectAppUpdate(@Field("appId") String appId);
}