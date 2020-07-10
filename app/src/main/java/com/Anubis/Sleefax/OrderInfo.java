package com.Anubis.Sleefax;

public class OrderInfo {

    String Name;
    String pageSize;
    String orientation;
    String fileType;
    String colorType;
    String copies;



    public OrderInfo(String Name, String pageSize, String orientation, String fileType, String colorType, String copies) {
        this.Name = Name;
        this.pageSize = pageSize;
        this.orientation = orientation;
        this.fileType = fileType;
        this.colorType = colorType;
        this.copies = copies;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getColorType() {
        return colorType;
    }

    public void setColorType(String colorType) {
        this.colorType = colorType;
    }

    public String getCopies() {
        return copies;
    }

    public void setCopies(String  copies) {
        this.copies = copies;
    }
}
