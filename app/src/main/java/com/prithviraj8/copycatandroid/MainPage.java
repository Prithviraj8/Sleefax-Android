package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainPage extends AppCompatActivity  {
    String CHANNEL_ID = "UsersChannel";
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

    String userId;


    Button signUp, signIn;

//    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//    ShopInfo info = new ShopInfo();


    private static final int RC_SIGN_IN = 9001;
    private static final String  TAG = "SignInActivity";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();
        notificationManager = NotificationManagerCompat.from(this);



        signUp = findViewById(R.id.signUp_btn);
        signIn = findViewById(R.id.signIn_Btn);
        if(FirebaseApp.getApps(this).size() == 1) {
            FirebaseApp.initializeApp(MainPage.this /* Context */, options, "Stores");
        }
        Log.d("USERRRRR", String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Intent intent = new Intent(MainPage.this, Select.class);
            startActivity(intent);
            finish();
        }


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this,SignInActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("SignUp",true);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this,SignInActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("SignUp",false);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });




        createNotificationChannel();
        askPermissions();
        if (userId != null) {
        setProgressForOrder();
        }
    }
    private void askPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Log.d("EXTERNAL ", String.valueOf(PackageManager.PERMISSION_DENIED));
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 3);
            Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 4);
            Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

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
                ref.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.hasChild(userID)){
//                            boolean settings = (boolean) dataSnapshot.getValue();
//                            if(!settings) {
                                ActivityCompat.requestPermissions(MainPage.this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 1);
                                if (ContextCompat.checkSelfPermission(MainPage.this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
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


    DatabaseReference orderDb = FirebaseDatabase.getInstance().getReference();
    OrderStatus obj = new OrderStatus();


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setProgressForOrder() {

        final ArrayList<String> orderKey = new ArrayList<>();

        orderDb.child("users").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                for(DataSnapshot order_Key: dataSnapshot.getChildren()){
                    orderKey.add(order_Key.getKey());
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (orderKey.size() != 0) {
                            createNotification(orderKey,orderKey.size());
                        }
                    }
                },300);
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


    String orderStatus = null;
    private void createNotification(ArrayList<String> orderKey, final int cnt) {
        // Create an Intent for the activity you want to start
        final Intent resultIntent = new Intent(this, YourOrders.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        final Bundle extras = new Bundle();
        resultIntent.putExtras(extras);
        extras.putInt("Orders Count",cnt);

        for (int i = 0; i < orderKey.size(); i++) {
            final String key = orderKey.get(i);

            final int finalI = i;
            orderDb.child("users").child(userId).child("Orders").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    NotificationPresented presented = new NotificationPresented(true);

                    Iterator<DataSnapshot> users = dataSnapshot.getChildren().iterator();


                    for(final DataSnapshot shop: dataSnapshot.getChildren()) {
                        for (final DataSnapshot order : shop.getChildren()) {
                            for(final DataSnapshot user: order.getChildren()) {
//                            while (users.hasNext()) {

                                String status = null;

                                boolean notify = false;
//                                DataSnapshot user = users.next();
                                if (user.getKey().equals("ShopLat")) {
                                    extras.putDouble("ShopLat", Double.parseDouble(user.getValue().toString()));
                                }
                                if (user.getKey().equals("ShopLong")) {
                                    extras.putDouble("ShopLong", Double.parseDouble(user.getValue().toString()));
                                }
                                if (user.getKey().equals("ShopsLocation")) {
                                    extras.putString("Location", (String) user.getValue());
                                }
                                if (user.getKey().equals("ShopName")) {
                                    extras.putString("ShopName", (String) user.getValue());
                                }
                                if (user.getKey().equals("files")) {
                                    extras.putInt("Files", Integer.parseInt(String.valueOf(user.getValue())));
                                }

                                //if(user.getKey().equals("presented") && user.getValue().equals(true)) {

                                final HashMap<String, Object> notified = new HashMap<String, Object>();

                                if (user.getKey().equals("orderStatus")) {
//                                    Log.d("STATUS", user.getValue().toString());
                                    orderStatus = user.getValue().toString();
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        if (orderStatus != null) {


                                            final String finalStatus = orderStatus;

                                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.appicon);

                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainPage.this, CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.notify)
                                                    .setLargeIcon(icon)
                                                    .setContentTitle("Order Status")
                                                    .setContentText("Order ID: "+order.getKey()+" "+ finalStatus)
                                                    .setGroup(CHANNEL_ID)
                                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Order ID: "+order.getKey()+" "+ finalStatus))
                                                    .setContentIntent(resultPendingIntent)
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH);

                                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                            if (orderStatus.equals("Retrieved")) {
//                                        Log.d("ORDERSTAT!",orderStatus);
//                                        if (user.getKey().equals("RT_Notified") && user.getValue().toString().equals(false)) {
                                                Log.d("ORDERSTAT",orderStatus);

//                                            status = "Retrieved";
//                                            Log.d("SHOPID",shop.getKey());
//                                            Log.d("ORDER",order.getKey());

                                                notified.put("RT_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
//                                           Present notification
//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                                                notificationManager.notify(0, builder.build());

                                                Log.d("Progress", String.valueOf(obj.progress));
//                                        }
                                            } else if (orderStatus.equals("In Progress")) {
//                                        Log.d("ORDERSTAT!",orderStatus);

                                                if (user.getKey().equals("IP_Notified") && user.getValue().toString().equals(false)) {
                                                    Log.d("ORDERSTAT",orderStatus);

//                                            status = "In Progress";
//                                            Log.d("SHOPID",shop.getKey());
//                                            Log.d("ORDER",order.getKey());

                                                    notified.put("IP_Notified", true);
                                                    orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
//                                           Present notification
//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                                                    notificationManager.notify(0, builder.build());

                                                    Log.d("Progress", String.valueOf(obj.progress));
                                                }
                                            } else if (orderStatus.equals("Ready")) {
//                                        Log.d("ORDERSTAT!",orderStatus);

                                                if (user.getKey().equals("R_Notified") && user.getValue().toString().equals(false)) {
                                                    Log.d("ORDERSTAT",orderStatus);

//                                            Log.d("SHOPID",shop.getKey());
//                                            Log.d("ORDER",order.getKey());

                                                    notified.put("R_Notified", true);
                                                    orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
//                                           Present notification
//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//                                            status = "Ready";

                                                    notificationManager.notify(0, builder.build());


                                                }
                                            }


//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//                                            notificationManager.notify(0, builder.build());
                                            extras.putString("OrderStatus", finalStatus);


                                        }
//                              }

                                    }
                                },300);

                                //                     }

                            }

                        }
                    }
                }

//                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
