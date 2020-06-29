package com.zwyl.myfile.main.filebrower;


import java.io.Serializable;

/**
 * 表示一个文件信息类
 **/
public class FileInfo implements Serializable {
    public String Name;//名字
    public String Path;//路径
    public long Size;//大小
    public boolean IsDirectory = false;//是否是目录（默认为否）
    public boolean IsFile = false;//是否是文件（默认为否）
    public boolean canRead = false;//可读？
    public int FileCount = 0;//文件个数
    public int FolderCount = 0;//文件夹的个数
    public String lastmodified;//最后修改时间
    public long lastmodifiedLong;//文件夹的个数

}