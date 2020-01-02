package com.Prithviraj8.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FlashActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ProgressDialog mProgressDialog;
    long cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);



        Intent intent = getIntent();
// Get the action of the intent
        String action = intent.getAction();
// Get the type of intent (Text or Image)
        String type = intent.getType();
// When Intent's action is 'ACTION+SEND' and Tyoe is not null
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image")) { // When type is 'image/*'
                handleSendImage(intent); // Handle single image being sent
            }else{
                handleSendFile(intent);
            }

        }else {
            mProgressDialog = new ProgressDialog(FlashActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Retreiving Orders");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
            Context context = getApplicationContext();
            CharSequence text = "No Orders to show";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            getOrders();
        }

    }

    public void getOrders(){

        final ArrayList<String> orderkey = new ArrayList<>();
        final ArrayList<String> shopKey = new ArrayList<>();

//        setProgressForOrder();

        ref.child("users").child(userId).child("Orders").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                shopKey.add(dataSnapshot.getKey());
                Log.d("USERID",userId);
                Log.d("ORDERCOUNT ", String.valueOf(dataSnapshot.getChildrenCount()));

                cnt = dataSnapshot.getChildrenCount()+cnt;
                Log.d("ORDERCOUNT ", String.valueOf((cnt)));

//                for(DataSnapshot order : dataSnapshot.getChildren()){
//                    orderkey.add(order.getKey());
//                }
                if (cnt == 0) {
//                    toast.show();
                    mProgressDialog.dismiss();
                } else {

                    Intent intent = new Intent(FlashActivity.this, YourOrders.class);
                    Bundle extras = new Bundle();
                    extras.putLong("Orders Count", cnt);
                    extras.putBoolean("NotificationPressed",true);
                    intent.putExtras(extras);
                    startActivity(intent);
                    mProgressDialog.dismiss();
                    finish();

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


    public void handleSendImage(Intent intent){
// Get the image URI from intent
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        ArrayList<String> images = new ArrayList<>();
        String mimeType = getContentResolver().getType(imageUri);

        // When image URI is not null
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Log.d("RECEIVED",imageUri.toString());

            images.add(imageUri.toString());
            Intent intent1 = new Intent(FlashActivity.this,PageInfo.class);
            Bundle extras = new Bundle();
            extras.putStringArrayList("URLS",images);
            extras.putString("FileType", mimeType);
            intent1.putExtras(extras);
            startActivity(intent1);
            finish();

        } else{
            Toast.makeText(this, "Error occured, URI is invalid", Toast.LENGTH_LONG).show();
        }
    }
    public void handleSendFile(Intent intent){
        Uri file = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String mimeType = getContentResolver().getType(file);

        Intent intent1 = new Intent(FlashActivity.this,PdfInfo.class);
        Bundle extras = new Bundle();
        extras.putString("PdfURL", file.toString());
        extras.putString("FileType", mimeType);
        intent1.putExtras(extras);
        startActivity(intent1);
        finish();
    }

}
