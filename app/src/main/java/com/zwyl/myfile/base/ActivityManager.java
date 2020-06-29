package com.zwyl.myfile.base;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

public enum ActivityManager {
    INSTANCE;
    private List<Activity> mList = new LinkedList<Activity>();

    public static ActivityManager getInstance() {
        return INSTANCE;
    }

    public void add(Activity activity) {
        mList.add(activity);
    }

    public void remove(Activity activity) {
        mList.remove(activity);
    }

    public void exitAll() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
            mList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Activity getTopActivity(){
        if(mList.size() <= 0)
            return null;
        return mList.get(mList.size() - 1);
    }
}
