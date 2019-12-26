package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

class OrderStatus{
    int progress = 0;
}


class info{

    public String name,PrintStatus;
    public String email,device,fileType,orderDateTime;
    public long num;
    public int copies;


    public info(String name, String email, long num, String device, String placed, String fileType, int copy, String orderDateTime){
        this.email = email;
        this.name = name;
        this.num = num;
        this.device = device;
        this.PrintStatus = placed;
        this.fileType = fileType;
        this.copies = copy;
        this.orderDateTime = orderDateTime;
    }

}

public class OrderPlaced extends AppCompatActivity {
    NotificationManagerCompat notificationManager;
//    MyFirebaseMessagingService notification = new MyFirebaseMessagingService();

    final String TAG = "PathGoogleMapActivity";
    ImageButton getDirection,call,back,help;
    TextView Files, shopName,Loc,Price,status1,status2,status3,status4,statusPercent;
//    ProgressBar orderProgress;

    CircularProgressBar orderProgress;

    String name,loc,orderKey,orderStatus,shopKey,fileType,pagesize,orientation,username,email;
    LatLng shopLoc, userLoc;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    int files, price;
    int copy,resultCode,requestCode,numberOfPages;
    String color,custom,orderDateTime;
    String CHANNEL_ID = "UsersChannel";
    boolean FromYourOrders =false, bothSides;

    long num;
    ArrayList<String> pdfs = new ArrayList<>();

    //    ShopInfo info = new ShopInfo();
    private static final int LOCATION_REQUEST = 500;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    OrderStatus obj = new OrderStatus();
    ArrayList<String> pageURL = new ArrayList<>();
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


        call.setOnClickListener(BtnListener);
        help.setOnClickListener(BtnListener);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        data = extras.getParcelable("Data");

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
        pdfs = extras.getStringArrayList("URLS");
        copy = extras.getInt("Copies");
        color = extras.getString("ColorType");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
        pageURL = extras.getStringArrayList("URLS");
        bothSides = extras.getBoolean("BothSides");
        custom = extras.getString("Custom");
        numberOfPages = extras.getInt("Pages");

        shopName.setText("Shop Name : " + name);
           Loc.setText(loc);
//           Price.setText("   Amount : ₹" + price);
            Price.setText("Amount : ₹" + price);

        Files.setText("    Files :  " + files);


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
            if (!fileType.equals("image/jpeg")) {
//            Toast.makeText(this,"Files are being sent",Toast.LENGTH_SHORT).show();
//                uploadFile(pdfs);
                new uploadFile().execute(pdfs);
            } else {
//                uploadImagesOrder();

                new uploadImagesOrder().execute();
            }
        }else{
            new setProgressForOrder().execute();
        }
    }

    @Override
    public void onBackPressed() {
            Intent intent = new Intent(this,Select.class);
            startActivity(intent);
            finish();
    }



    //Create an anonymous implementation of OnClickListener
    private View.OnClickListener BtnListener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked

            if(v == findViewById(R.id.callBtn)) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + num));
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
                finish();
            }
        }
    };

//    @Override
//    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case General.REQUESTPERMISSION:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //reload my activity with permission granted or use the features that required the permission
//
//                } else {
//
//                }
//                break;
//        }
//    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    public class setProgressForOrder extends AsyncTask<String,Void,Void> {

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(String... strings) {

            Intent resultIntent = new Intent(OrderPlaced.this, Select.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(OrderPlaced.this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
//        final PendingIntent resultPendingIntent = PendingIntent.getActivity(this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

//        obj.progress++;
            final int[] cnt = new int[1];

            Log.d("SHOPNAME", name);
            Log.d("ORDERID", orderKey);
            final HashMap<String, Object> notified = new HashMap<String, Object>();
            final String[] status = {null};

            NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notify)
                    .setContentTitle("Order Status")
                    .setContentText(orderStatus)
                    .setGroup(CHANNEL_ID)
//                  .setContentIntent(resultPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);


            if (orderStatus != null) {

                Log.d("SHOWINGYOURORDER","Y");

//
//                            notificationManager.notify(1, builder.build());


//                if (orderStatus.equals("Placed")) {
//                    obj.progress = 25;
//                    statusPercent.setText("25%");
//
//                } else if (orderStatus.equals("Retrieved")) {
//                    statusPercent.setText("50%");
//                    obj.progress = 50;
//                } else if (orderStatus.equals("In Progress")) {
//                    statusPercent.setText("75%");
//                    obj.progress = 75;
//                } else {
//                    statusPercent.setText("100%");
//                    obj.progress = 100;
//                }


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


                    Log.d("Progress", String.valueOf(obj.progress));

                    while (obj.progress > 25 && obj.progress <= 50) {

                        obj.progress++;
                        orderProgress.setProgress(obj.progress);

//                                orderProgress.setBackgroundResource(R.drawable.colorprogressblue);
                        orderProgress.setProgressBarColor(Color.YELLOW);

                    }
                    notified.put("RT_Notified", false);
                    orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).setValue(notified);
                } else if (orderStatus.equals("In Progress")) {

                    Log.d("Progress", String.valueOf(obj.progress));

                    while (obj.progress > 50 && obj.progress <= 75) {

                        obj.progress++;
                        orderProgress.setProgress(obj.progress);
//                                orderProgress.setBackgroundResource(R.drawable.colorprogressyellow);
                        orderProgress.setProgressBarColor(Color.BLUE);

                    }
                    notified.put("IP_Notified", false);
                    orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).setValue(notified);
                } else if (orderStatus.equals("Ready")) {

                    Log.d("Progress", String.valueOf(obj.progress));

                    while (obj.progress > 75 && obj.progress <= 100) {

                        obj.progress++;
                        orderProgress.setProgress(obj.progress);
//                                orderProgress.setBackgroundResource(R.drawable.colorprogressgreen);
                        orderProgress.setProgressBarColor(Color.GREEN);

                    }
                    notified.put("R_Notified", false);
                    orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).setValue(notified);
                }

                Log.d("ORDERPROG", String.valueOf(orderProgress.getProgress()));

                if (obj.progress <= 25) {
                    statusPercent.setText("25%");
                    status1.setVisibility(View.GONE);
                    status1.setText("Order in progress");
                }else
                if (obj.progress >= 50 && obj.progress <= 75) {
                    status2.setVisibility(View.GONE);
                    status1.setText("Order Placed");
                    statusPercent.setText("50%");

                }else
                if (obj.progress >= 75 && obj.progress < 100) {
                    status3.setVisibility(View.GONE);
                    status1.setText("Order in Progress");
                    statusPercent.setText("75%");

                }else
                if (obj.progress == 100) {
                    status4.setVisibility(View.GONE);
                    status1.setText("Order Ready");
                    statusPercent.setText("100%");

                }


            } else {

                orderDb.child("users").child(userId).child("Orders").child(shopKey).child(orderKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("VAL", dataSnapshot.getKey());

//                 Iterator<DataSnapshot> users = dataSnapshot.getChildren().iterator();
//                 cnt[0] = (int) dataSnapshot.getChildrenCount();

//                while (users.hasNext()){
//                    DataSnapshot user = users.next();
//                    Log.d("VVAL", user.getKey());


//                        }else {

                        for (DataSnapshot user : dataSnapshot.getChildren()) {

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.notify)
                                    .setContentTitle("Order Status")
                                    .setContentText(status[0])
                                    .setGroup(CHANNEL_ID)
//                                        .setContentIntent(resultPendingIntent)
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

                                if (orderStatus.equals("Placed")) {

                                    status[0] = "Placed";
                                    Log.d("Progress", String.valueOf(obj.progress));

                                    while (obj.progress <= 25) {
                                        obj.progress++;
                                        orderProgress.setProgress(obj.progress);
//                                        orderProgress.setBackgroundResource(R.drawable.colorprogressred);
                                        orderProgress.setProgressBarColor(Color.RED);

                                    }

                                } else if (orderStatus.equals("Retrieved")) {

//                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, "Channel_ID")
//                                            .setSmallIcon(R.drawable.notify)
//                                            .setContentTitle("Order Status")
//                                            .setContentText("Your order has been accepted.")
//                                            .setPriority(NotificationCompat.PRIORITY_HIGH);
//                                    notificationManager.notify(1, builder.build());
                                    Log.d("Progress", String.valueOf(obj.progress));
                                    status[0] = "Retrieved";

                                    while (obj.progress > 25 && obj.progress <= 50) {

                                        obj.progress++;
                                        orderProgress.setProgress(obj.progress);
//                                        orderProgress.setBackgroundResource(R.drawable.colorprogressblue);
                                        orderProgress.setProgressBarColor(Color.YELLOW);

                                    }
                                } else if (orderStatus.equals("In Progress")) {
//                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, "Channel_ID")
//                                            .setSmallIcon(R.drawable.notify)
//                                            .setContentTitle("Order Status")
//                                            .setContentText("Your documents are being printed.")
//                                            .setPriority(NotificationCompat.PRIORITY_HIGH);
//                                    notificationManager.notify(1, builder.build());
                                    Log.d("Progress", String.valueOf(obj.progress));
                                    status[0] = "In Progress";

                                    while (obj.progress > 50 && obj.progress <= 75) {

                                        obj.progress++;
                                        orderProgress.setProgress(obj.progress);
//                                        orderProgress.setBackgroundResource(R.drawable.colorprogressyellow);
                                        orderProgress.setProgressBarColor(Color.BLUE);

                                    }
                                } else if (orderStatus.equals("Ready")) {
//                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderPlaced.this, "Channel_ID")
//                                            .setSmallIcon(R.drawable.notify)
//                                            .setContentTitle("Order Status")
//                                            .setContentText("Your order is ready to be pickeup up.")
//                                            .setPriority(NotificationCompat.PRIORITY_HIGH);
//                                    notificationManager.notify(1, builder.build());
                                    Log.d("Progress", String.valueOf(obj.progress));
                                    status[0] = "Ready";

                                    while (obj.progress > 75 && obj.progress <= 100) {

                                        obj.progress++;
                                        orderProgress.setProgress(obj.progress);
//                                        orderProgress.setBackgroundResource(R.drawable.colorprogressgreen);
                                        orderProgress.setProgressBarColor(Color.GREEN);

                                    }
                                }


                            }

                        }

                        if (obj.progress <= 25) {
                            statusPercent.setText("25%");
                            status1.setVisibility(View.GONE);
                            status1.setText("Order in progress");
                        }else
                        if (obj.progress >= 50 && obj.progress <= 75) {
                            status2.setVisibility(View.GONE);
                            status1.setText("Order Placed");
                            statusPercent.setText("50%");

                        }else
                        if (obj.progress >= 75 && obj.progress < 100) {
                            status3.setVisibility(View.GONE);
                            status1.setText("Order in Progress");
                            statusPercent.setText("75%");

                        }else
                        if (obj.progress == 100) {
                            status4.setVisibility(View.GONE);
                            status1.setText("Order Ready");
                            statusPercent.setText("100%");

                        }

//                    }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }



            return null;
        }
    }




    public class uploadImagesOrder extends AsyncTask<Void,Void,Void>{

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {

        final ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        final int[] uploadCnt = {0};
        ArrayList<Uri> uri = new ArrayList<Uri>();

        if (pageURL.size() > 0) {
            String uniqueID = UUID.randomUUID().toString();
            final StorageReference filesRef = storageRef.child(uniqueID);
            final String uniqueID1 = UUID.randomUUID().toString();

            orderProgress.setProgress(15);
            statusPercent.setText("15%");

            for (int i = 0; i < pageURL.size(); i++) {
                Uri imageUri = Uri.parse(pageURL.get(i));

                //do something with the image (save it to some directory or whatever you need to do with it here.

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                } catch (IOException e) {
//                  Log.d("ERROR",e.printStackTrace());
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                images.add(bitmap);

                byte[] DATA = baos.toByteArray();

                final UploadTask uploadTask = filesRef.putFile(imageUri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("UPLOADING","IMAGE");

                        Log.d("UPLOAD", "Not successfull");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Log.d("UPLOADING","Ok SUCCESSFULL");

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    Log.d("UPLOADING", String.valueOf(task.getException()));

                                    throw task.getException();
                                }
                                Log.d("UPLOADING","YAYYYY");

                                // Continue with the task to get the download URL
                                uploadCnt[0]++;
                                return filesRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();

                                    pageURL.add(String.valueOf(downloadUri));
                                    if(uploadCnt[0] == pageURL.size()){
                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                        shopinfo orderInfo = new shopinfo(loc, name, "Placed", shopLat, shopLong, num, files, fileType, pagesize, orientation, price, bothSides, custom, orderDateTime,false, false, false, false);
                                        info userinfo = new info(username, email, num, "android", "Placed",fileType,copy,orderDateTime);

                                        String storeID = shopKey;

                                        storeDb = storeDb.child("Stores").child(storeID).child("Orders").child(userId).child(uniqueID1);
                                        storeDb.setValue(userinfo);
                                        db = db.child("users").child(userId).child("Orders").child(storeID).child(uniqueID1);
                                        db.setValue(orderInfo);
                                        Log.d("UPLOAD", "SUCCESSFULL");

                                        for(int i =0;i<pageURL.size();i++){
                                            singlePageInfo single = new singlePageInfo(pageURL.get(i), color, copy, fileType, fileType, pagesize, bothSides, orientation);
                                            db.push().setValue(single);
                                            storeDb.push().setValue(single);
                                            orderKey = uniqueID1;
                                            Log.d("UPLOADIMG", String.valueOf(i));
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                if(i==pageURL.size()-1){
                                                    new setProgressForOrder().execute(orderKey);
                                                }
                                            }
                                        }
//                                        orderProgress.setProgress(25);
//                                        statusPercent.setText("25%");


                                    }
                                } else {
                                    // Handle failures
                                    Log.d("IMAGE", "NOT RECIEVED");
                                    // ...
                                }
                            }
                        });
                        // ...
                    }
                    });






                }

            }
            return null;
        }

//        }
    }
    public class uploadFile extends AsyncTask<ArrayList<String>,Void,Void>{

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(ArrayList<String>... arrayLists) {

            final String uniqueID = UUID.randomUUID().toString();
            final StorageReference filesRef = storageRef.child(uniqueID);
            final Uri uri;

            String file = pdfs.get(0);
            uri = Uri.parse(file);

            Log.d("URIID", String.valueOf(pdfs.get(0)));

            orderProgress.setProgress(15);
            statusPercent.setText("15%");

//        if (Build.VERSION.SDK_INT < 19) {

//      getContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            final UploadTask uploadTask = filesRef.putFile(uri);
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
                    Toast.makeText(OrderPlaced.this,"Files are being sent",Toast.LENGTH_SHORT).show();

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

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String url;
                                Uri downloadUri = task.getResult();
                                url = String.valueOf(downloadUri);

                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                shopinfo orderInfo = new shopinfo(loc, name, "Placed", shopLat, shopLong, num, files, fileType, pagesize, orientation, price,bothSides,custom,orderDateTime, false, false, false, false);
                                info userinfo = new info(username, email, num, "android","Placed", fileType, copy,orderDateTime);

                                String storeID = shopKey;

                                storeDb = storeDb.child("Stores").child(storeID).child("Orders").child(userId).child(uniqueID);
                                storeDb.setValue(userinfo);
                                db = db.child("users").child(userId).child("Orders").child(storeID).child(uniqueID);
                                db.setValue(orderInfo);

                                singlePageInfo single = new singlePageInfo(url, color, copy, fileType, pagesize, orientation,bothSides,custom);
                                db.push().setValue(single);
                                storeDb.push().setValue(single);
                                orderKey = uniqueID;

                                Toast.makeText(OrderPlaced.this,"Files are being sent",Toast.LENGTH_SHORT).show();

                                new setProgressForOrder().execute(orderKey);


                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });
                    // ...
                }
            });

            return null;
        }
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
