package com.Anubis.Sleefax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class changeInfoPopUp extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId;

//////////////////////////////////////////new UI////////////////////////////////////////////////////////
    TextView Name,Phone,Email;
    TextView verify_text;

    ImageView verified;

    RelativeLayout emailRL,phoneRL,profileRL;

///for email Relative layout////
    TextView validText_email;
    View divider_email;
    Button saveChanges_email;//changing mail

    String emailId;


////////for changing umber//////
    Button saveChanges_number;//changing number
    View divider_number;
    TextView validtv_number;

    String PhoneNum;


    // as per the new UI I have two different layouts for changing email and number.
    //for number the otp section has to be added


//////////////////////////////////////////new UI////////////////////////////////////////////////////////


    EditText email,name,num;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info_pop_up);

//the new email and number edit texts
        email = findViewById(R.id.emailTV);
//        name = findViewById(R.id.nameTV);
        num = findViewById(R.id.numTV);

        //////////////////////////////////////////new UI////////////////////////////////////////////////////////

        verify_text = findViewById(R.id.verify_email);
        verified = findViewById(R.id.email_verified_iv);
        verify_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verified.setVisibility(View.VISIBLE);
                verify_text.setVisibility(View.INVISIBLE);

            }
        });
        Name = findViewById(R.id.Name);//the earlier user data
        Phone = findViewById(R.id.Phone);//the earlier user data
        Email = findViewById(R.id.Email);//the earlier user data
        profileRL = findViewById(R.id.profileRL);//the relative layout containing earlier user data
        emailRL = findViewById(R.id.new_emailRL);//new email relative layout
        phoneRL = findViewById(R.id.new_numRL);//new phone relative layout


        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileRL.setVisibility(View.INVISIBLE);
                emailRL.setVisibility(View.INVISIBLE);
                phoneRL.setVisibility(View.VISIBLE);
            }
        });
        Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileRL.setVisibility(View.INVISIBLE);
                phoneRL.setVisibility(View.INVISIBLE);
                emailRL.setVisibility(View.VISIBLE);


            }
        });

        ////for changing email////
        validText_email = findViewById(R.id.valid_text_email);
        divider_email = findViewById(R.id.bluediv_email);
        saveChanges_email = findViewById(R.id.save_changes_email);


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int color = Color.parseColor("#227093");
                divider_email.setBackgroundColor(color);

                emailId = email.getText().toString();


            }

            @Override
            public void afterTextChanged(Editable s) {

                saveChanges_email.setVisibility(View.VISIBLE);
                int color = Color.parseColor("#227093");
                divider_email.setBackgroundColor(color);

                emailId = email.getText().toString();



            }
        });

        emailId = email.getText().toString();

        saveChanges_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmailValid(emailId))
                    validText_email.setVisibility(View.VISIBLE);
                else
                {validText_email.setVisibility(View.INVISIBLE);

                }

            }
        });

        ////////////////changing number//////////////////
        saveChanges_number = findViewById(R.id.save_changes);
        divider_number = findViewById(R.id.bluediv1);
        validtv_number = findViewById(R.id.valid_text);


        num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



                // saveChanges.setVisibility(View.VISIBLE);
                int color = Color.parseColor("#227093");
                divider_number.setBackgroundColor(color);

            }

            @Override
            public void afterTextChanged(Editable s) {



                saveChanges_number.setVisibility(View.VISIBLE);
                int color = Color.parseColor("#227093");
                divider_number.setBackgroundColor(color);

                PhoneNum = num.getText().toString();

            }
        });



        saveChanges_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(PhoneNum.length()<10 || PhoneNum.length()>10){

                    validtv_number.setVisibility(View.VISIBLE);

                }
                if(PhoneNum.length() == 10){

                    ///otp activity to be called///
                }



            }
        });

        //////////////////////////////////////////new UI////////////////////////////////////////////////////////

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(changeInfoPopUp.this,Select.class);
//                startActivity(intent);
                finish();
            }
        });


//        getSupportActionBar().hide();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);



        int width = dm.widthPixels;
        int height = dm.heightPixels;

//        getWindow().setLayout((int)(width * 0.8),(int) (height* 0.6));



        //this has to be changed as now there are separate buttons  for email and number also when number is changed the otp activity pops up
        Button dismiss = findViewById(R.id.changeInfoBtn);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Long number = null;
                HashMap<String, Object> updateinfo = new HashMap<>();
                if (email.getText().toString().length() > 0) {
                    updateinfo.put("email", email.getText().toString());
                }
//                if(name.getText().toString().length()>0){
//                    updateinfo.put("name",name.getText().toString());
//                }
                if (num.getText().toString().length() >= 10) {
                    number = Long.valueOf(num.getText().toString());

                    createToast("Phone number changed successfully.");
                    updateinfo.put("num", num.getText().toString());
                }else if(num.getText().toString().length() > 0 && num.getText().toString().length() <10){

                    createToast("Number of digits in your phone number must be atleast 10.");

                }

                Log.d("USERSDATACHANGING", userId);
                ref.child("users").child(userId).updateChildren(updateinfo);

                String newEmail = "";
                newEmail = email.getText().toString();
                if (!newEmail.equals("")){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updateEmail(newEmail);

                    createToast("Details changed successfully. Please Log in once more.");

                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(changeInfoPopUp.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                    startActivity(intent);
                    finish();
                 }


                if(newEmail.equals("") && num.getText().toString().length() == 0){
                    finish();
                }


            }
        });
    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void createToast(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
