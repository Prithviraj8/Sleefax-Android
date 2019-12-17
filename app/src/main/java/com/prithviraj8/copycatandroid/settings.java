package com.prithviraj8.copycatandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class settings extends AppCompatActivity {
    Button changeInfo,deleteAC,signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        getSupportActionBar().hide();
        changeInfo = findViewById(R.id.changeInfoBtn);
        deleteAC = findViewById(R.id.delete);
        signout = findViewById(R.id.signOut);

        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settings.this,changeInfoPopUp.class);
                startActivity(intent);
            }
        });

        signout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                signout.performClick();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(settings.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                startActivity(intent);
                finish();

                return false;
            }

        });

    }
}
