package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

class shopinfo{
     public String  ShopsLocation,ShopName,orderStatus,fileType,pageSize,orientation;
     public Double ShopLat,ShopLong;
     public int files, price;
     public long num;
     boolean P_Notified,RT_Notified,IP_Notified,R_Notified;

    public shopinfo(String shopsLocation, String shopName, String orderStatus, Double ShopLat, Double ShopLong,long num,int files, String fileType, String pageSize, String orientation, int price, boolean P_Notified, boolean RT_Notified, boolean IP_Notified, boolean R_Notified) {
         ShopsLocation = shopsLocation;
         ShopName = shopName;
         this.orderStatus = orderStatus;
         this.ShopLat = ShopLat;
         this.ShopLong = ShopLong;
         this.num = num;
         this.files = files;
         this.fileType = fileType;
         this.price = price;
         this.pageSize = pageSize;
         this.orientation = orientation;
         this.P_Notified = P_Notified;
         this.RT_Notified = RT_Notified;
         this.IP_Notified = IP_Notified;
         this.R_Notified = R_Notified;
    }

//     public shopinfo(String shopsLocation, String shopName, String orderStatus, Double ShopLat, Double ShopLong,long num,int files, String fileType, int price) {
//         ShopsLocation = shopsLocation;
//         ShopName = shopName;
//         this.orderStatus = orderStatus;
//         this.ShopLat = ShopLat;
//         this.ShopLong = ShopLong;
//         this.num = num;
//         this.files = files;
//         this.fileType = fileType;
//         this.price = price;
//     }
 }

public class ShopsActivity extends AppCompatActivity {

    ListView shopsLV;
//    ArrayList<Integer> pageCopies = new ArrayList<Integer>();
//    ArrayList<String> storeID = new ArrayList<>();
    ArrayList<String> pageURL = new ArrayList<>();
//    ArrayList<Uri> pageURL = new ArrayList<>();

    int copy;
    String color;

    UserLoc user_loc = new UserLoc();
    Page_Info info = new Page_Info();
    final Location userLoc = new Location("");


    int ShopsCount,resultCode,requestCode;
    public static final int REQUEST_LOCATION = 1;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String fileType,orientation;
    DatabaseReference storeDb;
    String username,email,pagesize;
    long num;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
//        getSupportActionBar().hide();
        FirebaseApp app = FirebaseApp.getInstance("Stores");
        FirebaseDatabase DB = FirebaseDatabase.getInstance(app);
        storeDb = DB.getReferenceFromUrl("https://storeowner-9c355.firebaseio.com/").child("users");



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


        shopsLV = findViewById(R.id.ShopsListView);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        pageURL = extras.getStringArrayList("URLS");
//        pageURL = extras.getParcelableArrayList("URLS");
        copy = extras.getInt("Copies");
        color = extras.getString("ColorTypes");
        fileType = extras.getString("FileType");
//        storeID = extras.getStringArrayList("StoreID");
        ShopsCount = extras.getInt("ShopCount");

        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
//        username = extras.getString("username");
//        email = extras.getString("email");
//        user_num = extras.getLong("num");
        pagesize = extras.getString("PageSize");
        orientation = extras.getString("Orientation");
//        ShopsCnt =  intent.getIntExtra("ShopCount",1);
        Log.d("URLS ARE ", String.valueOf(pageURL));
        Log.d("COLOR TYPES ARE ", String.valueOf(color));


    }
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
                (ShopsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.d("LAT1 IS ", String.valueOf(latitude));
                Log.d("Long1 is ", String.valueOf(longitude));

                user_loc.latitude = latitude;
                user_loc.longitude = longitude;


            } else  if (location2 != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

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

                if(dataSnapshot.getKey().equals("Num")){
                    num = Long.parseLong(dataSnapshot.getValue().toString());
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
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

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
            final ArrayList<Long> price = new ArrayList<>();
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
            ref.child("Stores").addChildEventListener(new ChildEventListener() {

                final KProgressHUD hud = KProgressHUD.create(ShopsActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Finding print stores.")
                        .setMaxProgress(100);

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            hud.show();
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
                                    double finalShopLat = shopLat.get(position);
                                    double finalShopLong = shopLong.get(position);

                                    Location shopLoc = new Location("");
                                    shopLoc.setLatitude(finalShopLat);
                                    shopLoc.setLongitude(finalShopLong);

                                    double distanceFromShop = userLoc.distanceTo(shopLoc);
//                                    double distanceFromShop = distance(userLoc.getLatitude(),userLoc.getLongitude(),shopLoc.getLatitude(),shopLoc.getLongitude());
//                                    Log.d("SHOPLOCIS", String.valueOf(shopLoc.getLatitude()));
                                    distances.add(distanceFromShop);

                                }
                            },300);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    ShopsName.setText(shopNames.get(position));
                                    Location.setText(locations.get(position));
                                    Files.setText("Files : "+pageURL.size());
//                                    Log.d("DISTANCES",String.valueOf(distances.get(position)));
                                    Distance.setText("~"+(Double) (distances.get(position)) + "km");

                                    if(color != null){
                                        if(color.equals("Colors")){
                                            Price.setText("Rs. "+(5*pageURL.size()));
                                        }else{
                                            Price.setText("Rs. "+(pageURL.size()));
                                        }
                                    }


                                    hud.dismiss();
                                }
                            }, 500);



                            finalConvertView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("VIEW ","Tapped");

                                    if(color.equals("Colors")){
//                                        Log.d("STID",storeID.get(position));
//                                        Log.d("LOC",locations.get(position));
//                                        Log.d("LAT", String.valueOf(shopLat.get(position)));
//                                        Log.d("LAT", String.valueOf(shopLong.get(position)));
//                                        Log.d("LAT", String.valueOf(shopNames.get(position)));
//                                        Log.d("LAT", String.valueOf(numbers.get(position)));

                                        showErrorDialog("Confirm Order",storeID.get(position),locations.get(position),shopLat.get(position),shopLong.get(position),shopNames.get(position),numbers.get(position),pageURL.size(),(5*pageURL.size()),pagesize);
                                    }else{
                                        showErrorDialog("Confirm Order",storeID.get(position),locations.get(position),shopLat.get(position),shopLong.get(position),shopNames.get(position),numbers.get(position),pageURL.size(),pageURL.size(),pagesize);

                                    }

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


    public void showErrorDialog(String message,String storeID,String loc,Double shopLat, Double shopLong,String ShopName,long num,int files,int price,String pagesize){

//        new androidx.appcompat.app.AlertDialog.Builder(this)
//                .setTitle("Confirmation")
//                .setMessage(message)
//                .setPositiveButton(android.R.string.ok,null)
//                .setIcon(android.R.drawable.ic_input_add)
//                .show();
        String uniqueID = UUID.randomUUID().toString();
                 String orderKey = "";

//        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
//
//         shopinfo orderInfo = new shopinfo(loc,ShopName,"Placed",shopLat,shopLong,num,files,fileType,pagesize,orientation,price,false,false,false,false);
//         UserInfo userinfo = new UserInfo(username,email,num,"android");
//
//
//         storeDb = storeDb.child(storeID).child("Orders").child(userID).child(uniqueID);
//         storeDb.setValue(userinfo);
//         db = db.child("users").child(userID).child("Orders").child(storeID).child(uniqueID);
//         db.setValue(orderInfo);
//
//        for(int i =0;i<pageURL.size();i++){
//            singlePageInfo single = new singlePageInfo(pageURL.get(i),color,copy,fileType,pagesize,orientation);
//            db.push().setValue(single);
//            storeDb.push().setValue(single);
//            orderKey = uniqueID;
//        }
            orderKey = uniqueID;

        Intent intent = new Intent(ShopsActivity.this, OrderPlaced.class);
        Bundle extras = new Bundle();

//        extras.putParcelableArrayList("Uris",pageURL);
        extras.putStringArrayList("URLS", pageURL);
        extras.putString("ShopName",ShopName);
        extras.putString("Location",loc);
        extras.putDouble("ShopLat",shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putLong("Number",num);
        extras.putInt("Files",files);
        extras.putInt("Price",price);
        extras.putString("FileType",fileType);
        extras.putString("PageSize",pagesize);
        extras.putString("Orientation",orientation);
        extras.putString("Username",username);
        extras.putString("email",email);
        extras.putInt("Copies",copy);
        extras.putString("ColorType",color);

        extras.putString("OrderKey",orderKey);
        extras.putString("ShopKey",storeID);
//      extras.putString("OrderKey",uniqueID);
        extras.putString("UserID",userID);
        extras.putDouble("User Lat",userLoc.getLatitude());
        extras.putDouble("User Long",userLoc.getLongitude());

        extras.putInt("RequestCode",requestCode);
        extras.putInt("ResultCode",resultCode);
        Log.d("USERLAT", String.valueOf(userLoc.getLatitude()));
        Log.d("USERLAT", String.valueOf(userLoc.getLongitude()));

        intent.putExtras(extras);
        startActivity(intent);
//        finish();
    }
}
