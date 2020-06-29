package com.zwyl.myfile.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zwyl.myfile.R;


/**
 * 头像选择对话框 拍照 or 相册
 */
public class TitleDialog extends Dialog {

    public TitleDialog(Activity activity, String title, OnclickListener listener) {
        super(activity, R.style.dialog);
        View view = View.inflate(activity, R.layout.dialog_title, null);
        setContentView(view);
        //title
        TextView tv_titledialog_title = (TextView) view.findViewById(R.id.tv_titledialog_title);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        EditText edit_name = (EditText) view.findViewById(R.id.edit_name);
        edit_name.setVisibility(View.GONE);
        tv_name.setText(title);
        tv_titledialog_title.setText("提示!");
        //底部关闭按钮
        view.findViewById(R.id.tv_titledialog_cancle).setOnClickListener(v -> {
            dismiss();
        });
        //底部提交按钮
        view.findViewById(R.id.tv_titledialog_sure).setOnClickListener(v -> {
            listener.Onclick();
            dismiss();
        });
    }

    public interface OnclickListener {
        void Onclick();
    }

}

