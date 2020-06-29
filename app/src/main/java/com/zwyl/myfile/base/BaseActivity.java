package com.zwyl.myfile.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zwyl.myfile.App;
import com.zwyl.myfile.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class BaseActivity extends FragmentActivity {
    @BindView(R.id.head_back)
    TextView headBack;
    @BindView(R.id.tv_title_center)
    TextView tvTitleCenter;

    @BindView(R.id.ll_head_right)
    LinearLayout llHeadRight;
    @BindView(R.id.head_mode)
    ImageView headMode;
    @BindView(R.id.head_search)
    ImageView headSearch;
    @BindView(R.id.head_addfile)
    ImageView headAddfile;
    @BindView(R.id.head_options)
    ImageView headOptions;

    protected abstract int getContentViewId();

    public View mTitleView, mStatusView, mBodyView;
    private TextView mTitleCenter;
    public ImageView head_options;
    public int titleHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //       StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.tabtext_bulue_28c8f4));
        // TopStatusBar.fullScreen(this);
        FrameLayout base = new FrameLayout(this);
        base.setBackgroundColor(getResources().getColor(R.color.white));
        addContentView(base, new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        //状态栏
        mStatusView = new View(this);
        titleHeight = (int) getResources().getDimension(R.dimen.title_height);

        mStatusView.setBackgroundColor(getResources().getColor(R.color.white));
        base.addView(mStatusView, new FrameLayout.LayoutParams(-1, App.statusHeight));
        //title
        mTitleView = LayoutInflater.from(this).inflate(getTitleId(), null);
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(-1, titleHeight);
        titleParams.topMargin = App.statusHeight;
        base.addView(mTitleView, titleParams);
        //body
        if (getContentViewId() != 0) {
            mBodyView = LayoutInflater.from(this).inflate(getContentViewId(), null);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.topMargin = App.statusHeight + titleHeight;
            base.addView(mBodyView, params);
        }

        ButterKnife.bind(this);//Todo
        mTitleCenter = (TextView) findViewById(R.id.tv_title_center);
        head_options = (ImageView) mTitleView.findViewById(R.id.head_options);
        headBack.setOnClickListener(v -> finish());
        ActivityManager.getInstance().add(this);
        initView();
        initControl();
        initData();
    }


    protected int getTitleId() {
        return R.layout.title_layout_has_back;
    }

    protected void initView() {
    }

    protected void initControl() {
    }

    protected void initData() {

    }

    //隐藏标题
    public void hideTitle() {
        mTitleView.setVisibility(View.GONE);
        showTitle = false;
        initBody();
    }

    //返回点击事件
    public void setBackClick(View.OnClickListener l) {
        headBack.setOnClickListener(l);
    }

    //搜索点击事件
    public void setSearchClick(View.OnClickListener l) {
        headSearch.setOnClickListener(l);
    }

    //添加文件点击事件
    public void setAddFileClick(View.OnClickListener l) {
        headAddfile.setOnClickListener(l);
    }

    //切换列表
    public void setSwitchListClick(View.OnClickListener l) {
        headMode.setOnClickListener(l);
    }

    //options点击事件
    public void setOptionsClick(View.OnClickListener l) {
        headOptions.setOnClickListener(l);
    }


    //隐藏状态栏
    public void hideStatusBar() {
        mStatusView.setVisibility(View.GONE);
        showStatus = false;
        initBody();
    }

    public boolean showTitle = true, showStatus = true;

    //显示状态栏
    public void showStatusBar() {
        mStatusView.setVisibility(View.VISIBLE);
        showStatus = true;
        mStatusView.bringToFront();
    }


    public void initBody() {
        int top = 0;
        if (showStatus)
            top += App.statusHeight;
        if (showTitle)
            top += titleHeight;
        if (mBodyView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mBodyView.getLayoutParams();
            layoutParams.topMargin = top;
            mBodyView.setLayoutParams(layoutParams);
        }

    }

    //设置标题
    public void setTitleCenter(String title) {
        mTitleCenter.setText(title);
    }

    public void setTitleCenter(int title) {
        mTitleCenter.setText(getResources().getString(title));
    }

    //弹出框Toast
    protected void showToast(String str) {
        Toast.makeText(App.getContext(), str, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int id) {
        Toast.makeText(App.getContext(), id, Toast.LENGTH_SHORT).show();
    }

    //跳转界面(不传数据)
    protected void startActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    //创建Intent(传数据是使用)
    protected Intent createIntent(Class<? extends Activity> cls) {
        Intent intent = new Intent(this, cls);
        //        intent.putExtras(this.getIntent());
        return intent;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().remove(this);
    }

    //用来控制应用前后台切换的逻辑
    public boolean isCurrentRunningForeground = true;

    @Override
    protected void onStart() {
        super.onStart();
        if (!isCurrentRunningForeground) {
            isCurrentRunningForeground = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isCurrentRunningForeground = isRunningForeground();
        if (!isCurrentRunningForeground) {
        }
    }

    public boolean isRunningForeground() {
        android.app.ActivityManager activityManager = (android.app.ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程,查看该应用是否在运行
        for (android.app.ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(this.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Activity getTopActivity() {
        return ActivityManager.getInstance().getTopActivity();
    }

}
