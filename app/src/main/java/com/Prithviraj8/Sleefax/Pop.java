package com.Prithviraj8.Sleefax;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aspose.words.Document;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Pop extends AppCompatActivity {
    private static final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";

    int numberOfPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        getSupportActionBar().setBackgroundDrawable(R.drawable.col);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int) (height));

        Button selectPhotos = findViewById(R.id.selectphotos);
        Button selectAttachment = findViewById(R.id.selectattachment);

        selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
//                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Images"), 1);

//                intent.setComponent(ComponentName.unflattenFromString("com.google.android.apps.plus.photos"));
//                intent.addCategory("android.intent.category.LAUNCHER");
//                intent.setPackage("com.google.android.apps.plus");
//                startActivity(intent);
//                startActivityForResult(Intent.createChooser(intent,"Select files",GOOGLE_PHOTOS_PACKAGE_NAME));

//                Intent intent = new Intent(Pop.this,Select.class);
//                Bundle extras = new Bundle();
//                extras.putBoolean("Photos",true);
//                intent.putExtras(extras);
//                startActivity(intent);
//                finish();
            }
        });


        selectAttachment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select files"), 1);

//                Intent intent = new Intent(Pop.this,Select.class);
//                Bundle extras = new Bundle();
//                extras.putBoolean("Photos",false);
//                intent.putExtras(extras);
//                startActivity(intent);
//                finish();
            }
        });
    }


    ArrayList<Uri> uri = new ArrayList<>();
    String fileType;
    String fileName;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        uri.clear();

        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
//        Log.d("FILE CHOSEN",data.getData().toString());
//        Log.d("REQUESTCODE", String.valueOf(requestCode));

        if (requestCode == 1 && resultCode == RESULT_OK) {
            //if a file is selected


            if (data.getData() != null) {
                //uploading the file

                Uri returnUri = data.getData();
//                uri.add(returnUri);


                Log.d("URIID", String.valueOf(returnUri));
                String mimeType = getContentResolver().getType(returnUri);
                Log.d("MIME", mimeType);

                if (mimeType.contains("application")) {

                    Log.d("FILE", mimeType);
                    fileType = mimeType;

                    uploadFile(requestCode, resultCode,returnUri);

//                    if (returnUri.getScheme().equals("file")) {
//                        fileName = returnUri.getLastPathSegment();
//
//                        try {
//                            PdfReader reader = new PdfReader(fileName);
//                            int numPages = reader.getNumberOfPages();
//                            Log.d("PDFPAGES",String.valueOf(numPages));
//                        } catch (IOException e) {
//                            Log.d("CANT","GET");
//                            e.printStackTrace();
//                        }
//
//                    } else {
//                        Cursor cursor = null;
//
//                        try {
//
//                            cursor = getContentResolver().query(returnUri, new String[]{
//                                    MediaStore.Images.ImageColumns.DISPLAY_NAME
//                            }, null, null, null);
//
//                            if (cursor != null && cursor.moveToFirst()) {
//
//                                fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
//                                Log.d("FILENAME",fileName);
//
//                                try {
//
////                                  Document pdfDocument = new Document(fileName);
//
////                                    PDDocument doc = PDDocument.load(new File(fileName));
////                                    Log.d("PDFPAGES",String.valueOf(doc.getNumberOfPages()));
//
//                                    PdfReader reader = new PdfReader(String.valueOf(returnUri));
//                                    Log.d("PDFPAGES",String.valueOf(reader.getNumberOfPages()));
//
////                                    ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
////                                    PdfRenderer reader = new PdfRenderer(parcelFileDescriptor);
//
////                                    Document pdfDocument = new Document(Objects.requireNonNull(returnUri.toString()));
//                                } catch (IOException e) {
//                                    Log.d("CANT","GET");
//                                    e.printStackTrace();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                        } finally {
//                            if (cursor != null) {
//                                cursor.close();
//                            }
//                        }
//                    }




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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFile(int requestCode, int resultCode, Uri file){

        Intent goToPdfInfo = new Intent(Pop.this, PdfInfo.class);
        //goToPageInfo.putExtra("Pages",images);
        Bundle extras = new Bundle();
//        extras.putParcelable("PdfURL", file);
        extras.putInt("Pages",numberOfPages);
        extras.putString("PdfURL", file.toString());
        extras.putString("FileType", fileType);
        extras.putInt("RequestCode",requestCode);
        extras.putInt("ResultCode",resultCode);
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
//            extras.putParcelableArrayList("URLS", uri);
                goToPageInfo.putExtras(extras);
                startActivity(goToPageInfo);
            }
        }

    }

}
