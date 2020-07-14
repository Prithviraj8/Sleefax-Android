package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Anubis.Sleefax.Animations.GifImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.Anubis.Sleefax.PdfInfo.dpToPx;


public class FirstNameActivity extends AppCompatActivity {

    //////////////////////////////////////////new UI ////////////////////////////////////////////////
//Refer to the comments for the variables
    RelativeLayout rootlayout;//the outer relative layout
    RelativeLayout nameRL;// the relative layout of name edit text
    RelativeLayout emailRL;// the relative layout of email edit text
    Button nameBtn,emailBtn;// on clicking on the nameBtn nad emailBtn the animation is triggered and the outline of relative layout is changed to blue
    TextView nameTV,mailTv;//the text Name and email that comes to top of edit text after user starts typing
    RelativeLayout detailsRL;// the relative layout containing edit text name and email
    ImageView nameIV,mailIV; // two side icons of name and email edit text
    int top,left;//used for animation
    TextView nTv,eTv;// the enter your name and eter email textviews

    String EmailText;// get the text of Email entered by user

    TextView validTv;//Enter a valid email textview


    boolean flag = false;// used to adjust screen size after keyboard appears
    ////////////////////////////////////////////////new UI/////////////////////////////////////////////////

    EditText firstNameTV,Email;//the main Edit text


    public String SharedPrefs = "Data";
    EditText numberTV;
    private FirebaseAuth mAuth;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId,number;
    String name,isUser,email,num;

    String loc,orderStatus,shopKey,fileType,pagesize,orientation,shopName;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<String> customValues = new ArrayList<>();
    ArrayList<Integer> numberOfPages = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> fileSizes = new ArrayList<>();
    ArrayList<Integer> customPage1 = new ArrayList<>();
    ArrayList<Integer> customPage2 = new ArrayList<>();
    double pricePerFile[];
    double totalPrice;
    Boolean signUp,isShowPassword = false;
    int files;
    boolean FromYourOrders =false,isTester,newUser;
    long shopNum;


    RelativeLayout gifRL;
    GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_name);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        gifRL = findViewById(R.id.GifRL);
        gifImageView = (GifImageView) findViewById(R.id.GIF);
        gifImageView.setGifImageResource(R.raw.animation_640_kchmf79t);
        gifRL.setVisibility(View.GONE);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        newUser = extras.getBoolean("NewUser");
        number = extras.getString("Number");
        name = extras.getString("Name");

        if(newUser){
            getNewUserOrderDetails();
        }
        Log.d("IS_NEWUSER",String.valueOf(newUser));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 3);
            Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
        }


        firstNameTV = findViewById(R.id.FirstNameTV);
        numberTV = findViewById(R.id.numberTV);
        Email = findViewById(R.id.Email);

        // Start animation Automatically if the 3 conditions below are true and compile executes code inside the if
        if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null) {
            Log.d("YOUR_NUM",String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
            numberTV.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }else{
            Log.d("YOUR_NUM",String.valueOf(number));
            numberTV.setText(number);
        }
        if(FirebaseAuth.getInstance().getCurrentUser().getEmail() != null){
            Email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        if(name != null){
            firstNameTV.setText(name);
        }


        final Button continueButton = findViewById(R.id.FirstNameButton);
        ImageButton back = findViewById(R.id.back);

        /////////////////////////////////////////////////////new UI///////////////////////////////////////////////

       //these all are just used for either animation or changing backgrounds
        rootlayout= findViewById(R.id.rootlayout);
        nameTV = findViewById(R.id.usernameTV);
        nameRL = findViewById(R.id.nameRL);
        nameBtn = findViewById(R.id.name_button);
        detailsRL = findViewById(R.id.detailsLayout);
        nameIV = findViewById(R.id.nameIV);
        nTv = findViewById(R.id.entername_tv);
        emailRL = findViewById(R.id.emailRL);
        emailBtn = findViewById(R.id.email_button);
        mailIV = findViewById(R.id.mailIV);
        mailTv = findViewById(R.id.EmailTv);
        eTv = findViewById(R.id.entermail_tv);
        validTv = findViewById(R.id.valid_mail_text);


       // find whether keyboard is visible or not
        rootlayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int diff = rootlayout.getRootView().getHeight() - rootlayout.getHeight();
                if(diff > dpToPx(getApplicationContext(),200)){

                    //keyboard visible;adjust height


                    Resources r = getResources();
                    int margin = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            90,
                            r.getDisplayMetrics());



                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.topMargin = margin;
                    detailsRL.setLayoutParams(params);

                }





                if(diff < dpToPx(getApplicationContext(),200))
                {


                    //keyboard not visible

                    Resources r = getResources();
                    int margin = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            140,
                            r.getDisplayMetrics());



                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    if(flag){ params.topMargin = margin;
                        detailsRL.setLayoutParams(params);}



                } }
        });


        Resources r = getResources();
        top = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                25,
                r.getDisplayMetrics());
        left = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                120,
                r.getDisplayMetrics());

        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstNameTV.setVisibility(View.VISIBLE);
                nameTV.setVisibility(View.VISIBLE);

                Animation animation1 = new AlphaAnimation(1.0f,0.0f);
                animation1.setDuration(500);


                nameIV.startAnimation(animation1);
                nTv.startAnimation(animation1);


                Animation animation2 = new AlphaAnimation(0.1f,1.0f);
                animation2.setDuration(500);


                nameTV.startAnimation(animation2);
                firstNameTV.startAnimation(animation2);




                Animation animation = new TranslateAnimation(0,-left,0,-top);
                animation.setDuration(500);
                animation.setFillEnabled(true);
                animation.setFillAfter(true);

                nameTV.startAnimation(animation);

                nameBtn.setVisibility(View.INVISIBLE);



            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Email.requestFocus();

                Email.setVisibility(View.VISIBLE);
                mailTv.setVisibility(View.VISIBLE);

                Animation animation1 = new AlphaAnimation(1.0f,0.0f);
                animation1.setDuration(500);


                mailIV.startAnimation(animation1);
                eTv.startAnimation(animation1);


                Animation animation2 = new AlphaAnimation(0.1f,1.0f);
                animation2.setDuration(500);


                mailTv.startAnimation(animation2);
                Email.startAnimation(animation2);


                Animation animation = new TranslateAnimation(0,-left,0,-top);
                animation.setDuration(500);
                animation.setFillEnabled(true);
                animation.setFillAfter(true);

                mailTv.startAnimation(animation);

                emailBtn.setVisibility(View.INVISIBLE);

            }
        });

        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                EmailText = Email.getText().toString();
                continueButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                EmailText = Email.getText().toString();
                continueButton.setVisibility(View.VISIBLE);
            }
        });

        EmailText = Email.getText().toString();
        /////////////////////////////////////////////////////new UI///////////////////////////////////////////////



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
                gifRL.setVisibility(View.VISIBLE);
                if(!isEmailValid(EmailText))
                    validTv.setVisibility(View.VISIBLE);
                else
                {validTv.setVisibility(View.INVISIBLE);
                attemptRegistration();}
            }
        });

    }

    public void getNewUserOrderDetails(){
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //////////////////////////////////////////////////Shop Info//////////////////////////////////////////
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        shopName = extras.getString("ShopName");
        loc = extras.getString("Location");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
        totalPrice = extras.getDouble("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        shopKey = extras.getString("ShopKey");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");


        /////////////////////////////////////////////////Order info////////////////////////////////////////




        shopNum = extras.getLong("ShopNum");
        fileNames = extras.getStringArrayList("FileNames");
        fileSizes = extras.getStringArrayList("FileSizes");

        urls = extras.getStringArrayList("URLS");
        fileTypes = extras.getStringArrayList("FileType");
        pageSize = extras.getStringArrayList("PageSize");
        orientations = extras.getStringArrayList("Orientation");
        copies = extras.getIntegerArrayList("Copies");
        colors = extras.getStringArrayList("ColorType");
        bothSides = extras.getBooleanArray("BothSides");
        customPages = extras.getStringArrayList("Custom");
        numberOfPages = extras.getIntegerArrayList("Pages");

        customPage1 = extras.getIntegerArrayList("CustomPages1");
        customPage2 = extras.getIntegerArrayList("CustomPages2");

        pricePerFile = extras.getDoubleArray("PricePerFile");
        totalPrice = extras.getDouble("TotalPrice");

        isTester = extras.getBoolean("IsTester");

    }

    public void sendNewUsersOrderData(){

        Intent intent = new Intent(FirstNameActivity.this,Payments.class);
        Bundle extras = new Bundle();

        extras.putBoolean("SignUp",true);
        extras.putBoolean("NewUser",true);


        extras.putString("ShopName", shopName);
        extras.putString("Location", loc);
        extras.putDouble("ShopLat", shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putInt("Files", files);
        extras.putDouble("Price", totalPrice);

        extras.putBoolean("IsTester", isTester);
        extras.putLong("ShopNum", shopNum);


        extras.putStringArrayList("URLS", urls);
        extras.putIntegerArrayList("Pages", numberOfPages);

        extras.putBooleanArray("BothSides", bothSides);
        extras.putStringArrayList("Custom", customPages);
        extras.putStringArrayList("FileNames",fileNames);
        extras.putStringArrayList("FileType", fileTypes);
        extras.putStringArrayList("PageSize", pageSize);
        extras.putStringArrayList("Orientation", orientations);
        extras.putIntegerArrayList("Copies", copies);
        extras.putStringArrayList("ColorType", colors);

        extras.putString("ShopKey", shopKey);
        extras.putDouble("User Lat", userLat);
        extras.putDouble("User Long", userLong);
        extras.putStringArrayList("FileSizes",fileSizes);
        extras.putIntegerArrayList("CustomPages1",customPage1);
        extras.putIntegerArrayList("CustomPages2",customPage2);
        extras.putDoubleArray("PricePerFile",pricePerFile);
        extras.putDouble("TotalPrice",totalPrice);
        intent.putExtras(extras);

        startActivity(intent);
        finish();


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
            gifRL.setVisibility(View.GONE);

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(num)) {
            numberTV.setError(getString(R.string.error_field_required));
            focusView = numberTV;
            cancel = true;
            gifRL.setVisibility(View.GONE);

        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            gifRL.setVisibility(View.GONE);
        } else {
            Log.d("Name is ",name);
            Log.d("num is", String.valueOf(num));

            // TODO: Call create FirebaseUser() here
            uploadUserData(name,num);

        }
    }

    private void uploadUserData(final String name, final String num){

        final UserInfo info = new UserInfo(name,email,Long.parseLong(num),"android");

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                ///////Saving users name on the android phone locally./////
                saveDisplayNameLocally(name,Long.parseLong(num));

                    if(newUser){
                        Toast.makeText(FirstNameActivity.this, "NEW_USER "+newUser, Toast.LENGTH_SHORT).show();
                        ref.child("users").child(userId).setValue(info);
                        sendNewUsersOrderData();
                    }else{
                        Toast.makeText(FirstNameActivity.this, "NOT_NEW_USER "+newUser, Toast.LENGTH_SHORT).show();

                        Intent goToMainPage = new Intent(FirstNameActivity.this,Select.class);
                        ref.child("users").child(userId).setValue(info);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("NewUser",false);
                        goToMainPage.putExtras(bundle);
                        startActivity(goToMainPage);
                        finish();

                    }
                }
            });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

//    public void getInfoSavedLocally(){
//        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
//        isUser = sharedPreferences.getString("UserID","No user");
//
//    }


    public void saveDisplayNameLocally(String name,long num){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        sharedPreferences.edit().putString("DisplayName",name).apply();
        sharedPreferences.edit().putLong("UserNumber",num).apply();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            sharedPreferences.edit().putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();
        }
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
