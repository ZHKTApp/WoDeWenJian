package com.zwyl.myfile.main.filebrower;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.widget.PopupWindowCompat;
import android.text.TextUtils;
import android.text.style.ParagraphStyle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
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

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.zwyl.myfile.App;
import com.zwyl.myfile.R;
import com.zwyl.myfile.base.BaseActivity;
import com.zwyl.myfile.base.ComFlag;
import com.zwyl.myfile.base.adapter.CommonListAdapter;
import com.zwyl.myfile.dialog.TitleDialog;
import com.zwyl.myfile.dialog.bean.PopSubjectBean;
import com.zwyl.myfile.dialog.popwindow.PopupSubject;
import com.zwyl.myfile.service.UserService;
import com.zwyl.myfile.util.UpdataManager;
import com.zwyl.myfile.views.SwipeMenuLayout;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/*主界面显示类*/
public class FileBrowerApp extends BaseActivity {

    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.iv_item_search)
    ImageView ivItemSearch;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button cancleButton;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.gv_file)
    GridView gv_file;
    @BindView(R.id.linear_id)
    RelativeLayout layout;
    @BindView(R.id.et_key_search)
    EditText etKeySearch;
    private List<FileInfo> _files = new ArrayList<>();
    private String _rootPath = FileUtil.getSDPath() + "/DCIM/";//默认的为sd卡根目录
    private String _currentPath = _rootPath;//当前工作目录
    private CommonListAdapter adapter = null;//适配器
    private static final String TAG = "FileBrowerDemo";
    private UserService api;
    private String token;
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
    protected int getContentViewId() {
        return R.layout.activity_file_brower_app;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleCenter("我的文件");
        token = getIntent().getStringExtra("token");
        Log.e("token", "token : " + token);
        if (!TextUtils.isEmpty(token)) {
            UpdataManager updataManager = new UpdataManager(this, token, ComFlag.APP_ID);
            updataManager.getVersion();
        }
        //返回点击
        setBackClick(v -> back());
        //切换列表
        setSwitchListClick(v -> {
            if (listview.getVisibility() == View.VISIBLE) {
                listview.setVisibility(View.GONE);
                gv_file.setVisibility(View.VISIBLE);
                if (gv_file.getVisibility() == View.VISIBLE) {
                    gv_file.setAdapter(adapter = new CommonListAdapter<FileInfo>(App.mContext, _files, R.layout.item_gv_files) {
                        @Override
                        public void bindData(FileInfo data, int position) {
                            ImageView imageView = (ImageView) findViewById(R.id.file_icon);
                            TextView file_name = (TextView) findViewById(R.id.file_name);
                            TextView file_lastmodifiedTime = (TextView) findViewById(R.id.file_lastmodifiedTime);
                            String namefile = _files.get(position).Name;
                            //                if(namefile.equals("100ANDRO")) {
                            //                    mParent.setVisibility(View.GONE);
                            //                }
                            file_name.setText(namefile);
                            file_lastmodifiedTime.setText(_files.get(position).lastmodified);
                            //判断文件类型并设置图标
                            String name = _files.get(position).Name.toLowerCase().trim();
                            //	Log.i("==========name", "name=="+name);
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
                        }
                    });
                    gv_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            FileInfo f = _files.get(position);

                            if (f.canRead) {
                                if (f.IsDirectory) {//判断是否为目录
                                    Log.i(TAG, "onListItemClick======== f.Path=" + f.Path);
                                    viewFiles(f.Path);
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
            } else {
                listview.setVisibility(View.VISIBLE);
                gv_file.setVisibility(View.GONE);
                if (listview.getVisibility() == View.VISIBLE) {
                    listview.setAdapter(adapter = new CommonListAdapter<FileInfo>(App.mContext, _files, R.layout.file_item) {
                        @Override
                        public void bindData(FileInfo data, int position) {
                            ImageView imageView = (ImageView) findViewById(R.id.file_icon);
                            TextView file_name = (TextView) findViewById(R.id.file_name);
                            TextView file_item = (TextView) findViewById(R.id.file_item);
                            TextView file_lastmodifiedTime = (TextView) findViewById(R.id.file_lastmodifiedTime);
                            String namefile = _files.get(position).Name;
                            //                if(namefile.equals("100ANDRO")) {
                            //                    mParent.setVisibility(View.GONE);
                            //                }
                            Button reName = (Button) findViewById(R.id.btnRename);
                            Button delBtn = (Button) findViewById(R.id.btnDelete);
                            FrameLayout fl_file_item = (FrameLayout) findViewById(R.id.fl_file_item);
                            SwipeMenuLayout smLayout = (SwipeMenuLayout) findViewById(R.id.smLayout);
                            file_name.setText(namefile);
                            file_lastmodifiedTime.setText(_files.get(position).lastmodified);
                            //判断文件类型并设置图标
                            String name = _files.get(position).Name.toLowerCase().trim();
                            //	Log.i("==========name", "name=="+name);
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
                                    FileActivityHelper.reNameFile(FileBrowerApp.this, new File(fileInfo.Path), FileHandler);
                                }
                            });
                            fl_file_item.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FileInfo f = _files.get(position);

                                    if (f.canRead) {
                                        if (f.IsDirectory) {//判断是否为目录
                                            Log.i(TAG, "onListItemClick======== f.Path=" + f.Path);
                                            viewFiles(f.Path);
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

                    Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).build(), new AcpListener() {
                        @Override
                        public void onGranted() {
                            //showToast("同意了相机权限");
                            // 获取当前目录的文件列表
                            viewFiles(_currentPath);
                        }

                        @Override
                        public void onDenied(List<String> permissions) {
                            showToast(permissions.toString() + "权限拒绝");
                        }
                    });
                    listview.setOnItemClickListener((parent, view, position, id) ->
                    {
                        FileInfo f = _files.get(position);

                        if (f.canRead) {
                            if (f.IsDirectory) {//判断是否为目录
                                Log.i(TAG, "onListItemClick======== f.Path=" + f.Path);
                                viewFiles(f.Path);
                            } else {
                                openFile(f.Path);
                            }
                        } else {
                            //不可读
                            showFileCanNOTReadMyDialog();
                        }
                    });
                }
            }
        });
        //搜索点击
        setSearchClick(v -> {
                    Intent intent = new Intent(FileBrowerApp.this, FileSearchActivity.class);
                    intent.putExtra("filepath", _currentPath);
                    startActivityForResult(intent, 0);
                }
//                llSearch.setVisibility(View.VISIBLE)
        );
        //添加文件点击
        setAddFileClick(v -> FileActivityHelper.makeDir(this, _currentPath, FileHandler));
        //筛选点击
        setOptionsClick(v -> {
            List<PopSubjectBean> options = new ArrayList<>();
            options.add(new PopSubjectBean("按格式排序", 0));
            options.add(new PopSubjectBean("按名称升序", R.mipmap.up));
            options.add(new PopSubjectBean("按名称降序", R.mipmap.down));
            options.add(new PopSubjectBean("按时间升序", R.mipmap.up));
            options.add(new PopSubjectBean("按时间降序", R.mipmap.down));
            PopupSubject<PopSubjectBean> popupSubject = new PopupSubject<>(this, options, position -> {
                String name = options.get(position).name;
                switch (name) {
                    case "按格式排序":
                        viewFilesComparatr(_currentPath, ComFlag.FileComparator.FIEL_TYPE);
                        break;
                    case "按名称升序":
                        viewFilesComparatr(_currentPath, ComFlag.FileComparator.FIEL_NAME_UP);
                        break;
                    case "按名称降序":
                        viewFilesComparatr(_currentPath, ComFlag.FileComparator.FIEL_NAME_DOWN);
                        break;
                    case "按时间升序":
                        viewFilesComparatr(_currentPath, ComFlag.FileComparator.FIEL_TIME_UP);
                        break;
                    case "按时间降序":
                        viewFilesComparatr(_currentPath, ComFlag.FileComparator.FIEL_TIME_DOWN);
                        break;
                }

            });
            PopupWindowCompat.showAsDropDown(popupSubject, head_options, 5, 28, Gravity.END);

        });

        layout.setVisibility(View.GONE);//默认为不可见
        //        adapter = new FileListViewAdapter(this, _files);

        if (listview.getVisibility() == View.VISIBLE) {
            listview.setAdapter(adapter = new CommonListAdapter<FileInfo>(App.mContext, _files, R.layout.file_item) {
                @Override
                public void bindData(FileInfo data, int position) {
                    ImageView imageView = (ImageView) findViewById(R.id.file_icon);
                    TextView file_name = (TextView) findViewById(R.id.file_name);
                    TextView file_item = (TextView) findViewById(R.id.file_item);
                    TextView file_lastmodifiedTime = (TextView) findViewById(R.id.file_lastmodifiedTime);
                    String namefile = _files.get(position).Name;
                    Button reName = (Button) findViewById(R.id.btnRename);
                    Button delBtn = (Button) findViewById(R.id.btnDelete);
                    FrameLayout fl_file_item = (FrameLayout) findViewById(R.id.fl_file_item);
                    SwipeMenuLayout smLayout = (SwipeMenuLayout) findViewById(R.id.smLayout);
                    //                if(namefile.equals("100ANDRO")) {
                    //                    mParent.setVisibility(View.GONE);
                    //                }
                    file_name.setText(namefile);
                    file_lastmodifiedTime.setText(_files.get(position).lastmodified);
                    if (_files.get(position).IsDirectory) {
                        file_item.setVisibility(View.VISIBLE);
                        int countItem = getViewFilesCount(_files.get(position).Path);
                        file_item.setText(countItem + "项");
                    } else {
                        file_item.setVisibility(View.GONE);
                    }
                    //判断文件类型并设置图标
                    String name = _files.get(position).Name.toLowerCase().trim();
                    //	Log.i("==========name", "name=="+name);
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
                            FileActivityHelper.reNameFile(FileBrowerApp.this, new File(fileInfo.Path), FileHandler);
                        }
                    });
                    fl_file_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FileInfo f = _files.get(position);
                            setTitleCenter(f.Name);
                            if (f.canRead) {
                                if (f.IsDirectory) {//判断是否为目录
                                    Log.i(TAG, "onListItemClick======== f.Path=" + f.Path);
                                    viewFiles(f.Path);
                                } else {
                                    openFile(f.Path);
                                }
                            } else {
                                //不可读
                                showFileCanNOTReadMyDialog();
                            }
                        }
                    });

//                    holder.setOnClickListener(R.id.btnDelete, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(FileBrowerApp.this, "删除:" + position, Toast.LENGTH_SHORT).show();
//                            //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
//                            ((SwipeMenuLayout) holder.getConvertView()).quickClose();
//                            mDatas.remove(position);
//                            notifyDataSetChanged();
//                        }
//                    });
                }
            });

            // 对列表ListViw注册上下文菜单
            registerForContextMenu(listview);

            Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).build(), new AcpListener() {
                @Override
                public void onGranted() {
                    //showToast("同意了相机权限");
                    // 获取当前目录的文件列表
                    viewFiles(_currentPath);
                }

                @Override
                public void onDenied(List<String> permissions) {
                    showToast(permissions.toString() + "权限拒绝");
                }
            });
            listview.setOnItemClickListener((parent, view, position, id) ->
            {
                FileInfo f = _files.get(position);

                if (f.canRead) {
                    if (f.IsDirectory) {//判断是否为目录
                        Log.i(TAG, "onListItemClick======== f.Path=" + f.Path);
                        viewFiles(f.Path);
                    } else {
                        openFile(f.Path);
                    }
                } else {
                    //不可读
                    showFileCanNOTReadMyDialog();
                }
            });
        }

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

    private int getViewFilesCount(String filePath) {
        ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(this, filePath, ComFlag.FileComparator.FIEL_TYPE);
        int countItem = 0;
        // 清空数据
        if (tmp != null) {
            countItem = tmp.size();
            tmp.clear();
        } else {
            countItem = 0;
        }
        return countItem;
    }

    //排序专用
    public void viewFilesComparatr(String filePath, int Comparatortype) {
        ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(this, filePath, Comparatortype);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && requestCode == 0) {
            _currentPath = data.getStringExtra("filepath");
            viewFiles(_currentPath);
        }
    }

    /**
     * 覆盖重写返回键事件
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {//back键
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void back() {
        File f = new File(_currentPath);
        String parentPath = f.getParent();
        Log.e("filepath", " parentPath : " + parentPath);
        if (parentPath != null && !parentPath.equals("/storage/emulated/0")) {
            String parentName = parentPath.substring(parentPath.lastIndexOf("/") + 1);
            viewFiles(parentPath);//返回上一级
            if (parentPath.equals("/storage/emulated/0/DCIM")) {
                setTitleCenter("我的文件");
            } else {
                setTitleCenter(parentName);
            }
        } else {
//            this.exit();
            FileBrowerApp.this.finish();
            Process.killProcess(Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 退出程序
     **/
    public void exit() {
        new TitleDialog(this, "你确定退出吗?", () -> {
            FileBrowerApp.this.finish();
            Process.killProcess(Process.myPid());
            System.exit(0);
        }).show();
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
                Toast.makeText(FileBrowerApp.this, getResources().getString(R.string.renamesuccessed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                viewFiles(_currentPath);
                Toast.makeText(FileBrowerApp.this, getResources().getString(R.string.makedirsuccessed), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                viewFiles(_currentPath);
                Toast.makeText(FileBrowerApp.this, getResources().getString(R.string.makefilesuccessed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    //创建上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterContextMenuInfo info;

        try {
            info = (AdapterContextMenuInfo) menuInfo;
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
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
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
            progressDialog = ProgressDialog.show(FileBrowerApp.this, "", "Please wait...", true, false);
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
                exit();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //显示不可读对话框
    public void showFileCanNOTReadMyDialog() {
        new AlertDialog.Builder(FileBrowerApp.this).setIcon(R.drawable.dialogtitle_icon).setMessage(getResources().getString(R.string.nopermission)).setTitle(getResources().getString(R.string.dialogtitle)).setPositiveButton("确定", (dialog, which) -> dialog.dismiss()).show();
    }

    //显示帮助对话框
    public void showMyDialog() {
        new AlertDialog.Builder(FileBrowerApp.this).setIcon(R.drawable.dialogtitle_icon).setMessage(getResources().getString(R.string.apphelp)).setTitle(getResources().getString(R.string.dialogtitle)).setPositiveButton("确定", (dialog, which) -> dialog.dismiss()).show();
    }

    //显示关于对话框
    public void showAboutMyDialog() {
        new AlertDialog.Builder(FileBrowerApp.this).setIcon(R.drawable.dialogtitle_icon).setMessage(getResources().getString(R.string.appabout)).setTitle(getResources().getString(R.string.dialogtitle)).setPositiveButton("确定", (dialog, which) -> dialog.dismiss()).show();
    }

    //判断是否为点击的为复制功能
    public boolean isIscopy() {
        return iscopy;
    }

    public void setIscopy(boolean iscopy) {
        this.iscopy = iscopy;
    }


    @OnClick({R.id.iv_item_search, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_item_search:
                List<FileInfo> _files = new ArrayList<>();
                String keyWord = etKeySearch.getText().toString().trim();
                if (!TextUtils.isEmpty(keyWord)) {
                    ArrayList<FileInfo> allfile = FileActivityHelper.getFiles(this, _rootPath, ComFlag.FileComparator.FIEL_TYPE);
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
                        Intent intent = new Intent(FileBrowerApp.this, FileSearchActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filelist", (Serializable) _files);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    // 更新UI刷新
//                    adapter.notifyDataSetChanged();
                }
                llSearch.setVisibility(View.GONE);
                break;
            case R.id.button2:
                layout.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}