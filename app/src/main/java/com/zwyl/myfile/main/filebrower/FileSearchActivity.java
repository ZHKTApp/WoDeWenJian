package com.zwyl.myfile.main.filebrower;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zwyl.myfile.App;
import com.zwyl.myfile.R;
import com.zwyl.myfile.base.ComFlag;
import com.zwyl.myfile.base.adapter.CommonListAdapter;
import com.zwyl.myfile.dialog.TitleDialog;
import com.zwyl.myfile.views.SwipeMenuLayout;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileSearchActivity extends Activity {
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.iv_item_search)
    ImageView ivItemSearch;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button cancleButton;
    @BindView(R.id.search_list)
    ListView listview;
    @BindView(R.id.linear_id)
    RelativeLayout layout;
    @BindView(R.id.et_key_search)
    EditText etKeySearch;
    @BindView(R.id.tvBack)
    TextView tvBack;
    @BindView(R.id.tvClean)
    TextView tvClean;
    private List<FileInfo> _files = new ArrayList<>();
    private String _rootPath = FileUtil.getSDPath() + "/DCIM/";//默认的为sd卡根目录
    private String _currentPath = _rootPath;//当前工作目录
    private CommonListAdapter adapter = null;//适配器
    private static final String TAG = "FileSearchActivity";
    //上下文菜单项
    private final int MENU_RENAME = Menu.FIRST;
    private final int MENU_COPY = Menu.FIRST + 1;
    private final int MENU_MOVE = Menu.FIRST + 2;
    private final int MENU_DELETE = Menu.FIRST + 3;
    private final int MENU_INFO = Menu.FIRST + 4;

    //以下为粘贴，取消的布局，按钮等
    private ProgressDialog progressDialog;
    private boolean iscopy = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_search_list);
        ButterKnife.bind(this);
        _currentPath = getIntent().getStringExtra("filepath");
        Log.e("path", " path : " + _currentPath);
        layout.setVisibility(View.GONE);//默认为不可见
        if (listview.getVisibility() == View.VISIBLE) {
            listview.setAdapter(adapter = new CommonListAdapter<FileInfo>(App.mContext, _files, R.layout.file_item) {
                @Override
                public void bindData(FileInfo data, int position) {
                    ImageView imageView = (ImageView) findViewById(R.id.file_icon);
                    TextView file_name = (TextView) findViewById(R.id.file_name);
                    TextView file_lastmodifiedTime = (TextView) findViewById(R.id.file_lastmodifiedTime);
                    String namefile = _files.get(position).Name;
                    Button reName = (Button) findViewById(R.id.btnRename);
                    Button delBtn = (Button) findViewById(R.id.btnDelete);
                    FrameLayout fl_file_item = (FrameLayout) findViewById(R.id.fl_file_item);
                    SwipeMenuLayout smLayout = (SwipeMenuLayout) findViewById(R.id.smLayout);
                    file_name.setText(namefile);
                    file_lastmodifiedTime.setText(_files.get(position).lastmodified);
                    //判断文件类型并设置图标
                    String name = _files.get(position).Name.toLowerCase().trim();
                    if (_files.get(position).IsDirectory) {
                        imageView.setImageResource(R.mipmap.file_icon);
                    } else if (name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".3gp") || name.endsWith(".rmvb")) {
                        imageView.setImageResource(R.mipmap.video);
                    } else if (name.endsWith(".mp3") || name.endsWith(".mid") || name.endsWith(".wav")) {
                        imageView.setImageResource(R.drawable.audio);
                    } else if (name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".bmp")) {
                        imageView.setImageResource(R.mipmap.image);
                    } else if (name.endsWith(".txt") || name.endsWith(".log")) {
                        imageView.setImageResource(R.mipmap.txt);
                    } else if (name.endsWith(".excel")) {
                        imageView.setImageResource(R.mipmap.excel);
                    } else if (name.endsWith(".pdf")) {
                        imageView.setImageResource(R.mipmap.pdf);
                    } else if (name.endsWith(".ppt")) {
                        imageView.setImageResource(R.mipmap.ppt);
                    } else if (name.endsWith(".xls")) {
                        imageView.setImageResource(R.mipmap.xls);
                    } else {
                        imageView.setImageResource(R.mipmap.doc);
                    }
                    FileInfo fileInfo = _files.get(position);
                    delBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isDelete(new File(fileInfo.Path));
                            smLayout.quickClose();
                        }
                    });
                    reName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FileActivityHelper.reNameFile(FileSearchActivity.this, new File(fileInfo.Path), FileHandler);
                        }
                    });
                    fl_file_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FileInfo f = _files.get(position);

                            if (f.canRead) {
                                if (f.IsDirectory) {//判断是否为目录
//                                    Log.i(TAG, "onListItemClick======== f.Path=" + f.Path);
//                                    viewFiles(f.Path);
                                    back(f.Path);
                                } else {
                                    openFile(f.Path);
                                }
                            } else {
                                //不可读
                                showFileCanNOTReadMyDialog();
                            }
                        }
                    });
                }
            });

            // 对列表ListViw注册上下文菜单
            registerForContextMenu(listview);

            listview.setOnItemClickListener((parent, view, position, id) ->
            {
                FileInfo f = _files.get(position);

                if (f.canRead) {
                    if (f.IsDirectory) {//判断是否为目录
//                        Log.i(TAG, "onListItemClick======== f.Path=" + f.Path);
//                        viewFiles(f.Path);
                        back(f.Path);
                    } else {
                        openFile(f.Path);
                    }
                } else {
                    //不可读
                    showFileCanNOTReadMyDialog();
                }
            });
        }
        etKeySearch.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() > 0) {//限制长度
                    tvClean.setVisibility(View.VISIBLE);
                } else {
                    tvClean.setVisibility(View.GONE);
                }
            }
        });
        etKeySearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //发现执行了两次因为onkey事件包含了down和up事件，所以只需要加入其中一个即可。

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(FileSearchActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                    search();
                }
                return false;
            }
        });
    }

    /**
     * 获取该目录下所有文件
     **/
    public void viewFiles(String filePath) {
        ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(this, filePath, ComFlag.FileComparator.FIEL_TYPE);
        if (tmp != null) {
            // 清空数据
            _files.clear();
            _files.addAll(tmp);
            tmp.clear();

            // 设置当前目录
            _currentPath = filePath;

            // 更新UI刷新
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * 覆盖重写返回键事件
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {//back键
            back(_currentPath);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void back(String filePath) {
        Intent intent = new Intent();
        intent.putExtra("filepath", filePath);
        setResult(0, intent);
        finish();
    }


    private void openFile(String path) {//TODO 打开文件
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        File f = new File(path);
        String type = FileUtil.getMIMEType(f.getName());
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
    }
    //用各自的应用程序打开文件 eg: .mP3能被音乐播放器打开

    //信息处理
    @SuppressLint("HandlerLeak")
    public Handler FileHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {//接收到信息，更新界面
            if (msg.what == 0) {
                viewFiles(_currentPath);
                Toast.makeText(FileSearchActivity.this, getResources().getString(R.string.renamesuccessed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                viewFiles(_currentPath);
                Toast.makeText(FileSearchActivity.this, getResources().getString(R.string.makedirsuccessed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                viewFiles(_currentPath);
                Toast.makeText(FileSearchActivity.this, getResources().getString(R.string.makefilesuccessed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    //创建上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info;

        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        FileInfo f = _files.get(info.position);
        menu.setHeaderTitle(f.Name);
        menu.setHeaderIcon(R.drawable.dialogtitle_icon);
        menu.add(0, MENU_RENAME, 1, getString(R.string.file_rename));
        menu.add(0, MENU_COPY, 2, getString(R.string.file_copy));
        menu.add(0, MENU_MOVE, 3, getString(R.string.file_move));
        menu.add(0, MENU_DELETE, 4, getString(R.string.file_delete));
        menu.add(0, MENU_INFO, 5, getString(R.string.file_info));

    }

    //处理上下文菜单
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        FileInfo fileInfo = _files.get(info.position);
        final File f = new File(fileInfo.Path);

        switch (item.getItemId()) {
            case MENU_RENAME://重命名
                FileActivityHelper.reNameFile(this, f, FileHandler);
                break;
            case MENU_COPY:
                button1.setText("粘贴");
                setIscopy(true);
                layout.setVisibility(View.VISIBLE);
                copyAndMove(f);
                break;
            case MENU_MOVE:
                button1.setText("移动");
                setIscopy(false);
                layout.setVisibility(View.VISIBLE);
                copyAndMove(f);
                break;
            case MENU_DELETE://删除
                isDelete(f);
                break;
            case MENU_INFO://详情
                FileActivityHelper.fileInfo(this, f);
                break;

            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    //删除
    public void isDelete(final File f) {
        new TitleDialog(this, "确定删除此文件吗?", () -> {
            FileUtil.deleteFile(f);
            viewFiles(_currentPath);
            showToast("删除成功!");
        }).show();
    }

    //复制
    public void copyAndMove(final File f) {
        button1.setOnClickListener(v -> {
            final File src = new File(f.getPath());
            Log.i(TAG, "setOnClickListener=======src==" + f.getPath());
            //	Log.i(TAG, "setOnClickListener=======src=="+f.getAbsolutePath());
            if (!src.exists()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.filenoexit), Toast.LENGTH_SHORT).show();
                return;
            }
            String newPath = FileUtil.combinPath(_currentPath, src.getName());
            Log.i(TAG, "setOnClickListener========newPath=" + newPath);
            final File tar = new File(newPath);
            if (tar.exists()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.filehasexit), Toast.LENGTH_SHORT).show();
                return;
            }
            //进度条为不可取消的且不确定的
            progressDialog = ProgressDialog.show(FileSearchActivity.this, "", "Please wait...", true, false);
            new Thread() {
                @Override
                public void run() {

                    if (isIscopy()) {//如果为移动
                        try {
                            //调用文件夹或文件复制函数
                            FileUtil.copyFile(src, tar);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            //调用文件夹或文件移动函数
                            FileUtil.moveFile(src, tar);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    progressHandler.sendEmptyMessage(0);
                }
            }.start();

        });
    }

    @SuppressLint("HandlerLeak")
    private final Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 关闭ProgressDialog
            if (msg.what == 0) {
                progressDialog.dismiss();
                Log.i("===========", "_currentPath==" + _currentPath);
                viewFiles(_currentPath);
                layout.setVisibility(View.GONE);
                if (isIscopy()) {
                    showToast(getResources().getString(R.string.copysuccessed));
                } else {
                    showToast(getResources().getString(R.string.movefilesuccessed));
                }

            }

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.activity_file_brower_app, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case R.id.menu_home:
                viewFiles(_rootPath);
                break;
            case R.id.menu_refresh:
                //viewFiles(_currentPath);
                // 更新UI刷新
                adapter.notifyDataSetChanged();
                break;
            case R.id.menu_mkdirandfile:
                FileActivityHelper.makeDirFileAndFile(this, _currentPath, FileHandler);
                break;
            case R.id.menu_help:
                showMyDialog();
                break;
            case R.id.menu_about:
                showAboutMyDialog();
                break;
            case R.id.menu_exit:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //显示不可读对话框
    public void showFileCanNOTReadMyDialog() {
        new AlertDialog.Builder(FileSearchActivity.this).setIcon(R.drawable.dialogtitle_icon).setMessage(getResources().getString(R.string.nopermission)).setTitle(getResources().getString(R.string.dialogtitle)).setPositiveButton("确定", (dialog, which) -> dialog.dismiss()).show();
    }

    //显示帮助对话框
    public void showMyDialog() {
        new AlertDialog.Builder(FileSearchActivity.this).setIcon(R.drawable.dialogtitle_icon).setMessage(getResources().getString(R.string.apphelp)).setTitle(getResources().getString(R.string.dialogtitle)).setPositiveButton("确定", (dialog, which) -> dialog.dismiss()).show();
    }

    //显示关于对话框
    public void showAboutMyDialog() {
        new AlertDialog.Builder(FileSearchActivity.this).setIcon(R.drawable.dialogtitle_icon).setMessage(getResources().getString(R.string.appabout)).setTitle(getResources().getString(R.string.dialogtitle)).setPositiveButton("确定", (dialog, which) -> dialog.dismiss()).show();
    }

    //判断是否为点击的为复制功能
    public boolean isIscopy() {
        return iscopy;
    }

    public void setIscopy(boolean iscopy) {
        this.iscopy = iscopy;
    }

    private void search() {
        String keyWord = etKeySearch.getText().toString().trim();
        if (!TextUtils.isEmpty(keyWord)) {
            getFiles(_currentPath, keyWord);
//                // 更新UI刷新
            adapter.notifyDataSetChanged();
        }
    }

    private void getFiles(String filePaths, String keyWord) {
        ArrayList<FileInfo> allfile = FileActivityHelper.getFiles(this, filePaths, ComFlag.FileComparator.FIEL_TYPE);
        if (allfile != null) {
            for (int i = 0; i < allfile.size(); i++) {
                FileInfo fileInfo = allfile.get(i);
                if (fileInfo.IsDirectory) {
                    if (fileInfo.Name.contains(keyWord)) {
                        _files.add(allfile.get(i));
                    }
                    getFiles(fileInfo.Path, keyWord);
                } else {
                    //文件
                    if ((fileInfo.Name).contains(keyWord)) {
                        _files.add(allfile.get(i));
                    }
                }
            }
        }
    }

    @OnClick({R.id.iv_item_search, R.id.button2, R.id.tvBack, R.id.tvClean})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_item_search:
//                List<FileInfo> _files = new ArrayList<>();
                String keyWord = etKeySearch.getText().toString().trim();
                if (!TextUtils.isEmpty(keyWord)) {
                    ArrayList<FileInfo> allfile = FileActivityHelper.getFiles(this, _currentPath, ComFlag.FileComparator.FIEL_TYPE);
                    if (allfile != null) {
                        // 清空数据
                        _files.clear();
                        for (int i = 0; i < allfile.size(); i++) {
                            FileInfo fileInfo = allfile.get(i);
                            //判断是否为目录
                            if (fileInfo.IsDirectory) {
                                //目录

                                ArrayList<FileInfo> DirectoryAll = FileActivityHelper.getFiles(this, fileInfo.Path, ComFlag.FileComparator.FIEL_TYPE);
                                assert DirectoryAll != null;
                                if (0 < DirectoryAll.size()) {
                                    //文件夹中有文件
                                    for (int j = 0; j < DirectoryAll.size(); j++) {
                                        if ((DirectoryAll.get(j).Name).contains(keyWord)) {
                                            _files.add(DirectoryAll.get(j));
                                        }
                                    }
                                } else {
                                    ///文件夹中无文件
                                    if ((fileInfo.Name).contains(keyWord)) {
                                        _files.add(allfile.get(i));
                                    }
                                }
                            } else {
                                //文件
                                if ((fileInfo.Name).contains(keyWord)) {
                                    _files.add(allfile.get(i));
                                }
                            }
                        }
//                        Intent intent = new Intent(this, FileSearchActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("filelist", (Serializable) _files);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
                    }
//                // 更新UI刷新
                    adapter.notifyDataSetChanged();
                }
//                llSearch.setVisibility(View.GONE);
                break;
            case R.id.button2:
                layout.setVisibility(View.GONE);
                break;
            case R.id.tvBack:
                back(_currentPath);
            case R.id.tvClean:
                etKeySearch.setText("");
                break;
        }

    }

    protected void showToast(String str) {
        Toast.makeText(App.getContext(), str, Toast.LENGTH_SHORT).show();
    }
}

