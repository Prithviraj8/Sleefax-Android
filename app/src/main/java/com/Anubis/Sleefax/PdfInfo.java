package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;
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
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

public class PdfInfo extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId;
    int ShopsCnt=0,pdfCnt=0;

//    String colorType;

    Button done,viewPdf;
    Spinner pageSizeSpinner, orientSpinner;
    View colorsTV,bwTV,h,v;
    ToggleButton bothSidePrint;
    ImageButton back,scrollDown;
    ScrollView scrollView;
    PDFView pdfView;
    WebView webView;
    EditText customValue1,customValue2;
    int copy,custValue1,custValue2;

    //    String pdf_url;
    String pdf_url,pdf_uri;
    int resultCode,requestCode;
    String colour, pagesize;

    ArrayList<String> pdfURL = new ArrayList<>();
    ArrayList<String> fileType = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<String> customValues = new ArrayList<>();
    ArrayList<Integer> numberOfPages = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> fileSizes = new ArrayList<>();


    EditText copiesTV,pageCount;
    String URI = "";
    String orientation,fileName;
    String username,email,custom,shopType,customVal;
//    boolean bothSides = false;
    boolean isTester,newUser;

    ArrayList<String> storeID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_info);
//        getSupportActionBar().hide();

        pdfCnt = 0;

        colorsTV = findViewById(R.id.Pdf_Colors);
        bwTV = findViewById(R.id.Pdf_Black_White);
        done = findViewById(R.id.pdfDone);
        copiesTV = findViewById(R.id.PDF_copies);
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
        pdfURL = extras.getStringArrayList("URLS");
//        pdf_url = extras.getString("PdfURL");
        fileType = extras.getStringArrayList("FileType");
        username = extras.getString("username");
        email = extras.getString("email");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
//        numberOfPages = new double[pdfURL.size()];
        numberOfPages = extras.getIntegerArrayList("Pages");
        isTester = extras.getBoolean("IsTester");
        newUser = extras.getBoolean("NewUser");
        fileNames = extras.getStringArrayList("FileNames");
        fileSizes = extras.getStringArrayList("FileSizes");

        bothSides = new boolean[pdfURL.size()];

        Toast.makeText(this, "PDFCNT1 "+pdfURL.size(), Toast.LENGTH_SHORT).show();

        customValue1.setText("1");
        if(numberOfPages == null){
//            numberOfPages = new double[1];
            numberOfPages.add(pdfCnt,10);
        }
        customValue2.setText(String.valueOf((int) numberOfPages.get(pdfCnt)));

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


//        pdfURL.add((pdf_url));

        back = findViewById(R.id.back);
        back.setOnClickListener(BtnListener);

        bothSidePrint = findViewById(R.id.bothSidesToggle);

        bothSidePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // The toggle is enabled
                // The toggle is disabled


                bothSides[pdfCnt] = isChecked;

            }
        });

        colorsTV.setOnTouchListener(touchListener);
        bwTV.setOnTouchListener(touchListener);
        done.setOnClickListener(BtnListener);
        scrollDown.setOnClickListener(BtnListener);
        viewPdf.setOnClickListener(BtnListener);

        Log.d("PDFVISIBLE",String.valueOf(pdfView.VISIBLE));

    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if(view == findViewById(R.id.Pdf_Black_White)){

                colour = ("Black/White");
                colors.add(pdfCnt,colour);

                bwTV.setBackgroundResource(R.drawable.b_w_border);
                colorsTV.setBackgroundResource(R.drawable.black_white_view_backgroud);
            }
            if(view == findViewById(R.id.Pdf_Colors)){

                colour = "Colors";
                colors.add(pdfCnt,colour);

                colorsTV.setBackgroundResource(R.drawable.colors_border);
                bwTV.setBackgroundResource(R.drawable.black_white_view_backgroud);

            }

            return false;
        }
    };

    //Create an anonymous implementation of OnClickListener
    private View.OnClickListener BtnListener = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onClick(View v) {


            if(v == findViewById(R.id.back)) {
                if(pdfCnt>0){
                    pdfCnt = pdfCnt - 1;
                }else{
                    finish();
                }
            }

            if(v == findViewById(R.id.pdfDone)) {
                Toast.makeText(PdfInfo.this, "PDFCNT "+customValue2.getText(), Toast.LENGTH_SHORT).show();
                if(pdfCnt == pdfURL.size()-1) {
                    new findShops().execute();
                }

                copy = (Integer.parseInt(copiesTV.getText().toString()));
                if (copy == 0) {
                    copy = 1;
                }
                copies.add(pdfCnt,copy);

//                custom = customPages.getText().toString();
                final String[] sizes = new String[]{"A4", "A3", "A2"};
                pagesize = sizes[pageSizeSpinner.getSelectedItemPosition()];
                pageSize.add(pdfCnt,pagesize);

                final String[] ots = new String[]{"Portrait", "Landscape"};
                orientation = ots[orientSpinner.getSelectedItemPosition()];

//                if (customPages.getText().toString().equals("")) {
//                    custom = "All";
//                }

                custValue1 = 1;
                custValue2 = 2;

                if(customValue1.getText().toString().equals("1")){
                    custValue1 = 1;
                }else{
                    custValue1 = Integer.parseInt(customValue1.getText().toString());
                }

                if(customValue2.getText().toString().equals(String.valueOf(numberOfPages))){
                    custValue2 = (int) numberOfPages.get(pdfCnt);
                }else{
                    custValue2 = Integer.parseInt(customValue2.getText().toString());
                }


                if(orientation == null){
                    orientation = "Portrait";
                }
                orientations.add(pdfCnt,orientation);

                if(colour == null){
                    colour = "Black/White";
                }
                colors.add(pdfCnt,colour);

                if(custValue2 == (int) numberOfPages.get(pdfCnt) && custValue1 == 1){
                  custom = "All";
                  customPages.add(pdfCnt,custom);

                }else if(custValue1 != 1 && custValue2 != (int) numberOfPages.get(pdfCnt)){

                    if(custValue1 > custValue2){
                        int temp = custValue1;
                        custValue1 = custValue2;
                        custValue2 = temp;

                        custom = String.valueOf(custValue1)+"-"+String.valueOf(custValue2);
                        customPages.add(pdfCnt,custom);

                        customVal = String.valueOf(custValue2-custValue1 + 1);
                        customValues.add(pdfCnt,customVal);

                    }else{
                        custom = String.valueOf(custValue1)+"-"+String.valueOf(custValue2);
                        customPages.add(pdfCnt,custom);

                        customVal = String.valueOf(custValue2-custValue1 + 1);
                        customValues.add(pdfCnt,customVal);

                    }
                    pdfCnt = pdfCnt + 1;
                    customValue2.setText((int) numberOfPages.get(pdfCnt));
                    Toast.makeText(PdfInfo.this, "CUSV2 "+customValue2.getText(), Toast.LENGTH_SHORT).show();

//                    if(pdfCnt == pdfURL.size()) {
//                        new findShops().execute();
//                    }
                }
                else {

                    if (custValue1 > custValue2) {
                        int temp = custValue1;
                        custValue1 = custValue2;
                        custValue2 = temp;

                        custom = (custValue1) + "-" + (custValue2);
                        customPages.add(pdfCnt, custom);

                        customVal = String.valueOf(custValue2 - custValue1 + 1);
                        customValues.add(pdfCnt, customVal);

                    } else {
                        custom = (custValue1) + "-" + (custValue2);
                        customPages.add(pdfCnt, custom);

                        customVal = String.valueOf(custValue2 - custValue1 + 1);
                        customValues.add(pdfCnt, customVal);

                    }
                }
                Toast.makeText(PdfInfo.this, "CUSV2 "+customValue2.getText(), Toast.LENGTH_SHORT).show();
                pdfCnt = pdfCnt + 1;
                    if(pdfCnt < pdfURL.size()) {
                        customValue2.setText(String.valueOf((int) numberOfPages.get(pdfCnt)));
                    }
//                    if(pdfCnt == pdfURL.size()) {
//                        new findShops().execute();
//                    }


            }


            if(v == findViewById(R.id.scrollDownPdf)){
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            if(v == findViewById(R.id.viewPdfBtn)) {

                Toast.makeText(PdfInfo.this, "FTYPE "+fileType.get(pdfCnt), Toast.LENGTH_SHORT).show();
                if (fileType.get(pdfCnt).contains("pdf")) {
                    viewPdf.setVisibility(View.INVISIBLE);
                    bothSidePrint.setVisibility(View.INVISIBLE);
                    done.setVisibility(View.INVISIBLE);
                    pdfView.setVisibility(View.VISIBLE);


                    pdfView.fromUri(Uri.parse(pdfURL.get(pdfCnt)))
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(getApplicationContext()))
                            .enableDoubletap(true)
                            .onPageError(new OnPageErrorListener() {
                                @Override
                                public void onPageError(int page, Throwable t) {
                                    Log.d("PAGE ERROR", String.valueOf(page));
                                    Log.d("ERROR IS", String.valueOf(t));
                                }
                            })
                            .onLoad(new OnLoadCompleteListener() {
                                @Override
                                public void loadComplete(int nbPages) {
                                    Log.d("PDFNOP", String.valueOf(pdfView.getPageCount()));
//                                numberOfPages = pdfView.getPageCount();
                                }
                            })
                            .load();

                }else if(fileType.get(pdfCnt).contains("document") || fileType.get(pdfCnt).contains("powerpoint")){
                    Toast.makeText(PdfInfo.this, "YUP "+fileType.get(pdfCnt), Toast.LENGTH_SHORT).show();
                    ViewDoc(pdfURL,fileType.get(pdfCnt));
                }
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

    public void ViewDoc(ArrayList<String> word, String mimeType) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(word.get(pdfCnt)),mimeType);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Open File with"));
        }
        else {
            Toast.makeText(this, "No app found for opening this document", Toast.LENGTH_SHORT).show();
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

    KProgressHUD hud;

public class findShops extends AsyncTask<Void,Void,Integer>{

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
          hud = KProgressHUD.create(PdfInfo.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Finding stores")
                .setMaxProgress(100)
                .show();
    }

    int shopsCount;
    @Override
    protected Integer doInBackground(Void... integers) {
        getShopsCount();
//        Log.d("Shops Count is ", String.valueOf(shopsCount));
        return ShopsCnt;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
//        hud.dismiss();
    }
}


    private void getShopsCount(){


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                if(isTester){
                    if(dataSnapshot.getKey().equals(shopType)) {
                        ShopsCnt = (int) dataSnapshot.getChildrenCount();

                        Intent intent = new Intent(PdfInfo.this, ShopsActivity.class);
                        Bundle extras = new Bundle();
                        Toast.makeText(PdfInfo.this, "PDFCNT2 "+pdfURL.size(), Toast.LENGTH_SHORT).show();
                        extras.putInt("ShopCount", ShopsCnt);
                        extras.putStringArrayList("StoreID", storeID);

                        extras.putStringArrayList("URLS", pdfURL);
                        extras.putIntegerArrayList("Pages", numberOfPages);
                        extras.putBooleanArray("BothSides", bothSides);
                        extras.putIntegerArrayList("Copies", copies);
                        extras.putStringArrayList("ColorType", colors);
                        extras.putStringArrayList("FileType", fileType);
                        extras.putStringArrayList("PageSize", pageSize);
                        extras.putStringArrayList("Orientation", orientations);
                        extras.putStringArrayList("FileNames",fileNames);
                        extras.putStringArrayList("FileSizes",fileSizes);
                        extras.putBoolean("NewUser",newUser);

                        extras.putStringArrayList("Custom",customPages);
                        extras.putStringArrayList("CustomValue",customValues);
//                        if(custom == "All"){
//                            extras.putString("Custom", customPages);
//                        }else {
//                            extras.putString("CustomValue",customVal);
//                            extras.putString("Custom", custom);
//                        }
                        extras.putBoolean("IsTester",isTester);
                        intent.putExtras(extras);
                        hud.dismiss();
                        startActivity(intent);
                    }

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
    }

}



