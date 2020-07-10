package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ShopDetailsActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();


    TextView mon_fri_StartTime,mon_fri_EndTime;


    String shopID,shopType;
    boolean isTester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        mon_fri_StartTime = findViewById(R.id.Mon_Fri_StartTime);
        mon_fri_EndTime = findViewById(R.id.Mon_Fri_CloseTime);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        shopID = bundle.getString("ShopID");
        isTester = bundle.getBoolean("IsTester");
        shopType = bundle.getString("ShopType");

        Toast.makeText(this, "SHOPID "+ shopID, Toast.LENGTH_SHORT).show();

        if(isTester){
            shopType = "TestStores";
        }else{
            shopType = "Stores";
        }

        getShopDetails();

    }



    public void getShopDetails(){

        ref.child(shopType).child(shopID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

//                Log.d("OBJECT",String.valueOf(map.get("Mon-Fri")));
                if(map.get("Mon-Fri") != null){
//                    mon_fri_StartTime.setText(String.valueOf(map.get("Mon-Fri")));

                    String mon_fri = String.valueOf(map.get("Mon-Fri"));
                    for(int i=0;i< mon_fri.length();i++){
                        if(mon_fri.charAt(i) == '-'){
                            mon_fri_StartTime.setText(mon_fri.substring(0,i-1));
                            mon_fri_EndTime.setText(mon_fri.substring(i+1,mon_fri.length()-1));
                            Log.d("MON_FRI", String.valueOf(mon_fri.charAt(i)));
                        }
                    }

                }
                if(map.get("closedOn") != null){
                    Log.d("CLOSEDON ",String.valueOf(map.get("closedOn")));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
