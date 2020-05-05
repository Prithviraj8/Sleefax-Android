package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class verifyOTPActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();


    String number, mVerificationId, code;
    String loc,orderStatus,shopKey,fileType,pagesize,orientation,shopName;
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
    boolean FromYourOrders =false, bothSides,isTester,newUser;
    ArrayList<String> urls = new ArrayList<>();



    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthCredential credential,mcredential;

    EditText otp;
    Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        otp = findViewById(R.id.otpTV);
        done = findViewById(R.id.done);

        getNewUserOrderDetails();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mcredential == null) {
                    try {
                        if(otp.getText() != null) {
                            code = otp.getText().toString();
                            credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                            signInWithPhoneAuthCredential(credential);

                        }else {
                            Toast.makeText(verifyOTPActivity.this, "Enter the otp you received", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
//                        Toast.makeText(verifyOTPActivity.this,"Error "+String.valueOf(e),Toast.LENGTH_LONG).show();
                    }
                }else {
                    otp.setText(credential.getSmsCode());
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });
        
        
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        final KProgressHUD hud = KProgressHUD.create(verifyOTPActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100)
                .show();

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.d("SIGNIN", "signInWithCredential:success");
                            final FirebaseUser user = task.getResult().getUser();

                            ref.child("users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user.getUid())){
                                        Intent intent = new Intent(verifyOTPActivity.this,Select.class);
                                        startActivity(intent);
                                        finish();
                                        hud.dismiss();
                                    }else {
//                                        Toast.makeText(verifyOTPActivity.this, "UID"+user.getUid(), Toast.LENGTH_SHORT).show();

                                        if(!newUser) {
                                            Intent intent = new Intent(verifyOTPActivity.this, FirstNameActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("Number", number);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            hud.dismiss();
                                        }else{
                                            sendNewUsersOrderData();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    hud.dismiss();
                                }
                            });


                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("SIGNIN", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(verifyOTPActivity.this, " The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                                hud.dismiss();

                            }
                        }
                    }
                });
    }
    public void getNewUserOrderDetails(){


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
//        newUser = extras.getBoolean("NewUser");
        number = extras.getString("Number");
        mVerificationId = extras.getString("VID");
        mcredential = extras.getParcelable("Credential");


        //////////////////////////////////////////////////Shop Info//////////////////////////////////////////
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        shopName = extras.getString("ShopName");
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


    }

    public void sendNewUsersOrderData(){

        Intent intent = new Intent(verifyOTPActivity.this,FirstNameActivity.class);
        Bundle extras = new Bundle();

        extras.putBoolean("NewUser",true);
        extras.putString("Number", number);

//        extras.putString("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
//        extras.putString("Name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        extras.putStringArrayList("URLS", urls);
        extras.putString("ShopName", shopName);
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

}
