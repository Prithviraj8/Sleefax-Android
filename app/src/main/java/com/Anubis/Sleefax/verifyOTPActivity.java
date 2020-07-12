package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Anubis.Sleefax.Animations.GifImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

import static com.Anubis.Sleefax.PdfInfo.dpToPx;

public class verifyOTPActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    KProgressHUD hud;
    public String SharedPrefs = "Data";


    String number, mVerificationId, code;
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
    ArrayList<Integer> customPage1 = new ArrayList<>();
    ArrayList<Integer> customPage2 = new ArrayList<>();
    double pricePerFile[];
    double totalPrice;
    Boolean signUp,isShowPassword = false;
    int files;
    boolean FromYourOrders =false,isTester,newUser;
    long shopNum;



    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthCredential credential,mcredential;

    EditText otp, digit1, digit2, digit3, digit4, digit5, digit6;
    TextView valid, Phone;
    Button confirm;

    RelativeLayout rootLayout, otpLayout, GifRL;
    Button done;
    GifImageView gifImageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        // Progress HUD
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading")
                .setMaxProgress(100);

        GifRL = findViewById(R.id.GifRL);
        gifImageView = (GifImageView) findViewById(R.id.GIF);
        gifImageView.setGifImageResource(R.raw.animation_640_kchj0ms6);

        otpLayout = findViewById(R.id.OTP_RL);
        rootLayout = findViewById(R.id.rootlayout);


        Phone = findViewById(R.id.Phone);
        Phone.setText(number);

        confirm = findViewById(R.id.confirm);
        digit1 = findViewById(R.id.digit1);
        digit2 = findViewById(R.id.digit2);
        digit3 = findViewById(R.id.digit3);
        digit4 = findViewById(R.id.digit4);
        digit5 = findViewById(R.id.digit5);
        digit6 = findViewById(R.id.digit6);

        valid = findViewById(R.id.textview_valid);

        setInitialViewAnimationCode();
        getNewUserOrderDetails();




            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    hud.show();
                    confirm.setVisibility(View.GONE);
                    GifRL.setVisibility(View.VISIBLE);
                    if (mcredential == null) {
                        try {
//                            if (code != null && code.length() == 6) {
//                                code = otp.getText().toString();

                                code = digit1.getText().toString() + digit2.getText().toString() + digit3.getText().toString() + digit4.getText().toString() + digit5.getText().toString() + digit6.getText().toString();
                                Log.d("DIGIT1",digit1.getText().toString());


                                credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                                signInWithPhoneAuthCredential(credential);
//                                hud.dismiss();

//                            } else {
//                                Toast.makeText(verifyOTPActivity.this, "Enter the otp you received", Toast.LENGTH_SHORT).show();
//                            }
                        } catch (Exception e) {
                            Toast.makeText(verifyOTPActivity.this,"Error "+String.valueOf(e),Toast.LENGTH_LONG).show();
//                            hud.dismiss();
                            GifRL.setVisibility(View.GONE);
                            confirm.setVisibility(View.VISIBLE);

                        }
                    } else {
//                        otp.setText(credential.getSmsCode());
                        Log.d("CREDENTIALSMSCODE",String.valueOf(credential.getSmsCode()));
                        Toast.makeText(verifyOTPActivity.this, "CREDENTIALSMSCODE "+ (credential.getSmsCode()), Toast.LENGTH_SHORT).show();
                        signInWithPhoneAuthCredential(credential);
//                        hud.dismiss();

                    }
                }
            });
        }
        
//    }
    boolean isNewUser;

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

//        hud.show();

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.d("SIGNIN", "signInWithCredential:success");
                            final FirebaseUser user = task.getResult().getUser();

                            getInfoSavedLocally();

                            if(!newUser) {
                                ref.child("users").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Toast.makeText(verifyOTPActivity.this, "SENDING NO NEW ORDER DETAILS ", Toast.LENGTH_SHORT).show();

                                        if (dataSnapshot.hasChild(user.getUid())) {
                                            Intent intent = new Intent(verifyOTPActivity.this, Select.class);
                                            startActivity(intent);
//                                            hud.dismiss();
                                            GifRL.setVisibility(View.GONE);

                                        }
                                        else {
                                            Toast.makeText(verifyOTPActivity.this, "UID"+user.getUid(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(verifyOTPActivity.this, FirstNameActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("Number", number);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                                            hud.dismiss();
                                            GifRL.setVisibility(View.GONE);

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        hud.dismiss();
                                        GifRL.setVisibility(View.GONE);
                                        confirm.setVisibility(View.VISIBLE);

                                    }
                                });

                            }else{
                                if(!isUser.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    isNewUser = true;
                                }else{
                                    isNewUser = false;
                                }
                                GifRL.setVisibility(View.GONE);
                                sendNewUsersOrderData();
                            }
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("SIGNIN", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(verifyOTPActivity.this, " The verification code entered was invalid", Toast.LENGTH_SHORT).show();
//                                hud.dismiss();
                                GifRL.setVisibility(View.GONE);
                                confirm.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });
    }


    public void setInitialViewAnimationCode(){
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int diff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
                if(diff > dpToPx(getApplicationContext(),200)){

                    Resources r = getResources();
                    int margin = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            80,
                            r.getDisplayMetrics());



                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    // params.topMargin = margin;
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    otpLayout.setLayoutParams(params);
                }

                if(diff < dpToPx(getApplicationContext(),200)){

                    Resources r = getResources();
                    int margin = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            150,
                            r.getDisplayMetrics());



                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.topMargin = margin;
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    otpLayout.setLayoutParams(params);
                }


            }
        });
        digit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                confirm.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {


                confirm.setVisibility(View.VISIBLE);

                if(digit1.length() == 1)
                    digit2.requestFocus();

            }
        });
        digit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                confirm.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {


                confirm.setVisibility(View.VISIBLE);

                if(digit2.length() == 1)
                    digit3.requestFocus();

            }
        });
        digit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                confirm.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {


                confirm.setVisibility(View.VISIBLE);

                if(digit3.length() == 1)
                    digit4.requestFocus();

            }
        });
        digit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                confirm.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {



                confirm.setVisibility(View.VISIBLE);

                if(digit4.length() == 1)
                    digit5.requestFocus();

            }
        });
        digit5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                confirm.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

                confirm.setVisibility(View.VISIBLE);

                if(digit5.length() == 1)
                    digit6.requestFocus();

            }
        });
        digit6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



                confirm.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {


                confirm.setVisibility(View.VISIBLE);


            }
        });
    }


    String isUser;
    public void getInfoSavedLocally(){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        isUser = sharedPreferences.getString("UserID","No user");

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

        customPage1 = extras.getIntegerArrayList("CustomPages1");
        customPage2 = extras.getIntegerArrayList("CustomPages2");

        pricePerFile = extras.getDoubleArray("PricePerFile");
        totalPrice = extras.getDouble("TotalPrice");

        isTester = extras.getBoolean("IsTester");
        newUser = extras.getBoolean("NewUser");
        number = extras.getString("Number");
        mVerificationId = extras.getString("VID");
        mcredential = extras.getParcelable("Credential");

    }

    public void sendNewUsersOrderData(){
        Intent intent;
        Bundle extras;
        if(isNewUser){
            intent = new Intent(verifyOTPActivity.this,FirstNameActivity.class);
            extras = new Bundle();

        }else{
            intent = new Intent(verifyOTPActivity.this,Payments.class);
            extras = new Bundle();
        }


        extras.putBoolean("SignUp",true);
        extras.putBoolean("NewUser",true);
        extras.putString("Number", number);

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
        extras.putIntegerArrayList("CustomPages1",customPage1);
        extras.putIntegerArrayList("CustomPages2",customPage2);
        extras.putDoubleArray("PricePerFile",pricePerFile);
        extras.putDouble("TotalPrice",totalPrice);
        intent.putExtras(extras);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        startActivity(intent);


    }


}
