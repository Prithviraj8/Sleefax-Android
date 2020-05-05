package com.Anubis.Sleefax;

class page_INFO {
//
//    public int cnt = 0;
//    public boolean black = false;
//    public int page_cnt = 0;
//    public int shopCnt = 0;

    public String url;
    public String colorType,fileType,pageSize,orientation,custom;
    public int copies;

    public page_INFO(String url, String colorType, int copies, String fileType, String pagesize, String orientation){
        this.url = url;
        this.colorType = colorType;
        this.copies = copies;
        this.fileType = fileType;
        this.pageSize = pagesize;
        this.orientation = orientation;
    }

}
class eachFileInfo{

    public String url;
    public String colorType,fileType,pageSize,orientation,custom,fileName;
    public int copies;

    public eachFileInfo(String url, String colorType, int copies, String fileType, String pagesize, String orientation, String fileName, String custom){
        this.url = url;
        this.colorType = colorType;
        this.copies = copies;
        this.fileType = fileType;
        this.pageSize = pagesize;
        this.orientation = orientation;
        this.fileName = fileName;
        this.custom = custom;
    }
}