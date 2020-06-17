package com.Anubis.Sleefax;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.aspose.words.LoadFormat.DOC;

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
    int shortAnimationDuration,cnt = 0;
//    double[] numberOfPages;
    ArrayList<Integer> numberOfPages = new ArrayList<>();

    Boolean isTester,newUser,selectingFile;
    PDFView pdfView;
    Button nextActivity,dismissViewPDF,selectPhotos,selectAttachment;
    View bottomNavView;
    RelativeLayout pdfViewRL;

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

        selectPhotos = findViewById(R.id.selectphotos);
        selectAttachment = findViewById(R.id.selectattachment);
        nextActivity = findViewById(R.id.nextActivity);
        dismissViewPDF = findViewById(R.id.dismissBtn);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isTester = bundle.getBoolean("IsTester");
        newUser = bundle.getBoolean("NewUser");
        selectingFile = bundle.getBoolean("File");
        Toast.makeText(this, "Selecting File "+ selectingFile, Toast.LENGTH_LONG).show();

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

        selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMessage("Press and hold an image to select multiple images",true);
            }
        });


        selectAttachment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                alertMessage("Press and hold a file to select multiple files",false);
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
                cnt += 1;
                if(cnt == uri.size()) {
                    uploadFile(uri);
                }else{
                    ViewPDF(uri);
                }
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
        pdfViewRL.setAlpha(0f);
        pdfViewRL.setVisibility(View.VISIBLE);
        pdfViewRL.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        selectAttachment.setVisibility(View.GONE);
        selectPhotos.setVisibility(View.GONE);
    }

    String mimeType,fileName,fileSize;
    ArrayList<String> mimeTypes = new ArrayList<>();
    ArrayList<String> fileSizes = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        uri.clear();
        mimeTypes.clear();

        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file

        if (requestCode == 1 && resultCode == RESULT_OK) {
            //if a file is selected


            if (data.getData() != null) {
                //uploading the file

                Uri returnUri = data.getData();
//                file = returnUri;

                mimeType = getContentResolver().getType(returnUri);

                ///Running Pagecount api
//                startPageCountApi(returnUri,mimeType);

                Log.d("MIMETYPE1",String.valueOf(mimeType));

                Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();

                fileName = (returnCursor.getString(nameIndex));
                fileNames.add(fileName);


                if(Long.toString(returnCursor.getLong(sizeIndex)).length() >= 7) {
                    fileSize = (Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 0.000001)));
                    fileSizes.add(fileSize);

                }else{
                    fileSize = (Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 00.001)));
                    fileSizes.add(fileSize);
                }
                Log.d("FNAME1",fileName);
                Log.d("FSIZE1", String.valueOf(fileSize));

                if (mimeType.contains("application")) {
                    mimeTypes.add(mimeType);

                    uri.add(returnUri);
//                    numberOfPages = new double[uri.size()];
                    if(mimeType.equals("application/pdf")){

                        ViewPDF(uri);

                    }else {
//                        uri.add(returnUri);
                        try {
                            FileInputStream inputStream = (FileInputStream) getContentResolver().openInputStream(data.getData());

                            if(fileName.contains("docx")) {

                                XWPFDocument document;
                                document = new XWPFDocument(inputStream);
                                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                                int pages = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
                                numberOfPages.add(pages);
                                Toast.makeText(this, "NOPOF DOCX " + pages, Toast.LENGTH_SHORT).show();

                            }else if(fileName.contains("doc")){
                                HWPFDocument document = new HWPFDocument(inputStream);
                                int pages = document.getSummaryInformation().getPageCount();
                                numberOfPages.add(pages);
                                Toast.makeText(this, "NOPOF DOC " + pages, Toast.LENGTH_SHORT).show();
                            }else if(fileName.contains("ppt")){

                                HSLFSlideShow show = new HSLFSlideShow(inputStream);
                                SlideShow ss = new SlideShow(show);
                                Slide[] slides = ss.getSlides();

                                int slide_count = ss.getSlides().length;
                                numberOfPages.add(slide_count);
                                Toast.makeText(this, "NOPOF PPT " + slide_count, Toast.LENGTH_SHORT).show();
                                Log.d("PPTSLIDES ",String.valueOf(slide_count));

                            }else if(fileName.contains("pptx")){
                                XMLSlideShow slideShow = new XMLSlideShow(inputStream);
                                int slides = slideShow.getSlides().length;
                                numberOfPages.add(slides);

                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }


                        ViewDoc(uri,mimeType);
//                        uploadFile(uri);
                    }
                } else {

//                     mimeTypes.add("Image");

                    if(data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
//                            mimeTypes.add("Image");
                            if (data.getClipData().getItemAt(i).getUri() != null) {
                                uri.add(data.getClipData().getItemAt(i).getUri());
                                if (i == data.getClipData().getItemCount() - 1) {
                                    uploadImg( data,uri);
                                }
                            }
                        }
//                        uri.add(returnUri);
//                        uploadImg(requestCode, resultCode, data, uri);
                    }else{
                        uri.add(returnUri);
                        uploadImg(data,uri);
                    }
                }
            } else {


                Log.d("DATA", String.valueOf(data.getClipData()));

                if(data.getClipData() != null) {

                    if(data.getClipData().toString().contains("application")) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            if (data.getClipData().getItemAt(i).getUri() != null) {

                                uri.add(data.getClipData().getItemAt(i).getUri());
                                mimeTypes.add(getContentResolver().getType(data.getClipData().getItemAt(i).getUri()));

                                Cursor returnCursor = getContentResolver().query(data.getClipData().getItemAt(i).getUri(), null, null, null, null);
                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                returnCursor.moveToFirst();


                                if(Long.toString(returnCursor.getLong(sizeIndex)).length() >= 7) {
                                    fileSizes.add(Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 0.000001)));
                                }else{
                                    fileSizes.add(Long.toString((long) ((returnCursor.getLong(sizeIndex)) * 00.001)));
                                }
                                fileNames.add(returnCursor.getString(nameIndex));


                                try {
                                    FileInputStream inputStream = (FileInputStream) getContentResolver().openInputStream(data.getClipData().getItemAt(i).getUri());
                                    if(fileNames.get(i).contains("docx")) {
                                        XWPFDocument document;
                                         document = new XWPFDocument();
                                        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                                        int pages = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
                                        numberOfPages.add(pages);
                                        Log.d("DOCXPAGES",String.valueOf(pages));
                                        Toast.makeText(this, "NOPOF DOC "+ pages, Toast.LENGTH_SHORT).show();
                                    }else if(fileNames.get(i).contains("doc")){
                                        HWPFDocument document = new HWPFDocument(inputStream);
                                        int pages = document.getSummaryInformation().getPageCount();
                                        numberOfPages.add(pages);
                                        Log.d("DOCPAGES",String.valueOf(pages));

                                        Toast.makeText(this, "NOPOF DOC " + pages, Toast.LENGTH_SHORT).show();

                                    }else if(fileNames.get(i).contains("ppt")){

                                    }else if(fileNames.get(i).contains("pptx")){

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (i == data.getClipData().getItemCount() - 1) {
//                                    numberOfPages = new double[uri.size()];
//                                    uploadFile(uri);
                                    if(mimeTypes.get(i).equals("application/pdf")) {
                                        ViewPDF(uri);
                                    }else if(mimeTypes.get(i).contains("document") || mimeTypes.get(i).contains("powerpoint")){
                                        ViewDoc(uri,mimeType);
                                    }
                                }
                            }
                        }
                    }else {
                        fileType = "image/png";

                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
//                            mimeTypes.add("Image");
                            if (data.getClipData().getItemAt(i).getUri() != null) {
                                uri.add(data.getClipData().getItemAt(i).getUri());
                                if (i == data.getClipData().getItemCount() - 1) {
                                    uploadImg(data, uri);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public void ViewPDF(ArrayList<Uri> files){

        setVisibilities();

//        String fileName = getFileName(files.get(cnt));
//        fileNames.add(cnt,fileName);

        pdfView.fromUri(files.get(cnt))
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
//                        Log.d("PDFNOP", String.valueOf(pdfView.getPageCount()));

                        nextActivity.setAlpha(0f);
                        nextActivity.setVisibility(View.VISIBLE);
                        nextActivity.animate()
                                .alpha(1f)
                                .setDuration(shortAnimationDuration)
                                .setListener(null);

                        numberOfPages.add(cnt,pdfView.getPageCount());
                    }
                })
                .load();
    }

    public void ViewDoc(ArrayList<Uri> word, String mimeType) {

        uploadFile(uri);

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


//    public class uploadFiles extends AsyncTask<Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            Intent goToPdfInfo = new Intent(Pop.this, PdfInfo.class);
//            Bundle extras = new Bundle();
//
//            extras.putDoubleArray("Pages",numberOfPages);
//            extras.putStringArrayList("FileNames",fileNames);
//            Log.d("SOZEEE",String.valueOf(fileSizes.size()));
//            extras.putStringArrayList("FileSizes",fileSizes);
//            ArrayList<String> files = new ArrayList<>();
//
//            for (int i=0;i<uri.size();i++){
////                        Toast.makeText(Pop.this,"NOP "+numberOfPages[i],Toast.LENGTH_LONG).show();
//
//                files.add(uri.get(i).toString());
//                if(i == uri.size()-1){
//                    extras.putStringArrayList("URLS", files);
//                    extras.putStringArrayList("FileType", mimeTypes);
//                    extras.putBoolean("IsTester",isTester);
//                    extras.putBoolean("NewUser",newUser);
//
//                    goToPdfInfo.putExtras(extras);
//                    startActivity(goToPdfInfo);
//                    finish();
//
//                }
//            }
//
//            return null;
//        }
//    }
//    public class uploadImages extends AsyncTask<Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            Intent goToPageInfo = new Intent(Pop.this, PageInfo.class);
//            Bundle extras = new Bundle();
//            extras.putStringArrayList("FileType", mimeTypes);
//            ArrayList<String> images = new ArrayList<>();
//            int i;
//
//            for(i=0;i<uri.size();i++){
//                images.add(uri.get(i).toString());
//
//                if(i == uri.size()-1) {
////                Log.d("URISIZE", String.valueOf(uri.size()));
//                    extras.putStringArrayList("URLS", images);
////                    extras.putParcelable("Data",data);
//                    extras.putBoolean("IsTester",isTester);
//                    extras.putBoolean("NewUser",newUser);
//
////            extras.putParcelableArrayList("URLS", uri);
//                    goToPageInfo.putExtras(extras);
//                    startActivity(goToPageInfo);
//                }
//            }
//
//            return null;
//        }
//    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFile(ArrayList uri){

        Intent goToPdfInfo = new Intent(Pop.this, PdfInfo.class);
        Bundle extras = new Bundle();

        System.out.print("NUMBER OF PAGES SIZE "+numberOfPages.size());
        extras.putIntegerArrayList("Pages",numberOfPages);
        extras.putStringArrayList("FileNames",fileNames);
        extras.putStringArrayList("FileSizes",fileSizes);
        ArrayList<String> files = new ArrayList<>();

        for (int i=0;i<uri.size();i++){
//                        Toast.makeText(Pop.this,"NOP "+numberOfPages[i],Toast.LENGTH_LONG).show();

            files.add(uri.get(i).toString());
            if(i == uri.size()-1){
                extras.putStringArrayList("URLS", files);
                extras.putStringArrayList("FileType", mimeTypes);
                extras.putBoolean("IsTester",isTester);
                extras.putBoolean("NewUser",newUser);

                goToPdfInfo.putExtras(extras);
                startActivity(goToPdfInfo);
                finish();

            }
        }


    }


    public void uploadImg(Intent data, ArrayList<Uri> uri){
        Intent goToPageInfo = new Intent(Pop.this, PageInfo.class);
        Bundle extras = new Bundle();

        ArrayList<String> images = new ArrayList<>();
        int i;
        for(i=0;i<uri.size();i++){
            images.add(uri.get(i).toString());
            mimeTypes.add("Image");
            if(i == uri.size()-1) {
                extras.putStringArrayList("FileType", mimeTypes);
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
