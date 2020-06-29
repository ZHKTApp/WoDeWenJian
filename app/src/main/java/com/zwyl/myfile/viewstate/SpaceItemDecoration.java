package com.zwyl.myfile.viewstate;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
//        outRect.bottom = 32;
        outRect.left = space;
        outRect.top = space;
        outRect.right = space;
        //由于每行都只有5个，所以第一个都是5的倍数，把左边距设为0
//        if(parent.getChildLayoutPosition(view) % 5 == 0) {
//            outRect.left = 48;
//        }else{
//            outRect.left = 91;
//        }
    }

}
