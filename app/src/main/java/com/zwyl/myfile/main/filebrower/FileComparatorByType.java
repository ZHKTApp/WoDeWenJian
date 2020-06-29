package com.zwyl.myfile.main.filebrower;

import java.util.Comparator;


/** 排序 **/
public class FileComparatorByType implements Comparator<FileInfo> {


	public int compare(FileInfo file1, FileInfo file2) {

		if (file1.IsDirectory && !file2.IsDirectory) {
			return -1000;
		} else if (!file1.IsDirectory && file2.IsDirectory) {
			return 1000;
		}
		// 相同类型按名称排序
		return file1.Name.compareTo(file2.Name);
	}
}