package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paytm.pgsdk.PaytmPGService;
//import com.spire.presentation.Presentation;
//import com.spire.presentation.FileFormat;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Select extends AppCompatActivity {

    NotificationManagerCompat notificationManager;
    String CHANNEL_ID = "UsersChannel";
    ProgressDialog mProgressDialog;

    ArrayList<String> pageURL = new ArrayList<String>();
    ArrayList<Bitmap> images = new ArrayList<Bitmap>();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    String username,email;
    long num;

    Button selectFilesBtn;
    ImageButton help, orders,setting;
    View menu;

    int PICK_IMAGE_MULTIPLE = 1;
    final static int PICK_PDF_CODE = 2342;
    final static int PICK_IMAGE_CODE = 100;
    final long[] orderCnt = new long[1];

    String imageEncoded;
    List<String> imagesEncodedList;
    private boolean isMenuShown = false;
    ProgressBar progressBar;
    long cnt;
    boolean network;
    PaytmPGService Service = PaytmPGService.getProductionService();

//    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_files);

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 1);
//            Log.d("MANAGEPERMISSION", "PERMISSION");
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
//            Log.d("MANAGEPERMISSION", "PERMISSION");
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            Log.d("MANAGEPERMISSION", "PERMISSION");
//        }

        notificationManager = NotificationManagerCompat.from(this);
//        setProgressForOrder();
        createNotification();

        network = haveNetworkConnection();
        Log.d("NETWORK", String.valueOf(network));
        selectFilesBtn =(Button) findViewById(R.id.AddFilesButton);
        setting = findViewById(R.id.settings);
        orders = findViewById(R.id.YourOrders);
//        help = findViewById(R.id.help);

//        new getCurrentOrderCnt().execute();
//        selectFilesBtn.setOnClickListener(fileBtnListener);

        getOrders();
        selectFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SELECTing", String.valueOf(true));
//                selectFiles();
                selectFiles();

            }
        });

        orders.setOnClickListener(Listener);
//        orders.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("GETTING", "ORDERS");
////                new getCurrentOrderCnt().execute();
//            }
//
//        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Select.this,settings.class);
                startActivity(intent);
            }
        });

//        help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("HELP","PRESSED");
//                Intent intent = new Intent(Select.this,HelpActivity.class);
//                startActivity(intent);
//
//            }
//        });

    }

//     Create an anonymous implementation of OnClickListener
    private View.OnClickListener Listener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked
             Log.d("GETTING", "ORDERS");

            mProgressDialog = new ProgressDialog(Select.this);
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
            if (cnt == 0) {
                toast.show();
                mProgressDialog.dismiss();
            } else {

                Intent intent = new Intent(Select.this, YourOrders.class);
                Bundle extras = new Bundle();
                extras.putLong("Orders Count", cnt);
                intent.putExtras(extras);
                startActivity(intent);
                mProgressDialog.dismiss();
                finish();

            }

        }
    };


//    long cnt;
    public void getOrders(){
        final ArrayList<String> orderkey = new ArrayList<>();
        final ArrayList<String> shopKey = new ArrayList<>();

        ref.child("users").child(userId).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                            shopKey.add(dataSnapshot.getKey());


                if(dataSnapshot.getKey().equals("Orders")) {
                    Log.d("ORDERCOUNT ", String.valueOf(dataSnapshot.getChildrenCount()));
                    cnt = dataSnapshot.getChildrenCount();
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

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    //this function will get the pdf from the storage
    private void selectFiles() {

        //creating an intent for file chooser

//        finish();

        Intent pop = new Intent(Select.this,Pop.class);
        startActivity(pop);
//        finish();

//        Intent get = getIntent();
//        Boolean selectPhotos = get.getBooleanExtra("Photos",false);
//        Log.d("TYPEOFFILE", String.valueOf(selectPhotos));

//        if(selectPhotos){
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            startActivityForResult(Intent.createChooser(intent, "Select Images"), 1);
//
//        }else{
//            Intent intent = new Intent();
//            intent.setType("application/pdf");
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            startActivityForResult(Intent.createChooser(intent, "Select files"), 1);
//        }

//        Intent intent = new Intent();
//        intent.setType("application/pdf");
//        intent.setType("*/*");
//        intent.setType("image/*");
        // For error "Permission to manage documents"
//        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//         startActivityForResult(Intent.createChooser(intent, "Select files"), 1);
//        startActivityForResult(Intent.createChooser(intent, "select Picture"), PICK_PDF_CODE);

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




//    private void uploadImg(int requestCode, int resultCode, Intent data, final ArrayList<Uri> uri) {
//
//        final KProgressHUD hud = KProgressHUD.create(Select.this)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Please wait")
//                .setMaxProgress(100);
////                .show();
//
////        Sprite chasingDots = new ChasingDots();
////        progressBar.setVisibility(View.VISIBLE);
////        progressBar.setIndeterminateDrawable(chasingDots);
//
//        //Checking for internet connection
//        Boolean isNetwork = isNetworkAvailable();
//        if (!isNetwork) {
//            showErrorDialog("No internet connection detected");
//            hud.dismiss();
//        }
//
//        final int[] uploadCnt = {0};
//
//        if (requestCode == PICK_PDF_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (uri.size() > 0) {
//                    if (data.getClipData()!=null ) {
//
//                        int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
//                        for (int i = 0; i < count; i++) {
//
//                            final String uniqueID = UUID.randomUUID().toString();
//                            final StorageReference filesRef = storageRef.child(uniqueID);
//
//                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                            uri.add(imageUri);
//
//                            //do something with the image (save it to some directory or whatever you need to do with it here)
//                            Bitmap bitmap = null;
//                            try {
//                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                            images.add(bitmap);
//                            byte[] DATA = baos.toByteArray();
//                            final UploadTask uploadTask = filesRef.putBytes(DATA);
//
//
//                            uploadTask.addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception exception) {
//                                    // Handle unsuccessful uploads
//                                    Log.d("UPLOAD", "Not successfull");
//                                }
//                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                                    Log.d("UPLOAD", "SUCCESSFULL");
//                                    Log.d("UNIQUE",uniqueID);
//
//                                    uploadCnt[0]++;
//                                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                                        @Override
//                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                            if (!task.isSuccessful()) {
//                                                throw task.getException();
//                                            }
//
//                                            // Continue with the task to get the download URL
//                                            return filesRef.getDownloadUrl();
//                                        }
//                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//
//                                        @Override
//                                        public void onComplete(@NonNull Task<Uri> task) {
//                                            if (task.isSuccessful()) {
//                                                Uri downloadUri = task.getResult();
//                                                pageURL.add(String.valueOf(downloadUri));
//
//
//                                                Log.d("Pages ", String.valueOf(images));
//                                                Log.d("URLS ", String.valueOf(pageURL));
//                                                hud.dismiss();
//                                            if(uploadCnt[0] == pageURL.size()) {
//                                                Intent goToPageInfo = new Intent(Select.this, PageInfo.class);
//                                                Bundle extras = new Bundle();
//                                                extras.putStringArrayList("URLS", pageURL);
//                                                extras.putString("FileType", fileType);
////                                                Log.d("USERNAME", username);
////                                                extras.putString("username", username);
////                                                extras.putString("email", email);
////                                                extras.putLong("num", (num));
//
//                                                //extras.putParcelableArrayList("Images",images);
//                                                extras.putStringArray("URI", new String[]{String.valueOf(uri)});
//                                                goToPageInfo.putExtras(extras);
//
////                                                progressBar.setVisibility(View.INVISIBLE);
//
//                                                startActivity(goToPageInfo);
//
//                                            }
//
//                                            } else {
//                                                // Handle failures
//                                                // ...
//                                            }
//                                        }
//                                    });
//                                    // ...
//                                }
//                            });
//                        }
//
//
//
//                } else {
//
////                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
//                        String uniqueID = UUID.randomUUID().toString();
//                        final StorageReference filesRef = storageRef.child(uniqueID);
//
//                    for (int i = 0; i < uri.size(); i++) {
//                        Uri imageUri = uri.get(i);
//
////                        ClipData.Item data1 = data.getClipData().getItemAt(i);
//
////                        uri.add(imageUri);
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
//                                        uploadCnt[0]++;
//                                        return filesRef.getDownloadUrl();
//                                    }
//                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//
//                                    @Override
//                                    public void onComplete(@NonNull Task<Uri> task) {
//                                        if (task.isSuccessful()) {
//                                            Uri downloadUri = task.getResult();
//
//                                            pageURL.add(String.valueOf(downloadUri));
//                                            hud.dismiss();
//                                            if(uploadCnt[0] == pageURL.size()){
//                                                Intent goToPageInfo = new Intent(Select.this, PageInfo.class);
//                                                Bundle extras = new Bundle();
//                                                extras.putStringArrayList("URLS", pageURL);
//                                                extras.putString("FileType", fileType);
////                                                Log.d("USERNAME",username);
////                                                extras.putString("username",username);
////                                                extras.putString("email",email);
////                                                extras.putLong("num", (num));
//
//                                                goToPageInfo.putExtras(extras);
//                                                startActivity(goToPageInfo);
//                                            }
//                                        } else {
//                                            // Handle failures
//                                            Log.d("IMAGE", "NOT RECIEVED");
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
//
//              }
//            }
//        } else if (data.getData() != null) {
//            String imagePath = data.getData().getPath();
//            Log.d("IMAGE PATH ", String.valueOf(imagePath));
//            Log.d("IMAGE is ", String.valueOf(data.getData()));
//            //do something with the image (save it to some directory or whatever you need to do with it here)
//            hud.dismiss();
//        }
//    }








    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    DatabaseReference orderDb = FirebaseDatabase.getInstance().getReference();
    OrderStatus obj = new OrderStatus();

    ArrayList<String> orderKey = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setProgressForOrder() {


        orderDb.child("users").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                for(DataSnapshot order_Key: dataSnapshot.getChildren()){
                    orderKey.add(order_Key.getKey());
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (orderKey.size() != 0) {
                            new createNotification().execute(orderKey.size());
                        }
                    }
                },300);
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


    //    ArrayList<String> orderKey, final int cnt
    String orderStatus = null;
    private void createNotification() {
        // Create an Intent for the activity you want to start
        final Intent resultIntent = new Intent(Select.this, YourOrders.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(Select.this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        final Bundle extras = new Bundle();
        resultIntent.putExtras(extras);
//            extras.putInt("Orders Count", cnt);

        for (int i = 0; i < orderKey.size(); i++) {
            final String key = orderKey.get(i);

            final int finalI = i;
            orderDb.child("users").child(userId).child("Orders").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    NotificationPresented presented = new NotificationPresented(true);

                    Iterator<DataSnapshot> users = dataSnapshot.getChildren().iterator();

                    Log.d("ORDERLOG",dataSnapshot.getKey());
                    for (final DataSnapshot shop : dataSnapshot.getChildren()) {
                        for (final DataSnapshot order : shop.getChildren()) {
                            for (final DataSnapshot user : order.getChildren()) {
//                            while (users.hasNext()) {

                                String status = null;

                                boolean notify = false;
//                                DataSnapshot user = users.next();
                                if (user.getKey().equals("ShopLat")) {
                                    extras.putDouble("ShopLat", Double.parseDouble(user.getValue().toString()));
                                }
                                if (user.getKey().equals("ShopLong")) {
                                    extras.putDouble("ShopLong", Double.parseDouble(user.getValue().toString()));
                                }
                                if (user.getKey().equals("ShopsLocation")) {
                                    extras.putString("Location", (String) user.getValue());
                                }
                                if (user.getKey().equals("ShopName")) {
                                    extras.putString("ShopName", (String) user.getValue());
                                }
                                if (user.getKey().equals("files")) {
                                    extras.putInt("Files", Integer.parseInt(String.valueOf(user.getValue())));
                                }

                                //if(user.getKey().equals("presented") && user.getValue().equals(true)) {

                                final HashMap<String, Object> notified = new HashMap<String, Object>();

                                if (user.getKey().equals("orderStatus")) {
//                                    Log.d("STATUS", user.getValue().toString());
                                    orderStatus = user.getValue().toString();
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        if (orderStatus != null) {


                                            final String finalStatus = orderStatus;

                                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.appicon);

                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Select.this, CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.notify)
                                                    .setLargeIcon(icon)
                                                    .setContentTitle("Order Status")
                                                    .setContentText("Order ID: " + order.getKey() + " " + finalStatus)
                                                    .setGroup(CHANNEL_ID)
                                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Order ID: " + order.getKey() + " " + finalStatus))
                                                    .setContentIntent(resultPendingIntent)
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH);

                                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                            if (orderStatus.equals("Retrieved")) {
//                                        Log.d("ORDERSTAT!",orderStatus);
//                                        if (user.getKey().equals("RT_Notified") && user.getValue().toString().equals(false)) {
                                                Log.d("ORDERSTAT", orderStatus);

//                                            status = "Retrieved";
//                                            Log.d("SHOPID",shop.getKey());
//                                            Log.d("ORDER",order.getKey());

                                                notified.put("RT_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
//                                           Present notification
//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                                                notificationManager.notify(0, builder.build());

                                                Log.d("Progress", String.valueOf(obj.progress));
//                                        }
                                            } else if (orderStatus.equals("In Progress")) {
//                                        Log.d("ORDERSTAT!",orderStatus);

                                                if (user.getKey().equals("IP_Notified") && user.getValue().toString().equals(false)) {
                                                    Log.d("ORDERSTAT", orderStatus);

//                                            status = "In Progress";
//                                            Log.d("SHOPID",shop.getKey());
//                                            Log.d("ORDER",order.getKey());

                                                    notified.put("IP_Notified", true);
                                                    orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
//                                           Present notification
//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                                                    notificationManager.notify(0, builder.build());

                                                    Log.d("Progress", String.valueOf(obj.progress));
                                                }
                                            } else if (orderStatus.equals("Ready")) {
//                                        Log.d("ORDERSTAT!",orderStatus);

                                                if (user.getKey().equals("R_Notified") && user.getValue().toString().equals(false)) {
                                                    Log.d("ORDERSTAT", orderStatus);

//                                            Log.d("SHOPID",shop.getKey());
//                                            Log.d("ORDER",order.getKey());

                                                    notified.put("R_Notified", true);
                                                    orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
//                                           Present notification
//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//                                            status = "Ready";

                                                    notificationManager.notify(0, builder.build());


                                                }
                                            }


//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//                                            notificationManager.notify(0, builder.build());
                                            extras.putString("OrderStatus", finalStatus);


                                        }
//                              }

                                    }
                                }, 300);

                                //}


                            }

                        }
                    }
                }

//                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private class createNotification extends AsyncTask<Integer,Void,Void> {


        @Override
        protected Void doInBackground(Integer... cnt) {
            // Create an Intent for the activity you want to start
            final Intent resultIntent = new Intent(Select.this, YourOrders.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(Select.this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

            final Bundle extras = new Bundle();
            resultIntent.putExtras(extras);
//            extras.putInt("Orders Count", cnt);

            for (int i = 0; i < orderKey.size(); i++) {
                final String key = orderKey.get(i);

                final int finalI = i;
                orderDb.child("users").child(userId).child("Orders").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        NotificationPresented presented = new NotificationPresented(true);

                        Iterator<DataSnapshot> users = dataSnapshot.getChildren().iterator();


                        for (final DataSnapshot shop : dataSnapshot.getChildren()) {
                            for (final DataSnapshot order : shop.getChildren()) {
                                for (final DataSnapshot user : order.getChildren()) {
//                            while (users.hasNext()) {

                                    String status = null;

                                    boolean notify = false;
//                                DataSnapshot user = users.next();
                                    if (user.getKey().equals("ShopLat")) {
                                        extras.putDouble("ShopLat", Double.parseDouble(user.getValue().toString()));
                                    }
                                    if (user.getKey().equals("ShopLong")) {
                                        extras.putDouble("ShopLong", Double.parseDouble(user.getValue().toString()));
                                    }
                                    if (user.getKey().equals("ShopsLocation")) {
                                        extras.putString("Location", (String) user.getValue());
                                    }
                                    if (user.getKey().equals("ShopName")) {
                                        extras.putString("ShopName", (String) user.getValue());
                                    }
                                    if (user.getKey().equals("files")) {
                                        extras.putInt("Files", Integer.parseInt(String.valueOf(user.getValue())));
                                    }

                                    //if(user.getKey().equals("presented") && user.getValue().equals(true)) {

                                    final HashMap<String, Object> notified = new HashMap<String, Object>();

                                    if (user.getKey().equals("orderStatus")) {
//                                    Log.d("STATUS", user.getValue().toString());
                                        orderStatus = user.getValue().toString();
                                    }

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {


                                            if (orderStatus != null) {


                                                final String finalStatus = orderStatus;

                                                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.appicon);

                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(Select.this, CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.notify)
                                                        .setLargeIcon(icon)
                                                        .setContentTitle("Order Status")
                                                        .setContentText("Order ID: " + order.getKey() + " " + finalStatus)
                                                        .setGroup(CHANNEL_ID)
                                                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Order ID: " + order.getKey() + " " + finalStatus))
                                                        .setContentIntent(resultPendingIntent)
                                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                                if (orderStatus.equals("Retrieved")) {
//                                        Log.d("ORDERSTAT!",orderStatus);
//                                        if (user.getKey().equals("RT_Notified") && user.getValue().toString().equals(false)) {
                                                    Log.d("ORDERSTAT", orderStatus);

//                                            status = "Retrieved";
//                                            Log.d("SHOPID",shop.getKey());
//                                            Log.d("ORDER",order.getKey());

                                                    notified.put("RT_Notified", true);
                                                    orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
//                                           Present notification
//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                                                    notificationManager.notify(0, builder.build());

                                                    Log.d("Progress", String.valueOf(obj.progress));
//                                        }
                                                } else if (orderStatus.equals("In Progress")) {
//                                        Log.d("ORDERSTAT!",orderStatus);

                                                    if (user.getKey().equals("IP_Notified") && user.getValue().toString().equals(false)) {
                                                        Log.d("ORDERSTAT", orderStatus);

//                                            status = "In Progress";
//                                            Log.d("SHOPID",shop.getKey());
//                                            Log.d("ORDER",order.getKey());

                                                        notified.put("IP_Notified", true);
                                                        orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
//                                           Present notification
//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
                                                        notificationManager.notify(0, builder.build());

                                                        Log.d("Progress", String.valueOf(obj.progress));
                                                    }
                                                } else if (orderStatus.equals("Ready")) {
//                                        Log.d("ORDERSTAT!",orderStatus);

                                                    if (user.getKey().equals("R_Notified") && user.getValue().toString().equals(false)) {
                                                        Log.d("ORDERSTAT", orderStatus);

//                                            Log.d("SHOPID",shop.getKey());
//                                            Log.d("ORDER",order.getKey());

                                                        notified.put("R_Notified", true);
                                                        orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
//                                           Present notification
//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//                                            status = "Ready";

                                                        notificationManager.notify(0, builder.build());


                                                    }
                                                }


//                                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//                                            notificationManager.notify(0, builder.build());
                                                extras.putString("OrderStatus", finalStatus);


                                            }
//                              }

                                        }
                                    }, 300);

                                    //                     }

                                }

                            }
                        }
                    }

//                }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            return null;
        }
    }
}

