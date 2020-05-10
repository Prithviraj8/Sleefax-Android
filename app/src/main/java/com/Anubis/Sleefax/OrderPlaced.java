package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

class OrderStatus{
    int progress = 0;
}




public class OrderPlaced extends AppCompatActivity {
    NotificationManagerCompat notificationManager;
//    MyFirebaseMessagingService notification = new MyFirebaseMessagingService();
//    ProgressBar orderProgress;

    final String TAG = "PathGoogleMapActivity";
    ImageButton getDirection,call,back,help;
    TextView Files, shopName,Loc,Price,status1,orderStatusTV,status3,status4,statusPercent,orderid;
    Button showFullDetails;
    CircularProgressBar orderProgress;

    String name,loc,orderKey,orderStatus,shopKey,fileType,pagesize,orientation,username,email,paymentMode;
    LatLng shopLoc, userLoc;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    int files;
    double price;
    int copy;
    int resultCode;
    int requestCode;
    String color,custom,orderDateTime;
    String CHANNEL_ID = "UsersChannel",shopType;
    boolean FromYourOrders =false,isTester;

    long usernum,shopNum;

    //    ShopInfo info = new ShopInfo();
    private static final int LOCATION_REQUEST = 500;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    OrderStatus obj = new OrderStatus();
//    ArrayList<String> pageURL = new ArrayList<>();
    Intent data;


    boolean mLocationPermissionGranted;
    FusedLocationProviderClient mFusedLocationProviderClient;
    DatabaseReference orderDb = FirebaseDatabase.getInstance().getReference();
    DatabaseReference storeDb = FirebaseDatabase.getInstance().getReference();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ArrayList<String> downloadUrls = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<String> customValues = new ArrayList<>();
    double numberOfPages[];
    ArrayList<String> fileNames = new ArrayList<>();



    /// Order status views///
    ImageButton stat1,stat2,stat3,stat4;


    //Order Details View
    TextView file1,file2,file3,file1Price,file2Price,file3Price;


    //Order Confirmation Layout
    RelativeLayout orderConfirmRL,TopViewBtnsRL;
    Button yes,no;




    //Scroll View Layout
    ScrollView scrollView;
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
//        getSupportActionBar().hide();

        notificationManager = NotificationManagerCompat.from(this);

//        FirebaseApp app = FirebaseApp.getInstance("Stores");
//        FirebaseDatabase DB = FirebaseDatabase.getInstance(app);
//        storeDb = DB.getReferenceFromUrl("https://storeowner-9c355.firebaseio.com/").child("users");
//        orderProgress.setProgressBarWidth((float) 12.0);

        back = findViewById(R.id.backBtn);
        scrollView = findViewById(R.id.ScrollViewL);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderPlaced.this, Select.class);
                startActivity(intent);
                finish();

            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
//        shopMap.onCreate(mapViewBundle);
//        shopMap.getMapAsync(this);

        shopName = findViewById(R.id.OrderShop);
        Loc = findViewById(R.id.OrderLoc);
        Price = findViewById(R.id.OrderPrice);
        Files = findViewById(R.id.OrderFiles);
//        orderProgress = findViewById(R.id.OrderProgressBar);
        orderStatusTV = findViewById(R.id.orderStatus);
        getDirection = findViewById(R.id.Directions);
        orderProgress = findViewById(R.id.circularProgressBar);
        statusPercent = findViewById(R.id.orderStatus);
        call = findViewById(R.id.callBtn);
        help = findViewById(R.id.helpbtn);
        orderid = findViewById(R.id.orderID);

        ///Setting up status  views..
        stat1 = findViewById(R.id.Stat1View);
        stat2 = findViewById(R.id.Stat2View);
        stat3 = findViewById(R.id.Stat3View);
        stat4 = findViewById(R.id.Stat4View);

        // Setting up Order Details //
        file1 = findViewById(R.id.File1TV);
        file2 = findViewById(R.id.File2TV);
        file3 = findViewById(R.id.File3TV);
        file1Price = findViewById(R.id.FilePrice1);
        file2Price = findViewById(R.id.FilePrice2);
        file3Price = findViewById(R.id.FilePrice3);

        //Order Confirmation RL
        orderConfirmRL = findViewById(R.id.OrderPickedUpConfirmRL);
        yes = findViewById(R.id.YesConfirm);
        no = findViewById(R.id.NoConfirm);

        TopViewBtnsRL = findViewById(R.id.TopViewBtnsRL);


        yes.setOnClickListener(BtnListener);


        call.setOnClickListener(BtnListener);
        help.setOnClickListener(BtnListener);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        data = extras.getParcelable("Data");

        //////////////////////////////////////////////////Shop Info//////////////////////////////////////////
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        name = extras.getString("ShopName");
        loc = extras.getString("Location");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
        price = extras.getDouble("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        shopKey = extras.getString("ShopKey");
        orderKey = extras.getString("OrderKey");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");


        /////////////////////////////////////////////////Order info////////////////////////////////////////


        orderKey = extras.getString("OrderKey");
        urls = extras.getStringArrayList("URLS");
        fileTypes = extras.getStringArrayList("FileType");
        pageSize = extras.getStringArrayList("PageSize");
        orientations = extras.getStringArrayList("Orientation");
        copies = extras.getIntegerArrayList("Copies");
        colors = extras.getStringArrayList("ColorType");
        bothSides = extras.getBooleanArray("BothSides");
        customPages = extras.getStringArrayList("Custom");
        numberOfPages = extras.getDoubleArray("Pages");
        fileNames = extras.getStringArrayList("FileNames");

//        fileType = extras.getString("FileType");
//        pagesize = extras.getString("PageSize");
//        orientation = extras.getString("Orientation");
        username = extras.getString("Username");
        email = extras.getString("email");
        usernum = extras.getLong("UserNumber");
        shopNum = extras.getLong("ShopNum");
//        copy = extras.getInt("Copies");
//        color = extras.getString("ColorType");
//        pageURL = extras.getStringArrayList("URLS");
//        bothSides = extras.getBoolean("BothSides");
//        custom = extras.getString("Custom");
//        numberOfPages = extras.getDouble("Pages");
        isTester = extras.getBoolean("IsTester");
        paymentMode = extras.getString("PaymentMode");

        /////////////////////////////////////////////Setting Shop Details on screen/////////////////////////
//        shopName.setText("Shop Name : " + name);
//        Loc.setText(loc);
        Price.setText("₹ " + price);
//        Files.setText("Files  :  " + files);
//        orderid.setText(extras.getString("OrderKey"));


        Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        if (stat1.getLocalVisibleRect(scrollBounds)) {
            // Any portion of the imageView, even a single pixel, is within the visible window
            Log.d("INVIEW","YESS");
        } else {
            // NONE of the imageView is within the visible window
        }


        if(fileNames != null) {
            if (fileNames.get(0) != null && fileNames.size() > 0) {
                file1.setVisibility(View.VISIBLE);
                file1.setText(fileNames.get(0));
                file1Price.setVisibility(View.VISIBLE);
            }
            if (fileNames.size() > 1 && fileNames.get(1) != null ) {
                file2.setVisibility(View.VISIBLE);
                file2.setText(fileNames.get(1));
                file2Price.setVisibility(View.VISIBLE);
            }
            if (fileNames.size() > 2 && fileNames.get(2) != null ) {
                file3.setVisibility(View.VISIBLE);
                file3.setText(fileNames.get(2));
                file3Price.setVisibility(View.VISIBLE);
            }
        }

        showFullDetails = (Button) findViewById(R.id.OrderDetailsBtn);
        showFullDetails.setPaintFlags(showFullDetails.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if(isTester){
            shopType = "TestStores";
        }else{
            shopType = "Stores";
        }
//        shopType = "TestStores";

        Log.d("STORETYPE",shopType);
        Log.d("REQUEST", String.valueOf(requestCode));
        Log.d("RESULT",String.valueOf(resultCode));
//        Log.d("PAGES",String.valueOf(numberOfPages));


        userLoc = new LatLng(userLat,userLong);
        shopLoc = new LatLng(shopLat,shopLong);

        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+userLat+","+userLong+"&daddr="+shopLat+","+shopLong));
                startActivity(intent1);
            }
        });
//        new uploadFile().execute(pdfs);

        String currentDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
        Log.d("DATE",currentDate);
        Log.d("TIME",currentTime);

        orderDateTime = currentTime +" " +currentDate;



        if(fileTypes != null) {
//            new uploadFile().execute(urls);
            setProgress(orderKey);
        }else{
            setProgress(orderKey);
//            setProgressForOrder(orderKey,orderStatus);
            orderid.setText("Order ID: "+orderKey);
        }




        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Rect scrollBounds = new Rect();
                scrollView.getDrawingRect(scrollBounds);
                if (!stat1.getLocalVisibleRect(scrollBounds)
                        || scrollBounds.height() < stat1.getHeight()) {
                    // imageView is not within or only partially within the visible window
                    Log.d("ISVIEWVIS","false");

//                    TopViewBtnsRL.setBackgroundResource(R.drawable.orderstatusnavbar_shadow);
                    TopViewBtnsRL.setElevation(10);
                } else {
                    // imageView is completely visible
                    Log.d("ISVIEWVIS","true");
//                    TopViewBtnsRL.setBackgroundColor(Color.WHITE);
                    TopViewBtnsRL.setElevation(0);

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Select.class);
        startActivity(intent);
        finish();

    }

    private boolean isViewVisible(View view) {
        Rect scrollBounds = new Rect();
        scrollView.getDrawingRect(scrollBounds);

        float top = view.getY();
        float bottom = top + view.getHeight();

        if (scrollBounds.top < top && scrollBounds.bottom > bottom) {
            Log.d("ISVIEWVIS","true");
            return true;
        } else {
            Log.d("ISVIEWVIS","false");

            return false;
        }
    }
    public void alertBox(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(OrderPlaced.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Oh! Got it",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    //Create an anonymous implementation of OnClickListener
    private View.OnClickListener BtnListener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked

            if(v == findViewById(R.id.callBtn)) {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
                Uri number = Uri.parse("tel:"+shopNum);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
//                callIntent.setData(Uri.parse("tel:" + shopNum));

                if (ActivityCompat.checkSelfPermission(OrderPlaced.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        ActivityCompat.requestPermissions(OrderPlaced.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                        Log.d("MANAGEPERMISSION", "PERMISSION");
                    return;
                }

                startActivity(callIntent);
            }else if(v == findViewById(R.id.helpbtn)){

                Intent intent = new Intent(OrderPlaced.this,settings.class);
                startActivity(intent);
//                finish();
            }else if(v == findViewById(R.id.YesConfirm)){
                final HashMap<String, Object> orderStatusUpdate = new HashMap<String, Object>();
                orderStatusUpdate.put("orderStatus", "Done");
                ref.child("users").child(userId).child("Orders").child(orderKey).updateChildren(orderStatusUpdate);

                expandView(orderConfirmRL,250,0);
            }
        }
    };

    public void setProgress(final String orderKey){


        Log.d("SETTING","PROGRESS FOR"+orderKey);
        final HashMap<String, Object> notified = new HashMap<String, Object>();
        final String[] status = {null};


        orderDb.child("users").child(userId).child("Orders").child(orderKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("VALLLLLL", dataSnapshot.getKey());
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        notified.put("P_Notified", true);
                        orderDb.child("users").child(userId).child("Orders").child(orderKey).updateChildren(notified);
//                        notified.put("RT_Notified", false);
//                        orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
//                        notified.put("IP_Notified", false);
//                        orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
//                        notified.put("R_Notified", false);
//                        orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);

                        orderStatus = String.valueOf(map.get("orderStatus"));
                        Log.d("MAPPPP", String.valueOf(map));
                        Log.d("SHOWINGYOURORDER",orderStatus);
                Toast.makeText(OrderPlaced.this, "STATUPDATED "+ orderStatus, Toast.LENGTH_SHORT).show();
                        setProgressForOrder(orderKey,orderStatus);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void setProgressForOrder(final String orderKey,String orderStatus){


        Log.d("SETTING","PROGRESS FOR"+orderKey);
        final HashMap<String, Object> notified = new HashMap<String, Object>();
        final String[] status = {null};



            if (orderStatus.equals("Placed")) {

                    orderStatusTV.setText("Yayy..Order Placed");
                stat1.setBackgroundResource(R.drawable.status_shadow1);
                stat2.setBackgroundResource(R.drawable.status_shadow2);
                stat3.setBackgroundResource(R.drawable.status_shadow2);
                stat4.setBackgroundResource(R.drawable.status_shadow2);


            } else if (orderStatus.equals("Received")) {

                    orderStatusTV.setText("Order sent.\n" + "Take a breather.");

                stat1.setBackgroundResource(R.drawable.status_shadow1);
                stat2.setBackgroundResource(R.drawable.status_shadow2);
                stat3.setBackgroundResource(R.drawable.status_shadow2);
                stat4.setBackgroundResource(R.drawable.status_shadow2);


            } else if (orderStatus.equals("In Progress")) {

                    orderStatusTV.setText("Fun fact:\n" + "3D printers can \n" + "now print food.");
                stat1.setBackgroundResource(R.drawable.status_shadow2);
                stat2.setBackgroundResource(R.drawable.status_shadow1);
                stat3.setBackgroundResource(R.drawable.status_shadow2);
                stat4.setBackgroundResource(R.drawable.status_shadow2);


            } else if (orderStatus.equals("Ready")) {

                    orderStatusTV.setText("There's no hurry, \n" + "but your files\n" +"have been printed.");
                stat1.setBackgroundResource(R.drawable.status_shadow2);
                stat2.setBackgroundResource(R.drawable.status_shadow2);
                stat3.setBackgroundResource(R.drawable.status_shadow1);
                stat4.setBackgroundResource(R.drawable.status_shadow2);

                expandView(orderConfirmRL,0,250);

            }else if (orderStatus.equals("Done")) {


                    orderStatusTV.setText("We hope we were\n" + "helpful to you 😚");
                    stat1.setBackgroundResource(R.drawable.status_shadow2);
                    stat2.setBackgroundResource(R.drawable.status_shadow2);
                    stat3.setBackgroundResource(R.drawable.status_shadow2);
                    stat4.setBackgroundResource(R.drawable.status_shadow1);

            }

        }


    public void expandView(final View v,int initialHt,int finalHt){


        ValueAnimator slideAnimator = ValueAnimator.ofInt(initialHt,finalHt).setDuration(500);
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                v.getLayoutParams().height = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                v.requestLayout();

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

    }


    ArrayList<Bitmap> images = new ArrayList<Bitmap>();


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<Integer> custOrderIDS = new ArrayList<>();




//    public class uploadFile extends AsyncTask<ArrayList<String>,Void,Void>{
//        final int[] uploadCnt = {0};
//
//        @SuppressLint("WrongThread")
//        @Override
//        protected Void doInBackground(ArrayList<String>... arrayLists) {
//
//
//            Uri uri;
//            orderProgress.setProgress(15);
////            statusPercent.setText("15%");
//
//
//
//            for(int i =0;i<urls.size();i++) {
//                String file = urls.get(i);
//                uri = Uri.parse(file);
//
//                final String uniqueID = UUID.randomUUID().toString();
//                final StorageReference filesRef = storageRef.child(uniqueID);
//
//
//
////        if (Build.VERSION.SDK_INT < 19) {
//
////      getContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                final UploadTask uploadTask = filesRef.putFile(uri);
////        final UploadTask uploadTask = filesRef.putFile(changeExtension(new File(file.getPath()),"pdf"));
//                final int finalI = i;
//                final Uri finalUri = uri;
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful uploads
//                        Log.d("UPLOAD", "Not successfull");
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                        Log.d("UPLOAD", "SUCCESSFULL");
//                        Toast.makeText(OrderPlaced.this, "Files are being sent", Toast.LENGTH_SHORT).show();
//
//                        Log.d("UNIQUE", uniqueID);
//                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                            @Override
//                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                if (!task.isSuccessful()) {
//                                    throw task.getException();
//                                }
//                                Log.d("URIID", String.valueOf(finalUri));
//
//                                uploadCnt[0]++;
//                                // Continue with the task to get the download URL
//                                return filesRef.getDownloadUrl();
//                            }
//                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//
//                            @RequiresApi(api = Build.VERSION_CODES.N)
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//
//                                if (task.isSuccessful()) {
//                                    String url;
//                                    Uri downloadUri = task.getResult();
//                                    url = String.valueOf(downloadUri);
//                                    downloadUrls.add(url);
//                                    Log.d("DOWNLOADURL", String.valueOf(url));
//
//                                    if(urls.size() == downloadUrls.size()) {
//
//
//                                        Log.d("PAGESIZEGEE",pagesize);
//                                        Log.d("CUSTOM",custom);
//                                        Log.d("ORIENTATION",orientation);
//                                        Log.d("COLOR",color);
//                                        Log.d("COPY", String.valueOf(copy));
//                                        String orderID = UUID.randomUUID().toString();
//
//
//                                        int id = 0,custorderID=0;
//                                        if(ids.size()>0 && custOrderIDS.size()>0){
////                                            Log.d("IDS",String.valueOf(ids.get(2)));
//                                            id = ids.get(ids.size()-1)+1;
//                                            custorderID = custOrderIDS.get(ids.size()-1)+1;
//
//                                        }
//
//
//                                        if(custom == "" || custom == null){
//                                            custom = "All";
//                                        }
//
//                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
//                                        Log.d("CUSTORDERID", String.valueOf(custorderID));
//                                        shopinfo orderInfo = new shopinfo(loc, name, "Placed", shopLat, shopLong, shopNum, files, fileType, pagesize, orientation, price, custom, orderDateTime, true, false, false, false,false,paymentMode,custorderID);
//                                        info userinfo = new info(username, email, usernum, "android", "Placed", fileType, copy, orderDateTime, id,custom,price,bothSides,paymentMode);
//
//                                        String storeID = shopKey;
//
//                                        storeDb = storeDb.child(shopType).child(storeID).child("Orders").child(userId).child(orderID);
//                                        storeDb.setValue(userinfo);
//                                        db = db.child("users").child(userId).child("Orders").child(storeID).child(orderID);
//                                        db.setValue(orderInfo);
//                                        orderid.setText("Order ID: "+orderID);
//
//
//
//
//                                        for(int k =0;k<downloadUrls.size();k++) {
//
//                                            singlePageInfo single = new singlePageInfo(downloadUrls.get(k), color, copy, fileType, pagesize, orientation);
//                                            db.push().setValue(single);
//                                            storeDb.push().setValue(single);
//                                            orderKey = orderID;
//
//
//
//                                            if (k == downloadUrls.size() - 1) {
//                                                Toast.makeText(OrderPlaced.this, "Files are being sent", Toast.LENGTH_SHORT).show();
////                                                new setProgressForOrder().execute(orderKey);
//                                                setProgressForOrder(orderKey);
//
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    // Handle failures
//                                    // ...
//                                }
//                            }
//                        });
//                        // ...
//                    }
//                });
//
//            }
//            return null;
//
//        }
//
////        @Override
////        protected void onPostExecute(Void aVoid) {
////            super.onPostExecute(aVoid);
////            Toast.makeText(getApplicationContext(),"Thank you!😁",Toast.LENGTH_SHORT).show();
////        }
//    }



    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
