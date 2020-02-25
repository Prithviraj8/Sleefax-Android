package com.Anubis.Sleefax;

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
        if (Intent.ACTION_SEND_MULTIPLE.equals(action) || Intent.ACTION_SEND.equals(action) && type != null ) {
            handleSendData(intent); // Handle single image being sent
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


    ArrayList<Uri> uri = new ArrayList<>();
    String fileType;
    public void handleSendData(Intent data){
        if (data.getData() != null) {
            //uploading the file

            Uri returnUri = data.getData();
//                uri.add(returnUri);


            Log.d("URIID", String.valueOf(returnUri));
            String mimeType = getContentResolver().getType(returnUri);
            Log.d("MIME", mimeType);

            if (mimeType.contains("application/pdf")) {

                Log.d("FILE", mimeType);
                fileType = mimeType;
//                Log.d("DATACOUNT", String.valueOf(data.getClipData().getItemCount()));
                uploadFile(data,returnUri);


            } else {

                Log.d("FILE", "Image");
                fileType = mimeType;

                if(data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        if (data.getClipData().getItemAt(i).getUri() != null) {
                            uri.add(data.getClipData().getItemAt(i).getUri());
                            if (i == data.getClipData().getItemCount() - 1) {
                                uploadImg(data, uri);
                            }
                        }
                    }

                }else{
                    uri.add(returnUri);
                    uploadImg(data,uri);
                }
            }
        } else {

//                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            Log.d("MIMETP",String.valueOf(data.getClipData().getDescription()));
            Log.d("DATA", String.valueOf(data.getClipData()));
            fileType = "image/png";

            String type = String.valueOf(data.getClipData().getDescription());
            Log.d("SHARETYPE",type);
            if(type.contains("application") || type.contains("*/*")) {
                if (data.getClipData().getItemCount() > 1){
                    Log.d("WRONG", "FORMAT");
                    Toast.makeText(FlashActivity.this, "So Sorry! 😢 But we are still working on providing the functionality of selecting multiple pdf/docs and other formats.", Toast.LENGTH_LONG).show();
                }else {

                    uploadFile(data,data.getClipData().getItemAt(0).getUri());
                    Log.d("DRIVE","=DATA ");
                }
            }else{
                if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        if (data.getClipData().getItemAt(i).getUri() != null) {
                            uri.add(data.getClipData().getItemAt(i).getUri());
                            if (i == data.getClipData().getItemCount() - 1) {
                                uploadImg(data, uri);
                            }
                        }
                    }

                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFile(Intent data,Uri file){

        Intent goToPdfInfo = new Intent(FlashActivity.this, PdfInfo.class);
        //goToPageInfo.putExtra("Pages",images);
        Bundle extras = new Bundle();
//        extras.putParcelable("PdfURL", file);
//        extras.putInt("Pages",numberOfPages);
        extras.putString("PdfURL", file.toString());
        extras.putString("FileType", fileType);
//        extras.putInt("RequestCode",1);
//        extras.putInt("ResultCode",0);
//        extras.putString("FileName",fileName);
        goToPdfInfo.putExtras(extras);
        startActivity(goToPdfInfo);
        finish();

    }


    public void uploadImg(Intent data, ArrayList<Uri> uri){
        Intent goToPageInfo = new Intent(FlashActivity.this, PageInfo.class);
        Bundle extras = new Bundle();
        extras.putString("FileType", fileType);
        ArrayList<String> images = new ArrayList<>();
        int i;

        for(i=0;i<uri.size();i++){
            images.add(uri.get(i).toString());

            Log.d("I IS", String.valueOf(i));
            if(i == uri.size()-1) {
//                Log.d("URISIZE", String.valueOf(uri.size()));
                extras.putStringArrayList("URLS", images);
                extras.putParcelable("Data",data);
//            extras.putParcelableArrayList("URLS", uri);
                goToPageInfo.putExtras(extras);
                startActivity(goToPageInfo);
            }
        }

    }
}
