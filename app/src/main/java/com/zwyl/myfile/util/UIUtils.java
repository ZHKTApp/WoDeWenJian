package com.zwyl.myfile.util;

import com.bumptech.glide.request.RequestOptions;
import com.zwyl.myfile.R;
import com.zwyl.myfile.customveiw.CenterCropRoundCornerTransform;
import com.zwyl.myfile.customveiw.GlideRoundTransform;

public class UIUtils {
    public static RequestOptions options = new RequestOptions()
            //默认照片
            .placeholder(R.mipmap.textbook).error(R.mipmap.textbook);
    public static RequestOptions optionsTranform = new RequestOptions().centerCrop()//默认照片带圆角
            .placeholder(R.mipmap.textbook).error(R.mipmap.textbook).transform(new CenterCropRoundCornerTransform(10));
    public static RequestOptions optionsCenterCrop = new RequestOptions().centerCrop()//默认照片不带圆角
            .placeholder(R.mipmap.textbook).error(R.mipmap.textbook);
    public static RequestOptions optionsHead = new RequestOptions().centerCrop()//用户头像
            .circleCropTransform().placeholder(R.mipmap.textbook).error(R.mipmap.textbook);
    public static RequestOptions optionsDoctor = new RequestOptions().centerCrop()//医生头像
            .circleCropTransform().placeholder(R.mipmap.textbook).error(R.mipmap.textbook);
    public static RequestOptions addPhoto = new RequestOptions().centerCrop()//默认照片带圆角
            .error(R.mipmap.textbook).transform(new CenterCropRoundCornerTransform(10));
    public static RequestOptions option5 = new RequestOptions().placeholder(R.mipmap.textbook).error(R.mipmap.textbook).centerCrop()
            //            .centerInside()
            .transform(new GlideRoundTransform(5));
   
}
