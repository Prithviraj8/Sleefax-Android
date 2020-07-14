package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

class OrderStatus{
    int progress = 0;
}




public class OrderPlaced extends AppCompatActivity {
    NotificationManagerCompat notificationManager;
    final String TAG = "PathGoogleMapActivity";

    ImageButton getDirection,call,back,help;
    TextView Files, shopName,Loc,Price,status1,orderStatusTV,status3,status4,statusPercent,orderid;
    Button showFullDetails;
    CircularProgressBar orderProgress;

    String name,loc,orderKey,orderID,orderStatus,shopKey,fileType,pagesize,orientation,username,email,paymentMode;

    int files;
    double totalPrice = 0.0;
    String color,custom,orderDateTime;
    String CHANNEL_ID = "UsersChannel",shopType;
    boolean FromYourOrders =false,isTester;

    long usernum,shopNum;

    //    ShopInfo info = new ShopInfo();
    OrderStatus obj = new OrderStatus();
    Intent data;


    boolean mLocationPermissionGranted;
    FusedLocationProviderClient mFusedLocationProviderClient;
    DatabaseReference orderDb = FirebaseDatabase.getInstance().getReference();
    DatabaseReference storeDb = FirebaseDatabase.getInstance().getReference();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<Integer> numberOfPages = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> fileSizes = new ArrayList<>();
    ArrayList<Double> pricePerFile = new ArrayList<>();
    double price_Of_File[];
    ArrayList<String> fileLocations = new ArrayList<>();

    /// Order status views///
    ImageButton stat1,stat2,stat3,stat4;


    //Order Details View
    TextView file1,file2,file3,file1Price,file2Price,file3Price,file4,file4Price,file5,file5Price;
    ListView orderDetailsLV;


    //Order Confirmation Layout
    RelativeLayout orderConfirmRL,TopViewBtnsRL;
    Button yes,no;


    // Location Variables
    public static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    protected LocationListener locationListener;
    protected double latitude;
    protected double longitude;
    LatLng shopLoc, userLoc;
    double shopLat;

    double shopLong;
    double userLat,userLong;

    //Scroll View Layout
    ScrollView scrollView;
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
//        getSupportActionBar().hide();

        notificationManager = NotificationManagerCompat.from(this);

        connectViews();

        getData();
        inititalizeInitialData();





        showFullDetails = (Button) findViewById(R.id.OrderDetailsBtn);
        showFullDetails.setPaintFlags(showFullDetails.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        showFullDetails.setOnClickListener(BtnListener);
        getDirection.setOnClickListener(BtnListener);
        yes.setOnClickListener(BtnListener);
        call.setOnClickListener(BtnListener);
        help.setOnClickListener(BtnListener);
        back.setOnClickListener(BtnListener);



        scrollView = findViewById(R.id.ScrollViewL);
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

    public void connectViews(){

        back = findViewById(R.id.backBtn);
        shopName = findViewById(R.id.OrderShop);
        Loc = findViewById(R.id.OrderLoc);
        Price = findViewById(R.id.OrderPrice);
        Files = findViewById(R.id.OrderFiles);
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
        file4 = findViewById(R.id.File4TV);
        file5 = findViewById(R.id.File5TV);
        orderDetailsLV = findViewById(R.id.OrderDetailsLV);

        file1Price = findViewById(R.id.FilePrice1);
        file2Price = findViewById(R.id.FilePrice2);
        file3Price = findViewById(R.id.FilePrice3);
        file4Price = findViewById(R.id.FilePrice4);
        file5Price = findViewById(R.id.FilePrice5);

        //Order Confirmation RL
        orderConfirmRL = findViewById(R.id.OrderPickedUpConfirmRL);
        yes = findViewById(R.id.YesConfirm);
        no = findViewById(R.id.NoConfirm);
        TopViewBtnsRL = findViewById(R.id.TopViewBtnsRL);



//        if(fileTypes != null) {
//            setProgress(orderKey);
//        }else{
//            setProgress(orderKey);
//            orderid.setText("Order ID: "+orderKey);
//        }



    }

    public void getData(){
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
//        price = extras.getDouble("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        shopKey = extras.getString("ShopKey");
        orderKey = extras.getString("OrderKey");
        orderID = extras.getString("OrderID");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");


        /////////////////////////////////////////////////Order info////////////////////////////////////////


        urls = extras.getStringArrayList("URLS");
        numberOfPages = extras.getIntegerArrayList("Pages");
        username = extras.getString("Username");
        email = extras.getString("email");
        usernum = extras.getLong("UserNumber");
        shopNum = extras.getLong("ShopNum");
        isTester = extras.getBoolean("IsTester");
//        paymentMode = extras.getString("PaymentMode");


        price_Of_File = new double[files];
        setProgress(orderKey);
        orderid.setText("Order ID: "+orderKey);


        inititalizeInitialData();

    }

    public void inititalizeInitialData(){

        /////////////////////////////////////////////Setting Shop Details on screen/////////////////////////

        if(isTester){
            shopType = "TestStores";
        }else{
            shopType = "Stores";
        }

        getLocation();

        if(userLat > 0 && userLong > 0) {
            userLoc = new LatLng(userLat, userLong);
        }else{
            getLocation();
        }


        shopLoc = new LatLng(shopLat,shopLong);


        String currentDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
        orderDateTime = currentTime +" " +currentDate;

        getFileNamesForOrder(orderKey);

    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, Select.class);
        startActivity(intent);
        finish();

    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }


    public void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            if (ActivityCompat.checkSelfPermission(OrderPlaced.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (OrderPlaced.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(OrderPlaced.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                Log.d("INNN HEREEEE", "YESS");
            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d("INNN HEREEEE", "YESS");

                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//            Log.d("LAT IS ", String.valueOf(location.getLatitude()));

                if (location != null) {

                    userLat = location.getLatitude();
                    userLong = location.getLongitude();

                    Log.d("LAT IS ", String.valueOf(userLat));
                    Log.d("Long is ", String.valueOf(userLong));
                    userLoc = new LatLng(userLat, userLong);

                } else if (location1 != null) {
                    userLat = location1.getLatitude();
                    userLong = location1.getLongitude();
                    userLoc = new LatLng(userLat, userLong);


                    Log.d("LAT1 IS ", String.valueOf(latitude));
                    Log.d("Long1 is ", String.valueOf(longitude));


                } else if (location2 != null) {
                    userLat = location2.getLatitude();
                    userLong = location2.getLongitude();
                    userLoc = new LatLng(userLat, userLong);

                    Log.d("LAT2 IS ", String.valueOf(latitude));
                    Log.d("Long2 is ", String.valueOf(longitude));


                } else {
                    Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
            }else if(v == findViewById(R.id.OrderDetailsBtn)){
                sendData();
            }else if(v == findViewById(R.id.Directions)){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+userLoc.latitude+","+userLoc.longitude+"&daddr="+shopLat+","+shopLong));
                startActivity(intent);
            }else if(v == findViewById(R.id.backBtn)){
                Intent intent = new Intent(OrderPlaced.this, Select.class);
                startActivity(intent);
                finish();

            }
        }
    };



    public void getFileNamesForOrder(String orderKey){
        Log.d("ORDERKEYPLACED",String.valueOf(orderKey));
//        ref.child("users").child(userId).child("Orders").child(orderKey).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
////                    Log.d("KEYS ",String.valueOf(snapshot));
////                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
//
//                    if(snapshot.getValue().toString().contains("-")){
//                        for(DataSnapshot snapshot1: snapshot.getChildren()){
//                            Log.d("KEYS1 ",String.valueOf(snapshot1.getValue()));
//
//                            if(Objects.equals(snapshot1.getKey(), "fileName")) {
//                                if(fileNames.size() < files) {
////                                    fileNames.add(snapshot1.getValue().toString());
//                                }
//                            }
//                        }
//                    }
//
//                }
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                },1000);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        ref.child("users").child(userId).child("Orders").child(orderKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                Log.d("KEYS ",String.valueOf(map));

                for(DataSnapshot values: dataSnapshot.getChildren()){

//                    Log.d("KEYS ",String.valueOf(values.getKey()));
                    if(Objects.equals(values.getKey(), "fileName")) {
                        if(fileNames.size() < files) {
                            fileNames.add(values.getValue().toString());
                        }
                    }
                    if(Objects.equals(values.getKey(), "fileType")) {
                        if(fileTypes.size() < files) {
                            fileTypes.add(values.getValue().toString());
                        }
                    }
                    if(Objects.equals(values.getKey(), "pageSize")) {
                        if(pageSize.size() < files) {
                            pageSize.add(values.getValue().toString());
                        }
                    }
                    if(Objects.equals(values.getKey(), "orientation")) {
                        if(orientations.size() < files) {
                            orientations.add(values.getValue().toString());
                        }
                    }
                    if(Objects.equals(values.getKey(), "copies")) {
                        if(copies.size() < files) {
                            copies.add(Integer.parseInt(values.getValue().toString()));
                        }
                    }
                    if(Objects.equals(values.getKey(), "colorType")) {
                        if(colors.size() < files) {
                            colors.add(values.getValue().toString());
                        }
                    }
                    if(Objects.equals(values.getKey(),"fileSize")){
                        if(fileSizes.size() < files) {
                            fileSizes.add(values.getValue().toString());
                        }
                    }

                    if(Objects.equals(values.getKey(),"price")){
                        if(pricePerFile.size() < files) {
                            pricePerFile.add(Double.valueOf(values.getValue().toString()));
                        }
                    }
                    if(Objects.equals(values.getKey(),"location")){
                        if(fileLocations.size() < files) {
                            fileLocations.add((values.getValue().toString()));
                        }
                    }
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(fileNames != null && pricePerFile != null) {
                            OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter();
                            orderDetailsLV.setAdapter(orderDetailsAdapter);
                            setDynamicHeight(orderDetailsLV);
//                            if (fileNames.size() > 0 && fileNames.get(0) != null) {
//                                file1.setVisibility(View.VISIBLE);
//                                file1.setText(fileNames.get(0));
//                                file1Price.setVisibility(View.VISIBLE);
//                                file1Price.setText("₹ "+String.valueOf(pricePerFile.get(0)));
//                            }
//                            if (fileNames.size() > 1 && fileNames.get(1) != null ) {
//                                file2.setVisibility(View.VISIBLE);
//                                file2.setText(fileNames.get(1));
//                                file2Price.setVisibility(View.VISIBLE);
//                                file2Price.setText("₹ "+String.valueOf(pricePerFile.get(1)));
//
//                            }
//                            if (fileNames.size() > 2 && fileNames.get(2) != null ) {
//                                file3.setVisibility(View.VISIBLE);
//                                file3.setText(fileNames.get(2));
//                                file3Price.setVisibility(View.VISIBLE);
//                                file3Price.setText("₹ "+String.valueOf(pricePerFile.get(2)));
//
//                            }
//                            if (fileNames.size() > 3 && fileNames.get(3) != null ) {
//                                file4.setVisibility(View.VISIBLE);
//                                file4.setText(fileNames.get(3));
//                                file4Price.setVisibility(View.VISIBLE);
//                                file4Price.setText("₹ "+String.valueOf(pricePerFile.get(3)));
//
//                            }
//                            if (fileNames.size() > 4 && fileNames.get(4) != null ) {
//                                file5.setVisibility(View.VISIBLE);
//                                file5.setText(fileNames.get(4));
//                                file5Price.setVisibility(View.VISIBLE);
//                                file5Price.setText("₹ "+String.valueOf(pricePerFile.get(4)));
//
//                            }

                            double price = 0.0;
                            for(int i =0;i<pricePerFile.size();i++){
                                price_Of_File[i] = pricePerFile.get(i);
                                price = price + pricePerFile.get(i);
                                if(i == pricePerFile.size() - 1) {
                                    totalPrice = price;
                                    Price.setText("₹ " + String.valueOf(price));
                                }
                            }
                        }

                    }
                },500);
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
                        setProgressForOrder(orderKey,orderStatus);
                        paymentMode = String.valueOf(map.get("paymentMode"));
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

    public void sendData(){
        Intent intent = new Intent(OrderPlaced.this,ShowFullDetails.class);
        Bundle extras = new Bundle();

        extras.putString("OrderID",orderID);
        extras.putInt("Files",files);
        extras.putStringArrayList("FileNames",fileNames);
        extras.putStringArrayList("FileType", fileTypes);
        extras.putStringArrayList("PageSize", pageSize);
        extras.putStringArrayList("Orientation", orientations);
        extras.putIntegerArrayList("Copies", copies);
        extras.putStringArrayList("ColorType", colors);
        extras.putStringArrayList("FileSizes",fileSizes);
        extras.putStringArrayList("FileLocations",fileLocations);
        extras.putDoubleArray("PricePerFile",price_Of_File);
        extras.putDouble("TotalPrice",totalPrice);
        extras.putString("PaymentMode",paymentMode);
        intent.putExtras(extras);

        startActivity(intent);
    }



    ArrayList<Bitmap> images = new ArrayList<Bitmap>();


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<Integer> custOrderIDS = new ArrayList<>();


    public class OrderDetailsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return files;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.book_and_price,null);
            TextView fileNameTV, filePriceTV;

            if(convertView != null){

                fileNameTV = convertView.findViewById(R.id.pdfName);
                filePriceTV = convertView.findViewById(R.id.price);

                fileNameTV.setText(fileNames.get(position));
                filePriceTV.setText("₹ "+String.valueOf(pricePerFile.get(position)));
            }
            return convertView;
        }
    }
    public void setDynamicHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        //check adapter if null
        if (adapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
    }
}
