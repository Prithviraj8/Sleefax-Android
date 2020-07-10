package com.Anubis.Sleefax;

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
import android.widget.ScrollView;
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

class AllPagesInfo {

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
    String userId;
    int ShopsCnt=0;
    String username,email;
    long num;
    boolean isTester,newUser;


    AllPagesInfo[] allInfo = new AllPagesInfo[1000000];
//    ArrayList<String> allInfo = new ArrayList<String>() ;
    ImageView Page;
    TextView previmgTV,nextimgTV;
    ArrayList<String> pageURL = new ArrayList<>();
    ArrayList<Bitmap> images = new ArrayList<>();
    Intent data;

    View black_whiteV,colorsV,h,v;
    ScrollView scrollView;
    Button  pageBtn;
    ImageButton crop,back,prevImg,nextimg,scrollDown;
    ToggleButton bothSidePrint;
    Spinner pageSizeSpinner, orientSpinner;

    //    ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    EditText copyTV;
//    Page_Info info = new Page_Info();

    int copy =1;
    String colour,fileType, pagesize,orientation,shopType;

    int cnt = 0,imgCnt;
    ArrayList<String> storeID = new ArrayList<>();
    boolean setDefaultVal = false;
    private int shortAnimationDuration;

    ArrayList<String> pdfURL = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<String> customValues = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_info);
//        getSupportActionBar().hide();

        imgCnt =0;


        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pageURL = extras.getStringArrayList("URLS");
        bothSides = new boolean[pageURL.size()];
        bothSides[imgCnt] = false;
//        pageURL = extras.getParcelableArrayList("URLS");
//        Log.d("URLSIZE", String.valueOf(pageURL.size()));

        username = extras.getString("username");
        email = extras.getString("email");
        num = extras.getLong("num");
        fileTypes = extras.getStringArrayList("FileType");
        Log.d("FILETYPEIMAGE",fileTypes.get(0));
        images = extras.getParcelableArrayList("Images");
        newUser = extras.getBoolean("NewUser");
        if(!newUser){
            userId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        scrollView = findViewById(R.id.scrollViewImages);
        scrollDown = findViewById(R.id.scrollBtn);
        back = findViewById(R.id.back);
        colorsV = findViewById(R.id.colors);
        black_whiteV = findViewById(R.id.black_white);
        Page = (ImageView) findViewById(R.id.imageView);
        pageBtn = findViewById(R.id.imagesDone);
        copyTV = findViewById(R.id.CopiesText);
        orientSpinner = findViewById(R.id.orientationSpinner);
        pageSizeSpinner = findViewById(R.id.pageSizesDropDown);
        isTester = extras.getBoolean("IsTester");

        if(isTester){
            shopType = "TestStores";
        }else{
            shopType = "Stores";
        }
//        crop = findViewById(R.id.Crop);

        prevImg = findViewById(R.id.previousImg);
        nextimg = findViewById(R.id.nextImg);
        previmgTV = findViewById(R.id.previmageTV);
        nextimgTV = findViewById(R.id.nextimageTV);

        bothSidePrint = findViewById(R.id.bothSidesToggle);


//Get data from another app
        Intent shareIntent = getIntent();
        String action = shareIntent.getAction();
        String type = shareIntent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/*")) { // When type is 'image/*'
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                pageURL.add(imageUri.getPath());
            }
        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PageInfo.this,Select.class);
                startActivity(intent1);
                finish();
            }
        });

        bothSidePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.d("ENABLEDD", String.valueOf(isChecked));
                    bothSides[cnt] = true;
                } else {
                    // The toggle is disabled
                    bothSides[cnt] = false;
                    Log.d("ENABLEDD", String.valueOf(isChecked));

                }
            }
        });

        final String[] items = new String[]{"A4", "A3", "A2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        pageSizeSpinner.setAdapter(adapter);


        final String[] orientations = new String[]{"Portrait", "Landscape"};
        ArrayAdapter<String> orientationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, orientations);
        orientSpinner.setAdapter(orientationsAdapter);



        pageBtn.setText("Done");
//            new DownloadImage().execute(pageURL.get(0));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse((pageURL.get(0))));
            Page.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setanimation();

        colorsV.setOnTouchListener(touchListener);
        black_whiteV.setOnTouchListener(touchListener);

        nextimg.setOnClickListener(btnListener);
        prevImg.setOnClickListener(btnListener);
        pageBtn.setOnClickListener(btnListener);
        scrollDown.setOnClickListener(btnListener);
    }

    private void setanimation(){
        previmgTV.setVisibility(View.VISIBLE);
        nextimgTV.setVisibility(View.VISIBLE);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime) + 5000;

        if(pageURL.size() == 1) {
            prevImg.setVisibility(View.INVISIBLE);
            nextimg.setVisibility(View.INVISIBLE);
        }

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


    }

    private View.OnTouchListener  touchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {


            if(view == findViewById(R.id.black_white)){

                Log.d("BW","PRESSED");
                colour = ("Black/White");
                black_whiteV.setBackgroundResource(R.drawable.b_w_border);
                colorsV.setBackgroundResource(R.drawable.black_white_view_backgroud);

            }
            if(view == findViewById(R.id.colors)){

                Log.d("CS","PRESSED");
                colour = ("Colors");
                colorsV.setBackgroundResource(R.drawable.b_w_border);
                black_whiteV.setBackgroundResource(R.drawable.black_white_view_backgroud);

            }

            return false;
        }
    };

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == findViewById(R.id.previousImg)) {
                Log.d("PREV", pageURL.get(cnt));
                if (cnt > 0 && cnt < pageURL.size()) {
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

            if (v == findViewById(R.id.nextImg)) {
                Log.d("NEXT", pageURL.get(cnt));

                if (cnt >= 0 && cnt < pageURL.size() - 1) {
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

            if (v == findViewById(R.id.imagesDone)) {
                getShopsCount();

                copy = (Integer.parseInt(copyTV.getText().toString()));
                if (copy == 0) {
                    copy = 1;
                }
                copies.add(imgCnt, copy);

                final String[] items = new String[]{"A4", "A3", "A2"};
                pagesize = items[pageSizeSpinner.getSelectedItemPosition()];
                pageSize.add(imgCnt, pagesize);

                final String[] ots = new String[]{"Portrait", "Landscape"};
                orientation = ots[orientSpinner.getSelectedItemPosition()];
                orientations.add(imgCnt, orientation);

                if (orientation == null) {
                    orientation = "Portrait";
                }
                if (colour == null) {
                    colour = "Black/White";
                }
                colors.add(imgCnt, colour);

//                if (!info.black) {
//                    colour = "Colors";
//                } else {
//                    colour = "Black/White";
//                }

//                if (info.page_cnt == pageURL.size() - 1) {
//                    pageBtn.setText("Done");
//                }
                if(cnt == pageURL.size()-1){
                    pageBtn.setText("Done");
                }
                if (!bothSides[imgCnt]) {
                    bothSides[imgCnt] = false;
                }

                imgCnt += 1;
            }
            if (v == findViewById(R.id.scrollBtn)) {
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
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
                    if(dataSnapshot.getKey().equals(shopType)){
                        ShopsCnt = (int) dataSnapshot.getChildrenCount();
                        for(DataSnapshot ids: dataSnapshot.getChildren()){
                            storeID.add(ids.getKey());
                        }
                        if(ShopsCnt>0) {
                            Intent intent = new Intent(PageInfo.this, ShopsActivity.class);
                            Bundle extras = new Bundle();
                            extras.putStringArrayList("URLS", pageURL);
                            extras.putInt("ShopCount", ShopsCnt);
                            extras.putStringArrayList("StoreID", storeID);
                            extras.putString("Custom","All");
                            extras.putBoolean("IsTester",isTester);
                            extras.putBoolean("NewUser",newUser);
                            extras.putBooleanArray("BothSides", bothSides);
                            extras.putIntegerArrayList("Copies", copies);
                            extras.putStringArrayList("ColorType", colors);
                            extras.putStringArrayList("FileType", fileTypes);
                            extras.putStringArrayList("PageSize", pageSize);
                            extras.putStringArrayList("Orientation", orientations);

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
