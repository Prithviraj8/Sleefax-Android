package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirstNameActivity extends AppCompatActivity {


    private AutoCompleteTextView firstNameTV;
    EditText numberTV;
    private FirebaseAuth mAuth;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId;

    String name = " ",num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_name);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
//        getSupportActionBar().hide();
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

    private void uploadUserData(String name,String num){
        Intent intent = getIntent();
        String email = intent.getStringExtra("Email");

//        String name = intent.getStringExtra("Name");
//        num = Long.parseLong(numberTV.getText().toString());
//        name = firstNameTV.getText().toString();

        Log.d("NAME ",name);
        Log.d("NUM", String.valueOf(num));
        Log.d("EMAIL",email);
        final UserInfo info = new UserInfo(name,email,Long.parseLong(num),"android");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    final Intent goToMainPage = new Intent(FirstNameActivity.this, Select.class);


                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (!dataSnapshot.child("users").hasChild(userId)) {
                                ref.child("users").child(userId).setValue(info);
                                startActivity(goToMainPage);
                                finish();
//                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }


                    });

            }
        },300);

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
