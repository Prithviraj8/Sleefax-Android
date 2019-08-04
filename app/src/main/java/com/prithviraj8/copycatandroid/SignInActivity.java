package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;

public class SignInActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
//    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private AutoCompleteTextView emailTV;
    private EditText passwordTV;
   
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        emailTV = findViewById(R.id.EmailTV);
        passwordTV = findViewById(R.id.PasswordTV);
        Button SignInButton = findViewById(R.id.SignIn);

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
                attemptRegistration();

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
    private void attemptRegistration() {

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

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("CopyCat","Logging User"+task.isSuccessful());


                if (!task.isSuccessful()) {
                    Log.d("CopyCat","Failed to log in");
                    createUser(email,password);
                }else{
                    Intent intent = new Intent(SignInActivity.this,FirstNameActivity.class);
                    intent.putExtra("Email",email);
                    startActivity(intent);
                }
            }
        });


    }
    private void createUser(final String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("CopyCat","Created User"+task.isSuccessful());
                Log.d("SAving to database","Saved to the database");

                if (!task.isSuccessful()) {
                    Log.d("CopyCat","Failed to create User");
                    showErrorDialog("Failed to create your account");

                }else{
                    Intent intent = new Intent(SignInActivity.this,FirstNameActivity.class);
                    intent.putExtra("Email",email);
                    startActivity(intent);
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
