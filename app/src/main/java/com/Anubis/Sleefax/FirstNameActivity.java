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
import android.widget.Toast;

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
    String userId,number;
    String name = " ",email,num;

    String loc,orderStatus,shopKey,fileType,pagesize,orientation,shopName;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<String> customValues = new ArrayList<>();
    ArrayList<Integer> numberOfPages = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> fileSizes = new ArrayList<>();
    double pricePerFile[];
    double totalPrice;
    Boolean signUp,isShowPassword = false;
    int files;
    boolean FromYourOrders =false,isTester,newUser;
    long shopNum;


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
        number = extras.getString("Number");


        if(newUser){
            getNewUserOrderDetails();
        }
        Log.d("IS_NEWUSER",String.valueOf(newUser));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 3);
            Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
        }
        firstNameTV = findViewById(R.id.FirstNameTV);
        numberTV = findViewById(R.id.numberTV);
        numberTV.setText(number);

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

        //////////////////////////////////////////////////Shop Info//////////////////////////////////////////
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        shopName = extras.getString("ShopName");
        loc = extras.getString("Location");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
        totalPrice = extras.getDouble("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        shopKey = extras.getString("ShopKey");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");


        /////////////////////////////////////////////////Order info////////////////////////////////////////




        shopNum = extras.getLong("ShopNum");
        fileNames = extras.getStringArrayList("FileNames");
        fileSizes = extras.getStringArrayList("FileSizes");

        urls = extras.getStringArrayList("URLS");
        fileTypes = extras.getStringArrayList("FileType");
        pageSize = extras.getStringArrayList("PageSize");
        orientations = extras.getStringArrayList("Orientation");
        copies = extras.getIntegerArrayList("Copies");
        colors = extras.getStringArrayList("ColorType");
        bothSides = extras.getBooleanArray("BothSides");
        customPages = extras.getStringArrayList("Custom");
        numberOfPages = extras.getIntegerArrayList("Pages");

        pricePerFile = extras.getDoubleArray("PricePerFile");
        totalPrice = extras.getDouble("TotalPrice");

        isTester = extras.getBoolean("IsTester");

    }

    public void sendNewUsersOrderData(){

        Intent intent = new Intent(FirstNameActivity.this,Payments.class);
        Bundle extras = new Bundle();

        extras.putBoolean("SignUp",true);
        extras.putBoolean("NewUser",true);


        extras.putString("ShopName", shopName);
        extras.putString("Location", loc);
        extras.putDouble("ShopLat", shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putInt("Files", files);
        extras.putDouble("Price", totalPrice);

        extras.putBoolean("IsTester", isTester);
        extras.putLong("ShopNum", shopNum);


        extras.putStringArrayList("URLS", urls);
        extras.putIntegerArrayList("Pages", numberOfPages);

        extras.putBooleanArray("BothSides", bothSides);
        extras.putStringArrayList("Custom", customPages);
        extras.putStringArrayList("FileNames",fileNames);
        extras.putStringArrayList("FileType", fileTypes);
        extras.putStringArrayList("PageSize", pageSize);
        extras.putStringArrayList("Orientation", orientations);
        extras.putIntegerArrayList("Copies", copies);
        extras.putStringArrayList("ColorType", colors);

        extras.putString("ShopKey", shopKey);
        extras.putDouble("User Lat", userLat);
        extras.putDouble("User Long", userLong);
        extras.putStringArrayList("FileSizes",fileSizes);

        extras.putDoubleArray("PricePerFile",pricePerFile);
        extras.putDouble("TotalPrice",totalPrice);
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
                        Toast.makeText(FirstNameActivity.this, "NEW_USER "+newUser, Toast.LENGTH_SHORT).show();

                        ref.child("users").child(userId).setValue(info);
                        sendNewUsersOrderData();
                    }else{
                        Toast.makeText(FirstNameActivity.this, "NOT_NEW_USER "+newUser, Toast.LENGTH_SHORT).show();

                        Intent goToMainPage = new Intent(FirstNameActivity.this,Select.class);
                        ref.child("users").child(userId).setValue(info);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("NewUser",false);
                        goToMainPage.putExtras(bundle);
//                        startActivity(goToMainPage);
                    }


            }
        });

    }

    public void saveDisplayNameLocally(String name,long num){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        sharedPreferences.edit().putString("DisplayName",name).apply();
        sharedPreferences.edit().putLong("UserNumber",num).apply();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            sharedPreferences.edit().putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();
        }
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
