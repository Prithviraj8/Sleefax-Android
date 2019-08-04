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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.Toast;

import java.util.ArrayList;

 class shopLoc{
     String ShopsLocation,ShopName;

     public shopLoc(String shopsLocation, String shopName) {
         ShopsLocation = shopsLocation;
         ShopName = shopName;
     }
 }
public class ShopsActivity extends AppCompatActivity {

    ListView shopsLV;
    ArrayList<Integer> pageCopies = new ArrayList<Integer>();
    ArrayList<String> colorTypes = new ArrayList<String>();
    ArrayList<String> pageURL = new ArrayList<String>();

    UserLoc user_loc = new UserLoc();
    Page_Info info = new Page_Info();
    final Location userLoc = new Location("");

    double shopLat = 0.0;
    double shopLong = 0.0;

    int ShopsCnt=0;
    private static final int REQUEST_LOCATION = 1;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

//    private FusedLocationProviderClient fusedLocationClient;
    LocationManager locationManager;
    protected LocationListener locationListener;
    protected double latitude;
    protected double longitude;
    protected boolean gps_enabled,network_enabled;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

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
        pageURL = intent.getStringArrayListExtra("URLS");
        pageCopies = intent.getIntegerArrayListExtra("Copies");
        colorTypes = intent.getStringArrayListExtra("ColorTypes");
//        ShopsCnt =  intent.getIntExtra("ShopCount",1);
//        Log.d("S CNT ", String.valueOf(ShopsCnt));
        Log.d("URLS ARE ", String.valueOf(pageURL));
        Log.d("COLOR TYPES ARE ", String.valueOf(colorTypes));


    }
    private int getShopsCount(){
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
    private void getLocation() {
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
            Log.d("LAT IS ", String.valueOf(location.getLatitude()));

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
    public class ShopsAdapter extends BaseAdapter {

        private Activity mActivity;
        private DatabaseReference ref;
        private ArrayList<DataSnapshot> snapshots;
        private String shopName;
        private Context context;

        @Override
        public int getCount() {
//            getShopsCount();
            return getShopsCount();
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
            final String[] shopNames = new String[10];
            final String[] locations = new String[10];


            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();
            final LayoutInflater inflater;
            convertView = getLayoutInflater().inflate(R.layout.shops_row,null);
//            if(convertView != null) {

                    final TextView ShopsName = convertView.findViewById(R.id.ShopsName);
                    final TextView Location = convertView.findViewById(R.id.Location);
                    TextView Files = convertView.findViewById(R.id.Files);
                    TextView Price = convertView.findViewById(R.id.Price);
                    final TextView Distance = convertView.findViewById(R.id.Distance);
                    ImageButton button = convertView.findViewById(R.id.ShopsLVButton);


//                    ShopsName.setText("Shops1");

            final View finalConvertView = convertView;
            ref.child("Stores").addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            for(DataSnapshot snap:dataSnapshot.getChildren()){
//                                Log.d("SHOPS NAME IS ",snap.getKey());
                                shopNames[position] = dataSnapshot.getKey();
                                ShopsName.setText(shopNames[position]);
                                if(snap.getKey().equals("area")){
//                                    Log.d("Lat",snap.getValue().toString());
                                    locations[position] = snap.getValue().toString();
                                    Location.setText(locations[position]);

                                }
                                userLoc.setLatitude(user_loc.latitude);
                                userLoc.setLongitude(user_loc.longitude);
                                final Location shopLoc = new Location("");


                                if(snap.getKey().equals("latitude")){
//                                    shopLoc.setLatitude((Double)snap.getValue());
                                    shopLat = (Double)(snap.getValue());

                                }
                                if(snap.getKey().equals("longitude")){
//                                    shopLoc.setLongitude((Double)(snap.getValue()));
                                    shopLong = (Double)(snap.getValue());
                                }
                                int count = 0;
                                double distance;
                                double distanceFromShop = userLoc.distanceTo(shopLoc);
                                distance = distanceFromShop;
//                                while(distance != 0)
//                                {
//                                    // num = num/10
//                                    distance /= 10;
//                                    ++count;
//                                }
//                                Log.d("COUNT IS ", String.valueOf(count));
//                                if(count >6&&count<=8){
//                                    Distance.setText("~"+(int) (distanceFromShop/1000000) + "km");
//                                }
                                Distance.setText("~"+(int) (distanceFromShop/1000000) + "km");

                                final double finalShopLat = shopLat;
                                final double finalShopLong = shopLong;



                                finalConvertView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("VIEW ","Tapped");
                                        showErrorDialog("Confirm Order",locations[position],shopNames[position],pageURL.size());
                                    }
                                });

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

//            }
                    return convertView;
        }
    }
    private void showErrorDialog(String message,String loc,String ShopName,int files){
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
         DatabaseReference db = FirebaseDatabase.getInstance().getReference();

         shopLoc location = new shopLoc(loc,ShopName);
//         db.child("users").child(userID).child("Orders").push().setValue(location);
         db = db.child("users").child(userID).child("Orders").push();
         db.setValue(location);
        for(int i =0;i<pageURL.size();i++){

            singlePageInfo single = new singlePageInfo(pageURL.get(i),colorTypes.get(i),pageCopies.get(i));

            db.push().setValue(single);

        }
        Log.d("SSS LAT IS", String.valueOf((shopLat)));
        Log.d("SSS LONG IS", String.valueOf((shopLong)));

        Intent intent = new Intent(ShopsActivity.this,Maps.class);
        Bundle extras = new Bundle();
        extras.putString("ShopName",ShopName);
        extras.putString("Location",loc);
        extras.putDouble("ShopLat",shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putDouble("Files",files);
        ShopInfo info = new ShopInfo();
        info.latitude = shopLat;
        info.longitude = shopLong;
        intent.putExtra("User Loc",userLoc);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
