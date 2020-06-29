package com.zwyl.myfile.main.filebrower;

import java.util.Comparator;


/**
 * 将文件按名字降序排列
 **/
public class FileComparatorByNameDown implements Comparator<FileInfo> {

    @Override
    public int compare(FileInfo file1, FileInfo file2) {
        return file2.Name.compareTo(file1.Name);
    }

}