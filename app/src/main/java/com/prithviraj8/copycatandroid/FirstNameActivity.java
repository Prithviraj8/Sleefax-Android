package com.prithviraj8.copycatandroid;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirstNameActivity extends AppCompatActivity {


    private TextView firstNameTV,numberTV;
    private FirebaseAuth mAuth;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    String name;
    long num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_name);
//        getSupportActionBar().hide();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 3);
            Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
        }
        firstNameTV = findViewById(R.id.FirstNameTV);
        numberTV = findViewById(R.id.numberTV);
        Button continueButton = findViewById(R.id.FirstNameButton);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String email = intent.getStringExtra("Email");
//                String name = intent.getStringExtra("Name");


                 num = Long.parseLong(numberTV.getText().toString());
                 name = firstNameTV.getText().toString();
                Log.d("NAME ",name);
                Log.d("NUM", String.valueOf(num));
                final UserInfo info = new UserInfo(firstNameTV.getText().toString(),email,num,"android");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.child("users").hasChild(userId)) {
                                    ref.child("users").child(userId).setValue(info);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }


                        });

                    }
                },300);

                Intent goToMainPage = new Intent(FirstNameActivity.this, Select.class);
                startActivity(goToMainPage);
                finish();



            }
        });

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
