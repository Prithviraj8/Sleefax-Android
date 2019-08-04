package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.prithviraj8.copycatandroid.StorageUpload.Upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Select extends AppCompatActivity {

    String uniqueID = UUID.randomUUID().toString();
    ArrayList<String> pageURL = new ArrayList<String>();
    ArrayList<Bitmap> images = new ArrayList<Bitmap>();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference filesRef = storageRef.child(uniqueID);

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ImageButton selectFilesBtn, toggleMenuBtn, setting,orders;
    View menu;
    int PICK_IMAGE_MULTIPLE = 1;
    final static int PICK_PDF_CODE = 2342;

    String imageEncoded;
    List<String> imagesEncodedList;
    private boolean isMenuShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_files);
        getSupportActionBar().hide();


        selectFilesBtn = findViewById(R.id.AddFilesButton);
        toggleMenuBtn = findViewById(R.id.ToggleMenuButton);
        menu = findViewById(R.id.Menu_View);
        setting = findViewById(R.id.Settings);
        orders = findViewById(R.id.YourOrders);

        selectFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
////                intent.setType("image/*");
//                intent.setType("*/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
////                startActivityForResult(intent, 7);

                getPDF();
            }
        });

        final int TRANSLATION_Y = menu.getHeight();
//        final int menuHeight = menu.getHeight();
        final int settingHeight = setting.getHeight();
        final int ordersHeight = orders.getHeight();

        toggleMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMenuShown){
                    Animation menuUp = (Animation) AnimationUtils.loadAnimation(Select.this,R.anim.bottom_up);
//                    menu.startAnimation(menuUp);
                    menu.animate()
                            .translationYBy(-(TRANSLATION_Y/2))
                            .translationY(0)
                            .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    orders.animate()
                            .translationYBy(-(60))
                            .translationY(0)
                            .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    setting.animate()
                            .translationYBy(-(60))
                            .translationY(0)
                            .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    toggleMenuBtn.animate()
                            .translationYBy(-(60))
                            .translationY(0)
                            .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    isMenuShown = true;

                }else{

                    Animation menuDown = AnimationUtils.loadAnimation(Select.this,
                            R.anim.bottom_down);
//                    menu.startAnimation(menuDown);
                    menu.animate()
                            .translationYBy(0)
                            .translationY(-242)
                            .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    orders.animate()
                            .translationYBy(0)
                            .translationY(-400)
                            .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    setting.animate()
                            .translationYBy(0)
                            .translationY(-400)
                            .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    toggleMenuBtn.animate()
                            .translationYBy(0)
                            .translationY(-222)
                            .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    isMenuShown = false;
                }
            }
        });

        final int[] orderCnt = new int[1];
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Select.this,YourOrders.class);
                final Bundle extras = new Bundle();

                ref.child("users").child(userId).addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getKey().equals("Orders")){
                            orderCnt[0] = (int) dataSnapshot.getChildrenCount();
                            extras.putInt("Orders Count",orderCnt[0]);
                            Log.d("ORDER COUNT IS ", String.valueOf(orderCnt[0]));
                            intent.putExtras(extras);
                            startActivity(intent);
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
        });

    }
    //this function will get the pdf from the storage
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
//        intent.setType("application/pdf");
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                Log.d("DATAAA", String.valueOf(data.getData()));
                //uploading the file
//                uploadFile(data.getData());
                Uri returnUri = data.getData();
                String mimeType = getContentResolver().getType(returnUri);
                Log.d("MIME",mimeType);

                if(mimeType.contains("application")){
                    Log.d("FILE","PDF");

                    uploadFile(returnUri);
                }else{
                    Log.d("FILE","Image");
                    uploadImg(requestCode,resultCode,data);
                }
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }
    Uri[] uri = new Uri[1000];

    private void uploadFile(Uri file){

        final Uri uri;
        uri = file;
        final KProgressHUD hud = KProgressHUD.create(Select.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100)
                .show();
        final UploadTask uploadTask = filesRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("UPLOAD", "Not successfull");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.d("UPLOAD", "SUCCESSFULL");

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String url;
                            Uri downloadUri = task.getResult();
                            url = String.valueOf(downloadUri);
                            hud.dismiss();

                            Intent goToPdfInfo = new Intent(Select.this,PdfInfo.class);
//                                            finish();
                            //goToPageInfo.putExtra("Pages",images);
                            Bundle extras = new Bundle();
                            extras.putString("PdfURL",url);
                            extras.putString("URI", String.valueOf(uri));
                            goToPdfInfo.putExtras(extras);
                            startActivity(goToPdfInfo);


                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
                // ...
            }
        });

    }
    private void uploadImg(int requestCode, int resultCode, Intent data) {
        final KProgressHUD hud = KProgressHUD.create(Select.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100)
                .show();
        Log.d("RQ", String.valueOf(requestCode));
        if (requestCode == PICK_PDF_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        Log.d("IMAGE URI ", String.valueOf(imageUri));
                        uri[i]=(imageUri);

                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        images.add(bitmap);
                        byte[] DATA = baos.toByteArray();
                        final UploadTask uploadTask = filesRef.putBytes(DATA);


//                    Uri uri = Uri.fromFile(mImageUri);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.d("UPLOAD", "Not successfull");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                Log.d("UPLOAD", "SUCCESSFULL");

                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }

                                        // Continue with the task to get the download URL
                                        return filesRef.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();
                                            pageURL.add(String.valueOf(downloadUri));


                                            Log.d("Pages ", String.valueOf(images));
                                            Log.d("URLS ", String.valueOf(pageURL));
                                            hud.dismiss();
                                            Intent goToPageInfo = new Intent(Select.this,PageInfo.class);
//                                            finish();
                                            //goToPageInfo.putExtra("Pages",images);
                                            Bundle extras = new Bundle();
                                            extras.putStringArrayList("URLS",pageURL);
                                            Log.d("URIII", String.valueOf(uri));
                                            extras.putStringArray("URI", new String[]{String.valueOf(uri)});
                                            goToPageInfo.putExtras(extras);
                                            startActivity(goToPageInfo);


                                        } else {
                                            // Handle failures
                                            // ...
                                        }
                                    }
                                });
                                // ...
                            }
                        });
                    }

                }
            } else if (data.getData() != null) {
                String imagePath = data.getData().getPath();
                Log.d("IMAGE PATH ", String.valueOf(imagePath));
                Log.d("IMAGE is ", String.valueOf(data.getData()));
                //do something with the image (save it to some directory or whatever you need to do with it here)
                hud.dismiss();
            }
        }else{
            Log.d("Failed","To choose files");
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//
//        switch (requestCode) {
//            case 7:
//                if (resultCode == RESULT_OK) {
//                    String PathHolder = data.getData().getPath();
//                    Toast.makeText(Select.this, PathHolder, Toast.LENGTH_LONG).show();
//                }
//                break;
//        }
//    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        final KProgressHUD hud = KProgressHUD.create(Select.this)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Please wait")
//                .setMaxProgress(100)
//                .show();
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_MULTIPLE) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (data.getClipData() != null) {
//                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
//                    for (int i = 0; i < count; i++) {
//                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                        Log.d("IMAGE URI ", String.valueOf(imageUri));
//
//                        //do something with the image (save it to some directory or whatever you need to do with it here)
//                        Bitmap bitmap = null;
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        images.add(bitmap);
//                        byte[] DATA = baos.toByteArray();
//                        final UploadTask uploadTask = filesRef.putBytes(DATA);
//
//
////                    Uri uri = Uri.fromFile(mImageUri);
//                        uploadTask.addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
//                                // Handle unsuccessful uploads
//                                Log.d("UPLOAD", "Not successfull");
//                            }
//                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                                Log.d("UPLOAD", "SUCCESSFULL");
//
//                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                                    @Override
//                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                        if (!task.isSuccessful()) {
//                                            throw task.getException();
//                                        }
//
//                                        // Continue with the task to get the download URL
//                                        return filesRef.getDownloadUrl();
//                                    }
//                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//
//                                    @Override
//                                    public void onComplete(@NonNull Task<Uri> task) {
//                                        if (task.isSuccessful()) {
//                                            Uri downloadUri = task.getResult();
//                                            pageURL.add(String.valueOf(downloadUri));
//
//
//                                            Log.d("Pages ", String.valueOf(images));
//                                            Log.d("URLS ", String.valueOf(pageURL));
//                                            hud.dismiss();
//                                            Intent goToPageInfo = new Intent(Select.this,PageInfo.class);
////                                            finish();
//                                            //goToPageInfo.putExtra("Pages",images);
//                                            goToPageInfo.putExtra("URLS",pageURL);
//                                            startActivity(goToPageInfo);
//
//
//                                        } else {
//                                            // Handle failures
//                                            // ...
//                                        }
//                                    }
//                                });
//                                // ...
//                            }
//                        });
//                    }
//
//                }
//            } else if (data.getData() != null) {
//                String imagePath = data.getData().getPath();
//                Log.d("IMAGE PATH ", String.valueOf(imagePath));
//                Log.d("IMAGE is ", String.valueOf(data.getData()));
//                //do something with the image (save it to some directory or whatever you need to do with it here)
//                hud.dismiss();
//            }
//        }else{
//            Log.d("Failed","To choose files");
//        }
//    }
private void showErrorDialog(String message){
    new AlertDialog.Builder(this)
            .setTitle("What would you like to choose? " +
                    "An image or a file ?")
            .setMessage(message)
            .setPositiveButton(android.R.string.ok,null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
}
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("we are in","YAYYYY");
//        Log.d("Data is : ", String.valueOf(data));
//        Log.d("Request Code is ", String.valueOf(requestCode));
//        Log.d("Result Code is ", String.valueOf(resultCode));
//
//
//        try {
//            // When an Image is picked
//            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
//                    && data != null) {
//                // Get the Image from data
//
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                imagesEncodedList = new ArrayList<String>();
//                if(data.getData()!=null){
//
//                    Uri mImageUri=data.getData();
//
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    images.add(bitmap);
//                    byte[] DATA = baos.toByteArray();
//                    final UploadTask uploadTask = filesRef.putBytes(DATA);
//
//
//
////                    Uri uri = Uri.fromFile(mImageUri);
//                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle unsuccessful uploads
//                            Log.d("UPLOAD","Not successfull");
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                            Log.d("UPLOAD","SUCCESSFULL");
//
//                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                                @Override
//                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                    if (!task.isSuccessful()) {
//                                        throw task.getException();
//                                    }
//
//                                    // Continue with the task to get the download URL
//                                    return filesRef.getDownloadUrl();
//                                }
//                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    if (task.isSuccessful()) {
//                                        Uri downloadUri = task.getResult();
//                                        pageURL.add(String.valueOf(downloadUri));
//
////                                        Intent goToPageInfo = new Intent(Select.this,PageInfo.class);
////                                        finish();
//////                                        goToPageInfo.putExtra("Pages",images);
////                                        goToPageInfo.putExtra("URLS",pageURL);
//                                        Log.d("Pages ", String.valueOf(images));
//                                        Log.d("URLS ", String.valueOf(pageURL));
////                                        startActivity(goToPageInfo);
//
//                                    } else {
//                                        // Handle failures
//                                        // ...
//                                    }
//                                }
//                            });
//                            // ...
//                        }
//                    });
//
//                    // Get the cursor
//                    Cursor cursor = getContentResolver().query(mImageUri,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    imageEncoded  = cursor.getString(columnIndex);
//                    cursor.close();
//
//                } else if (data.getClipData() != null) {
//                        ClipData mClipData = data.getClipData();
//                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
//                        for (int i = 0; i < mClipData.getItemCount(); i++) {
//
//                            ClipData.Item item = mClipData.getItemAt(i);
//                            Uri uri = item.getUri();
//                            mArrayUri.add(uri);
//                            Log.d("IM URI is ", String.valueOf(uri));
//                            // Get the cursor
//                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//                            // Move to first row
//                            cursor.moveToFirst();
//
//                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                            imageEncoded  = cursor.getString(columnIndex);
//                            imagesEncodedList.add(imageEncoded);
//                            cursor.close();
//
//                        }
//                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
//
//                }
//            } else {
//                Log.d("No image selected","NOOO");
//                Toast.makeText(this, "You haven't picked Image",
//                        Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Log.d("No image selected","NOOO");
//
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
//                    .show();
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}
