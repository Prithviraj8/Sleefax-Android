package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class YourOrders extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_orders);

        ListView listView = findViewById(R.id.YourOrderLV);
        YourOrdersAdapter yourOrdersAdapter = new YourOrdersAdapter();
        listView.setAdapter(yourOrdersAdapter);

    }

    public class YourOrdersAdapter extends BaseAdapter{

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        int orders = extras.getInt("Orders Count");
        final int[] price = new int[orders];

        @Override
        public int getCount() {
            Log.d("Ordersis", String.valueOf(orders));
            return orders;
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
            final String[] shopNames = new String[orders];
            final String[] locations = new String[orders];
            final int[] files = new int[orders];

            final LayoutInflater inflater;
            convertView = getLayoutInflater().inflate(R.layout.your_order_row,null);
            if(convertView != null){
                final TextView ShopsName = convertView.findViewById(R.id.ShopsName);
                final TextView Location = convertView.findViewById(R.id.Location);
                final TextView Files = convertView.findViewById(R.id.Files);
                TextView Price = convertView.findViewById(R.id.Price);
                TextView orderStatus = convertView.findViewById(R.id.OrderStatus);
                ImageButton button = convertView.findViewById(R.id.YourOrdersLVBtn);

                ref.child("users").child(userID).child("Orders").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        int i =0;
                        for(DataSnapshot snap:dataSnapshot.getChildren()){
                            if(snap.getKey().equals("ShopsLocation")) {
                                    locations[i] = snap.getValue().toString();
                                    i++;
                            }
                            if (snap.getKey().equals("ShopName")) {
                                shopNames[i] = snap.getValue().toString();
                            }
                        }
                        for(i =0;i<orders;i++){
                            Location.setText(locations[position]);
                            ShopsName.setText(shopNames[position]);
                            Log.d("SLOC", locations[position]);
                            Log.d("SNAME",shopNames[position]);
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
                ref.child("users").child(userID).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getKey().equals("Orders")){
//                            if(dataSnapshot.getChildrenCount()>0){
//                                Files.setText(""+(dataSnapshot.getChildrenCount()-1));
//                            }

                            int i =0;
                            for(DataSnapshot snap:dataSnapshot.getChildren()) {

                                price[i] =(int) snap.getChildrenCount();
                                i++;
                            }
                            for( i =0;i<orders;i++){
                                Files.setText(""+(price[position]-1));
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

                Log.d("CNT IS ", String.valueOf(files[position]));


            }
            return convertView;
        }
    }

}
