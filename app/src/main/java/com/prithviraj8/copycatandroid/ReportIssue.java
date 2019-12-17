package com.prithviraj8.copycatandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
class issue {
    public String Issues;

    public issue(String issueText) {
        this.Issues = issueText;
    }
}
public class ReportIssue extends AppCompatActivity {
    TextView issueTV;
    Button submit;
    ImageButton back;

    DatabaseReference issueDb = FirebaseDatabase.getInstance().getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String issueText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);
//        getSupportActionBar().hide();

        issueTV = findViewById(R.id.IssueTV);
        submit = findViewById(R.id.SubmitIssue);
        back = findViewById(R.id.backBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ReportIssue.this,Select.class);
                startActivity(intent);
                finish();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                issueText = issueTV.getText().toString();
                Log.d("ISSUE IS ",issueText);
                if((!issueText.equals(""))) {

                    Context context = getApplicationContext();
                    CharSequence text = "Thank you for your feedback😁";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    issue obj = new issue(issueText);
                    issueDb.child("Issues").child(userId).setValue(obj);
                    Intent intent = new Intent(ReportIssue.this, Select.class);
                    startActivity(intent);
                    finish();
                }else{
                    Context context = getApplicationContext();
                    CharSequence text = "No feedback typed";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    Intent intent = new Intent(ReportIssue.this, Select.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
