package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

//import static org.bouncycastle.crypto.tls.ContentType.alert;

public class SignInActivity extends AppCompatActivity {
    private static final String  TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    public String SharedPrefs = "Data";

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
//    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private AutoCompleteTextView emailTV;
    private EditText passwordTV;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    TextView signUp_InTV;
    EditText phoneNum;



    String loc,orderStatus,shopKey,fileType,pagesize,orientation,email,shopName;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    long shopNum;
    int files;
    double price;
    int copy;
    int resultCode;
    int requestCode;
    double numberOfPages;
    String color,custom;
    boolean FromYourOrders =false, bothSides,isTester,newUser;
    ArrayList<String> urls = new ArrayList<>();
    Boolean signUp,isShowPassword = false;
    Button forgotPassword,back;
    ImageButton showPass;


    Intent otpIntent;
    Bundle otpBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

//        getSupportActionBar().hide();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.INTERNET}, 1);
        }
        emailTV = findViewById(R.id.EmailTV);
        passwordTV = findViewById(R.id.PasswordTV);
        Button SignInButton = findViewById(R.id.SignIn);
        signUp_InTV = findViewById(R.id.SignIn_Up);
        forgotPassword = findViewById(R.id.forgotPasswordBtn);
        back = findViewById(R.id.back);
        showPass = findViewById(R.id.showPassword);
        phoneNum = findViewById(R.id.phoneNumber);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        signUp = extras.getBoolean("SignUp");
        newUser = extras.getBoolean("NewUser");

        if(signUp){
            signUp_InTV.setText("Sign Up with");
            forgotPassword.setVisibility(View.INVISIBLE);
            forgotPassword.setEnabled(false);

        }else{
            signUp_InTV.setText("Sign in with");
            forgotPassword.setVisibility(View.VISIBLE);
            forgotPassword.setEnabled(true);
        }

        if(newUser){
            getNewUserOrderDetails();
        }



        emailTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {

                if (id == 200 || id == EditorInfo.IME_NULL) {
                    attemptRegistration();

                    return true;
                }

                return false;
            }
        });


        //TODO: Get a hold of an instance of firebase auth.
        mAuth = FirebaseAuth.getInstance();

        SignInButton.setOnClickListener(Listener);
        back.setOnClickListener(Listener);




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



        forgotPassword.setOnClickListener(Listener);
        showPass.setOnClickListener(Listener);

        otpIntent = new Intent(SignInActivity.this,verifyOTPActivity.class);
        otpBundle = new Bundle();

    }
    public void getNewUserOrderDetails(){


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
//        signUp = extras.getBoolean("SignUp");
//        newUser = extras.getBoolean("NewUser");



        //////////////////////////////////////////////////Shop Info//////////////////////////////////////////
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        shopName = extras.getString("ShopName");
        loc = extras.getString("Location");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
        price = extras.getDouble("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        shopKey = extras.getString("ShopKey");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");


        /////////////////////////////////////////////////Order info////////////////////////////////////////


        fileType = extras.getString("FileType");
        pagesize = extras.getString("PageSize");
        orientation = extras.getString("Orientation");

        shopNum = extras.getLong("ShopNum");
        urls = extras.getStringArrayList("URLS");
        copy = extras.getInt("Copies");
        color = extras.getString("ColorType");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
        bothSides = extras.getBoolean("BothSides");
        custom = extras.getString("Custom");
        numberOfPages = extras.getDouble("Pages");
        isTester = extras.getBoolean("IsTester");
        newUser = extras.getBoolean("NewUser");


    }
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthCredential mcredential;
    public void sendNewUsersOrderData(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent;

        Toast.makeText(this,String.valueOf(mVerificationId),Toast.LENGTH_LONG).show();
        if(mVerificationId != null){
            intent = new Intent(SignInActivity.this,verifyOTPActivity.class);
        }else {
            intent = new Intent(SignInActivity.this, FirstNameActivity.class);
        }
        Bundle extras = new Bundle();

        extras.putBoolean("SignUp",true);
        extras.putBoolean("NewUser",true);

        extras.putString("Number",phoneNum.getText().toString());
        extras.putString("VID",mVerificationId);
        otpBundle.putParcelable("Credential",mcredential);

        if(currentUser!=null) {
            extras.putString("Email", currentUser.getEmail());
            extras.putString("Name", currentUser.getDisplayName());
        }
        extras.putStringArrayList("URLS", urls);
        extras.putString("ShopName", shopName);
        extras.putString("Location", loc);
        extras.putDouble("ShopLat", shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putInt("Files", files);
        extras.putDouble("Price", price);
        android.util.Log.d("SSSSPRRICE", String.valueOf(price));

        extras.putString("FileType", fileType);
        extras.putString("PageSize", pagesize);
        extras.putString("Orientation", orientation);
        extras.putBoolean("IsTester", isTester);
        extras.putLong("ShopNum", shopNum);

        extras.putInt("Copies", copy);
        extras.putString("ColorType", color);
        extras.putBoolean("BothSides", bothSides);
        extras.putString("Custom", custom);
        extras.putString("ShopKey", shopKey);
//        extras.putString("UserID", userID);
        extras.putDouble("User Lat", userLat);
        extras.putDouble("User Long", userLong);
        extras.putInt("RequestCode", requestCode);
        extras.putInt("ResultCode", resultCode);
        extras.putDouble("Pages", numberOfPages);
        intent.putExtras(extras);
        startActivity(intent);


    }



    private View.OnClickListener Listener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked
            if(v == findViewById(R.id.SignIn)) {
                if(phoneNum.getText().length() == 0) {
                    attemptRegistration();
                }else{
                    Toast.makeText(SignInActivity.this, "PHONE "+phoneNum.getText(), Toast.LENGTH_SHORT).show();
                    attemptPhoneAuth("+1"+phoneNum.getText().toString().trim());
                }
            }else
            if(v == findViewById(R.id.forgotPasswordBtn)){
                // Check for a valid email address.

                emailTV.setError(null);

                // Store values at the time of the login attempt.
                String email = emailTV.getText().toString();
                if (!TextUtils.isEmpty(email)) {

                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(getApplicationContext(),"Reset Password email sent to the registered email.\n * Check your spam and promotions folder as well *",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                     }else{
                        emailTV.setError(getString(R.string.error_field_required));
                    }
                }else
                    if(v == findViewById(R.id.back)){
                        finish();
                    }else
                        if(v == findViewById(R.id.showPassword)){
                            if(!isShowPassword){
                                passwordTV.setInputType(InputType.TYPE_CLASS_TEXT);
                                isShowPassword = true;
                                Log.d("SHOWI", String.valueOf(isShowPassword));
                            }else{
                                passwordTV.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                isShowPassword = false;
                                Log.d("SHOWING", String.valueOf(isShowPassword));

                            }
                        }

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
                otpIntent.putExtras(otpBundle);

                startActivity(otpIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }else {
                sendNewUsersOrderData();
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e);

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
                otpBundle.putString("Number",phoneNum.getText().toString());
                otpBundle.putString("VID",mVerificationId);
                otpIntent.putExtras(otpBundle);
                startActivity(otpIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }else{
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
            hud.show();

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
                                hud.dismiss();
                                startActivity(intent);
                            }
//                            finish();
                        }else{
                            Intent intent = new Intent(SignInActivity.this, Select.class);
                            startActivity(intent);
                            hud.dismiss();
                            finish();
                        }
//                    }
//                    else {
//                        Intent intent = new Intent(SignInActivity.this, FirstNameActivity.class);
//                        Bundle extras = new Bundle();
//                        extras.putString("Email", currentUser.getEmail());
////                        extras.putString("Name",currentUser.getDisplayName());
//                        intent.putExtras(extras);
//                        hud.dismiss();
////                        startActivity(intent);
//                    }
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

        hud.show();

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
            hud.dismiss();
            passwordTV.setError(getString(R.string.error_invalid_password));
            focusView = passwordTV;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            hud.dismiss();
            emailTV.setError(getString(R.string.error_field_required));
            focusView = emailTV;
            cancel = true;
        } else if (!isEmailValid(email)) {
            hud.dismiss();
            emailTV.setError(getString(R.string.error_invalid_email));
            focusView = emailTV;
            cancel = true;
        }

        if (cancel) {
            hud.dismiss();
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Log.d("Email is ",email);
            Log.d("Password is",password);

            // TODO: Call create FirebaseUser() here
            login();
            hud.dismiss();


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
        hud.show();

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
                    finish();
                }
            }
        });
    }

    private void createUser(final String email, String password){

        final KProgressHUD hud = KProgressHUD.create(SignInActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait.")
                .setMaxProgress(100);
        hud.show();

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
