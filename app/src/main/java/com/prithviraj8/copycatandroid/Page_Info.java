package com.prithviraj8.copycatandroid;

public class Page_Info {

    public int cnt = 0;
    public boolean black = false;
    public int page_cnt = 0;
    public int shopCnt = 0;
}
class singlePageInfo{

    public String url;
    public String colorType,fileType,pagesize,orientation,custom;
    public int copies;
    boolean bothSides;
    public singlePageInfo(String url, String colorType, int copies, String fileType, String pagesize, String orientation, boolean bothSides,String custom){
        this.url = url;
        this.colorType = colorType;
        this.copies = copies;
        this.fileType = fileType;
        this.pagesize = pagesize;
        this.orientation = orientation;
        this.bothSides = bothSides;
        this.custom = custom;
    }
}