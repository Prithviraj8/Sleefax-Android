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
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    AllPagesInfo[] allInfo = new AllPagesInfo[1000000];
//    ArrayList<String> allInfo = new ArrayList<String>() ;
    ImageView Page;
    ArrayList<String> pageURL = new ArrayList<String>();

    View black_white,colors;
    Button sameForAll, pageBtn;
    ImageButton crop;

//    ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    EditText copies;
    Page_Info info = new Page_Info();

    ArrayList<Integer> pageCopies = new ArrayList<Integer>();
    ArrayList<String> colorTypes = new ArrayList<String>();
    String[] uri = new String[1000];
    int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_info);


        Page = (ImageView) findViewById(R.id.imageView);
        black_white = findViewById(R.id.Black_White);
        colors = findViewById(R.id.Colors);
        sameForAll = findViewById(R.id.SameForAll);
        pageBtn = findViewById(R.id.Page);
        copies = findViewById(R.id.CopiesText);
        crop = findViewById(R.id.Crop);


        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pageURL = extras.getStringArrayList("URLS");
        uri = (extras.getStringArray("URI"));
        Log.d("URIIII", String.valueOf(uri));
//        images = intent.getParcelableArrayListExtra("Pages");

        //Crop button
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage(uri[cnt]);
            }
        });

        Log.d("URLS are ", String.valueOf(pageURL));

        if(pageURL.size() == 1){
            pageBtn.setText("Done");
        }else{
            pageBtn.setText("Next page");

        }
        if(info.cnt == 0){
        new DownloadImage().execute(pageURL.get(0));
        }

        copies.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                info.cnt+=1;
//                Log.d("COPIES FILLED ","YAYYY");
                Log.d("COPIES ARE",copies.getText().toString());
                return false;
            }
        });

        pageBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(info.page_cnt==pageURL.size()){
                   Intent intent = new Intent(PageInfo.this,ShopsActivity.class);
//                   intent.putParcelableArrayListExtra("FilesInfo",allInfo);
                   intent.putExtra("URLS",pageURL);
                   intent.putExtra("Copies",pageCopies);
                   intent.putExtra("ColorTypes",colorTypes);
                   getShopsCount();
                   intent.putExtra("ShopCount",ShopsCnt);
                   startActivity(intent);
               }else{
                   cnt++;

            if(info.cnt>2){
                pageCopies.add(Integer.parseInt(copies.getText().toString()));

               if(info.colorType == true){
                   allInfo[info.page_cnt] = new AllPagesInfo("Colors",Integer.parseInt(copies.getText().toString()), pageURL.get(info.page_cnt));
                   colorTypes.add(info.page_cnt,"Colors");
               }else {
                   allInfo[info.page_cnt] = new AllPagesInfo("Black/White", Integer.parseInt(copies.getText().toString()), pageURL.get(info.page_cnt));
                   colorTypes.add(info.page_cnt,"Black/White");

               }

               info.page_cnt++;

               if(pageURL.size()>1 && info.page_cnt < pageURL.size()){
                   new DownloadImage().execute(pageURL.get(info.page_cnt));
               }else{
                   Intent intent = new Intent(PageInfo.this,ShopsActivity.class);
//                   intent.putParcelableArrayListExtra("FilesInfo",allInfo);
                   intent.putExtra("URLS",pageURL);
                   intent.putExtra("Copies",pageCopies);
                   intent.putExtra("ColorTypes",colorTypes);
                   getShopsCount();
                   intent.putExtra("ShopCount",ShopsCnt);
                   startActivity(intent);
               }
               if(info.page_cnt==pageURL.size()-1){
                   pageBtn.setText("Done");
               }

            }else {
                showErrorDialog("Please select all fields");
            }

               colors.setBackgroundColor(0x10FFFFFF);
               black_white.setBackgroundColor(0x10FFFFFF);
            }
           }

       });

        colors.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {0xFA9A0A,0xD15DF8});
                Log.d("Colors","Pressed");
                info.colorType = true;
                info.cnt++;
                colors.setBackgroundColor(Color.parseColor("#FA9A0A"));
                black_white.setBackgroundColor(Color.parseColor("#10000000"));
                return false;
            }
        });
        black_white.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v,MotionEvent event) {
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {0x000000,0x616061});
                Log.d("Black/White","Pressed");
                info.colorType = false;
                info.cnt++;
                black_white.setBackgroundColor(Color.parseColor("#616061"));
                colors.setBackgroundColor(Color.parseColor("#10000000"));
                return false;

            }
        });
    }

    private void cropImage(String uri){
//        Intent cropIntent = new Intent("com.android.camera.action.CROP")
//        cropIntent.setDataAndType(Uri.parse(uri),"image/*");

        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        editIntent.setDataAndType(Uri.parse(uri), "image/*");
        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.d("URIIS",uri);
        startActivity(Intent.createChooser(editIntent, null));

        Page.setImageURI(null);
        Page.setImageURI(Uri.parse(uri));

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
                ShopsCnt = (int) dataSnapshot.getChildrenCount();
                Log.d("Shops cnt ", String.valueOf(ShopsCnt));

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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            Page.setImageBitmap(result);
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
