package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PdfInfo extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId;
    int ShopsCnt=0;

//    String colorType;

    TextView  customPages;
    Button done,viewPdf;
    Spinner pageSizeSpinner, orientSpinner;
    View colorsTV,bwTV,h,v;
    ToggleButton bothSidePrint;
    ImageButton back,scrollDown;
    ScrollView scrollView;
    PDFView pdfView;
    WebView webView;
    EditText customValue1,customValue2;
    int custValue1,custValue2;

    //    String pdf_url;
    String pdf_url,pdf_uri;
    int copy,resultCode,requestCode,numberOfPages = 0;
    String colour, pagesize;
    ArrayList<String> pdfURL = new ArrayList<>();
    EditText copies,pageCount;
    String URI = "";
    String fileType, orientation,fileName;
    String username,email,custom,shopType;
    boolean bothSides = false;
    boolean isTester,newUser;

    ArrayList<String> storeID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_info);
//        getSupportActionBar().hide();


        colorsTV = findViewById(R.id.Pdf_Colors);
        bwTV = findViewById(R.id.Pdf_Black_White);
        done = findViewById(R.id.pdfDone);
        copies = findViewById(R.id.PDF_copies);
        pageSizeSpinner = findViewById(R.id.sizeSpinner);
        orientSpinner = findViewById(R.id.orientationSpinner);
        scrollView = findViewById(R.id.scrollViewPdfs);
        scrollDown = findViewById(R.id.scrollDownPdf);
        viewPdf = findViewById(R.id.viewPdfBtn);
        pageCount = findViewById(R.id.PageCount);
        pdfView = findViewById(R.id.viewPDF);

//        pdfView.setVisibility(View.INVISIBLE);
//        h = findViewById(R.id.h);
//        v = findViewById(R.id.v);
        final String[] sizes = new String[]{"A4", "A3", "A2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sizes);

        final String[] orientations = new String[]{"Portrait", "Landscape"};
        ArrayAdapter<String> orientationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, orientations);

        pageSizeSpinner.setAdapter(adapter);
        orientSpinner.setAdapter(orientationsAdapter);

        customValue1 = findViewById(R.id.customValue1);
        customValue2 = findViewById(R.id.customValue2);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
//        pdf_url = extras.getString("PdfURL");
//        pdf_url = extras.getParcelable("PdfURL");
        pdf_url = extras.getString("PdfURL");
        fileType = extras.getString("FileType");
        username = extras.getString("username");
        email = extras.getString("email");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
        numberOfPages = extras.getInt("Pages");
        isTester = extras.getBoolean("IsTester");
        newUser = extras.getBoolean("NewUser");

        if(!newUser){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        if(isTester){
            shopType = "TestStores";
        }else{
            shopType = "Stores";
        }
//        fileName = extras.getString("FileName");
//        pdf_uri = extras.getParcelable("PDFUri");


        pdfURL.add((pdf_url));

        back = findViewById(R.id.back);
        back.setOnClickListener(BtnListener);

        bothSidePrint = findViewById(R.id.bothSidesToggle);

        bothSidePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // The toggle is enabled
                // The toggle is disabled
                bothSides = isChecked;
            }
        });

        colorsTV.setOnTouchListener(touchListener);
        bwTV.setOnTouchListener(touchListener);
//        h.setOnTouchListener(touchListener);
//        v.setOnTouchListener(touchListener);
        done.setOnClickListener(BtnListener);
        scrollDown.setOnClickListener(BtnListener);
        viewPdf.setOnClickListener(BtnListener);

        Log.d("PDFVISIBLE",String.valueOf(pdfView.VISIBLE));

    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if(view == findViewById(R.id.Pdf_Black_White)){

                colour = ("Black/White");
                bwTV.setBackgroundResource(R.drawable.b_w_border);
                colorsTV.setBackgroundResource(R.drawable.black_white_view_backgroud);
            }
            if(view == findViewById(R.id.Pdf_Colors)){

                Log.d("Colors","Pressed");
                colour = "Colors";
                colorsTV.setBackgroundResource(R.drawable.colors_border);
                bwTV.setBackgroundResource(R.drawable.black_white_view_backgroud);

            }

            return false;
        }
    };

    //Create an anonymous implementation of OnClickListener
    private View.OnClickListener BtnListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onClick(View v) {


            if(v == findViewById(R.id.back)) {
//                Intent intent1 = new Intent(PdfInfo.this, Select.class);
//                startActivity(intent1);
                finish();
            }

            if(v == findViewById(R.id.pdfDone)) {
                copy = (Integer.parseInt(copies.getText().toString()));
//                custom = customPages.getText().toString();
                final String[] sizes = new String[]{"A4", "A3", "A2"};
                pagesize = sizes[pageSizeSpinner.getSelectedItemPosition()];

                final String[] orientations = new String[]{"Portrait", "Landscape"};
                orientation = orientations[orientSpinner.getSelectedItemPosition()];


//                if (customPages.getText().toString().equals("")) {
//                    custom = "All";
//                }

                custValue1 = 1;
                custValue2 = 2;

                if(customValue1.getText().toString().equals("")){
                    custValue1 = 1;
                }else{
                    custValue1 = Integer.parseInt(customValue1.getText().toString());

                }

                if(customValue2.getText().toString().equals("")){
                    custValue2 = 1;
                }else{
                    custValue2 = Integer.parseInt(customValue2.getText().toString());
                }


                if (copy == 0) {
                    copy = 1;
                }

                if(orientation == null){
                    orientation = "Portrait";
                }

                if(colour == null){
                    colour = "Black/White";
                }


                if(custValue2 == 1 && custValue1 == 1){
                    Log.d("NO CUSTOM","YES");
//                  alertBox("Please view your pdf and provide a page number till which you need a printout.\n For the entire pdf to be printed , specify the number of the last page.");
                  custom = "All";
                }else if(custValue1 != 1 && custValue2 != 1){
                    Log.d("CUSTOM","BOTH TV");

                    if(custValue1 > custValue2){
                        int temp = custValue1;
                        custValue1 = custValue2;
                        custValue2 = temp;
                        custom = String.valueOf(custValue2-custValue1 + 1);
                    }else{
                        custom = String.valueOf(custValue2-custValue1 + 1);
                    }
                    new findShops().execute();

                }else{
                    Log.d("CUSTOM","MAYBE BOTH");

                    if(custValue1 > custValue2){
                        int temp = custValue1;
                        custValue1 = custValue2;
                        custValue2 = temp;
                        custom = String.valueOf(custValue2-custValue1 + 1) ;
                    }else{
                        custom = String.valueOf(custValue2-custValue1 + 1);
                    }
                    new findShops().execute();

                }

                if(custom != "All") {
                    Log.d("CUSTOM","ALL");
                }else{
                    new findShops().execute();
//                    if (!pageCount.getText().toString().equals("")) {
////                        numberOfPages = Integer.parseInt(pageCount.getText().toString());
//                        new findShops().execute();
//                    } else {
//                        alertBox("Please view your pdf and provide a page number till which you need a printout.\n For the entire pdf to be printed , specify the number of the last page.");
//                    }
                }
            }


            if(v == findViewById(R.id.scrollDownPdf)){
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            if(v == findViewById(R.id.viewPdfBtn)){

                viewPdf.setVisibility(View.INVISIBLE);
                bothSidePrint.setVisibility(View.INVISIBLE);
                done.setVisibility(View.INVISIBLE);
                pdfView.setVisibility(View.VISIBLE);

                pdfView.fromUri(Uri.parse(pdf_url))
                        .enableSwipe(true)
                        .enableAnnotationRendering(true)
                        .scrollHandle(new DefaultScrollHandle(getApplicationContext()))
                        .enableDoubletap(true)
                        .onPageError(new OnPageErrorListener() {
                            @Override
                            public void onPageError(int page, Throwable t) {
                                Log.d("PAGE ERROR",String.valueOf(page));
                                Log.d("ERROR IS",String.valueOf(t));
                            }
                        })
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                Log.d("PDFNOP", String.valueOf(pdfView.getPageCount()));
                                numberOfPages = pdfView.getPageCount();
                            }
                        })
                        .load();

            }
        }
    };

    @Override
    public void onBackPressed() {
        if(pdfView.VISIBLE == 0){
            viewPdf.setVisibility(View.VISIBLE);
            bothSidePrint.setVisibility(View.VISIBLE);
            done.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void alertBox(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PdfInfo.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Oh! Got it",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


public class findShops extends AsyncTask<Void,Void,Integer>{

        int shopsCount;
    @Override
    protected Integer doInBackground(Void... integers) {
        getShopsCount();
//        Log.d("Shops Count is ", String.valueOf(shopsCount));

        return ShopsCnt;
    }

}


    private void getShopsCount(){
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                if(isTester){
                    if(dataSnapshot.getKey().equals(shopType)) {
                        ShopsCnt = (int) dataSnapshot.getChildrenCount();

//                        for (DataSnapshot ids : dataSnapshot.getChildren()) {
//                            for (DataSnapshot shopInfo : ids.getChildren()) {
//                                if (shopInfo.getKey().equals("ShopName")) {
//                                    if (!shopInfo.getValue().toString().contains("tester")) {
//                                        ShopsCnt = ShopsCnt + 1;
//                                    }
//                                }
//                            }
//                        }
                        Log.d("STORENO", String.valueOf(ShopsCnt));
                        Intent intent = new Intent(PdfInfo.this, ShopsActivity.class);
                        Bundle extras = new Bundle();
                        extras.putStringArrayList("URLS", pdfURL);
//                  extras.putParcelableArrayList("URLS", pdfURL);
//                    Log.d("COLORTYPE",colour);

                        extras.putInt("Pages", numberOfPages);
                        extras.putInt("Copies", copy);
                        extras.putString("ColorType", colour);
                        extras.putInt("ShopCount", ShopsCnt);
                        extras.putString("FileType", "application/pdf");
                        extras.putStringArrayList("StoreID", storeID);
                        extras.putString("PageSize", pagesize);
                        extras.putString("Orientation", orientation);
                        extras.putInt("RequestCode", requestCode);
                        extras.putInt("ResultCode", resultCode);
                        extras.putBoolean("BothSides", bothSides);
                        extras.putBoolean("NewUser",newUser);


                        if(custom == "All"){
                            extras.putString("Custom", "All");
                        }else {
                            extras.putString("Custom", custom);
                        }
                        extras.putBoolean("IsTester",isTester);

                        Log.d("ISTESTER",String.valueOf(isTester));
                        intent.putExtras(extras);
                        startActivity(intent);
                    }

//                }else{
//
//                    isTester = false;
//                if(dataSnapshot.getKey().equals("Stores")) {
//
//                    ShopsCnt = (int) dataSnapshot.getChildrenCount();
//                    for (DataSnapshot ids : dataSnapshot.getChildren()) {
//                        storeID.add(ids.getKey());
//                    }
//
//                    Log.d("STORENO", String.valueOf(ShopsCnt));
//                    Intent intent = new Intent(PdfInfo.this, ShopsActivity.class);
//                    Bundle extras = new Bundle();
//                    extras.putStringArrayList("URLS", pdfURL);
////                  extras.putParcelableArrayList("URLS", pdfURL);
////                    Log.d("COLORTYPE",colour);
//
//                    extras.putInt("Pages", numberOfPages);
//                    extras.putInt("Copies", copy);
//                    extras.putString("ColorType", colour);
//                    extras.putInt("ShopCount", ShopsCnt);
//                    extras.putString("FileType", "application/pdf");
//                    extras.putStringArrayList("StoreID", storeID);
//                    extras.putString("PageSize", pagesize);
//                    extras.putString("Orientation", orientation);
//                    extras.putInt("RequestCode", requestCode);
//                    extras.putInt("ResultCode", resultCode);
//                    extras.putBoolean("BothSides", bothSides);
//                    extras.putString("Custom", customPages.getText().toString());
//
//                    extras.putBoolean("Istester",isTester);
//                    intent.putExtras(extras);
//                    startActivity(intent);
//
//                    Log.d("SHOPCNT", String.valueOf(ShopsCnt));
//                    }
//                }
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        return ShopsCnt;
    }

}



