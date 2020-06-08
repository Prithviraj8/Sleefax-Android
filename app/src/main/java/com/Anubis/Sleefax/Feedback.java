package com.Anubis.Sleefax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.Anubis.Sleefax.AppReviewClass.feedback;


public class Feedback extends AppCompatActivity {


    RelativeLayout[] ratings;
    TextView[] textViews;


    TextView feedbackTV;
    Button submit;
    ImageButton back;

    RelativeLayout rateUs;

    DatabaseReference feedbackDb = FirebaseDatabase.getInstance().getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    String feedbackText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        ratings = new RelativeLayout[10];
        ratings[0] = findViewById(R.id.rating1);
        ratings[1] = findViewById(R.id.rating2);
        ratings[2] = findViewById(R.id.rating3);
        ratings[3] = findViewById(R.id.rating4);
        ratings[4] = findViewById(R.id.rating5);
        ratings[5] = findViewById(R.id.rating6);
        ratings[6] = findViewById(R.id.rating7);
        ratings[7] = findViewById(R.id.rating8);
        ratings[8] = findViewById(R.id.rating9);
        ratings[9] = findViewById(R.id.rating10);

        textViews = new TextView[10];
        textViews[0] = findViewById(R.id.tv1);
        textViews[1] = findViewById(R.id.tv2);
        textViews[2] = findViewById(R.id.tv3);
        textViews[3] = findViewById(R.id.tv4);
        textViews[4] = findViewById(R.id.tv5);
        textViews[5] = findViewById(R.id.tv6);
        textViews[6] = findViewById(R.id.tv7);
        textViews[7] = findViewById(R.id.tv8);
        textViews[8] = findViewById(R.id.tv9);
        textViews[9] = findViewById(R.id.tv10);

        rateUs = findViewById(R.id.rate_us_RL);
//        getSupportActionBar().hide();

        feedbackTV = findViewById(R.id.feedbackTV);
        submit = findViewById(R.id.submitFeedback);
        back = findViewById(R.id.backBtn);

        feedbackTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                Drawable drawable = getResources().getDrawable(R.drawable.edit_layout);
                feedbackTV.setBackground(drawable);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Drawable drawable = getResources().getDrawable(R.drawable.edit_layout_blue);
                feedbackTV.setBackground(drawable);
                submit.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

                Drawable drawable = getResources().getDrawable(R.drawable.edit_layout_blue);
                feedbackTV.setBackground(drawable);
                submit.setVisibility(View.VISIBLE);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(Feedback.this, Select.class);
//                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                feedbackText = feedbackTV.getText().toString();
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                if(!feedbackText.equals("")) {
                    CharSequence text = "Thank you for your feedback😁";
                    Toast toast = Toast.makeText(context, text, duration);

                    toast.show();

                    feedback obj = new feedback(feedbackText);
                    feedbackDb.child("Feedback").child(userId).setValue(obj);
                    Intent intent = new Intent(Feedback.this, Select.class);
                    startActivity(intent);
                    finish();

                }else{
                    CharSequence text = "No feedback typed";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    Intent intent = new Intent(Feedback.this, Select.class);
                    startActivity(intent);
                    finish();

                }
            }
        });


        ratings[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =0;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);

                }
            }
        });
        ratings[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =1;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);

                }
            }
        });
        ratings[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =2;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);

                }
            }
        });
        ratings[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =3;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);

                }
            }
        });
        ratings[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =4;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);

                }
            }
        });
        ratings[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =5;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);

                }
            }
        });
        ratings[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =6;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);
                    rateUs.setVisibility(View.VISIBLE);
                }
            }
        });
        ratings[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =7;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);
                    rateUs.setVisibility(View.VISIBLE);
                }
            }
        });
        ratings[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =8;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);
                    rateUs.setVisibility(View.VISIBLE);
                }
            }
        });
        ratings[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =9;i>=0;i--){

                    int color = getResources().getColor(R.color.colorPrimary);
                    ratings[i].setBackgroundColor(color);
                    textViews[i].setTextColor(Color.WHITE);
                    rateUs.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}
