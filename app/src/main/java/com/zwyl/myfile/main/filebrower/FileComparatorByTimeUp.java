package com.zwyl.myfile.main.filebrower;

import java.util.Comparator;


/**
 * 将文件按名字降序排列
 **/
public class FileComparatorByTimeUp implements Comparator<FileInfo> {

    @Override
    public int compare(FileInfo file1, FileInfo file2) {
        if(file1.lastmodifiedLong < file2.lastmodifiedLong) {
            return -1;// 最后修改的文件在前
        } else {
            return  1;
        }
    }

}