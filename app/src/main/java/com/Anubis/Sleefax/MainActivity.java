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

    Button addFilesBtn, signIn,privacypolicy;

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



        addFilesBtn = findViewById(R.id.AddfilesBtn);
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


        addFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, Select.class);
                    Bundle extras = new Bundle();
                    extras.putBoolean("NewUser", true);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("SignUp",false);
                extras.putBoolean("NewUser", false);

                intent.putExtras(extras);
                startActivity(intent);
            }
        });

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
