package com.Prithviraj8.Sleefax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.Prithviraj8.Sleefax.AppReviewClass.feedback;


public class Feedback extends AppCompatActivity {


    TextView feedbackTV;
    Button submit;
    ImageButton back;

    DatabaseReference feedbackDb = FirebaseDatabase.getInstance().getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    String feedbackText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
//        getSupportActionBar().hide();

        feedbackTV = findViewById(R.id.feedbackTV);
        submit = findViewById(R.id.submitFeedback);
        back = findViewById(R.id.backBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Feedback.this,Select.class);
                startActivity(intent);
                finish();

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
    }



}
