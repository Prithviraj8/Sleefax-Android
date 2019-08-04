package com.prithviraj8.copycatandroid;

public class Page_Info {

    int cnt = 0;
    Boolean colorType;
    int page_cnt = 0;
    int shopCnt = 0;
}
class singlePageInfo{

    String url;
    String colorType;
    int copies;

    public singlePageInfo(String url,String colorType,int copies){
        this.url = url;
        this.colorType = colorType;
        this.copies = copies;
    }
}