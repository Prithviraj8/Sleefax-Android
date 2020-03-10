package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class YourOrders extends AppCompatActivity {


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    SwipeRefreshLayout pullToRefresh;
    ImageButton back;
    private int shortAnimationDuration;

    int orderCnt;
    ListView listView;
    YourOrdersAdapter yourOrdersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_orders);
//        getSupportActionBar().hide();

//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
//        int orders = extras.getInt("Orders Count");

        getOrders();
        back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(YourOrders.this,Select.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();

            }
        });

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.RefreshLayout);




        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("REFRESHING", "onRefresh called from SwipeRefreshLayout");
                listView.setAdapter(yourOrdersAdapter);
                pullToRefresh.setRefreshing(false);

            }
        });
    }
    public void getOrders(){

        final ArrayList<String> orderkey = new ArrayList<>();
        final ArrayList<String> shopKey = new ArrayList<>();

//        setProgressForOrder();

        ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Orders").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                shopKey.add(dataSnapshot.getKey());
                for(DataSnapshot orderIDS: dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>) orderIDS.getValue();
                    if(String.valueOf(map.get("orderStatus")).equals("Done")){
//                        cnt = (int) (dataSnapshot.getChildrenCount()+cnt);
                        orderCnt = orderCnt + 1;
                        Log.d("ORDERDONE",String.valueOf(orderCnt));
                        yourOrderLV();
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

    public void yourOrderLV(){
        listView = findViewById(R.id.YourOrderLV);
        yourOrdersAdapter = new YourOrdersAdapter();
        listView.setAdapter(yourOrdersAdapter);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(YourOrders.this,Select.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public class YourOrdersAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            Log.d("Ordersis", String.valueOf(orderCnt));
            return  orderCnt;
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


            final ArrayList<String> shopNames = new ArrayList<>();
            final ArrayList<String> locations = new ArrayList<>();
            final ArrayList<String> orderStatus = new ArrayList<>();
            final ArrayList<String> orderkey = new ArrayList<>();
            final ArrayList<String> orderDate = new ArrayList<>();
            final ArrayList<String> paymentModes = new ArrayList<>();

            final ArrayList<String> shopKey = new ArrayList<>();
            final ArrayList<Double> shopLat = new ArrayList<>();
            final ArrayList<Double> shopLong = new ArrayList<>();
            final ArrayList<Integer> files = new ArrayList<>();
            final ArrayList<Double> price = new ArrayList<>();
            final ArrayList<Integer> ids = new ArrayList<>();
            final ArrayList<Integer>[] custOrderids = new ArrayList[]{new ArrayList<>()};


//            final int[] cnt = {0};
            final LayoutInflater inflater;
            convertView = getLayoutInflater().inflate(R.layout.your_order_row,null);

            if(convertView != null){

                final TextView ShopsName = convertView.findViewById(R.id.ShopsName);
                final TextView Location = convertView.findViewById(R.id.Location);
                final TextView Files = convertView.findViewById(R.id.Files);
                final TextView Price = convertView.findViewById(R.id.Price);
                final TextView orderDateAndTime = convertView.findViewById(R.id.orderDate);
                final TextView paymentModeTV = convertView.findViewById(R.id.paymentModeTV);

                final TextView OrderStatus = convertView.findViewById(R.id.OrderStatus);
                final RoundCornerProgressBar progressBar;
                final View contentView = convertView.findViewById(R.id.animateView);
                final TextView orderIDTV = convertView.findViewById(R.id.orderIDTV);

//                progressBar.setProgress(25);

                final View finalConvertView = convertView;

                ref.child("users").child(userID).child("Orders").addChildEventListener(new ChildEventListener() {
                    int cnt=0;
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        for(DataSnapshot SNAP:dataSnapshot.getChildren()) {
                            Log.d("ORDERKEY",SNAP.getKey());
                            shopKey.add(dataSnapshot.getKey());
                            orderkey.add(SNAP.getKey());
//                            orderCnt = orderkey.size();

                        }
//                   }
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

                final Handler handler = new Handler();
                final View finalConvertView1 = convertView;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finalConvertView1.setVisibility(View.GONE);
                        // Retrieve and cache the system's default "short" animation time.
                        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

//                        orderCnt = orderkey.size();
                        for (int i = 0; i < shopKey.size(); i++) {
//                            Log.d("SHOPKEY", shopKey.get(i));

                            ref.child("users").child(userID).child("Orders").child(shopKey.get(i)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot orderKeys) {

                                    for(DataSnapshot info: orderKeys.getChildren()) {
                                        Map<String, Object> map = (Map<String, Object>) info.getValue();
                                        if(String.valueOf(map.get("orderStatus")).equals("Done")) {
                                            files.add(Integer.parseInt(String.valueOf(map.get("files"))));
                                            locations.add(String.valueOf(map.get("ShopsLocation")));
                                            shopNames.add(String.valueOf(map.get("ShopName")));
                                            shopLat.add(Double.parseDouble(String.valueOf(map.get("ShopLat"))));
                                            shopLong.add(Double.parseDouble(String.valueOf(map.get("ShopLong"))));
                                            orderStatus.add(String.valueOf(map.get("orderStatus")));
                                            price.add(Double.parseDouble(String.valueOf(map.get("price"))));
                                            orderDate.add(String.valueOf(map.get("orderDateTime")));
                                            paymentModes.add(String.valueOf(map.get("paymentMode")));
                                            ids.add(Integer.valueOf(String.valueOf(map.get("id"))));
                                        }


//                                        for (DataSnapshot snap : info.getChildren()) {
//
//                                            if (snap.getKey().equals("files")) {
//                                                files.add(Integer.parseInt(snap.getValue().toString()));
//                                            }
//
//                                            if (snap.getKey().equals("ShopsLocation")) {
//                                                Log.d("LOCATIONS", snap.getValue().toString());
//                                                locations.add(snap.getValue().toString());
//                                            }
//
//                                            if (snap.getKey().equals("ShopName")) {
//                                                shopNames.add(snap.getValue().toString());
//                                            }
//
//                                            if (snap.getKey().equals("ShopLat")) {
//                                                shopLat.add((Double) snap.getValue());
//                                            }
//
//                                            if (snap.getKey().equals("ShopLong")) {
//                                                shopLong.add((Double) snap.getValue());
//                                            }
//
//                                            if (snap.getKey().equals("orderStatus")) {
//                                                orderStatus.add(snap.getValue().toString());
//                                            }
//
//                                            if (snap.getKey().equals("price")) {
//                                                price.add(Double.parseDouble(snap.getValue().toString()));
//                                            }
//                                            if (snap.getKey().equals("orderDateTime")) {
//                                                orderDate.add(snap.getValue().toString());
//                                            }
//                                            if (snap.getKey().equals("paymentMode")) {
//                                                paymentModes.add(snap.getValue().toString());
//                                                Log.d("PAYMENTMODES", snap.getValue().toString());
//
//                                            }
////                                            if (snap.getKey().equals("id")) {
////                                                ids.add(Integer.valueOf(snap.getValue().toString()));
////                                                custOrderids[0].add(Integer.valueOf(snap.getValue().toString()));
////                                            }
//                                        }
                                    }



                                    final Handler handler1 = new Handler();
                                    handler1.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finalConvertView1.setAlpha(0f);
                                            finalConvertView1.setVisibility(View.VISIBLE);
                                            finalConvertView1.animate()
                                                    .alpha(1f)
                                                    .setDuration(shortAnimationDuration)
                                                    .setListener(null);

//                                            Collections.reverse(shopNames);
//                                            Collections.reverse(locations);
//                                            Collections.reverse(orderStatus);
//                                            Collections.reverse(files);
//                                            Collections.reverse(price);
//                                            Collections.reverse(orderkey);
//                                            Collections.reverse(shopKey);
//                                            Collections.reverse(shopLat);
//                                            Collections.reverse(shopLong);
//                                            Collections.reverse(paymentModes);

//                                            Log.d("POSITION",String.valueOf(position));
//                                            Log.d("CUSTORDE",String.valueOf(ids.size()));
//                                            Collections.sort(ids);
//                                            for(int i=ids.size()-1;i>=0;i--){
//                                                for(int j=0;j<custOrderids[0].size();j++){
//                                                    if(custOrderids[0].get(j).equals(ids.get(i))){
////                                                        if(orderStatus.get(j).equals("Ready")) {
//                                                            Location.setText(locations.get(j));
//                                                            ShopsName.setText(shopNames.get(j));
//                                                            OrderStatus.setText(orderStatus.get(j));
//                                                            Files.setText("Files : " + files.get(j));
//                                                            Price.setText("Price : ₹" + price.get(j));
//                                                            orderDateAndTime.setText(orderDate.get(j));
//                                                            orderIDTV.setText("Order ID : " + orderkey.get(j));
//                                                            paymentModeTV.setText(paymentModes.get(j));
//                                                            break;
////                                                        }
//                                                    }
//                                                }
//                                            }
                                                    Location.setText(locations.get(position));
                                                    ShopsName.setText(shopNames.get(position));
                                                    OrderStatus.setText(orderStatus.get(position));
                                                    Files.setText("Files : " + files.get(position));
                                                    Price.setText("Price : ₹" + price.get(position));
                                                    orderDateAndTime.setText(orderDate.get(position));
                                                    orderIDTV.setText("Order ID : " + orderkey.get(position));
                                                    paymentModeTV.setText(paymentModes.get(position));

//                                                    if ((orderStatus.get(position)).equals("Placed")) {
//                                                        OrderStatus.setBackgroundResource(R.drawable.orderstatusview1);
//                                                    }
//                                                    if ((orderStatus.get(position)).equals("Retrieved")) {
//                                                        OrderStatus.setBackgroundResource(R.drawable.orderstatusview2);
//
//                                                    }
//                                                    if ((orderStatus.get(position)).equals("In Progress")) {
//                                                        OrderStatus.setBackgroundResource(R.drawable.orderstatusview3);
//
//                                                    }
//                                                    if ((orderStatus.get(position)).equals("Ready")){
//                                                        OrderStatus.setBackgroundResource(R.drawable.orderstatusview4);
//                                                    }
                                                    if ((orderStatus.get(position)).equals("Done")){
                                                        OrderStatus.setBackgroundResource(R.drawable.orderstatusview3);
                                                    }

                                        }
                                    }, 10);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    finalConvertView1.animate()
                                            .alpha(0f)
                                            .setDuration(shortAnimationDuration)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    finalConvertView1.setVisibility(View.GONE);
                                                }
                                            });
                                }
                            });

                        }

                    }

                }, 10);



                convertView.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(YourOrders.this, OrderPlaced.class);
                        Bundle extras = new Bundle();
//                        Log.d("LAT", String.valueOf(shopLat.get(position)));
                        extras.putString("ShopKey",shopKey.get(position));
                        extras.putString("OrderKey", orderkey.get(position));
                        extras.putString("ShopName", shopNames.get(position));
                        extras.putDouble("ShopLat", shopLat.get(position));
                        extras.putDouble("ShopLong", shopLong.get(position));
                        extras.putString("Location", locations.get(position));
                        extras.putInt("Files", files.get(position));
                        extras.putString("OrderStatus", orderStatus.get(position));
                        Log.d("ORDERSTATS",orderStatus.get(position));
                        extras.putDouble("Price", (price.get(position)));
                        extras.putBoolean("FromYourOrders", true);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                });



            }
            return convertView;
        }
    }

}
