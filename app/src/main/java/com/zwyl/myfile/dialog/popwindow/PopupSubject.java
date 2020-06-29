package com.zwyl.myfile.dialog.popwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zwyl.myfile.App;
import com.zwyl.myfile.base.adapter.CommonAdapter;
import com.zwyl.myfile.base.adapter.MultiItemTypeAdapter;
import com.zwyl.myfile.base.adapter.ViewHolder;
import com.zwyl.myfile.dialog.bean.PopSubjectBean;
import com.zwyl.myfile.R;
import com.zwyl.myfile.util.DensityUtil;

import java.util.List;

public class PopupSubject<T> extends PopupWindow {
    private int postionTag = - 1;
    private CommonAdapter madapter;

    public PopupSubject(Context context, List<T> list, OnClickListener listener) {
        super(context);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View contentView = LayoutInflater.from(context).inflate(R.layout.popwindow_left_drawer, null, false);
        setContentView(contentView);
        //adapter
        RecyclerView rl_recyclerview = (RecyclerView) contentView.findViewById(R.id.rl_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(App.mContext, OrientationHelper.VERTICAL, false);
        rl_recyclerview.setLayoutManager(linearLayoutManager);
        rl_recyclerview.setAdapter(madapter = new CommonAdapter<T>(App.mContext, R.layout.item_pop, list) {
            @Override
            protected void convert(ViewHolder holder, T data, int position) {
                PopSubjectBean bean = (PopSubjectBean) data;
                holder.setText(R.id.tv_item_name, bean.name);
                TextView tv_item_data = holder.getView(R.id.tv_item_name);
                ImageView tv_item_img = holder.getView(R.id.tv_item_img);
                tv_item_img.setImageResource(bean.drawable);
                if(position == postionTag) {
                    tv_item_data.setTextColor(context.getResources().getColor(R.color.c_green));
                    postionTag = position;
                } else {
                    tv_item_data.setTextColor(Color.BLACK);
                }

            }
        });
        madapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            //            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                listener.onClick(position);
                postionTag = position;
                madapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

    }


    public interface OnClickListener {
        void onClick(int position);
    }
}

