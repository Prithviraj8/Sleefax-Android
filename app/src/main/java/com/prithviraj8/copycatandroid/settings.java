package com.prithviraj8.copycatandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class settings extends AppCompatActivity {
    Button changeInfo,deleteAC,signout;
    Button reportIssueBtn, feedbackBtn;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        getSupportActionBar().hide();
        changeInfo = findViewById(R.id.changeInfoBtn);
        signout = findViewById(R.id.signOut);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settings.this,Select.class);
                startActivity(intent);
                finish();
            }
        });

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
                Toast.makeText(settings.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                finish();

                return false;
            }

        });



        reportIssueBtn = findViewById(R.id.ReportIssueBtn);
        feedbackBtn = findViewById(R.id.FeedbackBtn);
//        back = findViewById(R.id.backbtn);

//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(settings.this,Select.class);
//                startActivity(intent);
////                finish();
//
//            }
//        });
//        reportIssueBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(MotionEvent.ACTION_DOWN == 0){
//                    Log.d("PRESSED","ISSUE");
//                    Intent intent = new Intent(HelpActivity.this,ReportIssue.class);
//                    startActivity(intent);
//                }
//                return true;
//            }
//        });

        reportIssueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settings.this,ReportIssue.class);
                startActivity(intent);

            }
        });

//        feedbackBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(MotionEvent.ACTION_DOWN == 0){
//                    Log.d("PRESSED","ISSUE");
//                    Intent intent = new Intent(HelpActivity.this,Feedback.class);
//                    startActivity(intent);
//                }
//                return true;
//            }
//        });
        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settings.this,Feedback.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(settings.this,Select.class);
        startActivity(intent);
        finish();
    }
}
