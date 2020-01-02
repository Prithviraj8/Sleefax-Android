package com.Prithviraj8.Sleefax;

public class Page_Info {

    public int cnt = 0;
    public boolean black = false;
    public int page_cnt = 0;
    public int shopCnt = 0;
}
class singlePageInfo{

    public String url;
    public String colorType,fileType,pageSize,orientation,custom;
    public int copies;

    public singlePageInfo(String url, String colorType, int copies, String fileType, String pagesize, String orientation,String custom){
        this.url = url;
        this.colorType = colorType;
        this.copies = copies;
        this.fileType = fileType;
        this.pageSize = pagesize;
        this.orientation = orientation;
        this.custom = custom;
    }
}