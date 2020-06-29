package com.zwyl.myfile.main.filebrower;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zwyl.myfile.App;
import com.zwyl.myfile.R;
import com.zwyl.myfile.base.ComFlag;


public class FileActivityHelper {
    //    public static ArrayList<FileInfo> FindFile(Activity activity, String filePath, String key_search) {
    //        ArrayList<FileInfo> all_file = getFiles(activity, filePath);
    //        ArrayList<FileInfo> list = new ArrayList<FileInfo>();
    //        for(int i = 0; i < all_file.size(); i++) {
    //            FileInfo fileInfo = all_file.get(i);
    //            if(fileInfo.Name.contains(key_search)) {
    //                list.add(fileInfo);
    //            }
    //        }
    //        return list;
    //    }

    /**
     * 获取一个文件夹下的所有文件
     **/
    public static ArrayList<FileInfo> getFiles(Activity activity, String path, int fileComparatorType) {
        File f = new File(path);
        File[] files = f.listFiles();
        if (files == null) {
            Toast.makeText(activity, "文件为空,无法打开!\t" + path, Toast.LENGTH_SHORT).show();
            return null;
        }

        ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
        // 获取文件列表
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            FileInfo fileInfo = new FileInfo();
            fileInfo.Name = file.getName();
            fileInfo.IsFile = file.isFile();
            fileInfo.IsDirectory = file.isDirectory();
            fileInfo.Path = file.getPath();
            fileInfo.Size = file.length();
            fileInfo.canRead = file.canRead();
            long l = file.lastModified();
            fileInfo.lastmodifiedLong = file.lastModified();
            fileInfo.lastmodified = "" + new Date(file.lastModified()).toLocaleString();
            fileList.add(fileInfo);
        }

        // 排序
        if (fileComparatorType == ComFlag.FileComparator.FIEL_TYPE) {
            Collections.sort(fileList, new FileComparator());
        } else if (fileComparatorType == ComFlag.FileComparator.FIEL_NAME_UP) {
            Collections.sort(fileList, new FileComparatorByNameUp());
        } else if (fileComparatorType == ComFlag.FileComparator.FIEL_NAME_DOWN) {
            Collections.sort(fileList, new FileComparatorByNameDown());
        } else if (fileComparatorType == ComFlag.FileComparator.FIEL_TIME_UP) {
            Collections.sort(fileList, new FileComparatorByTimeUp());
        } else {
            Collections.sort(fileList, new FileComparatorByTimeDown());
        }

        return fileList;
    }

    //获取文件信息
    public static void fileInfo(Context con, File f) {
        View layout = LayoutInflater.from(con).inflate(R.layout.fileinfo, null);
        FileInfo info = FileUtil.getFileInfo(f);
        ((TextView) layout.findViewById(R.id.file_name)).setText("" + f.getName());
        ((TextView) layout.findViewById(R.id.file_size)).setText("" + FileUtil.formetFileSize(info.Size));
        ((TextView) layout.findViewById(R.id.file_lastmodified)).setText("" + new Date(f.lastModified()).toLocaleString());
        ((TextView) layout.findViewById(R.id.file_contents)).setText("Folder " + info.FolderCount + ", File " + info.FileCount);

        new AlertDialog.Builder(con).setIcon(R.drawable.dialogtitle_icon).setTitle("该文件或文件夹的详细信息如下所示:").setView(layout).setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }

        }).show();

    }


    //新建文件夹和文件
    public static void makeDirFileAndFile(final Context con, final String path, final Handler FileHandler) {

        new AlertDialog.Builder(con).setTitle("请选择新建类型？").setIcon(R.drawable.dialogtitle_icon).setSingleChoiceItems(con.getResources().getStringArray(R.array.mkdir), 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0://新建文件夹
                        makeDir(con, path, FileHandler);
                        break;
                    case 1://新建文件
                        makeFile(con, path, FileHandler);
                        break;
                    default:

                        break;
                }
                dialog.dismiss();
            }

        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }

        }).show();


    }

    //新建文件夹
    public static void makeDir(final Context con, final String path, final Handler FileHandler) {
        View layout = LayoutInflater.from(con).inflate(R.layout.dialog_title, null);
        EditText edit_name = (EditText) layout.findViewById(R.id.edit_name);
        TextView tv_titledialog_title = (TextView) layout.findViewById(R.id.tv_titledialog_title);
        TextView tv_titledialog_cancle = (TextView) layout.findViewById(R.id.tv_titledialog_cancle);
        TextView tv_titledialog_sure = (TextView) layout.findViewById(R.id.tv_titledialog_sure);
        tv_titledialog_title.setText("新建文件夹");
        AlertDialog dialog = new AlertDialog.Builder(con).setView(layout).show();
        tv_titledialog_cancle.setOnClickListener(v -> {
            dialog.dismiss();
        });
        tv_titledialog_sure.setOnClickListener(v -> {
            String newName = edit_name.getText().toString().trim();
            if (newName.length() == 0) {
                Toast.makeText(con, "文件名不能为空,请重新输入！", Toast.LENGTH_SHORT).show();
                return;
            }
            String fullFileName = FileUtil.combinPath(path, newName);
            File newFile = new File(fullFileName);
            if (newFile.exists()) {
                Toast.makeText(con, "该文件夹已经存在！", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    if (newFile.mkdir()) {//新建文件夹
                        Message m = new Message();
                        m.what = 1;
                        FileHandler.sendMessage(m); // 发送信息，更新界面
                        dialog.dismiss();
                    } else {
                        Toast.makeText(con, "新建文件夹失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(con, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //新建文件
    public static void makeFile(final Context con, final String path, final Handler FileHandler) {
        View layout = LayoutInflater.from(con).inflate(R.layout.filerename, null);
        final EditText edit = (EditText) layout.findViewById(R.id.edit_rename_id);

        new AlertDialog.Builder(con).setIcon(R.drawable.dialogtitle_icon).setTitle("请输入新建文件的名字:").setView(layout).setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                String newName = edit.getText().toString().trim();
                if (newName.length() == 0) {
                    Toast.makeText(con, "文件名不能为空,请重新输入！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String fullFileName = FileUtil.combinPath(path, newName);
                File newFile = new File(fullFileName);
                if (newFile.exists()) {
                    Toast.makeText(con, "该文件已经存在！", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        if (newFile.createNewFile()) {//如果新建文件成功就发送信息
                            Message m = new Message();
                            m.what = 2;
                            FileHandler.sendMessage(m); // 发送信息，更新界面
                        } else {
                            Toast.makeText(con, "新建文件失败！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(con, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).show();
    }

    //重命名文件夹或文件
    public static void reNameFile(final Context con, final File f, final Handler FileHandler) {
        View layout = LayoutInflater.from(con).inflate(R.layout.dialog_title, null);
        EditText edit_name = (EditText) layout.findViewById(R.id.edit_name);
        String suffix = "";
        if (f.getName().indexOf(".") != -1) {
            edit_name.setText(f.getName().substring(0, f.getName().lastIndexOf(".")));
            suffix = f.getName().substring(f.getName().lastIndexOf("."));
        } else {
            suffix = "";
        }

        TextView tv_titledialog_title = (TextView) layout.findViewById(R.id.tv_titledialog_title);
        TextView tv_titledialog_cancle = (TextView) layout.findViewById(R.id.tv_titledialog_cancle);
        TextView tv_titledialog_sure = (TextView) layout.findViewById(R.id.tv_titledialog_sure);
        tv_titledialog_title.setText("重命名");
        AlertDialog dialog = new AlertDialog.Builder(con).setView(layout).show();
        tv_titledialog_cancle.setOnClickListener(v -> {
            dialog.dismiss();
        });
        String finalSuffix = suffix;
        tv_titledialog_sure.setOnClickListener(v -> {
            String path = f.getParentFile().getPath();
            String newName = edit_name.getText().toString().trim();
            newName = newName + finalSuffix;
            if (newName.equalsIgnoreCase(f.getName())) {
                return;
            }
            if (newName.length() == 0) {
                Toast.makeText(con, "文件名不能为空！！", Toast.LENGTH_SHORT).show();
                return;
            }
            String fullFileName = FileUtil.combinPath(path, newName);
            File newFile = new File(fullFileName);
            if (newFile.exists()) {
                Toast.makeText(con, "该文件已经存在！", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    if (f.renameTo(newFile)) {
                        FileHandler.sendEmptyMessage(0); // 发送信息，更新界面
                    } else {
                        Toast.makeText(con, "重命名失败！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } catch (Exception ex) {
                    Toast.makeText(con, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            dialog.dismiss();
        });
    }


    /**
     * 按文件名排序
     */
    public static ArrayList<FileInfo> orderByName(List<FileInfo> fileList) {
        ArrayList<FileInfo> FileNameList = new ArrayList<FileInfo>();
        Collections.sort(fileList, new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                if (o1.IsDirectory && o2.IsFile)
                    return 1;
                if (o1.IsFile && o2.IsDirectory)
                    return -1;
                return o1.Name.compareTo(o2.Name);
            }
        });
        for (FileInfo file1 : fileList) {
            if (file1.IsDirectory) {
                FileNameList.add(file1);
            }
        }
        return FileNameList;
    }

}
