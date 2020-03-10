package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.app.AlarmManager.ELAPSED_REALTIME;
import static android.os.SystemClock.elapsedRealtime;

public class Select extends AppCompatActivity {
    public String SharedPrefs = "Data";
    NotificationManagerCompat notificationManager;
    String CHANNEL_ID = "Sleefax";
    ProgressDialog mProgressDialog;

    ArrayList<String> pageURL = new ArrayList<String>();
    ArrayList<Bitmap> images = new ArrayList<Bitmap>();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId;
    SwipeRefreshLayout pullToRefresh;

    int shortAnimationDuration;

    Button selectFilesBtn,orders;
    ImageButton more ,setting,sideMenu;
    View pickedUpOrderView;

    int PICK_IMAGE_MULTIPLE = 1;
    final static int PICK_PDF_CODE = 2342;
    final static int PICK_IMAGE_CODE = 100;
    int orderCnt = 0;

    String imageEncoded;
    List<String> imagesEncodedList;
    private boolean isMenuShown = false;
    int cnt;
    boolean network,isTester,newUser;
    PaytmPGService Service = PaytmPGService.getProductionService();

    RelativeLayout addfileRl2;
//    ConstraintLayout addfileCl;

    /////////// Variables for current order status layout/////
    RelativeLayout currentOrderLayout;
    TextView currentOrderStatus,currentOrderDateTime,currentOrderPrice,currentOrderID,currentOrderShopName, currentOrderShopLoc,orderPickBtn;


    //////////Variables for naviagtion drawer / menu ///////////////
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;



    //////// Live orders Listview /////////
    ListView listView;
    currentOrderAdapter adapter;

    //    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_files);




        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        selectFilesBtn =(Button) findViewById(R.id.AddFilesButton);
        setting = findViewById(R.id.settings);
        orders = findViewById(R.id.YourOrders);
        sideMenu = findViewById(R.id.SideMenu);
        addfileRl2 = findViewById(R.id.addfileRL2);


        notificationManager = NotificationManagerCompat.from(this);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            Log.d("NOTIFICATION ", String.valueOf(PackageManager.PERMISSION_DENIED));
        }

        /////Getting new User who has no account./////
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            newUser = extras.getBoolean("NewUser");
        }



        if(newUser){
            orders.setVisibility(View.INVISIBLE);
        }else{
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                ////Setting up navigation and drawer layour viewssss/////
                setUpNavigationViews();

                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                getOrders();

                ///////Order Cnt for order history///////
                getOrderCntForOrderHistory();


                /////// Order History button listener ///////
                orders.setOnClickListener(Listener);
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("tester") || FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("Tester")) {
                    isTester = true;
                } else {
                    isTester = false;
                }
                createNotificationChannel();
                //startService(new Intent(Select.this, notificationService.class));
                new createNotification().execute();

            }
        }
        Log.d("NEWUSER",String.valueOf(newUser));




        network = haveNetworkConnection();
        if(!network){
            Toast.makeText(Select.this,"Please check your internet connection.",Toast.LENGTH_LONG).show();
        }


//        setProgressForOrder();
        selectFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SELECTing", String.valueOf(true));
//                selectFiles();
                selectFiles();

            }
        });


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Select.this,settings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.RefreshLayout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("REFRESHING", "onRefresh called from SwipeRefreshLayout");

                currentOrderLV();
                pullToRefresh.setRefreshing(false);

            }
        });

        //////setting size of add file view to full page if cnt == 0  ////////
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setupLayoutParams();

            }
        },1000);


    }

    public void setupLayoutParams(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;

        View addFileView = findViewById(R.id.addFilesView);
        final ViewGroup.LayoutParams params = addFileView.getLayoutParams();
        final RelativeLayout.LayoutParams addfileParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        addFileView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        int liveOrderCnt = sharedPreferences.getInt("LiveOrderCnt",0);
        int orderHistoryCnt = sharedPreferences.getInt("OrderHistoryCnt",0);
        Log.d("liveOrderCnt",String.valueOf(liveOrderCnt));
        Log.d("orderHistoryCnt",String.valueOf(orderHistoryCnt));


        if(listView == null) {

            addFileView.setAlpha(0f);
            addFileView.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration + 500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            params.height = height;
                        }
                    });

            addfileRl2.setAlpha(0f);
            addfileRl2.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration+500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            addfileParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                            addfileRl2.setLayoutParams(addfileParams);
                        }
                    });
        }

        if(orderHistoryCnt == 0 ){


            orders.animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration + 500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            orders.setVisibility(View.GONE);

                        }
                    });
        }
    }


    public void setUpNavigationViews(){



        //////////////////////NAVIGATION VIEW/////////////////////////

        navigationView = findViewById(R.id.NavView);
        toolbar = findViewById(R.id.navaction);
        drawerLayout = findViewById(R.id.drawerLayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView username,useremail;
        View header = navigationView.getHeaderView(0);
        useremail =  header.findViewById(R.id.useremail);
        username = header.findViewById(R.id.username);


        username.setText(getDisplayNameSavedLocally());
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            useremail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
//        actionBarDrawerToggle = new ActionBarDrawerToggle(Select.this,drawerLayout,R.string.open,R.string.close);

//        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
//        actionBarDrawerToggle.setHomeAsUpIndicator(0);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.openmenu,getTheme());
        actionBarDrawerToggle.setHomeAsUpIndicator(drawable);
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(false);
                int menu_id = menuItem.getItemId();
                Log.d("MENUID", String.valueOf(menuItem.getItemId()));
                drawerLayout.closeDrawers();

                switch (menu_id){
                    case R.id.userprofile:

                        Intent intent = new Intent(Select.this,changeInfoPopUp.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

                    case R.id.logout:

                        FirebaseAuth.getInstance().signOut();
                        Intent signOutIntent = new Intent(Select.this, MainActivity.class);
                        signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                        startActivity(signOutIntent);
                        Toast.makeText(Select.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                        finish();
                        break;

                    case R.id.issue:

                        Intent issueIntent = new Intent(Select.this,ReportIssue.class);
                        startActivity(issueIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

                    case R.id.contact:


                        break;

                    case R.id.feedback:
                        Intent feedbackIntent = new Intent(Select.this,Feedback.class);
                        startActivity(feedbackIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                }

                return true;
            }
        });



    }

    public String getDisplayNameSavedLocally(){

        String name;
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        name = sharedPreferences.getString("DisplayName",null);
        return name;
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }

    public void currentOrderLV(){
        listView = findViewById(R.id.currentOrderLV);
        adapter = new Select.currentOrderAdapter();
        listView.setAdapter(adapter);

    }


    //     Create an anonymous implementation of OnClickListener
    private View.OnClickListener Listener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onClick(View v) {
            // do something when the button is clicked
            Log.d("GETTING", "ORDERS");

            if (!network) {
                Toast.makeText(Select.this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
            } else {
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

                Intent orderHistoryIntent = new Intent(Select.this, YourOrders.class);
                startActivity(orderHistoryIntent);
                finish();
                mProgressDialog.dismiss();

//                if (orderCnt == 0) {
//                    toast.show();
//                    mProgressDialog.dismiss();
//                } else {
//
//                    Intent intent = new Intent(Select.this, YourOrders.class);
//                    Bundle extras = new Bundle();
//                    extras.putInt("Orders Count", orderCnt);
//                    intent.putExtras(extras);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//
////                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(Select.this).toBundle());
//                    mProgressDialog.dismiss();
//                    finish();
//
//                }

            }
        }
    };





//    long cnt;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getOrders(){

        final ArrayList<String> orderkey = new ArrayList<>();
        final ArrayList<String> shopKey = new ArrayList<>();

//        setProgressForOrder();

        ref.child("users").child(userId).child("Orders").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                shopKey.add(dataSnapshot.getKey());

                for(DataSnapshot orderIDS: dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>) orderIDS.getValue();
                    if(!String.valueOf(map.get("orderStatus")).equals("Done")){
//                        cnt = (int) (dataSnapshot.getChildrenCount()+cnt);
                        cnt = cnt + 1;
                        Log.d("ORDERDONE",String.valueOf(cnt));

                        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
                        sharedPreferences.edit().putInt("LiveOrderCnt",cnt).apply();

                        currentOrderLV();
                    }


                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                setupLayoutParams();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getOrderCntForOrderHistory(){

        final ArrayList<String> orderkey = new ArrayList<>();
        final ArrayList<String> shopKey = new ArrayList<>();

        ref.child("users").child(userId).child("Orders").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                shopKey.add(dataSnapshot.getKey());
                orderCnt = (int) (dataSnapshot.getChildrenCount()+orderCnt);

                SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
                sharedPreferences.edit().putInt("OrderHistoryCnt",orderCnt).apply();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                orderCnt = orderCnt-1;
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
                sharedPreferences.edit().putInt("OrderHistoryCnt",orderCnt).apply();
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
        Intent pop = new Intent(Select.this,Pop.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsTester",isTester);
        bundle.putBoolean("NewUser",newUser);
        pop.putExtras(bundle);
        startActivity(pop);

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


    ArrayList<Uri> uri = new ArrayList<Uri>();
    String fileType;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        uri.clear();
//        super.onActivityResult(requestCode, resultCode, data);
//        //when the user choses the file
////        Log.d("FILE CHOSEN",data.getData().toString());
////        Log.d("REQUESTCODE", String.valueOf(requestCode));
//        Log.d("ACTIVI","RES");
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            //if a file is selected
//
//
//            if (data.getData() != null) {
//                //uploading the file
//
//                Uri returnUri = data.getData();
////                uri.add(returnUri);
//
//                Log.d("URIID", String.valueOf(returnUri));
//                String mimeType = getContentResolver().getType(returnUri);
//                Log.d("MIME", mimeType);
//
//
//                if (mimeType.contains("application")) {
//
//
//                    Log.d("FILE", "Image");
//                    fileType = mimeType;
////                    Log.d("URRISIZE", String.valueOf(uri.size()));
////                    uploadImg(requestCode, resultCode, data, uri);
//
////                    Log.d("IMAGESARE", String.valueOf(data.getClipData().getItemCount()));
//                    if(data.getClipData() != null) {
//                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
//                            if (data.getClipData().getItemAt(i).getUri() != null) {
//                                uri.add(data.getClipData().getItemAt(i).getUri());
//                                if (i == data.getClipData().getItemCount() - 1) {
//                                    uploadImg(requestCode, resultCode, data, uri);
//                                }
//                            }
//                        }
////                        uri.add(returnUri);
////                        uploadImg(requestCode, resultCode, data, uri);
//
//
//                    }else{
//                        uri.add(returnUri);
//                        uploadImg(requestCode,resultCode,data,uri);
//                    }
//                }
//            } else {
//                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
//                Log.d("DATA", String.valueOf(data.getClipData()));
//            }
//        }
//    }

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
//
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
////                                                Log.d("Pages ", String.valueOf(images));
////                                                Log.d("URLS ", String.valueOf(pageURL));
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


    DatabaseReference orderDb = FirebaseDatabase.getInstance().getReference();
    ArrayList<String> orderKeys = new ArrayList<>();



    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "Order Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }


    ////////Declaring variables for more details of orders/////////
//    String shopKey,orderKey,shopName,loc,orderStatus;
//    int files;
//    Double shopLat,shopLong,price;
//    boolean fromYourOrders = false;

    int notifyCnt = 0;
    public class notificationService extends IntentService{

        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         * @param name Used to name the worker thread, important only for debugging.
         */

        public notificationService() {
            super("notificationService");
        }

//        @Override
        public int onStartCommand(final Intent intent,
                                  final int flags,
                                  final int startId) {

            //your code
//            startService(new Intent(this, notificationService.class));
            return START_STICKY;
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            new createNotification().execute();
        }


//        @Override
//        public void onTaskRemoved(Intent rootIntent){
//            Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
//            restartServiceTask.setPackage(getPackageName());
//            PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
//            AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            myAlarmService.set(
//                    AlarmManager.ELAPSED_REALTIME,
//                    SystemClock.elapsedRealtime() + 1000,
//                    restartPendingIntent);
//
//            super.onTaskRemoved(rootIntent);
//        }

    }


    private class createNotification extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Create an Intent for the activity you want to start
            final Intent resultIntent = new Intent(Select.this, Select.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(Select.this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//          final PendingIntent resultPendingIntent = PendingIntent.getBroadcast(Select.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            orderDb.child("users").child(userId).child("Orders").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //                      Log.d("ORDERLOG",dataSnapshot.getKey());
                    for (final DataSnapshot shop : dataSnapshot.getChildren()) {

                        for (final DataSnapshot order : shop.getChildren()) {
//                            Log.d("ORDERID",order.getKey());

                            String orderStatus;
                            Double price;
                            String orderDateTime;

                            boolean RTnotifyStatus = true;
                            boolean IPnotifyStatus = true;
                            boolean RnotifyStatus = true,doneNotifyStatus = true;

                            Map<String, Object> map = (Map<String, Object>) order.getValue();

//                            orderDateTime = String.valueOf(map.get("orderDateTime"));
                            orderStatus = String.valueOf(map.get("orderStatus"));
//                            price = Double.parseDouble(String.valueOf(map.get("price")));

//                            shopName = String.valueOf(map.get("ShopName"));
//                            shopLat = Double.parseDouble(String.valueOf(map.get("ShopLat")));
//                            shopLong = Double.parseDouble(String.valueOf(map.get("ShopLong")));
//                            loc = String.valueOf(map.get("ShopsLocation"));
//                            files = Integer.parseInt(String.valueOf(map.get("files")));

                            RTnotifyStatus = Boolean.parseBoolean(String.valueOf(map.get("RT_Notified")));
                            IPnotifyStatus = Boolean.parseBoolean(String.valueOf(map.get("IP_Notified")));
                            RnotifyStatus =  Boolean.parseBoolean(String.valueOf(map.get("R_Notified")));
                            doneNotifyStatus =  Boolean.parseBoolean(String.valueOf(map.get("D_Notified")));

//                            for (final DataSnapshot user : order.getChildren()) {

                                String status = null;
                                boolean notify = false;
                                final HashMap<String, Object> notified = new HashMap<String, Object>();

//                                if (user.getKey().equals("orderStatus")) {
////                                    orderStatus = user.getValue().toString();
//                                }
//
//                                if(user.getKey().equals("RT_Notified")){
//                                    RTnotifyStatus = Boolean.parseBoolean(user.getValue().toString());
//                                }
//                                if(user.getKey().equals("IP_Notified")){
//                                    IPnotifyStatus = Boolean.parseBoolean(user.getValue().toString());
//                                }
//                                if(user.getKey().equals("R_Notified")){
//                                    RnotifyStatus = Boolean.parseBoolean(user.getValue().toString());
//                                }

//                                if(user.getKey().equals("orderDateTime")){
////                                    orderDateTime = user.getValue().toString();
//                                }
//                                if(user.getKey().equals("price")){
////                                    price = user.getValue();
//                                }

//                                Log.d("SHOPKEY",shop.getKey());
//                                Log.d("ORDER",order.getKey());

                                boolean finalRnotifyStatus = RnotifyStatus;
                                String finalOrderStatus = orderStatus;
                                boolean finalIPnotifyStatus = IPnotifyStatus;
                                boolean finalRTnotifyStatus = RTnotifyStatus;
                                boolean finalDoneNotifyStatus = doneNotifyStatus;
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {

                                        if (finalOrderStatus != null) {

                                            final String finalStatus = finalOrderStatus;
                                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.appicon);

                                            NotificationCompat.Builder builder;
                                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            if(!finalOrderStatus.equals("Done")){

                                                builder = new NotificationCompat.Builder(Select.this, CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.notify)
                                                        .setLargeIcon(icon)
                                                        .setContentTitle("Order Status :"+finalStatus)
                                                        .setContentText("Order ID: " + order.getKey() + " " + finalStatus)
                                                        .setGroup(CHANNEL_ID)
                                                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Order ID: " + order.getKey()))
                                                        .setContentIntent(resultPendingIntent)
                                                        .addAction(R.drawable.notify, "Check order Status", resultPendingIntent)
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                                builder.setSound(alarmSound);

                                            }else{
                                                builder = new NotificationCompat.Builder(Select.this, CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.notify)
                                                        .setLargeIcon(icon)
                                                        .setContentTitle("Order ID: " + order.getKey())
                                                        .setGroup(CHANNEL_ID)
                                                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Thank You for using Sleefax 😁 \n We hope you had a great experience."))
                                                        .setContentIntent(resultPendingIntent)
                                                        .addAction(R.drawable.notify, "Check order Status", resultPendingIntent)
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                                builder.setSound(alarmSound);

                                            }


//                                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            Log.d("ORDERSTAT!", finalOrderStatus);
//                                            Log.d("RN!", String.valueOf(finalRnotifyStatus));
//                                            Log.d("IPN!", String.valueOf(finalIPnotifyStatus));
//                                            Log.d("RtN!", String.valueOf(finalRTnotifyStatus));
//                                            Log.d("ORDERDATETIME",orderDateTime);


                                            if (finalOrderStatus.equals("Retrieved") && !finalRTnotifyStatus) {

                                                notified.put("RT_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
                                                notificationManager.notify(1, builder.build());
                                                notifyCnt = 0;

                                            } else if (finalOrderStatus.equals("In Progress") && !finalIPnotifyStatus) {

                                                notified.put("IP_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
                                                notificationManager.notify(1, builder.build());

                                            } else if (finalOrderStatus.equals("Ready") && !finalRnotifyStatus) {


                                                notified.put("R_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
                                                notificationManager.notify(1, builder.build());
                                            }
                                            else if (finalOrderStatus.equals("Done") && !finalDoneNotifyStatus) {


                                                notified.put("D_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(shop.getKey()).child(order.getKey()).updateChildren(notified);
                                                notificationManager.notify(1, builder.build());
                                            }

//                                            if(finalOrderStatus.equals("Placed")||finalOrderStatus.equals("Retrieved")||finalOrderStatus.equals("In Progress")){
//
//                                            }

                                        }
                                     }

                        }
                    }
//                }

//                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }
    }



//    public void setCurrentOrderStatus(String orderDateTime, String orderStatus, String orderKey, String shopKey, Double price, String shopName, String loc){
//
//        this.shopName = shopName;
//        this.shopKey = shopKey;
//        this.orderKey = orderKey;
//        this.orderStatus = orderStatus;
//        this.price = price;
//        this.loc = loc;
//
////        Log.d("ORDERDATETIME",(orderDateTime));
//
////        String orderID = orderKey.substring(orderKey.length()-8,orderKey.length());
//        currentOrderShopName.setText(shopName);
//        currentOrderID.setText("Order ID : "+orderKey);
//        currentOrderDateTime.setText(orderDateTime);
//        currentOrderPrice.setText("₹ "+(price));
//        currentOrderStatus.setText(orderStatus);
//        currentOrderShopLoc.setText(loc);
//
//        if (orderStatus.equals("Placed")){
////            progressBar.setProgress(25);
////            progressBar.setProgressColor(getResources().getColor(R.color.custom_progress_red_progress));
//            currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview1);
//        }else
//        if (orderStatus.equals("Retrieved")) {
////            progressBar.setProgress(50);
////            progressBar.setProgressColor(getResources().getColor(R.color.custom_progress_orange_progress));
//            currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview2);
//
//        }else
//        if (orderStatus.equals("In Progress")) {
////            progressBar.setProgress(75);
////            progressBar.setProgressColor(getResources().getColor(R.color.custom_progress_blue_progress));
//            currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview3);
//
//        }else
//        if (orderStatus.equals("Ready")){
////            progressBar.setProgress(100);
////            progressBar.setProgressColor(getResources().getColor(R.color.custom_progress_green_progress));
//            currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview4);
//
//        }
//
//    }


//    public void moreDetails(){
//
//
//        Intent intent = new Intent(Select.this, OrderPlaced.class);
//        Bundle extras = new Bundle();
////                        Log.d("LAT", String.valueOf(shopLat.get(position)));
//        extras.putString("ShopKey",shopKey);
//        extras.putString("OrderKey", orderKey);
//        extras.putString("ShopName", shopName);
//        extras.putDouble("ShopLat", shopLat);
//        extras.putDouble("ShopLong", shopLong);
//        extras.putString("Location", loc);
//        extras.putInt("Files", files);
//        extras.putString("OrderStatus", orderStatus);
//        extras.putDouble("Price", (Double) price);
//        extras.putBoolean("FromYourOrders", false);
//        extras.putBoolean("FromAddFilesPage", true);
//
//        intent.putExtras(extras);
//        startActivity(intent);
//
//    }


    final ArrayList<String> shopNames = new ArrayList<>();
    final ArrayList<String> locations = new ArrayList<>();
    final ArrayList<String> orderStatus = new ArrayList<>();
    final ArrayList<String> orderkey = new ArrayList<>();
    final ArrayList<String> orderDate = new ArrayList<>();
    final ArrayList<String> paymentModes = new ArrayList<>();

    final ArrayList<String> shopKey = new ArrayList<>();
    final ArrayList<Double> shopLat = new ArrayList<>();
    final ArrayList<Double> shopLong = new ArrayList<>();
    final ArrayList<Integer> files = new ArrayList<>();
    final ArrayList<Double> price = new ArrayList<>();

    class currentOrderAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Log.d("LIVEORDERSCNT",String.valueOf(cnt));
            convertView = getLayoutInflater().inflate(R.layout.current_order_row,null);

            if(convertView != null) {

                ///////////////////// Initializing views for current order layout   //////////////////////
                currentOrderLayout = convertView.findViewById(R.id.OrderRelativeL);
                currentOrderShopName = convertView.findViewById(R.id.currentOrderShopName);
                currentOrderShopLoc = convertView.findViewById(R.id.currentOrderShopLoc);
                currentOrderStatus = convertView.findViewById(R.id.currentOrderStatus);
                currentOrderDateTime = convertView.findViewById(R.id.currentOrderDateTime);
                currentOrderID = convertView.findViewById(R.id.currentOrderID);
                currentOrderPrice = convertView.findViewById(R.id.currentOrderPrice);
                pickedUpOrderView = convertView.findViewById(R.id.PickedUpOrderRL);
                orderPickBtn = convertView.findViewById(R.id.pickedOrderYes);

                ref.child("users").child(userId).child("Orders").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        for(DataSnapshot SNAP:dataSnapshot.getChildren()) {
                            Map<String, Object> map = (Map<String, Object>) SNAP.getValue();
                            if(!String.valueOf(map.get("orderStatus")).equals("Done")) {
                                shopKey.add(dataSnapshot.getKey());
                                orderkey.add(SNAP.getKey());
                            }
//                            orderCnt = orderkey.size();

                        }
//                   }
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

                final Handler handler = new Handler();
                final View finalConvertView = convertView;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Retrieve and cache the system's default "short" animation time.

//                        orderCnt = orderkey.size();
                        for (int i = 0; i < shopKey.size(); i++) {
//                            Log.d("SHOPKEY", shopKey.get(i));

                            final int finalI = i;
                            ref.child("users").child(userId).child("Orders").child(shopKey.get(i)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot orderKeys) {

                                    for(DataSnapshot info: orderKeys.getChildren()) {
                                        Map<String, Object> map = (Map<String, Object>) info.getValue();
                                        if(!String.valueOf(map.get("orderStatus")).equals("Done")) {
                                            files.add(Integer.parseInt(String.valueOf(map.get("files"))));
                                            locations.add(String.valueOf(map.get("ShopsLocation")));
                                            shopNames.add(String.valueOf(map.get("ShopName")));
                                            shopLat.add(Double.parseDouble(String.valueOf(map.get("ShopLat"))));
                                            shopLong.add(Double.parseDouble(String.valueOf(map.get("ShopLong"))));
                                            orderStatus.add(String.valueOf(map.get("orderStatus")));
                                            price.add(Double.parseDouble(String.valueOf(map.get("price"))));
                                            orderDate.add(String.valueOf(map.get("orderDateTime")));
                                            paymentModes.add(String.valueOf(map.get("paymentMode")));
                                        }
//                                        for (DataSnapshot snap : info.getChildren()) {
////                                            Log.d("ORDERINFO", snap.getKey());
//
//                                            if (snap.getKey().equals("files")) {
//                                                files.add(Integer.parseInt(snap.getValue().toString()));
//                                            }
//
//                                            if (snap.getKey().equals("ShopsLocation")) {
//                                                Log.d("LOCATIONS", snap.getValue().toString());
//                                                locations.add(snap.getValue().toString());
//                                            }
//
//                                            if (snap.getKey().equals("ShopName")) {
//                                                shopNames.add(snap.getValue().toString());
//                                            }
//
//                                            if (snap.getKey().equals("ShopLat")) {
//                                                shopLat.add((Double) snap.getValue());
//                                            }
//
//                                            if (snap.getKey().equals("ShopLong")) {
//                                                shopLong.add((Double) snap.getValue());
//                                            }
//
//                                            if (snap.getKey().equals("orderStatus")) {
//                                                orderStatus.add(snap.getValue().toString());
//                                            }
//
//                                            if (snap.getKey().equals("price")) {
//                                                price.add(Double.parseDouble(snap.getValue().toString()));
//                                            }
//                                            if (snap.getKey().equals("orderDateTime")) {
//                                                orderDate.add(snap.getValue().toString());
//                                            }
//                                            if (snap.getKey().equals("paymentMode")) {
//                                                paymentModes.add(snap.getValue().toString());
//                                            }
//                                        }
                                    }



//                                    final Handler handler1 = new Handler();
//                                    handler1.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {

//                                            if (!orderStatus.get(position).equals("Done")) {
                                                currentOrderShopLoc.setText(locations.get(position));
                                                currentOrderShopName.setText(shopNames.get(position));
                                                currentOrderStatus.setText(orderStatus.get(position));
                                                currentOrderPrice.setText("Price : ₹" + price.get(position));
                                                currentOrderDateTime.setText(orderDate.get(position));
                                                currentOrderID.setText("Order ID: " + orderkey.get(position));
                                                Log.d("LIVESTAT",orderStatus.get(position));

                                                if ((orderStatus.get(position)).equals("Placed")) {
                                                    currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview1);
                                                }
                                                if ((orderStatus.get(position)).equals("Retrieved")) {
                                                    currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview2);
                                                }
                                                if ((orderStatus.get(position)).equals("In Progress")) {
                                                    currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview3);
                                                }
                                                if ((orderStatus.get(position)).equals("Ready")) {

                                                    currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview4);
                                                    shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

                                                    pickedUpOrderView.setAlpha(0f);
                                                    pickedUpOrderView.setVisibility(View.VISIBLE);
                                                    pickedUpOrderView.animate()
                                                            .alpha(1f)
                                                            .setDuration(shortAnimationDuration)
                                                            .setListener(null);

                                                    orderPickBtn.setOnClickListener(new View.OnClickListener() {
                                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                                        @Override
                                                        public void onClick(View v) {
                                                            final HashMap<String, Object> orderStatusUpdate = new HashMap<String, Object>();
                                                            orderStatusUpdate.put("orderStatus", "Done");
                                                            ref.child("users").child(userId).child("Orders").child(shopKey.get(finalI)).child(orderkey.get(position)).updateChildren(orderStatusUpdate);
                                                            getOrders();
                                                        }
                                                    });
                                                }
//                                            }
//                                            else{


//                                                finalConvertView.setAlpha(0f);
//                                                finalConvertView.setVisibility(View.GONE);
//                                                finalConvertView.animate()
//                                                        .alpha(0f)
//                                                        .setDuration(shortAnimationDuration)
//                                                        .setListener(null);

//                                                currentOrderLV();
//                                                listView.setVisibility(View.GONE);

//                                                files.remove(position);
//                                                locations.remove(position);
//                                                shopNames.remove(position);
//                                                shopLat.remove(position);
//                                                shopLong.remove(position);
//                                                orderStatus.remove(position);
//                                                price.remove(position);
//                                                orderDate.remove(position);
//                                                paymentModes.remove(position);
//                                            }

//                                        }
//                                    }, 10);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                }, 100);


            }
            return convertView;
        }
    }
}

