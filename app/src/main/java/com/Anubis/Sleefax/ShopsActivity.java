package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;



public class ShopsActivity extends AppCompatActivity {

    ListView shopsLV;
//    ArrayList<Integer> pageCopies = new ArrayList<Integer>();
//    ArrayList<String> storeID = new ArrayList<>();
    ArrayList<String> pageURL = new ArrayList<>();
//    ArrayList<Uri> pageURL = new ArrayList<>();
    ImageButton crop,back;
    Button no,confirm;
    int copy;
    Double numberOfPages;
    double price;
    String color;


    UserLoc user_loc = new UserLoc();
    Page_Info info = new Page_Info();
    final Location userLoc = new Location("");




    View confirmView;
    TextView orderPrice,confirmOrder,paymentModeTV,tv;
    LinearLayout shopRows;
    int ShopsCount,resultCode,requestCode;
    public static final int REQUEST_LOCATION = 1;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String fileType,orientation,custom,shopType;
    DatabaseReference storeDb = FirebaseDatabase.getInstance().getReference();
    String username,email,pagesize;
    long usernum;
    Intent data;
    Boolean bothSides,isTester;

    //    private FusedLocationProviderClient fusedLocationClient;
    LocationManager locationManager;
    protected LocationListener locationListener;
    protected double latitude;
    protected double longitude;
    protected boolean gps_enabled,network_enabled;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

//    @SuppressLint("MissingPermission")
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        getCurrentUserInfo();
        ActivityCompat.requestPermissions(ShopsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
        getLocation();

        ListView listView = findViewById(R.id.ShopsListView);
        ShopsAdapter adapter = new ShopsAdapter();
        listView.setAdapter(adapter);



        Intent intent = getIntent();
        Bundle extras = intent.getExtras();



        back = findViewById(R.id.back);
        back.setOnClickListener(backListener);
        no = findViewById(R.id.noBtn);
        confirm = findViewById(R.id.confirmBtn);
        confirmView = findViewById(R.id.confirmView);
        orderPrice = findViewById(R.id.OrderPrice);
        shopRows = findViewById(R.id.ShopRows);
        confirmOrder = findViewById(R.id.ConfirmOrderTV);
        paymentModeTV = findViewById(R.id.paymentTV);

        pageURL = extras.getStringArrayList("URLS");
        copy = extras.getInt("Copies");
        color = extras.getString("ColorType");
        fileType = extras.getString("FileType");
        ShopsCount = extras.getInt("ShopCount");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
        pagesize = extras.getString("PageSize");
        orientation = extras.getString("Orientation");
        data = extras.getParcelable("Data");
        bothSides = extras.getBoolean("BothSides");
        custom = extras.getString("Custom");
        numberOfPages = Double.valueOf(extras.getInt("Pages"));

        isTester = extras.getBoolean("IsTester");

        Log.d("FILETYPE",fileType);
        if(isTester){
            shopType = "TestStores";
        }else{
            shopType = "Stores";
        }

//        ShopsCnt =  intent.getIntExtra("ShopCount",1);
        Log.d("URLS ARE ", String.valueOf(pageURL));
        Log.d("COLOR TYPES ARE ", String.valueOf(color));
        Log.d("CUSTOM", String.valueOf(custom));
        Log.d("NOPPP", String.valueOf(numberOfPages));
        Log.d("BOTHSIDES", String.valueOf(bothSides));

        Double cnt=0.0;
        if(!custom.equals("All")){
            if(bothSides) {
                Log.d("CUSTOMCNTPRICE",(custom));
                cnt = Double.parseDouble(custom)/2;

            }else{
                cnt = Double.parseDouble(custom);
            }

        }

        if(cnt>0){
            numberOfPages = cnt;
        }else{
            if(bothSides) {
                numberOfPages = numberOfPages / 2;
            }
        }

        if(color != null){

            if(fileType.equals("application/pdf")){

                if (color.equals("Colors")) {
                    Log.d("PRICE FOR PDF", String.valueOf(bothSides));

                    if(bothSides) {
                        orderPrice.setText("₹ " + ((5 * pageURL.size()) * numberOfPages * copy));
                        price =  ((5 * pageURL.size()) * numberOfPages * copy);
                    }else{
                        orderPrice.setText("₹ " + (5 * pageURL.size()) * numberOfPages * copy);
                        price =  ((5 * pageURL.size())* numberOfPages  * copy);
                    }
                } else {
                    if(bothSides) {
                        orderPrice.setText("₹ " + ((pageURL.size()) * numberOfPages * copy));
                        price =  ((pageURL.size()) * numberOfPages * copy);
                    }else{
                        orderPrice.setText("₹ " + (pageURL.size()) * numberOfPages * copy);
                        price =  ((pageURL.size()) * numberOfPages * copy);
                    }

                }

            }else{

                 Log.d("PRICEFOR","IMAGE");
                if (color.equals("Colors")) {
                    Log.d("BOTHSIDEIMG",String.valueOf(bothSides));
                    Log.d("IMAGENOS",String.valueOf(pageURL.size()));
                    Log.d("IMAGECOPIES",String.valueOf(copy));

                    if(bothSides) {
                        price = ((5 * pageURL.size()) * copy);
                        price = price/2;
                    }else{
                        price = (5 * pageURL.size()) * copy;
                    }
                    orderPrice.setText("₹ " + price);

                } else {
                    Log.d("BOTHSIDEIMG",String.valueOf(bothSides));
                    Log.d("IMAGENOS",String.valueOf(pageURL.size()));
                    Log.d("IMAGECOPIES",String.valueOf(copy));
                    if(bothSides){
                        Log.d("BSSS", String.valueOf(bothSides));
                        price = ((pageURL.size()) * copy);
                        price = price/2;
                    }else {
                        price = (pageURL.size()) * copy;
                    }
                    orderPrice.setText("₹ " + (price));
                    Log.d("PRICE FOR IMG", String.valueOf(price));

                }

            }
        }

        Log.d("FINAL","PRICE "+price);

        confirmView.setVisibility(View.INVISIBLE);
        confirmOrder.setVisibility(View.INVISIBLE);
        no.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.INVISIBLE);
        orderPrice.setVisibility(View.INVISIBLE);
        paymentModeTV.setVisibility(View.INVISIBLE);
    }


    //     Create an anonymous implementation of OnClickListener
    private View.OnClickListener backListener = new View.OnClickListener() {
        public void onClick(View v) {

            if(fileType.contains("application")){
//                Intent intent1 = new Intent(ShopsActivity.this,PdfInfo.class);
//                startActivity(intent1);
                finish();
            }else{
//                Intent intent1 = new Intent(ShopsActivity.this,PageInfo.class);
//                startActivity(intent1);
                finish();
            }

        }
    };


    public int getShopsCount(){
        final int[] count = {1};
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                count[0] = (int) dataSnapshot.getChildrenCount();
                Log.d("Shops cnt ", String.valueOf(info.shopCnt));
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
        return count.length;
    }


    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(ShopsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (ShopsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ShopsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Log.d("INNN HEREEEE","YESS");
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d("INNN HEREEEE","YESS");

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);
//            Log.d("LAT IS ", String.valueOf(location.getLatitude()));

            if (location != null) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.d("LAT IS ", String.valueOf(latitude));
                Log.d("Long is ", String.valueOf(longitude));
                user_loc.latitude = latitude;
                user_loc.longitude = longitude;

            } else  if (location1 != null) {
                    latitude = location1.getLatitude();
                    longitude = location1.getLongitude();


                Log.d("LAT1 IS ", String.valueOf(latitude));
                Log.d("Long1 is ", String.valueOf(longitude));

                user_loc.latitude = latitude;
                user_loc.longitude = longitude;


            } else  if (location2 != null) {
                latitude = location2.getLatitude();
                longitude = location2.getLongitude();

                Log.d("LAT2 IS ", String.valueOf(latitude));
                Log.d("Long2 is ", String.valueOf(longitude));

                user_loc.latitude = latitude;
                user_loc.longitude = longitude;

            }else{

                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void getCurrentUserInfo(){

        ref.child("users").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("INFOOO",dataSnapshot.getKey());
                if(dataSnapshot.getKey().equals("name")){
                    username = dataSnapshot.getValue().toString();
                }
                if(dataSnapshot.getKey().equals("email")){
                    email = dataSnapshot.getValue().toString();
                }

                if(dataSnapshot.getKey().equals("num")){
                    usernum = Long.parseLong(dataSnapshot.getValue().toString());
                }
                Log.d("GETTINGINFO", String.valueOf(true));

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

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

        private static final int earthRadius = 6371;
        public static float calculateDistance(float lat1, float lon1, float lat2, float lon2) {
            float dLat = (float) Math.toRadians(lat2 - lat1);
            float dLon = (float) Math.toRadians(lon2 - lon1);
            float a =
                    (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
            float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
            float d = earthRadius * c;
            return d;
        }


    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    //Shop Adapter getting Shop Info......
    public class ShopsAdapter extends BaseAdapter {

        public Activity mActivity;
        public DatabaseReference ref;
        public ArrayList<DataSnapshot> snapshots;
        public String shopName;
        public Context context;

        @Override
        public int getCount() {
//            getShopsCount();
            return ShopsCount;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
//            final String[] shopNames = new String[10];
//            final String[] locations = new String[10];
            final ArrayList<String> shopNames = new ArrayList<>();
            final ArrayList<String> locations = new ArrayList<>();
            final ArrayList<Double> shopLat = new ArrayList<>();
            final ArrayList<Double> shopLong = new ArrayList<>();
            final ArrayList<Integer> files = new ArrayList<>();
//            final ArrayList<Long> price = new ArrayList<>();
            final ArrayList<Double> distances = new ArrayList<>();
            final ArrayList<Long> numbers = new ArrayList<>();
            final ArrayList<String> storeID = new ArrayList<>();

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();
            final LayoutInflater inflater;
            convertView = getLayoutInflater().inflate(R.layout.shops_row,null);
//            if(convertView != null) {

                    final TextView ShopsName = convertView.findViewById(R.id.ShopsName);
                    final TextView Location = convertView.findViewById(R.id.Location);
                    final TextView Files = convertView.findViewById(R.id.Files);
                    final TextView Price = convertView.findViewById(R.id.Price);
                    final TextView Distance = convertView.findViewById(R.id.Distance);
//                    ImageButton button = convertView.findViewById(R.id.ShopsLVButton);
                    Files.setText("Files: "+pageURL.size());

//                    ShopsName.setText("Shops1");
            userLoc.setLatitude(user_loc.latitude);
            userLoc.setLongitude(user_loc.longitude);
            final View finalConvertView = convertView;

            Log.d("TYPES",shopType);
                ref.child(shopType).addChildEventListener(new ChildEventListener() {

                    final KProgressHUD hud = KProgressHUD.create(ShopsActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Finding print stores.")
                            .setMaxProgress(100);

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                            hud.show();
                        storeID.add(dataSnapshot.getKey());
                        for(DataSnapshot snap:dataSnapshot.getChildren()){
//                                Log.d("SHOPS NAME IS ",snap.getKey());
                            if(snap.getKey().equals("ShopName")){
                                shopNames.add(snap.getValue().toString());
                            }

                            if (snap.getKey().equals("area")) {
                                locations.add(snap.getValue().toString());
                            }

                            if (snap.getKey().equals("latitude")) {
                                shopLat.add((Double) snap.getValue());
                            }

                            if (snap.getKey().equals("longitude")) {
                                shopLong.add((Double) snap.getValue());
                            }

                            if(snap.getKey().equals("num")){
                                numbers.add(Long.parseLong(snap.getValue().toString()));
                            }


////                                while(distance != 0)
////                                {
////                                    // num = num/10
////                                    distance /= 10;
////                                    ++count;
////                                }
////                                Log.d("COUNT IS ", String.valueOf(count));
////                                if(count >6&&count<=8){
////                                    Distance.setText("~"+(int) (distanceFromShop/1000000) + "km");
////                            }
                        }



                        final Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(position<shopLat.size()&&position<shopLong.size()) {
                                    double finalShopLat = shopLat.get(position);
                                    double finalShopLong = shopLong.get(position);

                                    Location shopLoc = new Location("");
                                    shopLoc.setLatitude(finalShopLat);
                                    shopLoc.setLongitude(finalShopLong);

//                                    int distanceFromShop = (int) distance(userLoc.getLatitude(), userLoc.getLongitude(), shopLoc.getLatitude(), shopLoc.getLongitude());
//                                  double distanceFromShop = distance(userLoc.getLatitude(),userLoc.getLongitude(),shopLoc.getLatitude(),shopLoc.getLongitude());

                                    int distanceFromShop = (int) userLoc.distanceTo(shopLoc);


                                    Log.d("UL",userLoc.getLatitude()+"ULON"+userLoc.getLongitude());
                                    Log.d("SL",shopLoc.getLatitude()+"SLONG"+shopLoc.getLongitude());
                                    Log.d("DISTANCE", String.valueOf(distanceFromShop/1000));
                                    distances.add((double) (distanceFromShop / 1000));


                                }

                            }
                        },100);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(position<shopNames.size()) {
                                    ShopsName.setText(shopNames.get(position));
                                    Location.setText(locations.get(position));
                                    Files.setText("Files : " + pageURL.size());
                                    Distance.setText("~" + (distances.get(position)) + "km");
                                }
                            }
                        }, 300);



                        finalConvertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("VIEW ","Tapped");

                                shopRows.setBackgroundColor(Color.GRAY);
                                confirmView.setVisibility(View.VISIBLE);
                                confirmOrder.setVisibility(View.VISIBLE);
                                no.setVisibility(View.VISIBLE);
                                confirm.setVisibility(View.VISIBLE);
                                orderPrice.setVisibility(View.VISIBLE);
                                paymentModeTV.setVisibility(View.VISIBLE);

                                Log.d("PRICE", String.valueOf(price));

                                confirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if(color.equals("Colors")){
                                            processOrder("Confirm Order",storeID.get(position),locations.get(position),shopLat.get(position),shopLong.get(position),shopNames.get(position),numbers.get(position),pageURL.size(),price);
                                        }else{
                                            processOrder("Confirm Order",storeID.get(position),locations.get(position),shopLat.get(position),shopLong.get(position),shopNames.get(position),numbers.get(position),pageURL.size(),price);

                                        }
                                    }
                                });
                                no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        shopRows.setBackgroundColor(Color.WHITE);
                                        confirmOrder.setVisibility(View.INVISIBLE);
                                        confirmView.setVisibility(View.INVISIBLE);
                                        no.setVisibility(View.INVISIBLE);
                                        confirm.setVisibility(View.INVISIBLE);
                                        orderPrice.setVisibility(View.INVISIBLE);
                                        paymentModeTV.setVisibility(View.INVISIBLE);

                                    }
                                });



                            }

                        });

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

//            }
                    return convertView;
        }
    }
    String name,loc,orderKey,orderStatus,shopKey,orderDateTime;
    double shopLat;
    double shopLong;
    int files;
    Long shopNum;



    public void processOrder(String message,String storeID,String loc,Double shopLat, Double shopLong,String ShopName,long num,int files,double price){

//        String uniqueID = UUID.randomUUID().toString();
//        String orderKey = "";
//        orderKey = uniqueID;

        shopKey = storeID;
        this.loc = loc;
        this.shopLat = shopLat;
        this.shopLong = shopLong;
        this.name = ShopName;
        this.files = files;
        this.shopNum = num;






        Intent intent = new Intent(ShopsActivity.this, Payments.class);
        Bundle extras = new Bundle();

        extras.putStringArrayList("URLS", pageURL);
        extras.putString("ShopName", name);
        extras.putString("Location", loc);
        extras.putDouble("ShopLat", shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putInt("Files", files);
        extras.putDouble("Price", price);
        Log.d("PRICE", String.valueOf(price));
        extras.putString("FileType", fileType);
        extras.putString("PageSize", pagesize);
        extras.putString("Orientation", orientation);
        extras.putBoolean("IsTester", isTester);
        extras.putLong("ShopNum", shopNum);

        if (username != null && email != null && usernum > 0) {
            extras.putString("Username", username);
            extras.putString("email", email);
            extras.putLong("UserNumber", usernum);
        }

        extras.putInt("Copies", copy);
        extras.putString("ColorType", color);
        extras.putBoolean("BothSides", bothSides);
        extras.putString("Custom", custom);
//        extras.putString("OrderKey", orderKey);
        extras.putString("ShopKey", storeID);
        extras.putString("UserID", userID);
        extras.putDouble("User Lat", userLoc.getLatitude());
        extras.putDouble("User Long", userLoc.getLongitude());
        extras.putInt("RequestCode", requestCode);
        extras.putInt("ResultCode", resultCode);
        extras.putDouble("Pages", numberOfPages);

        intent.putExtras(extras);
        startActivity(intent);



//        finish();
    }



//    public void startProcessingOrderAnime(final int status){
//        final int[] pStatus = {0};
//        final Handler handler = new Handler();
//        Resources res = getResources();
//        Drawable drawable;
//        drawable = res.getDrawable(R.drawable.circular);
//
//        mProgress.setVisibility(View.VISIBLE);
//        tv.setVisibility(View.VISIBLE);
//
//        mProgress.setProgress(0);   // Main Progress
//        mProgress.setSecondaryProgress(100); // Secondary Progress
//        mProgress.setMax(100); // Maximum Progress
//        mProgress.setProgressDrawable(drawable);
//
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                pStatus[0] = status;
//                while (pStatus[0] < 100) {
////                    pStatus[0] += 1;
//
//                    handler.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            mProgress.setProgress(pStatus[0]);
//                            tv.setText(pStatus[0] + "%");
//
//                        }
//                    });
//                    try {
//                        // Sleep for 200 milliseconds.
//                        // Just to display the progress slowly
//                        Thread.sleep(16); //thread will take approx 3 seconds to finish
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
//    }





}
