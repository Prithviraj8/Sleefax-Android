package com.prithviraj8.copycatandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.prithviraj8.copycatandroid.Services.DirectionsJSONParser;
import com.prithviraj8.copycatandroid.Services.GetDirectionsData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maps extends AppCompatActivity {

    final String TAG = "PathGoogleMapActivity";
    Button getDirection;
    TextView Files, shopName,Loc,price;
    ProgressBar orderProgress;
    String name,loc;

    LatLng shopLoc, userLoc;
    double shopLat;
    double shopLong;
    int files;

    ShopInfo info = new ShopInfo();
    private static final int LOCATION_REQUEST = 500;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    boolean mLocationPermissionGranted;
    FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
//        shopMap.onCreate(mapViewBundle);
//        shopMap.getMapAsync(this);

        shopName = findViewById(R.id.OrderShop);
        Loc = findViewById(R.id.OrderLoc);
        price = findViewById(R.id.OrderPrice);
        Files = findViewById(R.id.OrderFiles);
        orderProgress = findViewById(R.id.OrderProgressBar);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        name = extras.getString("ShopName");
        loc = extras.getString("Location");

        shopName.setText(name);
        Loc.setText(loc);
        Files.setText(""+files);
        files = extras.getInt("Files");
        Log.d("Shop Lat", String.valueOf(shopLat));
        Log.d("Shop Long",String.valueOf(shopLong));

//        String url = getUrl(user.getPosition(), shop.getPosition(), "driving");

//        googleMap.isTrafficEnabled();
        getDirection = findViewById(R.id.Directions);

        userLoc = new LatLng(18.573595,73.875822);
        shopLoc = new LatLng(shopLat,shopLong);
        String sLat = String.valueOf(shopLoc.latitude);
        String sLong = String.valueOf(shopLoc.longitude);
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+userLoc.latitude+","+userLoc.longitude+"&daddr="+shopLat+","+shopLong));
                startActivity(intent1);
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
