package com.zwyl.myfile.base;

/**
 * Created by zhubo on 2017/11/24.
 */

public class ComFlag {
    public static final String PACKAGE_NAME = "我的文件";
    public static final String APP_ID= "wodewenjian";
    public class NumFlag {
        public static final int INTENT_WEL = 0;//app从后台到前台或者锁屏后回到app，跳转WelcomActivit时的参数
    }

    //文件排序
    public class FileComparator {
        public static final int FIEL_TYPE = 0;
        public static final int FIEL_NAME_UP = 1;
        public static final int FIEL_NAME_DOWN = 2;
        public static final int FIEL_TIME_UP = 3;
        public static final int FIEL_TIME_DOWN = 4;
    }

    public class StrFlag {
        public static final String TAG = "TAG";//常用字符串
    }

    /*课本详情界面popwindow*/
    public class PopFlag {
        public static final String TITLE = "title";//顶部button按钮
        public static final String NAME = "name";//下面的条目
    }
}
