package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.sql.Connection;
import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String  TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
//    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private AutoCompleteTextView emailTV;
    private EditText passwordTV;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    TextView signUp_InTV;



    Boolean signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
//        getSupportActionBar().hide();





        emailTV = findViewById(R.id.EmailTV);
        passwordTV = findViewById(R.id.PasswordTV);
        Button SignInButton = findViewById(R.id.SignIn);
        signUp_InTV = findViewById(R.id.SignIn_Up);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        signUp = extras.getBoolean("SignUp");

        if(signUp){
            signUp_InTV.setText("Sign Up with");
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

        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final KProgressHUD hud = KProgressHUD.create(SignInActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait")
                        .setMaxProgress(100)
                        .show();
//                hud.show();
                attemptRegistration();
                hud.dismiss();
            }
        });





//Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
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



    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }




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
                            Intent intent = new Intent(SignInActivity.this, FirstNameActivity.class);
                            Bundle extras = new Bundle();
                            extras.putString("Email", currentUser.getEmail());
                            extras.putString("Name", currentUser.getDisplayName());
                            intent.putExtras(extras);
                            hud.dismiss();
                            startActivity(intent);
                            finish();
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



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"Connection Failed");
    }


    private void attemptRegistration(){

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
            passwordTV.setError(getString(R.string.error_invalid_password));
            focusView = passwordTV;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailTV.setError(getString(R.string.error_field_required));
            focusView = emailTV;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailTV.setError(getString(R.string.error_invalid_email));
            focusView = emailTV;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Log.d("Email is ",email);
            Log.d("Password is",password);

            // TODO: Call create FirebaseUser() here
            login();


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
                    Intent intent = new Intent(SignInActivity.this,FirstNameActivity.class);
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
                    showErrorDialog("Failed to create your account");
                hud.dismiss();
                }else{

                    hud.dismiss();
                    Intent intent = new Intent(SignInActivity.this,FirstNameActivity.class);
                    intent.putExtra("Email",email);
                    startActivity(intent);
                    finish();
                }
            }
        });
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
