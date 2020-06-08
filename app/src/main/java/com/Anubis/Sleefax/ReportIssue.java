package com.Anubis.Sleefax;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
    RelativeLayout crashes,exp;
    TextView issue_tv;
    EditText issueTV;
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

        crashes = findViewById(R.id.crashes);
        exp = findViewById(R.id.exp);
        issue_tv = findViewById(R.id.issue_tv);

        issueTV = findViewById(R.id.IssueTV);
        submit = findViewById(R.id.submitIssue);
        back = findViewById(R.id.backBtn);

        issueTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Drawable drawable = getResources().getDrawable(R.drawable.edit_layout);
                issueTV.setBackground(drawable);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Drawable drawable = getResources().getDrawable(R.drawable.edit_layout_blue);
                issueTV.setBackground(drawable);

                submit.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

                Drawable drawable = getResources().getDrawable(R.drawable.edit_layout_blue);
                issueTV.setBackground(drawable);


                submit.setVisibility(View.VISIBLE);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(ReportIssue.this, Select.class);
//                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

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

        crashes.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                Drawable drawable = getResources().getDrawable(R.drawable.toggle_bg_report_an_issue);

                crashes.setBackground(drawable);
                Resources r = getResources();
                int elevation = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        10,
                        r.getDisplayMetrics());
                crashes.setElevation(elevation);
                issue_tv.setText("We really tried our best to humanize this. \n We hate that it broke down like a robot.");

                exp.setBackground(null);
            }
        });

        exp.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                Drawable drawable = getResources().getDrawable(R.drawable.toggle_bg_report_an_issue);

                exp.setBackground(drawable);
                Resources r = getResources();
                int elevation = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        10,
                        r.getDisplayMetrics());
                exp.setElevation(elevation);
                issue_tv.setText("Aw, Snap! This wasn't part of the plan.\n But working on your feedback is gonna be.");

                crashes.setBackground(null);


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
