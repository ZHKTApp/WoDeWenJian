package com.zwyl.myfile.customveiw;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zwyl.myfile.App;

public class TwoFingerRecycleView extends RecyclerView {
//    private Context context;
//    private CustomLinearLayoutManager layoutManager;


   // private int mTouchRepeat = 0; //过滤掉长按的情况
    private boolean mPoint2Down = false;//是否出现双指按下的情况

    public TwoFingerRecycleView(Context context) {
        super(context);
    }

    public TwoFingerRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoFingerRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


        @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
               // layoutManager.setScrollEnabled(false);
                setNestedScrollingEnabled(false);
                mPoint2Down = false;
               // mTouchRepeat = 0;
                break;
            case MotionEvent.ACTION_MOVE:
               // mTouchRepeat++;
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                mPoint2Down = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if(mPoint2Down ) { //do something here
                    //layoutManager.setScrollEnabled(true);
                    setNestedScrollingEnabled(true);
                    //                    App.mContext.showToast("双指点击");
                }
                break;

        }
        return true;
    }
}
