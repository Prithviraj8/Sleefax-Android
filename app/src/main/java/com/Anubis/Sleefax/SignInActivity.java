package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.Anubis.Sleefax.PdfInfo.dpToPx;

//import static org.bouncycastle.crypto.tls.ContentType.alert;

public class SignInActivity extends AppCompatActivity {
    private static final String  TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    public String SharedPrefs = "Data";

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    KProgressHUD hud;

    RelativeLayout signinRL,rootLayout, phoneRL, loginRL, gifRL;
    Button forgotPassword,back,Login, SignInButton;
    ImageButton showPass;

    private AutoCompleteTextView emailTV;
    private EditText passwordTV;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    TextView signUp_InTV,phoneNum,Terms,loginTV,mobileTv, ccTv,mtv, validTv;
    EditText Phone;
    ImageView phoneIv;
    GifImageView gifImageView;

    String loc,orderStatus,shopKey,fileType,pagesize,orientation,email,shopName;
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


    int top,left;
    Intent otpIntent;
    Bundle otpBundle;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

//        getSupportActionBar().hide();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.INTERNET}, 1);
        }
        // Progress HUD
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading")
                .setMaxProgress(100);
//        emailTV = findViewById(R.id.EmailTV);
//        passwordTV = findViewById(R.id.PasswordTV);
//        signUp_InTV = findViewById(R.id.SignIn_Up);
//        forgotPassword = findViewById(R.id.forgotPasswordBtn);
//        back = findViewById(R.id.back);
//        showPass = findViewById(R.id.showPassword);

        SignInButton = findViewById(R.id.SignIn);
        phoneNum = findViewById(R.id.phoneNumber);
        rootLayout = findViewById(R.id.rootlayout);
        signinRL = findViewById(R.id.signinRL);
        phoneRL = findViewById(R.id.phoneRL);
        loginRL = findViewById(R.id.relativeLayoutLogin);
        Terms = findViewById(R.id.TermsTV);
        Phone = findViewById(R.id.PhoneNumber);

        Login = findViewById(R.id.login_with_phoneBtn);
        mobileTv = findViewById(R.id.mobilenumTV);
        ccTv = findViewById(R.id.country_codeTV);
        phoneIv = findViewById(R.id.phoneIV);
        mtv = findViewById(R.id.phoneNumber);

        gifRL = findViewById(R.id.GifRL);
        gifImageView = (GifImageView) findViewById(R.id.GIF);
        gifImageView.setGifImageResource(R.raw.animation_640_kchmf79t);
//        gifRL.setVisibility(View.VISIBLE);

        setupInitialViews();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        signUp = extras.getBoolean("SignUp");
        newUser = extras.getBoolean("NewUser");

        if(newUser){
            getNewUserOrderDetails();
        }


        //TODO: Get a hold of an instance of firebase auth.
        mAuth = FirebaseAuth.getInstance();
        SignInButton.setOnClickListener(Listener);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("736925032543-ip2eafmsbot3u28o4i05dctidqjssc4r.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        com.google.android.gms.common.SignInButton GoogleSignIn = findViewById(R.id.GoogleSignIn);
        GoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        otpIntent = new Intent(SignInActivity.this,verifyOTPActivity.class);
        otpBundle = new Bundle();




    }


    public void setupInitialViews(){
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int diff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
                if(diff > dpToPx(getApplicationContext(),200)){


                    Resources r = getResources();
                    int margin = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            50,
                            r.getDisplayMetrics());



                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.topMargin = margin;
                    loginRL.setLayoutParams(params);


                    Terms.setVisibility(View.INVISIBLE);
                    signinRL.setVisibility(View.INVISIBLE);
                }


                if(diff < dpToPx(getApplicationContext(),200))
                {

                    Resources r = getResources();
                    int margin = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            60,
                            r.getDisplayMetrics());


                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.topMargin = margin;
                    loginRL.setLayoutParams(params);

                    Terms.setVisibility(View.VISIBLE);
                    signinRL.setVisibility(View.VISIBLE);

                }

            }
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

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccTv.setVisibility(View.VISIBLE);
                Phone.setVisibility(View.VISIBLE);
                mobileTv.setVisibility(View.VISIBLE);

                Animation animation1 = new AlphaAnimation(1.0f,0.0f);
                animation1.setDuration(500);


                phoneIv.startAnimation(animation1);
                phoneNum.startAnimation(animation1);


                Animation animation2 = new AlphaAnimation(0.1f,1.0f);
                animation2.setDuration(500);


                mobileTv.startAnimation(animation2);
                ccTv.startAnimation(animation2);
                Phone.startAnimation(animation2);




                Animation animation = new TranslateAnimation(0,-left,0,-top);
                animation.setDuration(500);
                animation.setFillEnabled(true);
                animation.setFillAfter(true);

                mobileTv.startAnimation(animation);

                Login.setVisibility(View.INVISIBLE);



            }
        });




        Phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                SignInButton.setVisibility(View.VISIBLE);
                signinRL.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

                SignInButton.setVisibility(View.VISIBLE);
                signinRL.setVisibility(View.VISIBLE);

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
        newUser = extras.getBoolean("NewUser");

    }


    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthCredential mcredential;




    private View.OnClickListener Listener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked

//            hud.show();
            gifRL.setVisibility(View.VISIBLE);
            if(v == findViewById(R.id.SignIn)) {
                if(Phone.getText().length() == 0) {
//                    hud.dismiss();
                    gifRL.setVisibility(View.GONE);
//                    attemptRegistration();
                }else{
                    SignInButton.setVisibility(View.GONE);
                    attemptPhoneAuth("+1"+Phone.getText().toString().trim());

                }
            }
//            else
//            if(v == findViewById(R.id.forgotPasswordBtn)){
//                // Check for a valid email address.
//
//                emailTV.setError(null);
//
//                // Store values at the time of the login attempt.
//                String email = emailTV.getText().toString();
//                if (!TextUtils.isEmpty(email)) {
//
//                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.d(TAG, "Email sent.");
//                                        Toast.makeText(getApplicationContext(),"Reset Password email sent to the registered email.\n * Check your spam and promotions folder as well *",Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                     }else{
//                        emailTV.setError(getString(R.string.error_field_required));
//                    }
//                }else
//                    if(v == findViewById(R.id.back)){
//                        finish();
//                    }else
//                        if(v == findViewById(R.id.showPassword)){
//                            if(!isShowPassword){
//                                passwordTV.setInputType(InputType.TYPE_CLASS_TEXT);
//                                isShowPassword = true;
//                                Log.d("SHOWI", String.valueOf(isShowPassword));
//                            }else{
//                                passwordTV.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                                isShowPassword = false;
//                                Log.d("SHOWING", String.valueOf(isShowPassword));
//
//                            }
//                        }

            }


        };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


//    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private void attemptPhoneAuth(final String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:" + credential);

            mcredential = credential;


            if(!newUser){
                otpBundle.putParcelable("Credential",credential);
                otpBundle.putString("Number",Phone.getText().toString());

                otpIntent.putExtras(otpBundle);

                startActivity(otpIntent);
//                hud.dismiss();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                gifRL.setVisibility(View.GONE);

            }else {
                Toast.makeText(SignInActivity.this, "NEW_NUMBER"+ Phone.getText().toString(), Toast.LENGTH_SHORT).show();
                sendNewUsersOrderData();
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e);
            SignInButton.setVisibility(View.VISIBLE);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            messageBox("Phone number entered seems of the wrong format 🤨");
            // ...
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            if(!newUser) {
                otpBundle.putString("Number",Phone.getText().toString());
                otpBundle.putString("VID",mVerificationId);
                otpIntent.putExtras(otpBundle);
                startActivity(otpIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

//                hud.dismiss();
                gifRL.setVisibility(View.GONE);

            }else{
                Toast.makeText(SignInActivity.this, "NEW_NUMBER"+ Phone.getText().toString(), Toast.LENGTH_SHORT).show();
                sendNewUsersOrderData();
            }



            // ...
        }
    };


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                SignInButton.setVisibility(View.VISIBLE);

                // ...
            }
        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Log.d("Failed ","Authentication Failed.");
                            updateUI(null);
                            SignInButton.setVisibility(View.VISIBLE);

                        }

                        // ...
                    }
                });
    }

    private void updateUI(final FirebaseUser currentUser){


        final KProgressHUD hud = KProgressHUD.create(SignInActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100);

        final String[] name = new String[1];
        if(currentUser != null) {
            saveEmailLocally(email);
//            hud.show();
            gifRL.setVisibility(View.VISIBLE);


            ref.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                    Log.d("USERID", dataSnapshot.getKey());
//                    if (dataSnapshot.getKey().equals(currentUser.getUid())) {
//                        Intent intent = new Intent(SignInActivity.this, Select.class);
//                        startActivity(intent);
//                        hud.dismiss();
//                        finish();

//                    }else {

                        if (!dataSnapshot.hasChild(currentUser.getUid())) {
                            if(newUser){
                                sendNewUsersOrderData();
                            }else {
                                Intent intent = new Intent(SignInActivity.this, FirstNameActivity.class);
                                Bundle extras = new Bundle();
                                extras.putString("Email", currentUser.getEmail());
                                extras.putString("Name", currentUser.getDisplayName());
                                intent.putExtras(extras);
//                                hud.dismiss();
                                gifRL.setVisibility(View.GONE);

                                startActivity(intent);
                            }
//                            finish();
                        }else{
                            Intent intent = new Intent(SignInActivity.this, Select.class);
                            startActivity(intent);
//                            hud.dismiss();
                            gifRL.setVisibility(View.GONE);

                        }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }




    private void attemptRegistration(){
        final KProgressHUD hud = KProgressHUD.create(SignInActivity.this)
                .setStyle(KProgressHUD.Style.PIE_DETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100);

//        hud.show();

        // Reset errors displayed in the form.
        emailTV.setError(null);
        passwordTV.setError(null);

        // Store values at the time of the login attempt.
        String email = emailTV.getText().toString();
        String password = passwordTV.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
//            hud.dismiss();
            gifRL.setVisibility(View.GONE);

            passwordTV.setError(getString(R.string.error_invalid_password));
            focusView = passwordTV;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
//            hud.dismiss();
            gifRL.setVisibility(View.GONE);

            emailTV.setError(getString(R.string.error_field_required));
            focusView = emailTV;
            cancel = true;
        } else if (!isEmailValid(email)) {
//            hud.dismiss();
            gifRL.setVisibility(View.GONE);

            emailTV.setError(getString(R.string.error_invalid_email));
            focusView = emailTV;
            cancel = true;
        }

        if (cancel) {
//            hud.dismiss();
            gifRL.setVisibility(View.GONE);

            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Log.d("Email is ",email);
            Log.d("Password is",password);

            // TODO: Call create FirebaseUser() here
            login();
//            hud.dismiss();


        }
    }


    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Add own logic to check for a valid password (minimum 6 characters)
        if(password.length()<6){
            showErrorDialog("Password must be atleast 6 letters/numbers.");
        }
        return true;
    }
    private void login(){
        final String email = emailTV.getText().toString();
        final String password = passwordTV.getText().toString();

        final KProgressHUD hud = KProgressHUD.create(SignInActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait.")
                .setMaxProgress(100);
//        hud.show();
        gifRL.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("CopyCat","Logging User"+task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d("CopyCat","Failed to log in");
                    createUser(email,password);
                    hud.dismiss();

                }else{
                    hud.dismiss();
                    Intent intent = new Intent(SignInActivity.this,Select.class);
                    intent.putExtra("Email",email);
                    startActivity(intent);
                }
            }
        });
    }

    private void createUser(final String email, String password){

        final KProgressHUD hud = KProgressHUD.create(SignInActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait.")
                .setMaxProgress(100);
//        hud.show();
        gifRL.setVisibility(View.VISIBLE);


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("CopyCat","Created User"+task.isSuccessful());
                Log.d("SAving to database","Saved to the database");



                if (!task.isSuccessful()) {
                    Log.d("CopyCat",task.toString());
                    hud.dismiss();
//                    showErrorDialog("Failed to create your account");
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showErrorDialog(e.getMessage());
                        }
                    });
                }else{

                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Log.d("HAS CHILD","YES");
                                hud.dismiss();
//                                Intent intent = new Intent(SignInActivity.this,Select.class);
//                                intent.putExtra("Email",email);
//                                startActivity(intent);
//                                finish();
                            }else{
                                if(newUser){
                                    sendNewUsersOrderData();
                                }else {
                                    hud.dismiss();
                                    Intent intent = new Intent(SignInActivity.this, FirstNameActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("NewUser", newUser);
                                    bundle.putString("Email", email);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
//                                finish();
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }



    public void sendNewUsersOrderData(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;

        if(mVerificationId != null){
            intent = new Intent(SignInActivity.this,verifyOTPActivity.class);
        }else {
            intent = new Intent(SignInActivity.this, FirstNameActivity.class);
        }
        Bundle extras = new Bundle();

        extras.putBoolean("SignUp",true);
        extras.putBoolean("NewUser",true);

        extras.putString("Number",Phone.getText().toString());
        extras.putString("VID",mVerificationId);
        extras.putParcelable("Credential",mcredential);

        if(currentUser!=null) {
            extras.putString("Email", currentUser.getEmail());
            extras.putString("Name", currentUser.getDisplayName());
        }
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

//            hud.dismiss();
        gifRL.setVisibility(View.GONE);
        startActivity(intent);


    }

    public void saveEmailLocally(String email){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        sharedPreferences.edit().putString("Email",email).apply();
    }

    protected void messageBox(String message) {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                    }
                });

        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }
    //TODO : Create an alert dialog

    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
