package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.UUID;

class OrderStatus{
    int progress = 0;
}


class info{

    public String name,orderStatus;
    public String email,device,fileType,orderDateTime,custom;
    public long num;
    public int copies,id;
    public double price;
    public boolean bothSides;

    public info(String name, String email, long num, String device, String placed, String fileType, int copy, String orderDateTime,int id,String custom,double price, boolean bothSides){
        this.email = email;
        this.name = name;
        this.num = num;
        this.device = device;
        this.orderStatus = placed;
        this.fileType = fileType;
        this.copies = copy;
        this.orderDateTime = orderDateTime;
        this.id = id;
        this.custom = custom;
        this.price = price;
        this.bothSides = bothSides;
    }

}

public class OrderPlaced extends AppCompatActivity {
    NotificationManagerCompat notificationManager;
//    MyFirebaseMessagingService notification = new MyFirebaseMessagingService();
//    ProgressBar orderProgress;

    final String TAG = "PathGoogleMapActivity";
    ImageButton getDirection,call,back,help;
    TextView Files, shopName,Loc,Price,status1,status2,status3,status4,statusPercent,orderid;

    CircularProgressBar orderProgress;

    String name,loc,orderKey,orderStatus,shopKey,fileType,pagesize,orientation,username,email;
    LatLng shopLoc, userLoc;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    int files;
    double price;
    int copy;
    int resultCode;
    int requestCode;
    double numberOfPages;
    String color,custom,orderDateTime;
    String CHANNEL_ID = "UsersChannel",shopType;
    boolean FromYourOrders =false, bothSides,isTester;

    long usernum,shopNum;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> downloadUrls = new ArrayList<>();

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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(obj.progress >=25) {
                    Intent intent = new Intent(OrderPlaced.this, Select.class);
                    startActivity(intent);
                    finish();
                }else{
                    alertBox("Please wait while you we process your order. ");
                }

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
        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);
        status3 = findViewById(R.id.status3);
        status4 = findViewById(R.id.status4);
        getDirection = findViewById(R.id.Directions);
        orderProgress = findViewById(R.id.circularProgressBar);
        statusPercent = findViewById(R.id.statusPercent);
        call = findViewById(R.id.callBtn);
        help = findViewById(R.id.helpbtn);
        orderid = findViewById(R.id.orderID);


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
        fileType = extras.getString("FileType");
        pagesize = extras.getString("PageSize");
        orientation = extras.getString("Orientation");
        username = extras.getString("Username");
        email = extras.getString("email");
        usernum = extras.getLong("UserNumber");
        shopNum = extras.getLong("ShopNum");
        urls = extras.getStringArrayList("URLS");
        copy = extras.getInt("Copies");
        color = extras.getString("ColorType");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
//        pageURL = extras.getStringArrayList("URLS");
        bothSides = extras.getBoolean("BothSides");
        custom = extras.getString("Custom");
        numberOfPages = extras.getDouble("Pages");
        isTester = extras.getBoolean("IsTester");


        /////////////////////////////////////////////Setting Shop Details on screen/////////////////////////
        shopName.setText("Shop Name : " + name);
             Loc.setText(loc);
        Price.setText("Amount : ₹" + price);
        Files.setText("Files  :  " + files);
        orderid.setText(extras.getString("OrderKey"));


        Log.d("ISTESTER",String.valueOf(isTester));
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
        if(fileType != null) {
//            getId();
//            new uploadFile().execute(urls);
            setProgress(orderKey);
        }else{
            setProgressForOrder(orderKey);
            orderid.setText("Order ID: "+orderKey);
        }

    }

    @Override
    public void onBackPressed() {
        if(obj.progress >=25) {
            Intent intent = new Intent(this, Select.class);
            startActivity(intent);
            finish();
        }else{
            alertBox("Please wait while we process your order.");
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
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + shopNum));
                if (ActivityCompat.checkSelfPermission(OrderPlaced.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(OrderPlaced.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        ActivityCompat.requestPermissions(OrderPlaced.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                        Log.d("MANAGEPERMISSION", "PERMISSION");
                    }
                    return;
                }
                startActivity(callIntent);
            }else if(v == findViewById(R.id.helpbtn)){

                Intent intent = new Intent(OrderPlaced.this,settings.class);
                startActivity(intent);
//                finish();
            }
        }
    };

    public void setProgress(final String orderKey){


        Log.d("SETTING","PROGRESS FOR"+orderKey);
        final HashMap<String, Object> notified = new HashMap<String, Object>();
        final String[] status = {null};


        orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("VAL", dataSnapshot.getKey());

                for (DataSnapshot user : dataSnapshot.getChildren()) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.notify)
                            .setContentTitle("Order Status")
                            .setContentText(status[0])
                            .setGroup(CHANNEL_ID)
//                              .setContentIntent(resultPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

//                            notificationManager.notify(1, builder.build());

                    if (user.getKey().equals("orderStatus")) {

                        notified.put("P_Notified", true);
                        orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
                        notified.put("RT_Notified", false);
                        orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
                        notified.put("IP_Notified", false);
                        orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
                        notified.put("R_Notified", false);
                        orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);

                        Log.d("STATUS", user.getValue().toString());

                        orderStatus = user.getValue().toString();
                        Log.d("SHOWINGYOURORDER",orderStatus);

                        if (orderStatus.equals("Placed")) {

                            status[0] = "Placed";
                            Log.d("Progress", String.valueOf(obj.progress));

                            while (obj.progress <= 25) {
                                obj.progress++;
                                orderProgress.setProgress(obj.progress);
                                orderProgress.setProgressBarColor(Color.RED);

                            }

                        } else if (orderStatus.equals("Retrieved")) {

                            Log.d("Progress", String.valueOf(obj.progress));
                            status[0] = "Retrieved";

                            while (obj.progress > 25 && obj.progress < 50) {

                                obj.progress++;
                                orderProgress.setProgress(obj.progress);
                                orderProgress.setProgressBarColor(Color.YELLOW);

                            }
                        } else if (orderStatus.equals("In Progress")) {
//
                            Log.d("Progress", String.valueOf(obj.progress));
                            status[0] = "In Progress";

                            while (obj.progress > 50 && obj.progress < 75) {

                                obj.progress++;
                                orderProgress.setProgress(obj.progress);
                                orderProgress.setProgressBarColor(Color.BLUE);

                            }
                        } else if (orderStatus.equals("Ready")) {
//
                            Log.d("Progress", String.valueOf(obj.progress));
                            status[0] = "Ready";

                            while (obj.progress > 75 && obj.progress < 100) {

                                obj.progress++;
                                orderProgress.setProgress(obj.progress);
//                                        orderProgress.setBackgroundResource(R.drawable.colorprogressgreen);
                                orderProgress.setProgressBarColor(Color.GREEN);

                            }
                        }


                    }

                }


                if (obj.progress <= 25) {
                    status2.setVisibility(View.VISIBLE);
                    status2.setText("Placing order");

                    statusPercent.setText("25%");

                }else
                if (obj.progress >= 25 && obj.progress <= 50) {
                    Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));

                    status2.setVisibility(View.VISIBLE);
                    status2.setText("Placing Order");
                    status4.setVisibility(View.VISIBLE);
                    status4.setText("Order Placed");

                    if(obj.progress == 25){
                        statusPercent.setText("25%");
                    }

                    if(obj.progress == 50){
                        statusPercent.setText("50%");
                    }
                }else
                if (obj.progress >= 50 && obj.progress <= 75) {
                    Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));

                    status2.setVisibility(View.VISIBLE);
                    status2.setText("Placing Order");
                    status4.setVisibility(View.VISIBLE);
                    status4.setText("Order Placed");
                    status1.setVisibility(View.VISIBLE);
                    status1.setText("Order in Progress");

                    if(obj.progress == 50){
                        statusPercent.setText("50%");
                    }

                    if(obj.progress == 75){
                        statusPercent.setText("75%");
                    }

                }else
                if (obj.progress >= 75 && obj.progress <= 100) {
                    Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));

                    status2.setVisibility(View.VISIBLE);
                    status2.setText("Placing Order");
                    status4.setVisibility(View.VISIBLE);
                    status4.setText("Order Placed");
                    status1.setVisibility(View.VISIBLE);
                    status1.setText("Order in Progress");
                    status3.setVisibility(View.VISIBLE);
                    status3.setText("Order Ready");

                    statusPercent.setText("100%");

                }


//                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void setProgressForOrder(final String orderKey){


        Log.d("SETTING","PROGRESS FOR"+orderKey);
        final HashMap<String, Object> notified = new HashMap<String, Object>();
        final String[] status = {null};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle("Order Status")
                .setContentText(orderStatus)
                .setGroup(CHANNEL_ID)
//                  .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        status1.setVisibility(View.INVISIBLE);
        status4.setVisibility(View.INVISIBLE);

        status3.setVisibility(View.INVISIBLE);

        status2.setVisibility(View.VISIBLE);
        status2.setText("Placing Order");
//        status1.setVisibility(View.VISIBLE);

        if (orderStatus != null) {

            Log.d("SHOWINGYOURORDER",orderStatus);

            if (orderStatus.equals("Placed")) {


                Log.d("Progress", String.valueOf(obj.progress));

                while (obj.progress <= 25) {
                    obj.progress++;
                    orderProgress.setProgress(obj.progress);
                    orderProgress.setProgressBarColor(Color.RED);
//                                orderProgress.setBackgroundColor(Color.RED);
//                                orderProgress.
                }
//                            notified.put("P_Notified", true);
//                            orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);

            } else if (orderStatus.equals("Retrieved")) {


                Log.d("RProgress", String.valueOf(obj.progress));

                while (obj.progress >= 0 && obj.progress < 50) {

                    obj.progress++;
                    orderProgress.setProgress(obj.progress);

//                                orderProgress.setBackgroundResource(R.drawable.colorprogressblue);
                    orderProgress.setProgressBarColor(Color.YELLOW);

                }
//                notified.put("RT_Notified", false);
//                orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
            } else if (orderStatus.equals("In Progress")) {

                Log.d("Progress", String.valueOf(obj.progress));

                while (obj.progress >= 0 && obj.progress < 75) {

                    obj.progress++;
                    orderProgress.setProgress(obj.progress);
//                                orderProgress.setBackgroundResource(R.drawable.colorprogressyellow);
                    orderProgress.setProgressBarColor(Color.BLUE);

                }
//                notified.put("IP_Notified", false);
//                orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
            } else if (orderStatus.equals("Ready")) {

                Log.d("Progress", String.valueOf(obj.progress));

                while (obj.progress >= 0 && obj.progress < 100) {

                    obj.progress++;
                    orderProgress.setProgress(obj.progress);
//                                orderProgress.setBackgroundResource(R.drawable.colorprogressgreen);
                    orderProgress.setProgressBarColor(Color.GREEN);

                }
//                notified.put("R_Notified", false);
//                orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
            }

            Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));

            if (obj.progress <= 25) {
                status2.setVisibility(View.VISIBLE);
                status2.setText("Placing order");

                statusPercent.setText("25%");

            }else
            if (obj.progress >= 25 && obj.progress <= 50) {
                Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));

                status2.setVisibility(View.VISIBLE);
                status2.setText("Placing Order");
                status4.setVisibility(View.VISIBLE);
                status4.setText("Order Placed");


                if(obj.progress == 25){
                    statusPercent.setText("25%");
                }

                if(obj.progress == 50){
                    statusPercent.setText("50%");
                }
            }else
            if (obj.progress >= 50 && obj.progress <= 75) {
                Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));

                status2.setVisibility(View.VISIBLE);
                status2.setText("Placing Order");
                status4.setVisibility(View.VISIBLE);
                status4.setText("Order Placed");
                status1.setVisibility(View.VISIBLE);
                status1.setText("Order in Progress");

                if(obj.progress == 50){
                    statusPercent.setText("50%");
                }

                if(obj.progress == 75){
                    statusPercent.setText("75%");
                }
            }else
            if (obj.progress >= 75 && obj.progress <= 100) {
                Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));

                status2.setVisibility(View.VISIBLE);
                status2.setText("Placing Order");
                status4.setVisibility(View.VISIBLE);
                status4.setText("Order Placed");
                status1.setVisibility(View.VISIBLE);
                status1.setText("Order in Progress");
                status3.setVisibility(View.VISIBLE);
                status3.setText("Order Ready");

                statusPercent.setText("100%");

            }

        }

    }


    ArrayList<Bitmap> images = new ArrayList<Bitmap>();


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    ArrayList<Integer> ids = new ArrayList<>();
    public void getId(){

        ref.child(shopType).child(shopKey).child("Orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("cust",dataSnapshot.getKey());

                for(DataSnapshot orders: dataSnapshot.getChildren()){
                    for(DataSnapshot values: orders.getChildren()) {
                        if (values.getKey().equals("id")) {
                            ids.add(Integer.parseInt(values.getValue().toString()));
                            Log.d("IDS",values.getValue().toString());
                            Collections.sort(ids);

                        }
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
    }


    public class uploadFile extends AsyncTask<ArrayList<String>,Void,Void>{
        final int[] uploadCnt = {0};

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(ArrayList<String>... arrayLists) {


            Uri uri;
            orderProgress.setProgress(15);
//            statusPercent.setText("15%");



            for(int i =0;i<urls.size();i++) {
                String file = urls.get(i);
                uri = Uri.parse(file);

                final String uniqueID = UUID.randomUUID().toString();
                final StorageReference filesRef = storageRef.child(uniqueID);



//        if (Build.VERSION.SDK_INT < 19) {

//      getContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                final UploadTask uploadTask = filesRef.putFile(uri);
//        final UploadTask uploadTask = filesRef.putFile(changeExtension(new File(file.getPath()),"pdf"));
                final int finalI = i;
                final Uri finalUri = uri;
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("UPLOAD", "Not successfull");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Log.d("UPLOAD", "SUCCESSFULL");
                        Toast.makeText(OrderPlaced.this, "Files are being sent", Toast.LENGTH_SHORT).show();

                        Log.d("UNIQUE", uniqueID);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                Log.d("URIID", String.valueOf(finalUri));

                                uploadCnt[0]++;
                                // Continue with the task to get the download URL
                                return filesRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()) {
                                    String url;
                                    Uri downloadUri = task.getResult();
                                    url = String.valueOf(downloadUri);
                                    downloadUrls.add(url);
                                    Log.d("DOWNLOADURL", String.valueOf(url));

                                    if(urls.size() == downloadUrls.size()) {


                                        Log.d("PAGESIZEGEE",pagesize);
                                        Log.d("CUSTOM",custom);
                                        Log.d("ORIENTATION",orientation);
                                        Log.d("COLOR",color);
                                        Log.d("COPY", String.valueOf(copy));
                                        String orderID = UUID.randomUUID().toString();


                                        int id = 0;
                                        if(ids.size()>0){
//                                            Log.d("IDS",String.valueOf(ids.get(2)));
                                            id = ids.get(ids.size()-1)+1;
                                        }

                                        if(custom == "" || custom == null){
                                            custom = "All";
                                        }

                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                        Log.d("ORDERPBOTHSIDES", String.valueOf(bothSides));
                                        shopinfo orderInfo = new shopinfo(loc, name, "Placed", shopLat, shopLong, shopNum, files, fileType, pagesize, orientation, price, custom, orderDateTime, false, false, false, false);
                                        info userinfo = new info(username, email, usernum, "android", "Placed", fileType, copy, orderDateTime, id,custom,price,bothSides);

                                        String storeID = shopKey;

                                        storeDb = storeDb.child(shopType).child(storeID).child("Orders").child(userId).child(orderID);
                                        storeDb.setValue(userinfo);
                                        db = db.child("users").child(userId).child("Orders").child(storeID).child(orderID);
                                        db.setValue(orderInfo);
                                        orderid.setText("Order ID: "+orderID);




                                        for(int k =0;k<downloadUrls.size();k++) {

                                            singlePageInfo single = new singlePageInfo(downloadUrls.get(k), color, copy, fileType, pagesize, orientation);
                                            db.push().setValue(single);
                                            storeDb.push().setValue(single);
                                            orderKey = orderID;



                                            if (k == downloadUrls.size() - 1) {
                                                Toast.makeText(OrderPlaced.this, "Files are being sent", Toast.LENGTH_SHORT).show();
//                                                new setProgressForOrder().execute(orderKey);
                                                setProgressForOrder(orderKey);

                                            }
                                        }
                                    }
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                        // ...
                    }
                });

            }
            return null;

        }

//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Toast.makeText(getApplicationContext(),"Thank you!😁",Toast.LENGTH_SHORT).show();
//        }
    }



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
