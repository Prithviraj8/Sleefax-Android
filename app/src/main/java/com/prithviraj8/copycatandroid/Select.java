package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.paytm.pgsdk.PaytmPGService;
//import com.spire.presentation.Presentation;
//import com.spire.presentation.FileFormat;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Select extends AppCompatActivity {
    NotificationManagerCompat notificationManager;
    String CHANNEL_ID = "UsersChannel";

    ArrayList<String> pageURL = new ArrayList<String>();
    ArrayList<Bitmap> images = new ArrayList<Bitmap>();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    String username,email;
    long num;


    Button selectFilesBtn,orders;
    ImageButton help, setting;
    View menu;

    int PICK_IMAGE_MULTIPLE = 1;
    final static int PICK_PDF_CODE = 2342;
    final static int PICK_IMAGE_CODE = 100;

    String imageEncoded;
    List<String> imagesEncodedList;
    private boolean isMenuShown = false;
    ProgressBar progressBar;

    PaytmPGService Service = PaytmPGService.getProductionService();

//    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_files);
//        getSupportActionBar().hide();

        progressBar = findViewById(R.id.progress);
//        notificationManager = NotificationManagerCompat.from(this);

        selectFilesBtn = findViewById(R.id.AddFilesButton);
        setting = findViewById(R.id.settings);
        orders = findViewById(R.id.YourOrders);
        help = findViewById(R.id.help);

        selectFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();

                Log.d("SELECTing", String.valueOf(true));
//                Intent popChoice = new Intent(Select.this, Pop.class);
//                startActivity(popChoice);
//                finish();
//
//
//                Intent get = getIntent();
//                Bundle extras = get.getExtras();
//
//
//                if (extras != null) {
//                    int selectPhotos = extras.getInt("Photos");
//                    Log.d("ATTACHMENT", String.valueOf(selectPhotos));
//
//                    if (selectPhotos != 2) {
////                        selectFiles(selectPhotos);
//                    }
//                }
            }
        });

//        selectFilesBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("MOTION", String.valueOf(event));
//                if((MotionEvent.ACTION_DOWN == 0) || (MotionEvent.ACTION_UP == 1)){
//                    selectFiles();
//                }
//                return true;
//            }
//        });

//        selectFilesBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                selectFiles();
//                return true;
//            }
//        });
//        getCurrentUserInfo();


        final long[] orderCnt = new long[1];
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(MotionEvent.ACTION_DOWN == 0 || MotionEvent.ACTION_UP == 1) {
                    final Intent intent = new Intent(Select.this, YourOrders.class);
                    final Bundle extras = new Bundle();

                    final ArrayList<String> orderkey = new ArrayList<>();
                    final ArrayList<String> shopKey = new ArrayList<>();

                    ref.child("users").child(userId).child("Orders").addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                            shopKey.add(dataSnapshot.getKey());

                            Log.d("ORDERCOUNT ", String.valueOf(dataSnapshot.getChildrenCount()));

                            for (DataSnapshot order_Key : dataSnapshot.getChildren()) {
                                orderkey.add(order_Key.getKey());
                            }


//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {

                                    if (orderkey.size() == 0) {

                                        Context context = getApplicationContext();
                                        CharSequence text = "No Orders to show";
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();

                                    } else {

                                        orderCnt[0] =  orderkey.size();
                                        extras.putLong("Orders Count", orderkey.size());
                                        intent.putExtras(extras);
                                        startActivity(intent);
//                                        finish();
                                    }
//                                }
//                            }, 300);

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
//                }
            }

        });


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Select.this,settings.class);
                startActivity(intent);
            }
        });
        //Checking for internet connection
        Boolean isNetwork = isNetworkAvailable();

//        setProgressForOrder();
//        help.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                help.performClick();
//                Log.d("HELP","PRESSED");
//                Intent intent = new Intent(Select.this,HelpActivity.class);
//                startActivity(intent);
//                return true;
//            }
//        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HELP","PRESSED");
                Intent intent = new Intent(Select.this,HelpActivity.class);
                startActivity(intent);

            }
        });




    }



    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        try {

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            haveConnectedWifi = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return haveConnectedWifi;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return haveConnectedWifi;
    }


    //this function will get the pdf from the storage
    private void selectFiles() {

        //creating an intent for file chooser

//        finish();
//        Intent get = getIntent();
//        Boolean selectPhotos = get.getBooleanExtra("Photos",false);
//        Log.d("SELECTFILES", String.valueOf(selectPhotos));
//
//        if(selectPhotos == 1){
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            startActivityForResult(Intent.createChooser(intent, "Select Images"), 1);
//
//        }else{
//            Intent intent = new Intent();
//            intent.setType("application/pdf");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            startActivityForResult(Intent.createChooser(intent, "Select files"), 1);
//        }

        Intent intent = new Intent();
//        intent.setType("application/pdf");
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//         startActivityForResult(Intent.createChooser(intent, "Select files"), 1);

        startActivityForResult(Intent.createChooser(intent, "select Picture"), PICK_PDF_CODE);

    }

    ArrayList<Uri> uri = new ArrayList<Uri>();
    String fileType;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        uri.clear();
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null ) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file

                Uri returnUri = data.getData();
                uri.add(returnUri);

                Log.d("URIID", String.valueOf(returnUri));
                String mimeType = getContentResolver().getType(returnUri);
                Log.d("MIME", mimeType);


                if (mimeType.contains("application")) {

                    Log.d("FILE", mimeType);
                    fileType = mimeType;
                    uploadFile(requestCode, resultCode,returnUri);
//                    uploadFile(returnUri);

                } else {

                    Log.d("FILE", "Image");
                    fileType = mimeType;
                    uploadImg(requestCode, resultCode, data, uri);

                }
            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void uploadFile(int requestCode, int resultCode, Uri file){
        Intent goToPdfInfo = new Intent(Select.this, PdfInfo.class);
        //goToPageInfo.putExtra("Pages",images);
        Bundle extras = new Bundle();
//        extras.putParcelable("PdfURL", file);
        Log.d("URIPASSED",file.toString());
        extras.putString("PdfURL", file.toString());
        extras.putString("FileType", fileType);
        extras.putInt("RequestCode",requestCode);
        extras.putInt("ResultCode",resultCode);
        goToPdfInfo.putExtras(extras);
        startActivity(goToPdfInfo);


    }


    public File changeExtension(File file, String extension) {
        String filename = file.getName();

        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf('.'));
        }
        filename += "." + extension;

        file.renameTo(new File(file.getParentFile(), filename));
        return file;
    }


//    private void uploadFile(Uri file) {
//        final String uniqueID = UUID.randomUUID().toString();
//
//        final StorageReference filesRef = storageRef.child(uniqueID);
//
////        Log.d("FILEPDF", String.valueOf(changeExtension(new File(file.getPath()),"pdf")));
//
//
//        final Uri uri;
//        uri = file;
//
//        Log.d("PATHIS",uri.toString());
////        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://cloudconvert.com/anything-to-pdf")));
////        ConvertToPDF(file.toString(),file.getLastPathSegment()+"/"+uniqueID);
//
//        final KProgressHUD hud = KProgressHUD.create(Select.this)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Please wait")
//                .setMaxProgress(100);
////                .show();
//
//
//        Sprite chasingDots = new ChasingDots();
//        progressBar.setVisibility(View.VISIBLE);
//        progressBar.setIndeterminateDrawable(chasingDots);
//
//
//        //Checking for internet connection
//        Boolean isNetwork = isNetworkAvailable();
//        if (!isNetwork) {
//            showErrorDialog("No internet connection detected");
//            hud.dismiss();
//        }
//        final UploadTask uploadTask = filesRef.putFile(file);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//                Log.d("UPLOAD", "Not successfull");
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                Log.d("UPLOAD", "SUCCESSFULL");
//                Log.d("UNIQUE",uniqueID);
//                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if (!task.isSuccessful()) {
//                            throw task.getException();
//                        }
//
//                        // Continue with the task to get the download URL
//                        return filesRef.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()) {
//                            String url;
//                            Uri downloadUri = task.getResult();
//                            url = String.valueOf(downloadUri);
//                            hud.dismiss();
//
//                            Intent goToPdfInfo = new Intent(Select.this, PdfInfo.class);
////                                            finish();
//                            //goToPageInfo.putExtra("Pages",images);
//                            Bundle extras = new Bundle();
//                            extras.putString("PdfURL", url);
//                            extras.putString("URI", String.valueOf(uri));
//                            extras.putString("FileType", fileType);
//
//                            goToPdfInfo.putExtras(extras);
//
//                            progressBar.setVisibility(View.INVISIBLE);
//
//                            startActivity(goToPdfInfo);
//                            finish();
//
//                        } else {
//                            // Handle failures
//                            // ...
//                        }
//                    }
//                });
//                // ...
//            }
//        });
//    }



    public void ConvertToPDF(String docPath, String pdfPath) {
        final String uniqueID = UUID.randomUUID().toString();

//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String path = Environment.getExternalStorageDirectory().toString();
        File folder = new File(path, uniqueID);

//        File file = new File(folder, uniqueID);
        folder.mkdir();
        File pdfFile = new File(folder, "File");

        try {
            pdfFile.createNewFile();
            InputStream doc = new FileInputStream(new File(docPath));
            XWPFDocument document = new XWPFDocument(doc);
            PdfOptions options = PdfOptions.create();
//            OutputStream out = new FileOutputStream(new File(String.valueOf(path)));
            OutputStream out = new FileOutputStream(pdfFile);
            PdfConverter.getInstance().convert(document, out, options);
            Log.d("DoneConvert", String.valueOf(out));
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            Log.d("FILENOTFOUND", String.valueOf(ex));
        } catch (IOException ex) {
            Log.d("IOEXCEPTION",ex.getMessage());
        }
    }




    private void uploadImg(int requestCode, int resultCode, Intent data, final ArrayList<Uri> uri) {
//        Uri returnUri = data.getData();
//        uri.add(returnUri);
//        final String uniqueID = UUID.randomUUID().toString();
//        final StorageReference filesRef = storageRef.child(uniqueID);

        final KProgressHUD hud = KProgressHUD.create(Select.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setMaxProgress(100);
//                .show();

        Sprite chasingDots = new ChasingDots();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminateDrawable(chasingDots);

        //Checking for internet connection
        Boolean isNetwork = isNetworkAvailable();
        if (!isNetwork) {
            showErrorDialog("No internet connection detected");
            hud.dismiss();
        }

        final int[] uploadCnt = {0};

        if (requestCode == PICK_PDF_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (uri.size() > 0) {
                    if (data.getClipData()!=null ) {

                        int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                        for (int i = 0; i < count; i++) {

                            final String uniqueID = UUID.randomUUID().toString();
                            final StorageReference filesRef = storageRef.child(uniqueID);

                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            uri.add(imageUri);

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
                                    Log.d("UNIQUE",uniqueID);

                                    uploadCnt[0]++;
                                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                            if(uploadCnt[0] == pageURL.size()) {
                                                Intent goToPageInfo = new Intent(Select.this, PageInfo.class);
                                                Bundle extras = new Bundle();
                                                extras.putStringArrayList("URLS", pageURL);
                                                extras.putString("FileType", fileType);
//                                                Log.d("USERNAME", username);
//                                                extras.putString("username", username);
//                                                extras.putString("email", email);
//                                                extras.putLong("num", (num));

                                                //extras.putParcelableArrayList("Images",images);
                                                extras.putStringArray("URI", new String[]{String.valueOf(uri)});
                                                goToPageInfo.putExtras(extras);

                                                progressBar.setVisibility(View.INVISIBLE);

                                                startActivity(goToPageInfo);

                                            }

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



                } else {

//                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                        String uniqueID = UUID.randomUUID().toString();
                        final StorageReference filesRef = storageRef.child(uniqueID);

                    for (int i = 0; i < uri.size(); i++) {
                        Uri imageUri = uri.get(i);

//                        ClipData.Item data1 = data.getClipData().getItemAt(i);

//                        uri.add(imageUri);

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
                                        uploadCnt[0]++;
                                        return filesRef.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();

                                            pageURL.add(String.valueOf(downloadUri));
                                            hud.dismiss();
                                            if(uploadCnt[0] == pageURL.size()){
                                                Intent goToPageInfo = new Intent(Select.this, PageInfo.class);
                                                Bundle extras = new Bundle();
                                                extras.putStringArrayList("URLS", pageURL);
                                                extras.putString("FileType", fileType);
//                                                Log.d("USERNAME",username);
//                                                extras.putString("username",username);
//                                                extras.putString("email",email);
//                                                extras.putLong("num", (num));

                                                goToPageInfo.putExtras(extras);
                                                startActivity(goToPageInfo);
                                            }
                                        } else {
                                            // Handle failures
                                            Log.d("IMAGE", "NOT RECIEVED");
                                            // ...
                                        }
                                    }
                                });
                                // ...
                            }
                        });
                    }

                }

              }
            }
        } else if (data.getData() != null) {
            String imagePath = data.getData().getPath();
            Log.d("IMAGE PATH ", String.valueOf(imagePath));
            Log.d("IMAGE is ", String.valueOf(data.getData()));
            //do something with the image (save it to some directory or whatever you need to do with it here)
            hud.dismiss();
        }
    }


//    public void uploadImg(int requestCode, int resultCode, Intent data, final ArrayList<Uri> uri){
//        Intent goToPageInfo = new Intent(Select.this, PageInfo.class);
//        Bundle extras = new Bundle();
//        extras.putString("FileType", fileType);
//        Log.d("URISIZE", String.valueOf(uri.size()));
////        extras.putString("URLS", String.valueOf(uri));
//        extras.putParcelableArrayList("URLS",uri);
//        goToPageInfo.putExtras(extras);
//        startActivity(goToPageInfo);
//
//    }




//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//
//        switch (requestCode) {
//            case 7:
//                if (resultCode == RESULT_OK) {
//                    String PathHolder = data.getData().getPath();
//                    Toast.makeText(select.this, PathHolder, Toast.LENGTH_LONG).show();
//                }
//                break;
//        }
//    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        final KProgressHUD hud = KProgressHUD.create(select.this)
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
//                                            Intent goToPageInfo = new Intent(select.this,PageInfo.class);
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
////                                        Intent goToPageInfo = new Intent(select.this,PageInfo.class);
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



    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
