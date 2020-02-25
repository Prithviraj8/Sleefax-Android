package com.Anubis.Sleefax;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ProcessingOrderProgressActivity extends AppCompatActivity {

    String name,loc,orderKey,orderStatus,shopKey,fileType,pagesize,orientation,username,email;
    LatLng shopLoc, userLoc;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    int files;
    double price;
    int copy;
    int resultCode;
    int requestCode;
    double numberOfPages;
    String color,custom,orderDateTime;
    String CHANNEL_ID = "UsersChannel",shopType;
    boolean FromYourOrders =false, bothSides,isTester;

    long usernum,shopNum;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> downloadUrls = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_order_progress);



    }
}
