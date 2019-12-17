package com.prithviraj8.copycatandroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class Pop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        getSupportActionBar().hide();

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int) (height));

        Button selectPhotos = findViewById(R.id.selectphotos);
        Button selectAttachment = findViewById(R.id.selectattachment);

        selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Images"), 1);

//                Intent intent = new Intent(Pop.this,Select.class);
//                Bundle extras = new Bundle();
//                extras.putBoolean("Photos",true);
//                intent.putExtras(extras);
//                startActivity(intent);
//                finish();
            }
        });
        selectAttachment.setOnClickListener(new View.OnClickListener() {
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

    ArrayList<Uri> uri = new ArrayList<Uri>();
    String fileType;

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
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://cloudconvert.com/anything-to-pdf")));
                    uploadFile(requestCode, resultCode,returnUri);
//                    uploadFile(returnUri);
//                    Intent i = new Intent(Intent.ACTION_VIEW, returnUri);
//                    startActivity(i);
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
                    }else{
                        uri.add(returnUri);
                        uploadImg(requestCode,resultCode,data,uri);
                    }
                }
            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFile(int requestCode, int resultCode, Uri file){

        Intent goToPdfInfo = new Intent(Pop.this, PdfInfo.class);
        //goToPageInfo.putExtra("Pages",images);
        Bundle extras = new Bundle();
//        extras.putParcelable("PdfURL", file);
        Log.d("URIPASSED",file.toString());
        extras.putString("PdfURL", file.toString());
        extras.putString("FileType", fileType);
        extras.putInt("RequestCode",requestCode);
        extras.putInt("ResultCode",resultCode);
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
