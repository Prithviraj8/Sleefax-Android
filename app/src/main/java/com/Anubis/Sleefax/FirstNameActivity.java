package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FirstNameActivity extends AppCompatActivity {


    public String SharedPrefs = "Data";
    private AutoCompleteTextView firstNameTV;
    EditText numberTV;
    private FirebaseAuth mAuth;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId;
    boolean newUser;
    String name = " ",email,num;

    String loc,orderStatus,shopKey,fileType,pagesize,orientation;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    long shopNum;
    int files;
    double price;
    int copy;
    int resultCode;
    int requestCode;
    double numberOfPages;
    String color,custom;
    boolean FromYourOrders =false, bothSides,isTester;
    ArrayList<String> urls = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_name);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        newUser = extras.getBoolean("NewUser");
        if(newUser){
            getNewUserOrderDetails();
        }
        Log.d("FFFNEWUSER",String.valueOf(newUser));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 3);
            Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
        }
        firstNameTV = findViewById(R.id.FirstNameTV);
        numberTV = findViewById(R.id.numberTV);
        Button continueButton = findViewById(R.id.FirstNameButton);

        ImageButton back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(FirstNameActivity.this,SignInActivity.class);
//                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(FirstNameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                startActivity(intent);
                finish();
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

    }
    public void getNewUserOrderDetails(){


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
//        newUser = extras.getBoolean("NewUser");
        email = extras.getString("Email");
        //////////////////////////////////////////////////Shop Info//////////////////////////////////////////
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        name = extras.getString("ShopName");
        loc = extras.getString("Location");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
        price = extras.getDouble("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        shopKey = extras.getString("ShopKey");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");


        /////////////////////////////////////////////////Order info////////////////////////////////////////


        fileType = extras.getString("FileType");
        pagesize = extras.getString("PageSize");
        orientation = extras.getString("Orientation");

        shopNum = extras.getLong("ShopNum");
        urls = extras.getStringArrayList("URLS");
        copy = extras.getInt("Copies");
        color = extras.getString("ColorType");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
        bothSides = extras.getBoolean("BothSides");
        custom = extras.getString("Custom");
        numberOfPages = extras.getDouble("Pages");
        isTester = extras.getBoolean("IsTester");
        newUser = extras.getBoolean("NewUser");

        android.util.Log.d("FFFFPRICE", String.valueOf(price));

    }


    public void sendNewUsersOrderData(){

        Intent intent = new Intent(FirstNameActivity.this,Payments.class);
        Bundle extras = new Bundle();

        extras.putBoolean("NewUser",false);
        extras.putString("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        extras.putString("Name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        extras.putStringArrayList("URLS", urls);
        extras.putString("ShopName", name);
        extras.putString("Location", loc);
        extras.putDouble("ShopLat", shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putInt("Files", files);
        extras.putDouble("Price", price);
        extras.putString("FileType", fileType);
        extras.putString("PageSize", pagesize);
        extras.putString("Orientation", orientation);
        extras.putBoolean("IsTester", isTester);
        extras.putLong("ShopNum", shopNum);

        extras.putInt("Copies", copy);
        extras.putString("ColorType", color);
        extras.putBoolean("BothSides", bothSides);
        extras.putString("Custom", custom);
        extras.putString("ShopKey", shopKey);
//        extras.putString("UserID", userID);
        extras.putDouble("User Lat", userLat);
        extras.putDouble("User Long", userLong);
        extras.putInt("RequestCode", requestCode);
        extras.putInt("ResultCode", resultCode);
        extras.putDouble("Pages", numberOfPages);
        intent.putExtras(extras);
        startActivity(intent);

    }

    private void attemptRegistration(){

        // Reset errors displayed in the form.
        firstNameTV.setError(null);
        numberTV.setError(null);

        // Store values at the time of the login attempt.
        name = firstNameTV.getText().toString();
        num = (numberTV.getText().toString());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(name) ) {
            firstNameTV.setError(getString(R.string.error_field_required));
            focusView = firstNameTV;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(num)) {
            numberTV.setError(getString(R.string.error_field_required));
            focusView = numberTV;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Log.d("Name is ",name);
            Log.d("num is", String.valueOf(num));

            // TODO: Call create FirebaseUser() here
            uploadUserData(name,num);

        }
    }

    private void uploadUserData(final String name, final String num){

        final UserInfo info = new UserInfo(name,email,Long.parseLong(num),"android");

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ///////Saving users name on the android phone locally./////
                saveDisplayNameLocally(name,Long.parseLong(num));

                    if(newUser){
                        ref.child("users").child(userId).setValue(info);
                        sendNewUsersOrderData();
                    }else{
                        Intent goToMainPage = new Intent(FirstNameActivity.this,Select.class);
                        ref.child("users").child(userId).setValue(info);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("NewUser",false);
                        goToMainPage.putExtras(bundle);
                        startActivity(goToMainPage);
                        finish();
                    }


//                    ref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                            if (!dataSnapshot.child("users").hasChild(userId)) {
//
////                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//
//
//                    });

            }
        });

    }

    public void saveDisplayNameLocally(String name,long num){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        sharedPreferences.edit().putString("DisplayName",name).apply();
        sharedPreferences.edit().putLong("UserNumber",num).apply();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(FirstNameActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
        startActivity(intent);
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
