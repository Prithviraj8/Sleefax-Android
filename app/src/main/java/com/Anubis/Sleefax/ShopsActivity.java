package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class ShopsActivity extends AppCompatActivity {

    ListView shopsLV;
//    ArrayList<Integer> pageCopies = new ArrayList<Integer>();
//    ArrayList<String> storeID = new ArrayList<>();
//    ArrayList<Uri> urls = new ArrayList<>();
    ImageButton back;
    Button no,confirm;
    int copy;
    ArrayList<Double> price = new ArrayList<>();
    String color;


    UserLoc user_loc = new UserLoc();
//    Page_Info info = new Page_Info();
    final Location userLoc = new Location("");

    RelativeLayout relativeLayoutUpper;

    View confirmView;
    TextView orderPrice,confirmOrder,paymentModeTV,tv;
    LinearLayout shopRows;
    int ShopsCount,resultCode,requestCode;
    public static final int REQUEST_LOCATION = 1;
    String userID,customVal;
    String fileType,orientation,custom,shopType;
    DatabaseReference storeDb = FirebaseDatabase.getInstance().getReference();
    String username,email,pagesize;
    long usernum;
    Intent data;
    Boolean isTester,newUser;

    int mScreenHeight;

    RelativeLayout proceedLayout;

    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<String> customValues = new ArrayList<>();
    ArrayList<Integer> numberOfPages = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> fileSizes = new ArrayList<>();
    ArrayList<Integer> customPage1 = new ArrayList<>();
    ArrayList<Integer> customPage2 = new ArrayList<>();
    double pricePerFile[];
    double totalPrice;

    //    private FusedLocationProviderClient fusedLocationClient;
    LocationManager locationManager;
    protected LocationListener locationListener;
    protected double latitude;
    protected double longitude;
    protected boolean gps_enabled,network_enabled;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

//    @SuppressLint("MissingPermission")
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;

        relativeLayoutUpper = findViewById(R.id.view6);
        if(relativeLayoutUpper.getHeight() == 0)
            expandView(relativeLayoutUpper,0,mScreenHeight/4 );

//        getCurrentUserInfo();
        ActivityCompat.requestPermissions(ShopsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            final Intent poke = new Intent();
//            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//            poke.setData(Uri.parse("3"));
//            sendBroadcast(poke);

            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

//        getLocation();

        ListView listView = findViewById(R.id.ShopsListView);
        ShopsAdapter adapter = new ShopsAdapter();
        listView.setAdapter(adapter);



        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        proceedLayout = findViewById(R.id.proceed_for_payment);



        back = findViewById(R.id.back);
        back.setOnClickListener(backListener);
        no = findViewById(R.id.noBtn);
        confirm = findViewById(R.id.confirmBtn);
        confirmView = findViewById(R.id.confirmView);
        orderPrice = findViewById(R.id.OrderPrice);
        shopRows = findViewById(R.id.ShopRows);
        confirmOrder = findViewById(R.id.ConfirmOrderTV);
        paymentModeTV = findViewById(R.id.paymentTV);

        urls = extras.getStringArrayList("URLS");
        copies = extras.getIntegerArrayList("Copies");
        colors = extras.getStringArrayList("ColorType");
        fileTypes = extras.getStringArrayList("FileType");
        ShopsCount = extras.getInt("ShopCount");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
        pageSize = extras.getStringArrayList("PageSize");
        orientations = extras.getStringArrayList("Orientation");
        data = extras.getParcelable("Data");
        bothSides = extras.getBooleanArray("BothSides");
        customPages = extras.getStringArrayList("Custom");
        numberOfPages = extras.getIntegerArrayList("Pages");
        newUser = extras.getBoolean("NewUser");
        customValues = extras.getStringArrayList("CustomValue");
        fileNames = extras.getStringArrayList("FileNames");
        fileSizes = extras.getStringArrayList("FileSizes");

        customPage1 = extras.getIntegerArrayList("CustomPages1");
        customPage2 = extras.getIntegerArrayList("CustomPages2");


        pricePerFile = extras.getDoubleArray("PricePerFile");
        totalPrice = extras.getDouble("TotalPrice");

        if(!newUser){
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        isTester = extras.getBoolean("IsTester");

        if(isTester){
            shopType = "TestStores";
        }else{
            shopType = "Stores";
        }

//        Toast.makeText(this, "PDFCNT3 "+urls.size(), Toast.LENGTH_SHORT).show();

//        calculatePrice();



        confirmView.setVisibility(View.INVISIBLE);
        confirmOrder.setVisibility(View.INVISIBLE);
        no.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.INVISIBLE);
        orderPrice.setVisibility(View.INVISIBLE);
        paymentModeTV.setVisibility(View.INVISIBLE);
    }


    //Create an anonymous implementation of OnClickListener
    private View.OnClickListener BtnListener = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onClick(View v) {

            if(v == findViewById(R.id.showtime)){

            }
        }
    };

            double finalPrice = 0.0;
    public void calculatePrice(){
        ArrayList<Integer> cnt = new ArrayList<>();

        for(int i =0;i<urls.size();i++) {

//            Log.d("CP ",customPages.get(i));
//            Log.d("BS ", String.valueOf(bothSides[i]));
//            Log.d("COL ",String.valueOf(colors.get(i)));
//            Log.d("NOP ",String.valueOf(numberOfPages[i]));
//            Toast.makeText(this, "CP "+customPages.get(i), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "BS "+bothSides[i], Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "CV "+customPages.get(i), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "NOP "+numberOfPages[i], Toast.LENGTH_SHORT).show();

            if(bothSides[i]){
                if(fileTypes.get(i).contains("application")){
                    if(!customPages.get(i).equals("All")){

                    }
                }else{

                }
            }


            if (!customPages.get(i).equals("All")) {
                Log.d("CV ",String.valueOf(customValues.get(i)));

                if (bothSides[i]) {
                    cnt.add((int) (Double.parseDouble(customValues.get(i)) / 2));
                } else {
                    cnt.add((int) Double.parseDouble(customValues.get(i)));
                }

            }

            if(cnt.size()>0) {
                if (cnt.get(i) > 0) {
//                    numberOfPages[i] = (cnt.get(i));
                    numberOfPages.add(i,cnt.get(i));
                } else {
                    if (bothSides[i]) {
//                        numberOfPages[i] = numberOfPages[i] / 2;
                        numberOfPages.add(i,numberOfPages.get(i)/2);
                    }
                }
            }
            if (colors.get(i) != null) {

                if (fileTypes.get(i).contains("application")) {

                    if (colors.get(i).equals("Colors")) {
                        Log.d("PRICE FOR PDF", String.valueOf(bothSides));

                        if (bothSides[i]) {
                            price.add(i, (double) ((5 * urls.size()) * numberOfPages.get(i) * copies.get(i)));
                        } else {
                            price.add(i, (double) ((5 * urls.size()) * numberOfPages.get(i) * copies.get(i)));
                        }
                    } else {
                        if (bothSides[i]) {
                            price.add(i, (double) ((urls.size()) * numberOfPages.get(i) * copies.get(i)));
                        } else {
                            price.add(i, (double) ((urls.size()) * numberOfPages.get(i) * copies.get(i)));
                        }

                    }

                } else {

                    if (colors.get(i).equals("Colors")) {

                        if (bothSides[i]) {
                            price.add(i, (double) (5 * urls.size() * copies.get(i))/2);
//                            price = price.get(i) / 2;
                        } else {
                            price.add(i, (double) (5 * urls.size() * copies.get(i)));
                        }
//                        orderPrice.setText("₹ " + price);

                    } else {
                        if (bothSides[i]) {
                            price.add(i, (double) ((urls.size()) * copies.get(i))/2);
//                            price = price / 2;
                        } else {
                            price.add(i, (double) (urls.size() * copies.get(i)));
                        }
//                        orderPrice.setText("₹ " + (price));
                        Log.d("PRICE FOR IMG", String.valueOf(price));

                    }

                }
            }

            //setting final price of order for multiple files...
            if(i == urls.size()-1){
                for(int j = 0; j < price.size(); j++) {
                    finalPrice += price.get(j);
                    if(j == price.size()-1){
                        Toast.makeText(this, "FINAL PRICE "+(finalPrice/2), Toast.LENGTH_SHORT).show();
//                        orderPrice.setText((int) finalPrice);
                    }
                }
            }
        }
    }

    //     Create an anonymous implementation of OnClickListener
    private View.OnClickListener backListener = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
            if(fileTypes.get(0).contains("application")){
//                Intent intent1 = new Intent(ShopsActivity.this,PdfInfo.class);
//                startActivity(intent1);
            }else{
//                Intent intent1 = new Intent(ShopsActivity.this,PageInfo.class);
//                startActivity(intent1);
            }

        }
    };




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
        int shortAnimationDuration;

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


            // Getting prices set by the shopkeeper while signing up////
            final ArrayList<Integer> bwBothSidesPrice = new ArrayList<>();
            final ArrayList<Integer> bwPrice = new ArrayList<>();
            final ArrayList<Integer> colorBothSidesPrice = new ArrayList<>();
            final ArrayList<Integer> colorPrice = new ArrayList<>();


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
                    final TextView Price = convertView.findViewById(R.id.OrderPrice);
                    final TextView Distance = convertView.findViewById(R.id.Distance);
                    final RelativeLayout ShopLayout = convertView.findViewById(R.id.shop);
                    final ImageButton shopDetails = convertView.findViewById(R.id.showtime);




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
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        ///Getting shop details///
                        shopNames.add(String.valueOf(map.get("ShopName") != null ? map.get("ShopName") : "Shop Name NA"));
                        locations.add(String.valueOf(map.get("area") != null ? map.get("area") : "Area NA"));
                        shopLat.add(Double.parseDouble(String.valueOf(map.get("latitude") != null ?  map.get("latitude") : 0.00)));
                        shopLong.add(Double.parseDouble(String.valueOf(map.get("longitude") != null ? map.get("longitude"): 0.00)));
                        numbers.add(Long.parseLong(String.valueOf(map.get("shopNo") != null ? map.get("shopNo"): 000)));

                        ////Getting prices of various types of print from db .///
                        bwBothSidesPrice.add((int) Double.parseDouble(String.valueOf(map.get("blackAndwhitePrintOutBothSidesPerPage") != null ? map.get("blackAndwhitePrintOutBothSidesPerPage"): 0)));
                        colorBothSidesPrice.add((int) Double.parseDouble(String.valueOf(map.get("colorPrintOutBothSidesPerPage") != null ? map.get("colorPrintOutBothSidesPerPage") : 0)));
                        bwPrice.add((int) Double.parseDouble(String.valueOf(map.get("blackAndwhitePrintOutSingleSidePerPage") != null ? map.get("blackAndwhitePrintOutSingleSidePerPage") : 0)));
                        colorPrice.add((int) Double.parseDouble(String.valueOf(map.get("colorPrintOutSingleSidePerPage") != null ? map.get("colorPrintOutSingleSidePerPage") : 0)));


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

                                    int distanceFromShop = (int) userLoc.distanceTo(shopLoc);
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
                                    Distance.setText("~" + (distances.get(position)) + "km");

                                }
                            }
                        }, 300);



                        finalConvertView.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {

//                                Drawable initialDrawable = getDrawable(R.drawable.white_bg_layout);
//                                ShopLayout.setBackground(initialDrawable);


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    Log.d("FOCUS",String.valueOf(finalConvertView.getFocusable()));
                                }

                                Drawable drawable = getDrawable(R.drawable.outline);
                                ShopLayout.setBackground(drawable);
                                proceedLayout.setVisibility(View.VISIBLE);

                                proceedLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        processOrder(storeID.get(position),locations.get(position),shopLat.get(position),shopLong.get(position),shopNames.get(position),numbers.get(position),urls.size(),1);
                                    }
                                });

                                ////// Haven't calculated price yet for selection of multiple orders//////
                                 //   processOrder(storeID.get(position),locations.get(position),shopLat.get(position),shopLong.get(position),shopNames.get(position),numbers.get(position),urls.size(),1);

                            }

                        });


                        shopDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(ShopsActivity.this,ShopDetailsActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("ShopID",storeID.get(position));
                                bundle.putBoolean("IsTester",isTester);
                                bundle.putString("ShopType",shopType);
                                intent.putExtras(bundle);
                                startActivity(intent);

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
    String shopName,loc,orderKey,orderStatus,shopKey,orderDateTime;
    double shopLat;
    double shopLong;
    int files;
    Long shopNum;



    public void processOrder(String storeID,String loc,Double shopLat, Double shopLong,String ShopName,long num,int files,double price){

//        String uniqueID = UUID.randomUUID().toString();
//        String orderKey = "";
//        orderKey = uniqueID;

        shopKey = storeID;
        this.loc = loc;
        this.shopLat = shopLat;
        this.shopLong = shopLong;
        this.shopName = ShopName;
        this.files = files;
        this.shopNum = num;
        Intent intent;
        Bundle extras;

        if(!newUser) {
            intent = new Intent(ShopsActivity.this, Payments.class);
            extras = new Bundle();
        }else{
            intent = new Intent(ShopsActivity.this, SignInActivity.class);
            extras = new Bundle();
            extras.putBoolean("SignUp",true);
            extras.putBoolean("NewUser",true);
        }

        extras.putStringArrayList("URLS", urls);
        extras.putString("ShopName", shopName);
        extras.putString("Location", loc);
        extras.putDouble("ShopLat", shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putInt("Files", files);
        extras.putIntegerArrayList("Copies", copies);
        extras.putStringArrayList("ColorType", colors);
        extras.putStringArrayList("Custom", customPages);
        extras.putStringArrayList("FileType", fileTypes);
        extras.putStringArrayList("PageSize", pageSize);
        extras.putStringArrayList("Orientation", orientations);
        extras.putIntegerArrayList("Pages", numberOfPages);
        extras.putStringArrayList("FileNames",fileNames);
        extras.putStringArrayList("FileSizes",fileSizes);
        extras.putBoolean("IsTester", isTester);
        extras.putLong("ShopNum", shopNum);
        extras.putString("ShopKey", storeID);
        extras.putString("UserID", userID);
        extras.putDouble("User Lat", userLoc.getLatitude());
        extras.putDouble("User Long", userLoc.getLongitude());
        extras.putIntegerArrayList("CustomPages1",customPage1);
        extras.putIntegerArrayList("CustomPages2",customPage2);
        extras.putDoubleArray("PricePerFile",pricePerFile);
        extras.putDouble("TotalPrice",totalPrice);

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

    public void expandView(final View v, int initialHt, int finalHt){




        ValueAnimator slideAnimator = ValueAnimator.ofInt(initialHt,finalHt + 50).setDuration(1000);
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





}
