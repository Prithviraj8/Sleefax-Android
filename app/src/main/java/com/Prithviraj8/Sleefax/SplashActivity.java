package com.Prithviraj8.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    final FirebaseDatabase database = com.google.firebase.database.FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();



    String userId,username,email;
    long num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            getCurrentUserInfo(userId);

            if(username == null) {
                Log.d("USERNAME", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            }

        }else{
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    int isUser=0;
    private void getCurrentUserInfo(final String userId){

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                isUser++;
                Log.d("ISUSER",String.valueOf(isUser));
                if(dataSnapshot.hasChild(userId)){
                    Log.d("ALREADY","THERE");
                    ref.child("users").child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot info:dataSnapshot.getChildren()){
                                Log.d("INFOOO",info.getValue().toString());

                                if(info.getKey().equals("name")){
                                    username = info.getValue().toString();
                                }

                                if(info.getKey().equals("email")){
                                    email = info.getValue().toString();
                                }

                                if(info.getKey().equals("num")){
                                    num = Long.parseLong(info.getValue().toString());
                                }

                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

//                                    Log.d("USER",username);
//                                    Log.d("EMAIL",email);
//                                    Log.d("NUM",String.valueOf(num));
                                    if(username != null && email != null && num > 0) {
                                        Intent intent = new Intent(SplashActivity.this, Select.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Hmmm! It seems some of your information is missing",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SplashActivity.this, FirstNameActivity.class);
                                        intent.putExtra("Email",email);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            },200);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else
                if(isUser == 5){
                    Log.d("NOT","THERE");
                    Toast.makeText(getApplicationContext(),"Hmmm! It seems some of your information is missing",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashActivity.this, FirstNameActivity.class);
                    intent.putExtra("Email",email);
                    startActivity(intent);
                    finish();
                }
//                else{
////
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
}
