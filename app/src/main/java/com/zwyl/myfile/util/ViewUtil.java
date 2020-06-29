package com.zwyl.myfile.util;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.zwyl.myfile.App;
import com.zwyl.myfile.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.internal.Utils;
import cn.appsdream.nestrefresh.util.L;

/**
 * Created by BinBinWang on 2018/1/19.
 */

public class ViewUtil {

    public static final int dp9 = DensityUtil.dip2px(App.getContext(), 9);
    public static final int dp19 = DensityUtil.dip2px(App.getContext(), 19);
    public static final int dp420 = DensityUtil.dip2px(App.getContext(), 420);


    //单列文字
    public static View getTextView(CharSequence text) {
        TextView tv = new TextView(App.getContext());
        tv.setText(text);
        tv.setTextSize(27);
        tv.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        return tv;
    }

    //两列文字
    public static View getTextViewTwo(String str1, String str2) {
        LinearLayout linearLayout = new LinearLayout(App.mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(App.getContext());
        tv.setText(str1);
        tv.setTextSize(18);
        tv.setPadding(19, 0, 0, 0);
        tv.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        TextView tvStr = new TextView(App.getContext());
        tvStr.setPadding(19, 0, 0, 0);
        tvStr.setText(str2);
        tvStr.setTextSize(18);
        tvStr.setTextColor(App.getContext().getResources().getColor(R.color.gray_8888));
       // tvStr.setOnClickListener(listener);
        linearLayout.addView(tv);
        linearLayout.addView(tvStr);
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    //横向线性布局
    public static LinearLayout get_ll_horizontal() {
        LinearLayout linearLayout = new LinearLayout(App.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp420);
        //        params.topMargin = ViewUtil.dp15;
        //        params.bottomMargin = ViewUtil.dp18;
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    /**
     * @param mRadio //单选
     * @param mJudge //判断
     */
    //获取RadioGroup
    public static LinearLayout getRadioGroup(String[] mRadio, int[] mJudge, OncheckedClick listener) {
        LinearLayout linearLayout = new LinearLayout(App.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ViewUtil.dp9;
        params.bottomMargin = ViewUtil.dp9;
        params.leftMargin = ViewUtil.dp19;
        TextView textView = new TextView(App.mContext);
        textView.setText("作答: ");
        textView.setTextSize(18);
        textView.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
        textView.setCompoundDrawables(App.mContext.getResources().getDrawable(R.mipmap.add), null, null, null);
        linearLayout.addView(textView);
        RadioGroup radioGroup = new RadioGroup(App.mContext);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        if(null != mRadio) {//单选题
            for(int i = 0; i < mRadio.length; i++) {
                RadioButton radioButton = new RadioButton(App.mContext);
                radioButton.setPadding(10, 0, 0, 0);
                radioButton.setText(mRadio[i]);
                radioButton.setTextSize(18);
                radioButton.setTextColor(App.getContext().getResources().getColor(R.color.gray_3333));
                radioGroup.addView(radioButton, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            //判断题
        } else {
            for(int i = 0; i < mJudge.length; i++) {
                RadioButton radioButton = new RadioButton(App.mContext);
                Drawable drawable = App.mContext.getResources().getDrawable(mJudge[i]);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                radioButton.setCompoundDrawables(drawable, null, null, null);
                radioGroup.addView(radioButton, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }

        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            int indexNum = - 1;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton viewById1 = (RadioButton) group.findViewById(checkedId);
                for(int i = 0; i < group.getChildCount(); i++) {
                    if(group.getChildAt(i) == viewById1) {
                        indexNum = i;
                    }
                }
                listener.OnChecked(indexNum);
            }
        });
        linearLayout.addView(radioGroup);
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    public interface OncheckedClick {
        void OnChecked(int id);
    }

}
