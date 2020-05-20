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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

    private static final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";

    int shortAnimationDuration,cnt = 0;
    double[] numberOfPages;

    Boolean isTester,newUser;
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


                    if(mimeType.equals("application/pdf")){
                        uri.add(returnUri);
                        numberOfPages = new double[uri.size()];

                        ViewPDF(uri);

                    }else {
                        uri.add(returnUri);
//                        ViewDoc(uri);

                        uploadFile(uri);
                    }
                } else {

                    Log.d("FILE", "Image");
                    fileType = mimeType;

                    if(data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            mimeTypes.add(fileType);
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

                                Log.d("FNAME3",fileNames.get(i));
                                Log.d("FSIZE3",String.valueOf(fileSizes.get(i)));

//                                Toast.makeText(this, "FILE " + uri.get(i).toString(), Toast.LENGTH_LONG).show();
//                                Toast.makeText(this, "MIME " + mimeTypes.get(i), Toast.LENGTH_LONG).show();

                                if (i == data.getClipData().getItemCount() - 1) {
                                    numberOfPages = new double[uri.size()];uploadFile(uri);
                                    if(mimeTypes.get(i).equals("application/pdf")) {
                                        ViewPDF(uri);
                                    }
                                }
                            }
                        }
                    }else {
                        fileType = "image/png";

                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            mimeTypes.add(fileType);
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

                        numberOfPages[cnt]=(pdfView.getPageCount());
                    }
                })
                .load();
    }

    public void ViewDoc(Uri word) {

        Log.d("FILETYPESELE",fileType);
        uploadFile(uri);
//        File file = new File(Environment.getExternalStorageDirectory(), "Report.pdf");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(word, mimeType);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        this.startActivity(intent);
//        startActivity(Intent.createChooser(intent, "Choose an Application:"));


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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFile(ArrayList uri){

        Intent goToPdfInfo = new Intent(Pop.this, PdfInfo.class);
        Bundle extras = new Bundle();

        extras.putDoubleArray("Pages",numberOfPages);
        extras.putStringArrayList("FileNames",fileNames);
        Log.d("SOZEEE",String.valueOf(fileSizes.size()));
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
        extras.putStringArrayList("FileType", mimeTypes);
        ArrayList<String> images = new ArrayList<>();
        int i;

        for(i=0;i<uri.size();i++){
            images.add(uri.get(i).toString());

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


    ProgressDialog progress;

    public void startPageCountApi(final Uri returnUri, final String contentType){
        progress = new ProgressDialog(Pop.this);
        progress.setTitle("Uploading");
        progress.setMessage("Please wait...");
        progress.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d("STARTINGAPI","YES");
                File f  = new File(returnUri.getPath());
//                    Toast.makeText(MainActivity.this, "PATH "+FilePickerActivity.RESULT_FILE_PATH, Toast.LENGTH_SHORT).show();
//                    File f= new File(data.getDataString());
                String content_type  = contentType;
//                    String content_type  = getMimeType(data.getData().toString());

                String file_path = f.getAbsolutePath();
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type",content_type)
                        .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                        .build();

                Request request = new Request.Builder()
                        .url("https://www.sleefax.com/test/upload.php")
                        .post(request_body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    if(!response.isSuccessful()){
                        throw new IOException("Error : "+response);
                    }

                    progress.dismiss();
                    sendjsonrequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        t.start();

    }

    String url = "https://www.sleefax.com/test/page_count.php";
    String count;
    RequestQueue rq;

    public void sendjsonrequest(){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    count = response.getString("PageCount");
                    Log.i("PageCount",count);
//                    countText.setText(count);
                    Log.d("COUNTIS ",String.valueOf(count));
                    Log.d("RESPONSE ",String.valueOf(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                count = error.getMessage();

            }
        });
    };


}
