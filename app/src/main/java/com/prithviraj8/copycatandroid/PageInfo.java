package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
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
    ImageView Page,leftarr,rightarr;
    TextView previmgTV,nextimgTV;
    ArrayList<String> pageURL = new ArrayList<>();
    ArrayList<Bitmap> images = new ArrayList<>();
    Intent data;

    View black_white,colors,h,v;

    Button sameForAll, pageBtn,prevImg,nextimg;
    ImageButton crop;
    ToggleButton bothSidePrint;
    Spinner pageSizeSpinner;

    //    ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    EditText copies;
    Page_Info info = new Page_Info();

    ArrayList<Integer> pageCopies = new ArrayList<Integer>();
    ArrayList<String> colorTypes = new ArrayList<String>();
    int copy =1;
    String colour;
    String fileType, pagesize,orientation;

    int cnt = 0;
    ArrayList<String> storeID = new ArrayList<>();
    final boolean[] color = new boolean[1];
    boolean setDefaultVal = false, bothSides = false;
    private int shortAnimationDuration;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_info);
//        getSupportActionBar().hide();


        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pageURL = extras.getStringArrayList("URLS");
//        pageURL = extras.getParcelableArrayList("URLS");
        Log.d("URLSIZE", String.valueOf(pageURL.size()));

        username = extras.getString("username");
        email = extras.getString("email");
        num = extras.getLong("num");
        fileType = extras.getString("FileType");
        images = extras.getParcelableArrayList("Images");
        data = extras.getParcelable("Data");


        colors = findViewById(R.id.colors);
        black_white = findViewById(R.id.black_white);

        Page = (ImageView) findViewById(R.id.imageView);
        pageBtn = findViewById(R.id.imagesDone);
        copies = findViewById(R.id.CopiesText);
//        crop = findViewById(R.id.Crop);
        h = findViewById(R.id.h);
        v = findViewById(R.id.v);
        prevImg = findViewById(R.id.previousImg);
        nextimg = findViewById(R.id.nextImg);
        previmgTV = findViewById(R.id.previmageTV);
        nextimgTV = findViewById(R.id.nextimageTV);
        leftarr = findViewById(R.id.leftarr);
        rightarr = findViewById(R.id.rightarr);
        bothSidePrint = findViewById(R.id.bothSidesToggle);

        bothSidePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    bothSides = true;
                } else {
                    // The toggle is disabled
                    bothSides = false;
                }
            }
        });

        pageSizeSpinner = findViewById(R.id.pageSizesDropDown);
        final String[] items = new String[]{"A4", "A3", "A2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        pageSizeSpinner.setAdapter(adapter);
        pagesize = items[pageSizeSpinner.getSelectedItemPosition()];

        pageBtn.setText("Done");
//            new DownloadImage().execute(pageURL.get(0));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse((pageURL.get(0))));
            Page.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setanimation();

        colors.setOnTouchListener(touchListener);
//        colors.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                colour = ("Colors");
//                colors.setBackgroundResource(R.drawable.colors_border);
//                black_white.setBackgroundResource(R.drawable.black_white_view_backgroud);
////                colors.setBackgroundColor(Color.parseColor("#FA9A0A"));
////                bwTV.setBackgroundColor(Color.parseColor("#10000000"));
//                return false;
//            }
//        });


        black_white.setOnTouchListener(touchListener);
//        black_white.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                GradientDrawable gd = new GradientDrawable(
//                        GradientDrawable.Orientation.LEFT_RIGHT,
//                        new int[] {0x000000,0x616061});
//                Log.d("Black/White","Pressed");
//
//                colour = ("Black/White");
//                black_white.setBackgroundResource(R.drawable.b_w_border);
//                colors.setBackgroundResource(R.drawable.colors_view_background);
////                bwTV.setBackgroundColor(Color.parseColor("#616061"));
////                colorsTV.setBackgroundColor(Color.parseColor("#10000000"));
//                return false;
//            }
//        });




         //Crop button
//        crop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String image = String.valueOf(Uri.parse(pageURL.get(0)));
//                cropImage(image);
//            }
//        });






        h.setOnTouchListener(touchListener);
//        h.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//
//                h.setBackgroundResource(R.drawable.orientation_after_clicked);
//                v.setBackgroundResource(R.drawable.orientation);
//                orientation = "h";
//
//                return false;
//            }
//        });

        v.setOnTouchListener(touchListener);
//        v.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                v.setBackgroundResource(R.drawable.orientation_after_clicked);
//                h.setBackgroundResource(R.drawable.orientation);
//                orientation = "v";
//                return false;
//            }
//        });


        nextimg.setOnClickListener(btnListener);
//        nextimg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(cnt >= 0 && cnt < pageURL.size()-1) {
//                    cnt++;
////                    new DownloadImage().execute(pageURL.get(cnt));
//
//                    try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse((pageURL.get(cnt))));
//                        Page.setImageBitmap(bitmap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

        prevImg.setOnClickListener(btnListener);
//        prevImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if(cnt > 0 && cnt < pageURL.size()) {
////                    cnt--;
//////                    new DownloadImage().execute(pageURL.get(cnt));
////                    try {
////                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse((pageURL.get(cnt))));
////                        Page.setImageBitmap(bitmap);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
//            }
//        });


        pageBtn.setOnClickListener(btnListener);
//        pageBtn.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               getShopsCount();
//
//
////                   cnt++;
//
//                 copy = (Integer.parseInt(copies.getText().toString()));
//
//                 if(setDefaultVal == false){
//                     showErrorDialog("Default number of copies is set to 1");
//                     copies.setText("1");
//                     setDefaultVal = true;
//                 }
//                 if (info.black == false) {
//                     colour = "Colors";
//                 } else {
//                     colour = "Black/White";
//                 }
//
//                 if(info.page_cnt==pageURL.size()-1){
//                     pageBtn.setText("Done");
//                 }
//
//
//
//           }
//
//       });

    }

    private void setanimation(){
        previmgTV.setVisibility(View.VISIBLE);
        nextimgTV.setVisibility(View.VISIBLE);
        leftarr.setVisibility(View.VISIBLE);
        rightarr.setVisibility(View.VISIBLE);
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime) + 5000;
//        leftarr.setAlpha(0f);
//        rightarr.setAlpha(0f);
//
//        leftarr.setVisibility(View.GONE);
//        rightarr.setVisibility(View.GONE);
//
//        leftarr.animate()
//                .alpha(1f)
//                .setDuration(shortAnimationDuration)
//                .setListener(null);
//
//        rightarr.animate()
//                .alpha(1f)
//                .setDuration(shortAnimationDuration)
//                .setListener(null);




        prevImg.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        prevImg.setBackgroundResource(R.drawable.status_views);
                    }
                });
        nextimg.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        nextimg.setBackgroundResource(R.drawable.status_views);
                    }
                });

        prevImg.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        nextimg.setBackgroundColor(Color.parseColor("#00FFFFFF"));


        previmgTV.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        previmgTV.setVisibility(View.GONE);
                    }
                });
        nextimgTV.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        nextimgTV.setVisibility(View.GONE);
                    }
                });
        leftarr.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        leftarr.setVisibility(View.GONE);
                    }
                });
        rightarr.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rightarr.setVisibility(View.GONE);
                    }
                });
    }

    private View.OnTouchListener  touchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if(view == findViewById(R.id.h)){
                h.setBackgroundResource(R.drawable.orientation_after_clicked);
                v.setBackgroundResource(R.drawable.orientation);
                orientation = "h";
            }
            if(view == findViewById(R.id.v)){
                v.setBackgroundResource(R.drawable.orientation_after_clicked);
                h.setBackgroundResource(R.drawable.orientation);
                orientation = "v";
            }
            if(view == findViewById(R.id.black_white)){

                Log.d("BW","PRESSED");
                colour = ("Black/White");
                black_white.setBackgroundResource(R.drawable.b_w_border);
                colors.setBackgroundResource(R.drawable.black_white_view_backgroud);

            }
            if(view == findViewById(R.id.colors)){

                Log.d("CS","PRESSED");
                colour = ("Colors");
                colors.setBackgroundResource(R.drawable.b_w_border);
                black_white.setBackgroundResource(R.drawable.black_white_view_backgroud);

            }

            return false;
        }
    };

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v == findViewById(R.id.previousImg)){
                Log.d("PREV",pageURL.get(cnt));
                if(cnt > 0 && cnt < pageURL.size()) {
                    cnt--;
//                    new DownloadImage().execute(pageURL.get(cnt));
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse((pageURL.get(cnt))));
                        Page.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(v == findViewById(R.id.nextImg)){
                Log.d("NEXT",pageURL.get(cnt));

                if(cnt >= 0 && cnt < pageURL.size()-1) {
                    cnt++;
//                    new DownloadImage().execute(pageURL.get(cnt));
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse((pageURL.get(cnt))));
                        Page.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(v == findViewById(R.id.imagesDone)){
                getShopsCount();

                copy = (Integer.parseInt(copies.getText().toString()));

                if(!setDefaultVal){
//                    showErrorDialog("Default number of copies is set to 1");
                    copies.setText("1");
                    setDefaultVal = true;
                }
                if (!info.black) {
                    colour = "Colors";
                } else {
                    colour = "Black/White";
                }

                if(info.page_cnt==pageURL.size()-1){
                    pageBtn.setText("Done");
                }

            }

        }
    };




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


    }

    private void getShopsCount(){
       new getShops().execute();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private class getShops extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(PageInfo.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Gathering nearby shops.");
            // Set progressdialog message
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.getKey().equals("Stores")){
                        ShopsCnt = (int) dataSnapshot.getChildrenCount();
                        for(DataSnapshot ids: dataSnapshot.getChildren()){
                            storeID.add(ids.getKey());
                        }
                        if(ShopsCnt>0) {
                            Intent intent = new Intent(PageInfo.this, ShopsActivity.class);
                            Bundle extras = new Bundle();
                            extras.putStringArrayList("URLS", pageURL);
//                            extras.putParcelableArrayList("URLS", pageURL);
                            extras.putInt("Copies", copy);
                            extras.putString("ColorType", colour);
                            extras.putInt("ShopCount", ShopsCnt);
                            extras.putString("FileType", fileType);
                            extras.putString("PageSize", pagesize);
                            extras.putStringArrayList("StoreID", storeID);
                            extras.putString("Orientation", orientation);
                            extras.putBoolean("BothSides",bothSides);

                            intent.putExtras(extras);
                            mProgressDialog.dismiss();
                            startActivity(intent);

                        }else{
                            Context context = getApplicationContext();
                            CharSequence text ="Gathering nearby shops." ;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            mProgressDialog.dismiss();
                        }
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
            return null;
        }
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
