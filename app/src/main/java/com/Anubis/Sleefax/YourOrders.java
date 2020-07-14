package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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

import java.util.ArrayList;
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
    TextView noOrdersTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_orders);
//        getSupportActionBar().hide();

//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
//        int orders = extras.getInt("Orders Count");

        noOrdersTV = findViewById(R.id.NoOrdersTV);

        getOrders();
        getCurrentOrderDetails();
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

     ArrayList<String> orderkey = new ArrayList<>();
     ArrayList<String> shopKey = new ArrayList<>();
     ArrayList<String> shopNames = new ArrayList<>();
     ArrayList<String> locations = new ArrayList<>();
     ArrayList<String> orderStatus = new ArrayList<>();
     ArrayList<String> orderDate = new ArrayList<>();
     ArrayList<String> paymentModes = new ArrayList<>();

     ArrayList<Double> shopLat = new ArrayList<>();
     ArrayList<Double> shopLong = new ArrayList<>();
     ArrayList<Integer> files = new ArrayList<>();
     ArrayList<Double> price = new ArrayList<>();
     ArrayList<Integer> ids = new ArrayList<>();
     ArrayList<String> orderID = new ArrayList<>();

    public void getOrders(){


//        setProgressForOrder();

        ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Orders").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                shopKey.add(dataSnapshot.getKey());
//                for(DataSnapshot orderIDS: dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(String.valueOf(map.get("orderStatus")).equals("Done")) {
//                        cnt = (int) (dataSnapshot.getChildrenCount()+cnt);
//                        orderCnt = orderCnt + 1;
                        shopKey.add(String.valueOf(map.get("storeId")));
                        orderkey.add(dataSnapshot.getKey());
                        orderID.add(String.valueOf(map.get("orderID")));
                    }
//                }
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
    public void getCurrentOrderDetails(){
        ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot orderKeys) {

                files.clear();
                locations.clear();
                shopNames.clear();
                shopLat.clear();
                shopLong.clear();
                orderStatus.clear();
                price.clear();
                orderDate.clear();
                paymentModes.clear();

//                Toast.makeText(Select.this,"ORDERID"+orderKeys.getChildrenCount(),Toast.LENGTH_LONG).show();
                Log.d("NEWORDERMAP",String.valueOf(orderKeys.getValue()));
                for(DataSnapshot values: orderKeys.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) values.getValue();

                    if (map != null) {
                        Log.d("MAPVAL", String.valueOf(map));
                        if (String.valueOf(map.get("orderStatus")).equals("Done")) {
//                            cnt = (int) orderKeys.getChildrenCount();

                            files.add(Integer.parseInt(String.valueOf(map.get("files"))));
                            locations.add(String.valueOf(map.get("ShopsLocation")));
                            shopNames.add(String.valueOf(map.get("ShopName")));
                            shopLat.add(Double.parseDouble(String.valueOf(map.get("ShopLat"))));
                            shopLong.add(Double.parseDouble(String.valueOf(map.get("ShopLong"))));
                            orderStatus.add(String.valueOf(map.get("orderStatus")));
                            price.add(Double.parseDouble(String.valueOf(map.get("price"))));
                            orderDate.add(String.valueOf(map.get("orderDateTime")));
                            paymentModes.add(String.valueOf(map.get("paymentMode")));
                        }
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        yourOrderLV();
                    }
                },500);


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


            if(orderkey.size() ==0){
                noOrdersTV.setVisibility(View.VISIBLE);
            }
            return orderkey.size();
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



            final ArrayList<Integer>[] custOrderids = new ArrayList[]{new ArrayList<>()};


//            final int[] cnt = {0};
            final LayoutInflater inflater;
            convertView = getLayoutInflater().inflate(R.layout.your_order_row,null);

            if(convertView != null){

                final TextView ShopsName = convertView.findViewById(R.id.ShopsName);
                final TextView Location = convertView.findViewById(R.id.Location);
                final TextView Files = convertView.findViewById(R.id.Files);
                final TextView Price = convertView.findViewById(R.id.OrderPrice);
                final TextView orderDateAndTime = convertView.findViewById(R.id.orderDate);
                final TextView paymentModeTV = convertView.findViewById(R.id.paymentModeTV);

                final TextView OrderStatus = convertView.findViewById(R.id.OrderStatus);
                final RoundCornerProgressBar progressBar;
                final View contentView = convertView.findViewById(R.id.animateView);
                final TextView orderIDTV = convertView.findViewById(R.id.orderIDTV);

                final View finalConvertView = convertView;



                final Handler handler = new Handler();
                final View finalConvertView1 = convertView;
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {

                Location.setText(locations.get(position));
                ShopsName.setText(shopNames.get(position));
                Files.setText("Files : " + files.get(position));
                Price.setText("Price : ₹" + price.get(position));
                orderDateAndTime.setText(orderDate.get(position));
                orderIDTV.setText("Order ID : " + orderID.get(position));
                paymentModeTV.setText(paymentModes.get(position));

                if ((orderStatus.get(position)).equals("Done")){
                  //  OrderStatus.setText("Completed");
                 //   OrderStatus.setBackgroundResource(R.drawable.orderstatusview3);
                    OrderStatus.setVisibility(View.VISIBLE);

                }


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
                        extras.putDouble("Price", (price.get(position)));
                        extras.putString("OrderID",orderID.get(position));
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
