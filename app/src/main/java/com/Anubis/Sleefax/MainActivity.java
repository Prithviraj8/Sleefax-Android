package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity  {
    String CHANNEL_ID = "Sleefax";
    NotificationManagerCompat notificationManager;

    FirebaseOptions options = new FirebaseOptions.Builder()
            .setApplicationId("1:736925032543:android:ba95b0d9a45b160f") // Required for Analytics.
            .setApiKey("AIzaSyD3XSkHxs8cNaIPPmeD6v0e6AnblrphYAQ") // Required for Auth.
            .setDatabaseUrl("https://storeowner-9c355.firebaseio.com/") // Required for RTDB.
            .build();

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    String userId,username,email;
    long num;


    Button signUp, signIn,privacypolicy;

//    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//    ShopInfo info = new ShopInfo();


    private static final int RC_SIGN_IN = 9001;
    private static final String  TAG = "SignInActivity";

//    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager = NotificationManagerCompat.from(this);



        signUp = findViewById(R.id.signUp_btn);
        signIn = findViewById(R.id.signIn_Btn);
        privacypolicy = findViewById(R.id.privacypolicyBtn);

        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.Anubis.Sleefax.privacypolicy.class);
                startActivity(intent);
                finish();
            }
        });

//        if(FirebaseApp.getApps(this).size() == 1) {
//            FirebaseApp.initializeApp(MainActivity.this /* Context */, options, "Stores");
//        }

        Log.d("USERRRRR", String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));

//        if(FirebaseAuth.getInstance().getCurrentUser() != null){
//            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//            getCurrentUserInfo(userId);
//
//            if(username == null) {
//                Log.d("USERNAME", FirebaseAuth.getInstance().getCurrentUser().getEmail());
//            }
//
//        }


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("SignUp",true);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("SignUp",false);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

//      createNotificationChannel();
        askPermissions();
        if (userId != null) {
//            setProgressForOrder();
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
                                        Intent intent = new Intent(MainActivity.this, Select.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Hmmm! It seems some of your information is missing",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, FirstNameActivity.class);
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
                if(isUser == 6){
                    Log.d("NOT","THERE");
                    Toast.makeText(getApplicationContext(),"Hmmm! It seems some of your information is missing",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, FirstNameActivity.class);
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

    private void askPermissions() {

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            Log.d("EXTERNAL ", String.valueOf(PackageManager.PERMISSION_DENIED));
//        }



//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
//        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            int importance = NotificationManager.IMPORTANCE_HIGH;

            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            if(FirebaseAuth.getInstance().getCurrentUser() != null){

                final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.hasChild(userID)){
//                            boolean settings = (boolean) dataSnapshot.getValue();
//                            if(!settings) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 1);
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                                    // Permission is not granted
                                    Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
                                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
//                                    startActivity(intent);
                                }
                                Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
                                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
//                                startActivity(intent);
//                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }else{
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
//                startActivity(intent);
            }

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {

            if (getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            return super.dispatchTouchEvent(ev);
        }


}
