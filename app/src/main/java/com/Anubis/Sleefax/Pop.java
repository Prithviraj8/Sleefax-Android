package com.Anubis.Sleefax;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Pop extends AppCompatActivity {

    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }


    private static final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";
    int shortAnimationDuration,cnt = 0,shopCnt;
//    double[] numberOfPages;
    String mimeType,fileName,fileSize;
    ArrayList<String> mimeTypes = new ArrayList<>();

    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
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
    ArrayList<Integer> customPage1 = new ArrayList<>();
    ArrayList<Integer> customPage2 = new ArrayList<>();
    ArrayList<Uri> fileLocations = new ArrayList<>();
    double pricePerFile[];
    double totalPrice;

    boolean newUser,isTester,addingMoreFiles,selectingFile;


    String fileType;

    PDFView pdfView;
    Button nextActivity,dismissViewPDF,selectPhotos,selectAttachment;
    View bottomNavView;
    RelativeLayout pdfViewRL;
    KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        cnt = 0;

        pdfViewRL = findViewById(R.id.PdfViewRL);
        pdfView = findViewById(R.id.viewPDF);
        bottomNavView = findViewById(R.id.bottomNavView);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int) (height));


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isTester = bundle.getBoolean("IsTester");
        newUser = bundle.getBoolean("NewUser");
        selectingFile = bundle.getBoolean("File");
        addingMoreFiles = bundle.getBoolean("AddingMoreFiles");

        if(addingMoreFiles){
            getOrderInfo();
        }


        if(selectingFile){
            Intent fileIntent = new Intent();
            fileIntent.setType("application/*");
            fileIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            fileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(fileIntent, "Select files"), 1);
        }else{
            Intent imageIntent = new Intent();
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(imageIntent, "Select Images"), 1);

        }



        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        // Progress HUD
        hud = KProgressHUD.create(Pop.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading")
                .setMaxProgress(100);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(Pop.this,Select.class);
//        startActivity(intent);
        if(hud.isShowing()){
            hud.dismiss();
        }
        finish();
    }

    public void getOrderInfo(){



        Intent intent = getIntent();
        Bundle extras = intent.getExtras();


        urls = extras.getStringArrayList("URLS");
        copies = extras.getIntegerArrayList("Copies");
        colors = extras.getStringArrayList("ColorType");
        mimeTypes = extras.getStringArrayList("FileType");
        shopCnt = extras.getInt("ShopCount");
        pageSize = extras.getStringArrayList("PageSize");
        orientations = extras.getStringArrayList("Orientation");
        bothSides = extras.getBooleanArray("BothSides");
        customPages = extras.getStringArrayList("Custom");
        numberOfPages = extras.getIntegerArrayList("Pages");
        newUser = extras.getBoolean("NewUser");
        customValues = extras.getStringArrayList("CustomValue");
        fileNames = extras.getStringArrayList("FileNames");
        fileSizes = extras.getStringArrayList("FileSizes");
        isTester = extras.getBoolean("IsTester");
        cnt = extras.getInt("FileCount");
        customPage1 = extras.getIntegerArrayList("CustomPages1");
        customPage2 = extras.getIntegerArrayList("CustomPages2");

        pricePerFile = extras.getDoubleArray("PricePerFile");
        totalPrice = extras.getDouble("TotalPrice");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        uri.clear();
//        mimeTypes.clear();

        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file

        hud.show();
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //if a file is selected
            renderFiles(data);
        }
    }




    public void renderFiles(final Intent data){


        if(!hud.isShowing()) {
            hud.show();
        }

        final ArrayList<String> pdfs = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (data.getData() != null) {
                    //uploading the file

                    Uri returnUri = data.getData();
                    fileLocations.add(returnUri);

                    Log.d("FILELOC",returnUri.toString());
                    mimeType = getContentResolver().getType(returnUri);

                    if (mimeType.contains("application")) {
//                    mimeTypes.add(mimeType);

                        Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();

                        fileName = (returnCursor.getString(nameIndex));
                        fileNames.add(fileName);


                        if (Long.toString(returnCursor.getLong(sizeIndex)).length() >= 7) {
                            fileSize = (Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 0.000001)) + " MB");
                            fileSizes.add(fileSize);

                        } else {
                            fileSize = (Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 00.001)) + " kB");
                            fileSizes.add(fileSize);
                        }
                        urls.add(String.valueOf(returnUri));
//                    numberOfPages = new double[uri.size()];
                        if (mimeType.equals("application/pdf")) {
                            mimeTypes.add("PDF");
//                            ViewPDF(returnUri.toString());
                            hud.dismiss();
                            uploadFile(urls);

                            pdfs.add(returnUri.toString());
//                            ViewPDF(pdfs);

                        } else {
//                        uri.add(returnUri);
                            try {
                                FileInputStream inputStream = (FileInputStream) getContentResolver().openInputStream(data.getData());

                                if (mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                                    mimeTypes.add(cnt,"Docx");

                                    XWPFDocument document = null;
                                    if (inputStream != null) {
                                        document = new XWPFDocument(inputStream);
                                    }else{

                                    }
                                    XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                                    int pages = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
                                    numberOfPages.add(pages);
                                    Log.d("NOPOFDOCX", String.valueOf(pages));

                                } else if (mimeType.equals("application/msword")) {

                                    try {
                                        HWPFDocument document = new HWPFDocument(inputStream);
                                        int pages = document.getSummaryInformation().getPageCount();
                                        numberOfPages.add(pages);
                                        mimeTypes.add(cnt,"Word");

                                    } catch (Exception e) {
                                        FileInputStream docxIS = (FileInputStream) getContentResolver().openInputStream(data.getData());
                                        XWPFDocument document;
                                        document = new XWPFDocument(docxIS);
                                        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                                        int pages = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
                                        numberOfPages.add(pages);
                                        // Adding mimeType as PPTX even though PPT is selected because the page count algo for PPT is crashing and for PPTX is working for this selected file
                                        mimeTypes.add(cnt,"Docx");

                                    }

//                                Toast.makeText(this, "NOPOF DOC " + pages, Toast.LENGTH_SHORT).show();
                                } else if (mimeType.equals("application/vnd.ms-powerpoint")) {
//                                XMLSlideShow slideShow = new XMLSlideShow(inputStream);
//                                int slides = slideShow.getSlides().length;
//                                numberOfPages.add(slides);

                                    try {
                                        HSLFSlideShow show = new HSLFSlideShow(inputStream);
                                        SlideShow ss = new SlideShow(show);
                                        Slide[] slides = ss.getSlides();
//
                                        int slide_count = ss.getSlides().length;
                                        numberOfPages.add(slide_count);
                                        mimeTypes.add(cnt,"PPT");

                                        Log.d("NOPOF PPT ", String.valueOf(slide_count));

                                    } catch (Exception e) {
                                        XMLSlideShow slideShow = new XMLSlideShow(inputStream);
                                        int slides = slideShow.getSlides().length;
                                        numberOfPages.add(slides);
                                        mimeTypes.add(cnt,"PPTX");

                                        Log.d("NOPOF PPTX ", String.valueOf(slides));
                                    }


                                } else if (mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                                    mimeTypes.add(cnt,"PPTX");

                                    XMLSlideShow slideShow = new XMLSlideShow(inputStream);
                                    int slides = slideShow.getSlides().length;
                                    numberOfPages.add(slides);
                                    Log.d("NOPOF PPTX ", String.valueOf(slides));

                                }
//                                cnt = cnt + 1;

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


//                        ViewDoc();
                            hud.dismiss();
                            uploadFile(urls);
                        }
                    } else {


                        if (data.getClipData() != null) {
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
//                            mimeTypes.add("Image");
                                if (data.getClipData().getItemAt(i).getUri() != null) {
                                    urls.add(String.valueOf(data.getClipData().getItemAt(i).getUri()));
                                    mimeTypes.add("IMAGE");

                                    Log.d("IMGSELE",String.valueOf(data.getClipData().getItemAt(i).getUri()));
                                    Cursor returnCursor = getContentResolver().query(data.getClipData().getItemAt(i).getUri(), null, null, null, null);
                                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    returnCursor.moveToFirst();

                                    if (Long.toString(returnCursor.getLong(sizeIndex)).length() >= 7) {
                                        fileSizes.add(Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 0.000001)) + " MB");
                                    } else {
                                        fileSizes.add(Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 00.001)) + " kB");
                                    }
                                    fileNames.add(returnCursor.getString(nameIndex));


                                    if (i == data.getClipData().getItemCount() - 1) {
                                        hud.dismiss();
                                        uploadImg(urls);
                                    }
                                }
                            }
//                        uri.add(returnUri);
//                        uploadImg(requestCode, resultCode, data, uri);
                        } else {
                            mimeTypes.add("IMAGE");

                            Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();

                            fileName = (returnCursor.getString(nameIndex));
                            fileNames.add(fileName);


                            if (Long.toString(returnCursor.getLong(sizeIndex)).length() >= 7) {
                                fileSize = (Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 0.000001)) + " MB");
                                fileSizes.add(fileSize);

                            } else {
                                fileSize = (Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 00.001)) + " kB");
                                fileSizes.add(fileSize);
                            }
                            urls.add(String.valueOf(returnUri));
                            hud.dismiss();
                            uploadImg(urls);
                        }
                    }

                    if(hud.isShowing()) {
                        hud.dismiss();
                    }
                } else {

                    if (data.getClipData() != null) {

                        if (data.getClipData().toString().contains("application")) {
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                fileLocations.add(data.getClipData().getItemAt(i).getUri());

                                cnt = i;

                                if (data.getClipData().getItemAt(i).getUri() != null) {

                                    urls.add(String.valueOf(data.getClipData().getItemAt(i).getUri()));
//                                mimeTypes.add(getContentResolver().getType(data.getClipData().getItemAt(i).getUri()));


                                    Cursor returnCursor = getContentResolver().query(data.getClipData().getItemAt(i).getUri(), null, null, null, null);
                                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    returnCursor.moveToFirst();


                                    if (Long.toString(returnCursor.getLong(sizeIndex)).length() >= 7) {
                                        fileSizes.add(Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 0.000001)) + " MB");
                                    } else {
                                        fileSizes.add(Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 00.001)) + " kB");
                                    }
                                    fileNames.add(returnCursor.getString(nameIndex));


                                    try {
                                        FileInputStream inputStream = (FileInputStream) getContentResolver().openInputStream(data.getClipData().getItemAt(i).getUri());
                                        if (getContentResolver().getType(data.getClipData().getItemAt(i).getUri()).equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                                            mimeTypes.add(i, "Docx");

                                            XWPFDocument document;
                                            document = new XWPFDocument(inputStream);
                                            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                                            int pages = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
                                            numberOfPages.add(pages);
                                            Log.d("DOCXPAGES", String.valueOf(pages));
                                        } else if (getContentResolver().getType(data.getClipData().getItemAt(i).getUri()).equals("application/msword")) {

                                            try {
                                                HWPFDocument document = new HWPFDocument(inputStream);
                                                int pages = document.getSummaryInformation().getPageCount();
                                                numberOfPages.add(pages);
                                                mimeTypes.add(i, "Word");

                                                Log.d("DOCPAGES", String.valueOf(pages));
                                            }catch (Exception e){
                                                FileInputStream docxIS = (FileInputStream) getContentResolver().openInputStream(data.getClipData().getItemAt(i).getUri());
                                                XWPFDocument document;
                                                document = new XWPFDocument(docxIS);
                                                int pages = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
                                                numberOfPages.add(pages);
                                                // Adding mimeType as DOCX even though word is selected because the page count algo of docx is crashing and word algo is working for this selected docx file
                                                mimeTypes.add("Docx");

                                            }

                                        } else if (getContentResolver().getType(data.getClipData().getItemAt(i).getUri()).equals("application/vnd.ms-powerpoint")) {

//                                        HSLFSlideShow show = new HSLFSlideShow(inputStream);
//                                        SlideShow ss = new SlideShow(show);
//                                        Slide[] slides = ss.getSlides();
////
//                                        int slide_count = ss.getSlides().length;
//                                        numberOfPages.add(slide_count);

                                            try {
                                                HSLFSlideShow show = new HSLFSlideShow(inputStream);
                                                SlideShow ss = new SlideShow(show);
                                                Slide[] slides = ss.getSlides();
//
                                                int slide_count = slides.length;
                                                numberOfPages.add(slide_count);
                                                mimeTypes.add(i, "PPT");

                                                Log.d("PAGES_PPT", String.valueOf(slide_count));

                                            } catch (Exception e) {
                                                FileInputStream pptxIS = (FileInputStream) getContentResolver().openInputStream(data.getClipData().getItemAt(i).getUri());

                                                XMLSlideShow slideShow = new XMLSlideShow(pptxIS);
                                                int slides = slideShow.getSlides().length;
                                                numberOfPages.add(slides);

                                                // Adding mimeType as PPTX even though PPT is selected because the page count algo for PPT is crashing and for PPTX is working for this selected file
                                                mimeTypes.add(i, "PPTX");
                                                Log.d("PAGES_PPT", String.valueOf(slides));

                                            }

                                        } else if (getContentResolver().getType(data.getClipData().getItemAt(i).getUri()).equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                                            mimeTypes.add(i, "PPTX");

                                            XMLSlideShow slideShow = new XMLSlideShow(inputStream);
                                            int slides = slideShow.getSlides().length;
                                            numberOfPages.add(slides);
                                        } else if (getContentResolver().getType(data.getClipData().getItemAt(i).getUri()).equals("application/pdf")) {
                                            mimeTypes.add(i, "PDF");
                                            pdfs.add(String.valueOf(data.getClipData().getItemAt(i).getUri()));
                                            Log.d("ONLYPDF","YES");
//                                            ViewPDF(String.valueOf(data.getClipData().getItemAt(i).getUri()));
//                                            ViewPDF(pdfs);
                                        }

                                        if (i == data.getClipData().getItemCount() - 1) {
                                            hud.dismiss();
                                            uploadFile(urls);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        } else {

                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                mimeTypes.add("IMAGE");

                                if (data.getClipData().getItemAt(i).getUri() != null) {
                                    urls.add(String.valueOf(data.getClipData().getItemAt(i).getUri()));
                                    Cursor returnCursor = getContentResolver().query(data.getClipData().getItemAt(i).getUri(), null, null, null, null);
                                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    returnCursor.moveToFirst();

                                    if (Long.toString(returnCursor.getLong(sizeIndex)).length() >= 7) {
                                        fileSizes.add(Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 0.000001)) + " MB");
                                    } else {
                                        fileSizes.add(Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 00.001)) + " kB");
                                    }
                                    fileNames.add(returnCursor.getString(nameIndex));


                                    if (i == data.getClipData().getItemCount() - 1) {
                                        hud.dismiss();
                                        uploadImg(urls);
                                    }
                                }
                            }
                        }
                    }
                    if(hud.isShowing()) {
                        hud.dismiss();
                    }
                }

            }
        }).start();
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFile(ArrayList uri){

        if(addingMoreFiles){
            sendOrderInfo();
        }else {
            Intent goToPdfInfo = new Intent(Pop.this, PdfInfo.class);
            Bundle extras = new Bundle();

            Log.d("Number of pages ",String.valueOf(numberOfPages.size()));

            extras.putIntegerArrayList("Pages", numberOfPages);
            extras.putStringArrayList("FileNames", fileNames);
            extras.putStringArrayList("FileSizes", fileSizes);
            extras.putStringArrayList("URLS", urls);
            extras.putStringArrayList("FileType", mimeTypes);
            extras.putBoolean("IsTester", isTester);
            extras.putBoolean("NewUser", newUser);

//            extras.putInt("FileCount", urls.size());
//            extras.putBoolean("AddingMoreFiles", addingMoreFiles);

            goToPdfInfo.putExtras(extras);

            if(hud.isShowing()){
                hud.dismiss();
            }
            startActivity(goToPdfInfo);
            finish();

        }


    }


    public void uploadImg(ArrayList<String> uri){

        hud.show();
        Intent goToPageInfo = new Intent(Pop.this, PdfInfo.class);
        Bundle extras = new Bundle();

        ArrayList<String> images = new ArrayList<>();
        int i;
//        for(i=0;i<urls.size();i++){
//            images.add(urls.get(i));
            mimeTypes.add("IMAGE");

                if(!addingMoreFiles) {
                    extras.putStringArrayList("FileType", mimeTypes);
                    extras.putStringArrayList("URLS", uri);
                    extras.putBoolean("IsTester", isTester);
                    extras.putBoolean("NewUser", newUser);
                    extras.putBoolean("AddingMoreFiles", addingMoreFiles);
                    extras.putStringArrayList("FileNames", fileNames);
                    extras.putStringArrayList("FileSizes", fileSizes);

                    hud.dismiss();
                    goToPageInfo.putExtras(extras);
                    startActivity(goToPageInfo);
                }else{
                    hud.dismiss();
                    sendOrderInfo();
                }
//        }
    }

    public void sendOrderInfo(){
        Intent intent;
        Bundle extras;
        extras = new Bundle();

        intent = new Intent(Pop.this, PdfInfo.class);
        extras.putBoolean("AddingMoreFiles",true);
        extras.putInt("FileCount",urls.size());

        extras.putInt("ShopCount", shopCnt);
        extras.putStringArrayList("URLS", urls);
        extras.putIntegerArrayList("Pages", numberOfPages);
        extras.putBooleanArray("BothSides", bothSides);
        extras.putIntegerArrayList("Copies", copies);
        extras.putStringArrayList("ColorType", colors);
        extras.putStringArrayList("FileType", mimeTypes);
        extras.putStringArrayList("PageSize", pageSize);
        extras.putStringArrayList("Orientation", orientations);
        extras.putStringArrayList("FileNames",fileNames);
        extras.putStringArrayList("FileSizes",fileSizes);
        extras.putBoolean("NewUser",newUser);
        extras.putStringArrayList("Custom",customPages);
        extras.putStringArrayList("CustomValue",customValues);
        extras.putBoolean("IsTester",isTester);
        extras.putIntegerArrayList("CustomPages1",customPage1);
        extras.putIntegerArrayList("CustomPages2",customPage2);
        extras.putDoubleArray("PricePerFile",pricePerFile);
        extras.putDouble("TotalPrice",totalPrice);

        intent.putExtras(extras);
        startActivity(intent);
    }





    protected void alertMessage(String message, final boolean isImage) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        if(isImage) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(Intent.createChooser(intent, "Select Images"), 1);
                            dialog.dismiss();
                        }else{
                            Intent intent = new Intent();
                            intent.setType("application/*");
                            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);



                            startActivityForResult(Intent.createChooser(intent, "Select files"), 1);
                        }

                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }



}
