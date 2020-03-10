package com.Anubis.Sleefax;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;
import java.util.ArrayList;

import static com.aspose.words.LoadFormat.DOC;

public class Pop extends AppCompatActivity {

    private static final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";

    int numberOfPages,shortAnimationDuration;
    Boolean isTester,newUser;
    PDFView pdfView;
    Button nextActivity,dismissViewPDF,selectPhotos,selectAttachment;
    View bottomNavView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//      getSupportActionBar().setBackgroundDrawable(R.drawable.col);

        pdfView = findViewById(R.id.viewPDF);
        bottomNavView = findViewById(R.id.bottomNavView);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int) (height));

        selectPhotos = findViewById(R.id.selectphotos);
        selectAttachment = findViewById(R.id.selectattachment);
        nextActivity = findViewById(R.id.nextActivity);
        dismissViewPDF = findViewById(R.id.dismissBtn);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isTester = bundle.getBoolean("IsTester");
        newUser = bundle.getBoolean("NewUser");

        selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
//                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Images"), 1);

            }
        });


        selectAttachment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("application/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select files"), 1);

            }
        });

        nextActivity.setOnClickListener(Listener);
        dismissViewPDF.setOnClickListener(Listener);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);


    }

    ArrayList<Uri> uri = new ArrayList<>();
    String fileType;
    Uri file;


    //     Create an anonymous implementation of OnClickListener
    private View.OnClickListener Listener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onClick(View v) {
            // do something when the button is clicked
            Log.d("GETTING", "ORDERS");


            if(v == findViewById(R.id.nextActivity)){
                uploadFile(file);
            }
            if(v == findViewById(R.id.dismissBtn)){
                pdfView.setVisibility(View.GONE);
                bottomNavView.setVisibility(View.GONE);
                nextActivity.setVisibility(View.GONE);
                dismissViewPDF.setVisibility(View.GONE);

                selectAttachment.setVisibility(View.VISIBLE);
                selectPhotos.setVisibility(View.VISIBLE);
            }
        }
    };


    public void setVisibilities(){
        pdfView.setAlpha(0f);
        pdfView.setVisibility(View.VISIBLE);
        pdfView.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);



        bottomNavView.setAlpha(0f);
        bottomNavView.setVisibility(View.VISIBLE);
        bottomNavView.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);



        dismissViewPDF.setAlpha(0f);
        dismissViewPDF.setVisibility(View.VISIBLE);
        dismissViewPDF.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);


        selectAttachment.setVisibility(View.GONE);
        selectPhotos.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        uri.clear();
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file

        if (requestCode == 1 && resultCode == RESULT_OK) {
            //if a file is selected


            if (data.getData() != null) {
                //uploading the file

                Uri returnUri = data.getData();
//                uri.add(returnUri);
                file = returnUri;


                Log.d("URIID", String.valueOf(returnUri));
                String mimeType = getContentResolver().getType(returnUri);
                Log.d("MIME", mimeType);

                if (mimeType.contains("application")) {

                    Log.d("FILE", mimeType);
                    fileType = mimeType;

//                    uploadFile(requestCode, resultCode,returnUri);
                    if(mimeType.equals("application/pdf")){
                        ViewPDF(returnUri);
                    }else if(mimeType.contains("msword")|| mimeType.contains("docx")){
                        ViewDoc(returnUri);
                    }
                } else {

                    Log.d("FILE", "Image");
                    fileType = mimeType;
//                    Log.d("URRISIZE", String.valueOf(uri.size()));
//                    uploadImg(requestCode, resultCode, data, uri);

//                    Log.d("IMAGESARE", String.valueOf(data.getClipData().getItemCount()));
                    if(data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            if (data.getClipData().getItemAt(i).getUri() != null) {
                                uri.add(data.getClipData().getItemAt(i).getUri());
                                if (i == data.getClipData().getItemCount() - 1) {
                                    uploadImg(requestCode, resultCode, data, uri);
                                }
                            }
                        }
//                        uri.add(returnUri);
//                        uploadImg(requestCode, resultCode, data, uri);


                    }else{
                        uri.add(returnUri);
                        uploadImg(requestCode,resultCode,data,uri);
                    }
                }
            } else {
//                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
                Log.d("DATA", String.valueOf(data.getClipData()));
                fileType = "image/png";

                if(data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        if (data.getClipData().getItemAt(i).getUri() != null) {
                            uri.add(data.getClipData().getItemAt(i).getUri());
                            if (i == data.getClipData().getItemCount() - 1) {
                                uploadImg(requestCode, resultCode, data, uri);
                            }
                        }
                    }
                }
            }
        }
    }


    public void ViewPDF(Uri file){
        setVisibilities();
        pdfView.fromUri(file)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(getApplicationContext()))
                .enableDoubletap(true)
                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {
                        Toast.makeText(Pop.this,"Sorry but we faced an error on our side 😢. \n Please select this file again.",Toast.LENGTH_LONG).show();

                    }
                })
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        Log.d("PDFNOP", String.valueOf(pdfView.getPageCount()));

                        nextActivity.setAlpha(0f);
                        nextActivity.setVisibility(View.VISIBLE);
                        nextActivity.animate()
                                .alpha(1f)
                                .setDuration(shortAnimationDuration)
                                .setListener(null);

                        numberOfPages = pdfView.getPageCount();
                    }
                })
                .load();
    }

//    public void ViewDoc(Uri file){
////        setVisibilities();
//        Toast.makeText(this,"OPENING DOC",Toast.LENGTH_LONG).show();
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_VIEW);
//        String type = "application/msword";
//        intent.setDataAndType(file, type);
//        startActivity(intent);
//    }
    public void ViewDoc(Uri word) {
//        nextActivity.setVisibility(View.VISIBLE);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(word, "application/msword");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(intent);
        startActivity(Intent.createChooser(intent, "Choose an Application:"));


    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFile(Uri file){

        Intent goToPdfInfo = new Intent(Pop.this, PdfInfo.class);
        //goToPageInfo.putExtra("Pages",images);
        Bundle extras = new Bundle();
//        extras.putParcelable("PdfURL", file);
        extras.putInt("Pages",numberOfPages);
        extras.putString("PdfURL", file.toString());
//        extras.putParcelable("PDFUri",file);
        extras.putString("FileType", fileType);
//        extras.putInt("RequestCode",requestCode);
//        extras.putInt("ResultCode",resultCode);
        extras.putBoolean("IsTester",isTester);
        extras.putBoolean("NewUser",newUser);

//        extras.putString("FileName",fileName);
        goToPdfInfo.putExtras(extras);
        startActivity(goToPdfInfo);
        finish();

    }


    public void uploadImg(int requestCode, int resultCode, Intent data, ArrayList<Uri> uri){
        Intent goToPageInfo = new Intent(Pop.this, PageInfo.class);
        Bundle extras = new Bundle();
        extras.putString("FileType", fileType);
        ArrayList<String> images = new ArrayList<>();
        int i;

        for(i=0;i<uri.size();i++){
            images.add(uri.get(i).toString());

            Log.d("I IS", String.valueOf(i));
            if(i == uri.size()-1) {
//                Log.d("URISIZE", String.valueOf(uri.size()));
                extras.putStringArrayList("URLS", images);
                extras.putParcelable("Data",data);
                extras.putBoolean("IsTester",isTester);
                extras.putBoolean("NewUser",newUser);

//            extras.putParcelableArrayList("URLS", uri);
                goToPageInfo.putExtras(extras);
                startActivity(goToPageInfo);
            }
        }

    }

}
