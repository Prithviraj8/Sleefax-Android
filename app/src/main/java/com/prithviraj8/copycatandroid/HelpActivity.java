package com.prithviraj8.copycatandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class HelpActivity extends AppCompatActivity {


    Button reportIssueBtn, feedbackBtn;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
//        getSupportActionBar().hide();

        reportIssueBtn = findViewById(R.id.ReportIssueBtn);
        feedbackBtn = findViewById(R.id.FeedbackBtn);
        back = findViewById(R.id.backbtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HelpActivity.this,Select.class);
                startActivity(intent);
//                finish();

            }
        });
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
                Intent intent = new Intent(HelpActivity.this,ReportIssue.class);
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
                Intent intent = new Intent(HelpActivity.this,Feedback.class);
                startActivity(intent);
            }
        });

    }
}
