package com.Anubis.Sleefax;

public class shopinfo {
    public String storeId, ShopsLocation, ShopName, orderStatus, fileType, pageSize, orientation, custom, orderDateTime, paymentMode,orderID, otp;
    public Double ShopLat, ShopLong;
    public int files, id;
    public double price;
    public long num;
    public boolean P_Notified, RT_Notified, IP_Notified, R_Notified, D_Notified, otpUsed;

    //    public shopinfo(String storeId, String shopsLocation, String shopName, String orderStatus, Double ShopLat, Double ShopLong, long num, int files, String fileType, String pageSize, String orientation, double price, String custom, String orderDateTime, boolean P_Notified, boolean RT_Notified, boolean IP_Notified, boolean R_Notified,boolean D_Notified, String paymentMode, int id) {
//        this.storeId = storeId;
//        ShopsLocation = shopsLocation;
//        ShopName = shopName;
//        this.orderStatus = orderStatus;
//        this.ShopLat = ShopLat;
//        this.ShopLong = ShopLong;
//        this.num = num;
//        this.files = files;
//        this.fileType = fileType;
//        this.price = price;
//        this.pageSize = pageSize;
//        this.orientation = orientation;
//        this.custom = custom;
//        this.orderDateTime = orderDateTime;
//        this.P_Notified = P_Notified;
//        this.RT_Notified = RT_Notified;
//        this.IP_Notified = IP_Notified;
//        this.R_Notified = R_Notified;
//        this.D_Notified = D_Notified;
//        this.paymentMode = paymentMode;
//        this.id = id;
//
//    }
    public shopinfo(String storeId, String shopsLocation, String shopName, String orderStatus, Double ShopLat, Double ShopLong, long num, int files, double price, String orderDateTime, boolean P_Notified, boolean RT_Notified, boolean IP_Notified, boolean R_Notified, boolean D_Notified, String paymentMode, int id,String orderID, String otp, boolean otpUsed){
        this.storeId = storeId;
        ShopsLocation = shopsLocation;
        ShopName = shopName;
        this.orderStatus = orderStatus;
        this.ShopLat = ShopLat;
        this.ShopLong = ShopLong;
        this.num = num;
        this.files = files;
//        this.fileType = fileType;
        this.price = price;
//        this.pageSize = pageSize;
//        this.orientation = orientation;
//        this.custom = custom;
        this.orderDateTime = orderDateTime;
        this.P_Notified = P_Notified;
        this.RT_Notified = RT_Notified;
        this.IP_Notified = IP_Notified;
        this.R_Notified = R_Notified;
        this.D_Notified = D_Notified;
        this.paymentMode = paymentMode;
        this.id = id;
        this.orderID = orderID;
    }
}