package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

class AllPagesInfo{
    String colorType, url;
    int copies;

    public AllPagesInfo(String colorType,int copies,String url){
        this.colorType = colorType;
        this.copies = copies;
        this.url = url;
    }

}

public class PageInfo extends AppCompatActivity {

    ProgressDialog mProgressDialog;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    int ShopsCnt=0;
    String username,email;
    long num;

    AllPagesInfo[] allInfo = new AllPagesInfo[1000000];
//    ArrayList<String> allInfo = new ArrayList<String>() ;
    ImageView Page;
    ArrayList<String> pageURL = new ArrayList<>();
    ArrayList<Bitmap> images = new ArrayList<>();


    View black_white,colors,h,v;
    Button sameForAll, pageBtn;
    ImageButton crop;
    ToggleButton colorType;
    Spinner pageSizeSpinner;

    //    ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    EditText copies;
    Page_Info info = new Page_Info();

    ArrayList<Integer> pageCopies = new ArrayList<Integer>();
    ArrayList<String> colorTypes = new ArrayList<String>();
    int copy;
    String colour;
    String fileType, pagesize,orientation;

    String[] uri = new String[1000];
    int cnt = 0;
    ArrayList<String> storeID = new ArrayList<>();
    final boolean[] color = new boolean[1];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_info);
//        getSupportActionBar().hide();


        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pageURL = extras.getStringArrayList("URLS");
//        pageURL = extras.getParcelableArrayList("URLS");

        username = extras.getString("username");
        email = extras.getString("email");
        num = extras.getLong("num");
        fileType = extras.getString("FileType");
        images = extras.getParcelableArrayList("Images");


        colors = findViewById(R.id.colors);
        black_white = findViewById(R.id.black_white);
        Page = (ImageView) findViewById(R.id.imageView);
        pageBtn = findViewById(R.id.Page);
        copies = findViewById(R.id.CopiesText);
//        crop = findViewById(R.id.Crop);
        h = findViewById(R.id.h);
        v = findViewById(R.id.v);
        pageSizeSpinner = findViewById(R.id.pageSizesDropDown);
        final String[] items = new String[]{"A4", "A3", "A2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        pageSizeSpinner.setAdapter(adapter);
        pagesize = items[pageSizeSpinner.getSelectedItemPosition()];




        colors.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                colour = ("Colors");
                colors.setBackgroundResource(R.drawable.colors_border);
                black_white.setBackgroundResource(R.drawable.black_white_view_backgroud);
//                colors.setBackgroundColor(Color.parseColor("#FA9A0A"));
//                bwTV.setBackgroundColor(Color.parseColor("#10000000"));
                return false;
            }
        });

        black_white.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {0x000000,0x616061});
                Log.d("Black/White","Pressed");

                colour = ("Black/White");
                black_white.setBackgroundResource(R.drawable.b_w_border);
                colors.setBackgroundResource(R.drawable.colors_view_background);
//                bwTV.setBackgroundColor(Color.parseColor("#616061"));
//                colorsTV.setBackgroundColor(Color.parseColor("#10000000"));
                return false;
            }
        });




        //Crop button
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String image = String.valueOf(Uri.parse(pageURL.get(0)));
                cropImage(image);
            }
        });

//      Log.d("URLS are ", String.valueOf(pageURL));

        if(pageURL.size() == 1){
            pageBtn.setText("Done");
            new DownloadImage().execute(pageURL.get(0));
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), (pageURL.get(0)));
//                Page.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        }else{
            pageBtn.setText("Next page");
            new DownloadImage().execute(pageURL.get(0));
//            try {
////                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(pageURL.get(0)));
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), (pageURL.get(0)));
//                Page.setImageBitmap(bitmap);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }


        copies.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                info.cnt+=1;
//                Log.d("COPIES FILLED ","YAYYY");
                Log.d("COPIES ARE",copies.getText().toString());
//                copy = (Integer.parseInt(copies.getText().toString()));
                return false;
            }
        });

        h.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                h.setBackgroundResource(R.drawable.orientation_after_clicked);
                v.setBackgroundResource(R.drawable.orientation);
                orientation = "h";

                return false;
            }
        });

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                v.setBackgroundResource(R.drawable.orientation_after_clicked);
                h.setBackgroundResource(R.drawable.orientation);
                orientation = "v";
                return false;
            }
        });


        pageBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
         if(info.page_cnt==pageURL.size()){
                getShopsCount();
//                 Intent intent = new Intent(PageInfo.this,ShopsActivity.class);
//                 Bundle extras = new Bundle();
//                 extras.putStringArrayList("URLS",pageURL);
//                 extras.putInt("Copies",copy);
//                 extras.putString("ColorTypes",colour);
//                 extras.putInt("ShopCount",ShopsCnt);
//                 extras.putString("FileType",fileType);
//                 extras.putStringArrayList("StoreID",storeID);
//                 intent.putExtras(extras);
//                 startActivity(intent);

         }else {
                   cnt++;

             if (info.cnt > 1) {
                 copy = (Integer.parseInt(copies.getText().toString()));

                 if (info.black == false) {
                     colour = "Colors";
                 } else {
                     colour = "Black/White";
                 }
                   info.page_cnt++;
//                 Log.d("FIRSTIMG", String.valueOf(images.get(info.page_cnt)));

                 if(pageURL.size()>1 && info.page_cnt < pageURL.size()){
                       new DownloadImage().execute(pageURL.get(info.page_cnt));
//                         Uri uri = MediaStore.Images.Media.getContentUri(String.valueOf(pageURL.get(info.page_cnt)));
//                         Page.setImageURI(uri);

                   }else{
                     getShopsCount();
//                     Intent intent = new Intent(PageInfo.this, ShopsActivity.class);
//                     Bundle extras = new Bundle();
//                     extras.putStringArrayList("URLS", pageURL);
//                     extras.putInt("Copies", copy);
//                     extras.putString("ColorTypes", colour);
//                     extras.putInt("ShopCount", ShopsCnt);
//                     extras.putString("FileType",fileType);
//                     intent.putExtras(extras);
//                     startActivity(intent);
                   }


                 if(info.page_cnt==pageURL.size()-1){
                     pageBtn.setText("Done");
                 }
             } else {
                 showErrorDialog("Please select all fields");
             }
         }

           }

       });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(PageInfo.this,Select.class);
        intent.putExtra("StopAnimation",true);
        startActivity(intent);
        finish();


    }

    private void cropImage(String uri){
//        Intent cropIntent = new Intent("com.android.camera.action.CROP")
//        cropIntent.setDataAndType(Uri.parse(uri),"image/*");

        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        editIntent.setDataAndType(Uri.parse(uri), "image/*");
        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(editIntent, null));

//        Page.setImageURI(null);
//        Page.setImageURI(Uri.parse(uri));

//        cropIntent.putExtra("crop",true);
//        cropIntent.putExtra("outputX",180);
//        cropIntent.putExtra("outputY",180);
//        cropIntent.putExtra("aspectX",3);
//        cropIntent.putExtra("aspectY",4);
//        cropIntent.putExtra("scaleUpIfNeeded",true);
//        cropIntent.putExtra("return-data",true);
//        startActivityForResult(editIntent,1);

    }

    private void getShopsCount(){
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("Stores")){
                    ShopsCnt = (int) dataSnapshot.getChildrenCount();
                    for(DataSnapshot ids: dataSnapshot.getChildren()){
                        storeID.add(ids.getKey());
                    }

                }

             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     Intent intent = new Intent(PageInfo.this, ShopsActivity.class);
                     Bundle extras = new Bundle();
                     extras.putStringArrayList("URLS", pageURL);
//                     extras.putParcelableArrayList("URLS", pageURL);

                     extras.putInt("Copies", copy);
                     extras.putString("ColorTypes", colour);
                     extras.putInt("ShopCount", ShopsCnt);
                     extras.putString("FileType",fileType);
//                     extras.putString("username",username);
//                     extras.putString("email",email);
//                     extras.putLong("num", (num));
                     extras.putString("PageSize",pagesize);
                     extras.putStringArrayList("StoreID",storeID);
                     extras.putString("Orientation",orientation);

                     intent.putExtras(extras);
                     startActivity(intent);


                 }
             },200);


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


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(PageInfo.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Retreiving Image");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
//                Page.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
//            if(result!=null){
            Page.setImageBitmap(result);
//            }else{
//            }
            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }



    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
