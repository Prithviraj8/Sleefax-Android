package com.Anubis.Sleefax;

import android.net.Uri;

class page_INFO {
//
//    public int cnt = 0;
//    public boolean black = false;
//    public int page_cnt = 0;
//    public int shopCnt = 0;

    public String url;
    public String colorType,fileType,pageSize,orientation,custom,fileSize,location,fileName;
    public int copies;
    public double price;

    public page_INFO(String url, String colorType, int copies, String fileType, String pagesize, String orientation,String fileName, String fileSize, double price, String location){
        this.url = url;
        this.colorType = colorType;
        this.copies = copies;
        this.fileType = fileType;
        this.pageSize = pagesize;
        this.orientation = orientation;
        this.fileSize = fileSize;
        this.location = location;
        this.price = price;
        this.fileName = fileName;

    }

}
class eachFileInfo{

    public String url,fileTypeConverted, colorType,fileType,pageSize,orientation,custom,fileName,fileSize,location;
    public int copies;
    public double price;


    public eachFileInfo(String url, String colorType, int copies, String fileType, String fileTypeConverted, String pagesize, String orientation, String fileName, String custom, String fileSize, double price, String location){
        this.url = url;
        this.colorType = colorType;
        this.copies = copies;
        this.fileType = fileType;
        this.fileTypeConverted = fileTypeConverted;

        this.pageSize = pagesize;
        this.orientation = orientation;
        this.fileName = fileName;
        this.custom = custom;
        this.fileSize = fileSize;

        this.price = price;
        this.location = location;
    }
}