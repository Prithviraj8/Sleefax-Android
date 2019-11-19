package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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
import com.kaopiz.kprogresshud.KProgressHUD;
import com.prithviraj8.copycatandroid.Services.MyFirebaseMessagingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static com.prithviraj8.copycatandroid.Select.PICK_PDF_CODE;

class OrderStatus{
    int progress = 0;

}
public class OrderPlaced extends AppCompatActivity {
    NotificationManagerCompat notificationManager;
//    MyFirebaseMessagingService notification = new MyFirebaseMessagingService();

    final String TAG = "PathGoogleMapActivity";
    ImageButton getDirection;
    TextView Files, shopName,Loc,Price,status1,status2,status3,status4;
    ProgressBar orderProgress;
    ImageButton back;

    String name,loc,orderKey,orderStatus,shopKey,fileType,pagesize,orientation,username,email;
    LatLng shopLoc, userLoc;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    int files, price;
    int copy,resultCode,requestCode;
    String color;
    String CHANNEL_ID = "UsersChannel";
    boolean FromYourOrders =false;
    long num;

//    ShopInfo info = new ShopInfo();
    private static final int LOCATION_REQUEST = 500;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    OrderStatus obj = new OrderStatus();


    boolean mLocationPermissionGranted;
    FusedLocationProviderClient mFusedLocationProviderClient;
    DatabaseReference orderDb = FirebaseDatabase.getInstance().getReference();
    DatabaseReference storeDb;
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
        FirebaseApp app = FirebaseApp.getInstance("Stores");
        FirebaseDatabase DB = FirebaseDatabase.getInstance(app);
        storeDb = DB.getReferenceFromUrl("https://storeowner-9c355.firebaseio.com/").child("users");
        ArrayList<Uri> pdfs = new ArrayList<>();

        back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrderPlaced.this,Select.class);
                startActivity(intent);
                finish();

            }
        });

//        FirebaseApp app = FirebaseApp.getInstance("StoreOwners");
//        FirebaseDatabase ownersDB = FirebaseDatabase.getInstance(app);
//        orderDb = ownersDB.getReferenceFromUrl("https://copycat-store-owner-7ddcc.firebaseio.com").child("Store Owner Users");

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
        orderProgress = findViewById(R.id.OrderProgressBar);
        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);
        status3 = findViewById(R.id.status3);
        status4 = findViewById(R.id.status4);
        getDirection = findViewById(R.id.Directions);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        name = extras.getString("ShopName");
        loc = extras.getString("Location");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
        price = extras.getInt("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        shopKey = extras.getString("ShopKey");
        orderKey = extras.getString("OrderKey");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");
        fileType = extras.getString("FileType");
        pagesize = extras.getString("PageSize");
        orientation = extras.getString("Orientation");
        username = extras.getString("Username");
        email = extras.getString("email");
        num = extras.getLong("Number");
        pdfs = extras.getParcelableArrayList("URLS");
        copy = extras.getInt("Copies");
        color = extras.getString("ColorTypes");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");


        shopName.setText("Shop Name : " + name);
           Loc.setText(loc);
           Price.setText("   Amount : ₹" + price);
           Files.setText("    Files :  " + files);


        Log.d("Shop Lat", String.valueOf(shopLat));
        Log.d("Shop Long",String.valueOf(shopLong));

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

        uploadFile(pdfs);
        setProgressForOrder();

//        createNotificationChannel();
    }

    @Override
    public void onBackPressed() {
            Intent intent = new Intent(this,Select.class);
            startActivity(intent);
            finish();
    }


    public void uploadFile(ArrayList<Uri> pdfs){


        final String uniqueID = UUID.randomUUID().toString();


//        final String uniqueID = UUID.randomUUID().toString();

        final StorageReference filesRef = storageRef.child(uniqueID);

//        Log.d("FILEPDF", String.valueOf(changeExtension(new File(file.getPath()),"pdf")));


        final Uri uri;
        uri = pdfs.get(0);

                Log.d("URIID", String.valueOf(pdfs.get(0)));

                final UploadTask uploadTask = filesRef.putFile(pdfs.get(0));
//        final UploadTask uploadTask = filesRef.putFile(changeExtension(new File(file.getPath()),"pdf"));
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
                        Log.d("UNIQUE", uniqueID);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return filesRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String url;
                                    Uri downloadUri = task.getResult();
                                    url = String.valueOf(downloadUri);


                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                    shopinfo orderInfo = new shopinfo(loc, name, "Placed", shopLat, shopLong, num, files, fileType, pagesize, orientation, price, false, false, false, false);
                                    UserInfo userinfo = new UserInfo(username, email, num, "android");

                                    String storeID = shopKey;

                                    storeDb = storeDb.child(storeID).child("Orders").child(userId).child(uniqueID);
                                    storeDb.setValue(userinfo);
                                    db = db.child("users").child(userId).child("Orders").child(storeID).child(uniqueID);
                                    db.setValue(orderInfo);

                                    singlePageInfo single = new singlePageInfo(url, color, copy, fileType, pagesize, orientation);
                                    db.push().setValue(single);
                                    storeDb.push().setValue(single);
                                    orderKey = uniqueID;


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




    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setProgressForOrder() {

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, Select.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
//        final PendingIntent resultPendingIntent = PendingIntent.getActivity(this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

//        obj.progress++;
        final int[] cnt = new int[1];

//        Log.d("SHOPNAME",name);
//        Log.d("ORDERID",orderKey);
        orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 Log.d("VAL",dataSnapshot.getKey());

//                 Iterator<DataSnapshot> users = dataSnapshot.getChildren().iterator();
//                 cnt[0] = (int) dataSnapshot.getChildrenCount();

//                while (users.hasNext()){
                    for(DataSnapshot user: dataSnapshot.getChildren()){
//                    DataSnapshot user = users.next();
//                    Log.d("VVAL", user.getKey());

                    final HashMap<String, Object> notified = new HashMap<String, Object>();
                    String status = null;
                    if(orderStatus != null){
                        if(orderStatus.equals("Placed")){
                            obj.progress = 25;
                        }else if(orderStatus.equals("Retrieved")){
                            obj.progress = 50;
                        }else if(orderStatus.equals("In Process")){
                            obj.progress = 75;
                        }else{
                            obj.progress = 100;
                        }


                        if (orderStatus.equals("Placed")) {


                            Log.d("Progress", String.valueOf(obj.progress));

                            while (obj.progress <= 25) {
                                obj.progress++;
                                orderProgress.setProgress(obj.progress, true);
//                                orderProgress.setBackgroundColor(Color.RED);
                            }
//                            notified.put("P_Notified", true);
//                            orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);

                        }else
                        if (orderStatus.equals("Retrieved")) {


                            Log.d("Progress", String.valueOf(obj.progress));

                            while (obj.progress > 25 && obj.progress <= 50) {

                                obj.progress++;
                                orderProgress.setProgress(obj.progress, true);
                                orderProgress.setBackgroundResource(R.drawable.colorprogressblue);

                            }
//                            notified.put("RT_Notified", false);
//                            orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).setValue(notified);
                        }else
                        if (orderStatus.equals("In Progress")) {

                            Log.d("Progress", String.valueOf(obj.progress));

                            while (obj.progress > 50 && obj.progress <= 75) {

                                obj.progress++;
                                orderProgress.setProgress(obj.progress, true);
                                orderProgress.setBackgroundResource(R.drawable.colorprogressyellow);

                            }
//                            notified.put("IP_Notified", false);
//                            orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).setValue(notified);
                        }else
                        if (orderStatus.equals("Ready")) {

                            Log.d("Progress", String.valueOf(obj.progress));

                            while (obj.progress > 75 && obj.progress <= 100) {

                                obj.progress++;
                                orderProgress.setProgress(obj.progress, true);
                                orderProgress.setBackgroundResource(R.drawable.colorprogressgreen);

                            }
//                            notified.put("R_Notified", false);
//                            orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).setValue(notified);
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.notify)
                                .setContentTitle("Order Status")
                                .setContentText(orderStatus)
                                .setGroup(CHANNEL_ID)
//                                .setContentIntent(resultPendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                        notificationManager.notify(1, builder.build());

                        Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));
                    }else {

                            if (user.getKey().equals("orderStatus")) {
                            notified.put("P_Notified", true);
                            orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
                            notified.put("RT_Notified", false);
                            orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
                            notified.put("IP_Notified", false);
                            orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);
                            notified.put("R_Notified", false);
                            orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).updateChildren(notified);

                                Log.d("STATUS",user.getValue().toString());

                                orderStatus = user.getValue().toString();

                                if (orderStatus.equals("Placed")) {

                                    status = "Placed";
                                    Log.d("Progress", String.valueOf(obj.progress));

                                    while (obj.progress <= 25) {
                                        obj.progress++;
                                        orderProgress.setProgress(obj.progress, true);
                                        orderProgress.setBackgroundResource(R.drawable.colorprogressred);

                                    }

                                }else
                                if (orderStatus.equals("Retrieved")) {

//                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, "Channel_ID")
//                                            .setSmallIcon(R.drawable.notify)
//                                            .setContentTitle("Order Status")
//                                            .setContentText("Your order has been accepted.")
//                                            .setPriority(NotificationCompat.PRIORITY_HIGH);
//                                    notificationManager.notify(1, builder.build());
                                    Log.d("Progress", String.valueOf(obj.progress));
                                    status = "Retrieved";

                                    while (obj.progress > 25 && obj.progress <= 50) {

                                        obj.progress++;
                                        orderProgress.setProgress(obj.progress, true);
                                        orderProgress.setBackgroundResource(R.drawable.colorprogressblue);

                                    }
                                }else
                                if (orderStatus.equals("In Progress")) {
//                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, "Channel_ID")
//                                            .setSmallIcon(R.drawable.notify)
//                                            .setContentTitle("Order Status")
//                                            .setContentText("Your documents are being printed.")
//                                            .setPriority(NotificationCompat.PRIORITY_HIGH);
//                                    notificationManager.notify(1, builder.build());
                                    Log.d("Progress", String.valueOf(obj.progress));
                                    status = "In Process";

                                    while (obj.progress > 50 && obj.progress <= 75) {

                                        obj.progress++;
                                        orderProgress.setProgress(obj.progress, true);
                                        orderProgress.setBackgroundResource(R.drawable.colorprogressyellow);

                                    }
                                }else
                                if (orderStatus.equals("Ready")) {
//                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, "Channel_ID")
//                                            .setSmallIcon(R.drawable.notify)
//                                            .setContentTitle("Order Status")
//                                            .setContentText("Your order is ready to be pickeup up.")
//                                            .setPriority(NotificationCompat.PRIORITY_HIGH);
//                                    notificationManager.notify(1, builder.build());
                                    Log.d("Progress", String.valueOf(obj.progress));
                                    status = "Ready";

                                    while (obj.progress > 75 && obj.progress <= 100) {

                                        obj.progress++;
                                        orderProgress.setProgress(obj.progress, true);
                                        orderProgress.setBackgroundResource(R.drawable.colorprogressgreen);

                                    }
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.notify)
                                        .setContentTitle("Order Status")
                                        .setContentText(status)
                                        .setGroup(CHANNEL_ID)
//                                        .setContentIntent(resultPendingIntent)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                notificationManager.notify(1, builder.build());
                            }
//                        Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));

                        }
                        if (obj.progress >= 25) {
                            //                        Animation fadeIn = new AlphaAnimation(0, 1);
                            //                        fadeIn.setInterpolator(new DecelerateInterpolator());
                            //                        fadeIn.setDuration(1000);

                            status1.setVisibility(View.VISIBLE);
                        }
                        if(obj.progress >= 50){
                            status2.setVisibility(View.VISIBLE);

                        }
                        if(obj.progress >= 75){
                            status3.setVisibility(View.VISIBLE);

                        }
                        if(obj.progress >= 100){
                            status4.setVisibility(View.VISIBLE);

                        }
                   }
             }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
//        if (mapViewBundle == null) {
//            mapViewBundle = new Bundle();
//            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
//        }
//
//        shopMap.onSaveInstanceState(mapViewBundle);
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        shopMap.onResume();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        shopMap.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        shopMap.onStop();
//    }

//    @Override
//    public void onMapReady(final GoogleMap googleMap) {
//        MarkerOptions options = new MarkerOptions();
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        Log.d("MAP", "IS READY");
//
//        LatLng userLoc = new LatLng(18.573595,73.875822);
//        LatLng shopLoc = new LatLng(shopLat,shopLong);
//        String sLat = String.valueOf(shopLoc.latitude);
//        String sLong = String.valueOf(shopLoc.longitude);
//
//
//
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 16));
//
//        user = new MarkerOptions().position(new LatLng(18.573595, 73.875822)).title("Current Location");
//        shop = new MarkerOptions().position(new LatLng(shopLat, shopLong)).title("Store");
//
//        markerPoints.add(userLoc);
//        markerPoints.add(shopLoc);
//        googleMap.addMarker(user);
//        googleMap.addMarker(shop);
//        googleMap.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(userLoc,15f)
//        );
//
//        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    Activity#requestPermissions
////            googleMap.setMyLocationEnabled(true);
//            mLocationPermissionGranted = true;
//
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for Activity#requestPermissions for more details.
//            return;
//        }
//        StringBuilder sb = new StringBuilder();
//        Object[] datatransfer = new Object[4];
//        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
//        sb.append("origins="+userLoc.latitude + ","+ userLoc.longitude);
//        sb.append("&destination="+shopLoc.latitude+","+shopLoc.longitude);
//        sb.append("&key="+"AIzaSyBRE-1UMmGznNw8hvLE4quITaDBEF00qr4");
//
//        GetDirectionsData data = new GetDirectionsData(getApplicationContext());
//        datatransfer[0] = googleMap;
//        datatransfer[1] = sb.toString();
//        datatransfer[2] = new LatLng(userLoc.latitude,userLoc.longitude);
//        datatransfer[3] = new LatLng(shopLoc.latitude,shopLoc.longitude);
//        data.execute(datatransfer);
//        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//
//    }


//    @Override
//    protected void onPause() {
//        shopMap.onPause();
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        shopMap.onDestroy();
//        super.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        shopMap.onLowMemory();
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
