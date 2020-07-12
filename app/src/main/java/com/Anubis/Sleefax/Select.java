package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Anubis.Sleefax.CONSTANTS.CONSTANTS;
import com.Anubis.Sleefax.Services.NotificationService;
import com.github.mmin18.widget.RealtimeBlurView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class Select extends AppCompatActivity {

    PaytmPGService Service = PaytmPGService.getProductionService();
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

    int shortAnimationDuration,liveOrderRLInitHT;

    Button orders,orderPickBtn;
    ImageButton selectFilesBtn,more ,setting,sideMenu,selectPhotos,selectAttachment;

    View addFileView;
    RealtimeBlurView blurrView;

    int orderCnt = 0,mScreenHeight, mScreenWidth;

    int cnt;
    boolean network,isTester,newUser;

    RelativeLayout contactsRl,addfilePage,addfileTVRL,pickedUpOrderView,CurrentOrderRowRL,liveOrderRL;

    TextView addFilesText,addPhotosText;

    Vibrator vibrator;

    ////// Buttons and items of contacts page /////
    Button num1,num2;
    ImageButton back;


    /////////// Variables for current order status layout/////
    RelativeLayout currentOrderLayout;
    TextView currentOrderStatus,currentOrderDateTime,currentOrderPrice,currentOrderID,currentOrderShopName, currentOrderShopLoc,orderPickedTV,stat1,stat2,stat3,stat4;
    View stat1view,stat2view,stat3view,stat4view;

    //////////Variables for naviagtion drawer / menu ///////////////
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

//////////////////////////////home page UI elements///////////////////////////////////
    RelativeLayout liveorders,homepage;
    boolean scrollingLeft = false;

    HorizontalScrollView horizontalScrollView;

    ImageButton home,cart;
    TextView hellonameTv;//this variable to be changed for the name on home page
//////////////////////////////home page UI elements///////////////////////////////////


    //////// Live orders Listview /////////
    ListView listView;
    currentOrderAdapter adapter;

    //    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_files);

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);


        //////////////////////////////home page UI elements///////////////////////////////////

        liveorders = findViewById(R.id.LiveOrdersRL);
        homepage = findViewById(R.id.HomePageLayout);

        horizontalScrollView = findViewById(R.id.ImagesScrollView);


        horizontalScrollView.setSmoothScrollingEnabled(true);

        hellonameTv = findViewById(R.id.helloTV);


//////////////////////////////shifting between home page and live orders/////////////////////////////////////////////
        home = findViewById(R.id.home);
        cart = findViewById(R.id.cart);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(20);//80 represents the milliseconds (the duration of the vibration)
                home.setImageResource(R.drawable.home_blue);
                cart.setImageResource(R.drawable.shopping_grey);
                homepage.setVisibility(View.VISIBLE);
                liveorders.setVisibility(View.GONE);



            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(20);//80 represents the milliseconds (the duration of the vibration)

                home.setImageResource(R.drawable.home_grey);
                cart.setImageResource(R.drawable.supermarket);
                liveorders.setVisibility(View.VISIBLE);
                homepage.setVisibility(View.GONE);
            }
        });


        //////////////////////////////animation///////////////////////////////////


        Timer timer = new Timer("horizontalScrollViewTimer");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (scrollingLeft) {

                            if (horizontalScrollView.getScrollX() == 0) {
                                horizontalScrollView.smoothScrollBy(10, 0);
                                scrollingLeft = false;
                            } else {
                                horizontalScrollView.smoothScrollBy(-10, 0);
                            }

                        } else {
                            if (horizontalScrollView.canScrollHorizontally(View.FOCUS_RIGHT)) {
                                horizontalScrollView.smoothScrollBy(10, 0);
                            } else {

                                horizontalScrollView.smoothScrollBy(-10, 0);
                                scrollingLeft = true;
                            }
                        }
                    }
                });
            }
        }, 0, 50);
        //////////////////////////////home page UI elements///////////////////////////////////


        Random random = new Random();
        String randomNumber = String.valueOf(random.nextInt());
        Log.d("RANDOM ",String.valueOf(randomNumber.substring(randomNumber.length()-4)));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;
        mScreenWidth = displaymetrics.widthPixels;

        //View that will come from the top of the screen when user clicks on add file btn///
        addfileTVRL = findViewById(R.id.AddfileTVRL);
        blurrView = findViewById(R.id.blurr);

        //Buttons for image and files
        selectAttachment = findViewById(R.id.SelectFile);
        selectPhotos = findViewById(R.id.SelectImage);
        addFilesText = findViewById(R.id.add_files_text);
        addPhotosText = findViewById(R.id.add_photos_text);

        addPhotosText.animate().translationXBy(-1000f);
        addFilesText.animate().translationXBy(1000f);

        addFileView = findViewById(R.id.liveordersTopView);
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        selectFilesBtn = findViewById(R.id.AddFilesButton);

//        setting = findViewById(R.id.settings);
//        orders = findViewById(R.id.YourOrders);

        sideMenu = findViewById(R.id.SideMenu);
        more = findViewById(R.id.more);
        contactsRl = findViewById(R.id.contactsRelativeL);
        addfilePage = findViewById(R.id.addfilesRL);
        back = findViewById(R.id.contactspageback);


        //Live Orders Top Relative Layour
        liveOrderRL = findViewById(R.id.LiveOrderTopRL);
        liveOrderRLInitHT = liveOrderRL.getLayoutParams().height;


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
//            orders.setVisibility(View.INVISIBLE);
        }else{
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
                sharedPreferences.edit().putString("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();

                ////Setting up navigation and drawer layour viewssss/////
                setUpNavigationViews();

                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                getOrders();

                getCurrentOrderDetails();
                ///////Order Cnt for order history///////
                getOrderCntForOrderHistory();


                /////// Order History button listener ///////
//                orders.setOnClickListener(Listener);
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Log.d("USERID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if(FirebaseAuth.getInstance().getCurrentUser().getEmail() != null && FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("tester")){
                        isTester = true;
                    }else if(FirebaseAuth.getInstance().getCurrentUser().getEmail() != null && FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("Tester")){
                        isTester = true;
                    }else if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null && FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().contains("123456789")){
                        isTester = true;

                    }else{
                        isTester = false;
                    }
//                    if (FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("tester") || FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("Tester") || FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().contains("1234567890")) {
//                        isTester = true;
//                    } else {
//                        isTester = false;
//                    }
                }


                ///Creating notification service
                Intent notificationServiceIntent = new Intent(Select.this, NotificationService.class);
                startService(notificationServiceIntent);
            }
        }
        Log.d("NEWUSER",String.valueOf(newUser));




        network = haveNetworkConnection();
        if(!network){
            Toast.makeText(Select.this,"Please check your internet connection.",Toast.LENGTH_LONG).show();
        }


//        setProgressForOrder();
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.RefreshLayout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("REFRESHING", "onRefresh called from SwipeRefreshLayout");

//                getOrders();
                pullToRefresh.setRefreshing(false);
                currentOrderLV();

            }
        });

        selectFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                selectFiles(view);

                addfileTVRL.setVisibility(View.VISIBLE);

                homepage.setVisibility(View.INVISIBLE);

                if(addfileTVRL.getHeight() == 0) {
                    blurrView.setVisibility(View.VISIBLE);
                    expandView(addfileTVRL, 0, mScreenHeight / 4);
                }

            }
        });



        selectAttachment.setOnClickListener(Listener);
        selectPhotos.setOnClickListener(Listener);


        ////setting size of add file view to full page if cnt == 0  ////////
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                setupLayoutParams();
            }
        },200);

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            // do your stuff..
            checkPermissionREAD_EXTERNAL_STORAGE(Select.this);

        }
    }
    //Create an anonymous implementation of OnClickListener
    private View.OnClickListener contactsPageListener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked

            if(v == findViewById(R.id.num1)) {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
                Uri number = Uri.parse("tel:"+num1.getText());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
//                callIntent.setData(Uri.parse("tel:" + shopNum));

                if (ActivityCompat.checkSelfPermission(Select.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(Select.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    Log.d("MANAGEPERMISSION", "PERMISSION");
//                    startActivity(callIntent);
//                    return;
                }
                startActivity(callIntent);

            }else if(v == findViewById(R.id.num2)){
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
                Uri number = Uri.parse("tel:"+num1.getText());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
//                callIntent.setData(Uri.parse("tel:" + shopNum));

                if (ActivityCompat.checkSelfPermission(Select.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(Select.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    Log.d("MANAGEPERMISSION", "PERMISSION");
//                    startActivity(callIntent);
//                    return;
                }
                startActivity(callIntent);
            }else if(v == findViewById(R.id.contactspageback)){
                finish();

            }
        }
    };



    //     Create an anonymous implementation of OnClickListener
    private View.OnClickListener Listener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onClick(View v) {
            // do something when the button is clicked
            Log.d("GETTING", "ORDERS");

            if (!network) {
                Toast.makeText(Select.this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
            } else {
                 if(v == findViewById(R.id.SelectFile)){
                    Log.d("SELECTINGPHOTOS","TRUE");
                    Intent intent = new Intent(Select.this,Pop.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("IsTester",isTester);
                    bundle.putBoolean("NewUser",newUser);
                    bundle.putBoolean("File",true);
                    intent.putExtras(bundle);
                    startActivity(intent);


                }else if(v == findViewById(R.id.SelectImage)){
                    Log.d("SELECTINGPHOTOS","FALSE");

                    Intent intent = new Intent(Select.this,Pop.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("IsTester",isTester);
                    bundle.putBoolean("NewUser",newUser);
                    bundle.putBoolean("File",false);
                    intent.putExtras(bundle);
                    startActivity(intent);


                }

            }


        }
    };


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) { 
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void selectFiles(final View v) {

        Intent pop = new Intent(Select.this,Pop.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsTester",isTester);
        bundle.putBoolean("NewUser",newUser);


        //Todo: Animation Type 1
        animateFileAndImageBtns();


        //Todo: Animation Type 2
           // animate2File_Photos(v);
    }



    int i = 1;
    public void animateFileAndImageBtns(){
        i++;
        // add.setRotation(45f);

        Resources r = getResources();
        int xaxistext = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                70,
                r.getDisplayMetrics());
        int xxaxistext = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                125,
                r.getDisplayMetrics());
        int yaxis = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                100,
                r.getDisplayMetrics());
        int xaxis = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                90,
                r.getDisplayMetrics());
        if(i%2==0){
            collapseView(liveOrderRL,liveOrderRL.getLayoutParams().height,0);

//            selectFilesBtn.setRotation(45f);
            ObjectAnimator.ofFloat(selectFilesBtn, "rotation", 0f, 45f).start();

//            pullToRefresh.setAlpha(0.1f);
//            listView.setAlpha(0.1f);

            addPhotosText.animate().translationXBy(1000f + mScreenWidth/6 - 10).setDuration(400);
            // addPhotosText.animate().translationXBy(1000f).translationXBy(xaxistext).setDuration(400);
            // addFilesText.setVisibility(View.VISIBLE);
            selectAttachment.animate().translationXBy(mScreenWidth/4 - 20).translationYBy(-(float) yaxis).setDuration(400);
            selectAttachment.setVisibility(View.VISIBLE);
            selectPhotos.animate().translationXBy(-(mScreenWidth/4)).translationYBy(-(float) yaxis).setDuration(400);
            selectPhotos.setVisibility(View.VISIBLE);
            addFilesText.animate().translationXBy(-(1000f + mScreenWidth/5 + 10)).setDuration(400);
            //  addPhotosText.setVisibility(View.VISIBLE);
        } else
        {
            collapseView(addfileTVRL,mScreenHeight/4,0);
//            selectFilesBtn.setRotation(90f);
            ObjectAnimator.ofFloat(selectFilesBtn, "rotation", 45f, 0f).start();

//            pullToRefresh.setAlpha(1f);
//            listView.setAlpha(1);

            selectAttachment.animate().translationXBy(-(mScreenWidth/4 - 20)).translationYBy((float) yaxis).setDuration(400);
            addPhotosText.animate().translationXBy(-(1000f + mScreenWidth/6 - 10)).setDuration(400);
            // addFiles.setVisibility(View.INVISIBLE);
            //  addPhotos.animate().translationXBy((float) (xaxis-15)).translationYBy(-(float) yaxis).setDuration(200);
            //  addPhotos.setVisibility(View.INVISIBLE);
            selectPhotos.animate().translationXBy((mScreenWidth/4)).translationYBy((float) yaxis).setDuration(400);
            addFilesText.animate().translationXBy(1000f + mScreenWidth/5 + 10).setDuration(400);
            // addFilesText.setVisibility(View.INVISIBLE);
            // addPhotosText.setVisibility(View.INVISIBLE);
            blurrView.setVisibility(View.GONE);

            expandView(liveOrderRL,0,liveOrderRLInitHT);

        }
    }


    public void animate2File_Photos(final View v){


        final CoordinatorLayout mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        int transitionId;
        collapseView(liveOrderRL,liveOrderRL.getLayoutParams().height,0);

        if(!isTop1) {
            selectFilesBtn.setBackgroundResource(R.drawable.addfilebtnshadow2);
            ObjectAnimator.ofFloat(selectFilesBtn, "rotation", 0f, 45f).start();
        }
        transitionId = (R.transition.arc_motion_transition);
        // Get the transition inflater
        TransitionInflater inflater = TransitionInflater.from(Select.this);
        // Inflate the specified transition
        Transition arcMotionTransition = inflater.inflateTransition(transitionId);

        // Aet the transition duration
        arcMotionTransition.setDuration(500);

        // Specify the target for the transition
        arcMotionTransition.addTarget(selectAttachment);
        arcMotionTransition.addTarget(selectPhotos);

        // Add a listener for the transition
        arcMotionTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

                selectAttachment.setVisibility(View.VISIBLE);
                selectPhotos.setVisibility(View.VISIBLE);


            }

            @Override
            public void onTransitionEnd(Transition transition) {
                // At the end of transition hide the target
                if(!isTop1 && !isTop2){
                    TransitionManager.beginDelayedTransition(mCLayout);
                    selectAttachment.setVisibility(View.INVISIBLE);
                    selectPhotos.setVisibility(View.INVISIBLE);
                    ObjectAnimator.ofFloat(v, "rotation", 45f, 0f).start();

                    selectFilesBtn.setBackgroundColor(Color.parseColor("#00FFFFFF"));

                    collapseView(addfileTVRL,mScreenHeight/4,0);
                    blurrView.setVisibility(View.GONE);

                    expandView(liveOrderRL,0,liveOrderRLInitHT);
                }
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        // Begin the delayed transition
        TransitionManager.beginDelayedTransition(mCLayout,arcMotionTransition);

        // Toggle the button position
        togglePositionBtn1();
        togglePositionBtn2();

    }

    protected void togglePositionBtn1(){
        // Change the button widget location to animate it
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)selectPhotos.getLayoutParams();

        if(isTop1){
            // Put the button at the layout bottom horizontal center
            params.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
            isTop1 = false;
        }else {
            // Put the button at the layout top left
            params.gravity = Gravity.TOP| Gravity.LEFT;
            isTop1 = true;
        }
        selectPhotos.setLayoutParams(params);
    }
    protected void togglePositionBtn2(){
        // Change the button widget location to animate it
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)selectAttachment.getLayoutParams();

        if(isTop2){
            // Put the button at the layout bottom horizontal center
            params.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
            isTop2 = false;
        }else {
            // Put the button at the layout top right
            params.gravity = Gravity.TOP| Gravity.RIGHT;
            isTop2 = true;
        }
        selectAttachment.setLayoutParams(params);
    }

    public void disableListViewItemSelection(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Select.this, "GETCHILDC "+cnt, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < 12; i++){
                    View v = listView.getChildAt(i);
                    v.setEnabled(false);
                }
            }
        },500);
    }

    int initialHt;
    public void setupLayoutParams(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;

        final ViewGroup.LayoutParams params = addFileView.getLayoutParams();
        final RelativeLayout.LayoutParams addfileParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        addFileView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initialHt = params.height;

        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        int liveOrderCnt = sharedPreferences.getInt("LiveOrderCnt",0);
        int orderHistoryCnt = sharedPreferences.getInt("OrderHistoryCnt",0);


        CONSTANTS obj = new CONSTANTS();
        Log.d("liveOrderCnt",String.valueOf(cnt));
//        Log.d("orderHistoryCnt",String.valueOf(orderCnt));
        if(cnt == 0) {
//            addFileView.setAlpha(0f);
//            addFileView.animate()
//                    .alpha(1f)
//                    .setDuration(shortAnimationDuration + 500)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            params.height = height;
//                        }
//                    });

//            expandView(addFileView,initialHt,mScreenHeight);


        }
    }


    public void expandView(final View v,int initialHt,int finalHt){

        ValueAnimator slideAnimator = ValueAnimator.ofInt(initialHt,finalHt).setDuration(shortAnimationDuration);
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                v.getLayoutParams().height = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                v.requestLayout();

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

    }

    public void collapseView(final View v,int initialHt,int finalHt){

        blurrView.setVisibility(View.GONE);

        ValueAnimator slideAnimator = ValueAnimator.ofInt(initialHt,finalHt).setDuration(shortAnimationDuration);
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                v.getLayoutParams().height = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                v.requestLayout();

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

    }
    public String getDisplayNameSavedLocally(){

        String name;
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        name = sharedPreferences.getString("DisplayName",null);
        hellonameTv.setText("Hello "+name + ",");
        return name;
    }

    public void setUpNavigationViews(){

        //////////////////////NAVIGATION VIEW/////////////////////////

        Toolbar toolbar = findViewById(R.id.toolbar);

        final DuoDrawerLayout drawerLayout = (DuoDrawerLayout) findViewById(R.id.drawerLayout);
        final DuoDrawerToggle drawerToggle = new DuoDrawerToggle(Select.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerToggle.setDrawerIndicatorEnabled(false);

        drawerToggle.setHomeAsUpIndicator(R.drawable.more_2);
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();




        DuoMenuView duoMenuView = findViewById(R.id.Duo_Menu);


        //Todo: Basic Navigation view
//        navigationView = findViewById(R.id.NavView);
//        toolbar = findViewById(R.id.navaction);
//        drawerLayout = findViewById(R.id.drawerLayout);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView username,useremail;

        //Todo: Uncomment next line if you want to use the basic navigation menu
//        View header = navigationView.getHeaderView(0);
        useremail =  duoMenuView.findViewById(R.id.useremail);
        username = duoMenuView.findViewById(R.id.username);


        username.setText(getDisplayNameSavedLocally());
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            useremail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

        setUpMenuButtonActions();


        //Todo: Basic actionBarToggle
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
//
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.openmenublack,getTheme());
//        actionBarDrawerToggle.setHomeAsUpIndicator(drawable);
//        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    drawerLayout.openDrawer(GravityCompat.START);
//                }
//            }
//        });

//        navigationView.setItemIconTintList(null);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                menuItem.setChecked(false);
//                int menu_id = menuItem.getItemId();
//                Log.d("MENUID", String.valueOf(menuItem.getItemId()));
//                drawerLayout.closeDrawers();
//
//                switch (menu_id){
//                    case R.id.userprofile:
//
//                        Intent intent = new Intent(Select.this,changeInfoPopUp.class);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        break;
//
//                    case R.id.logout:
//
//                        FirebaseAuth.getInstance().signOut();
//                        Intent signOutIntent = new Intent(Select.this, MainActivity.class);
//                        signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
//                        startActivity(signOutIntent);
//                        Toast.makeText(Select.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
//                        finish();
//                        break;
//
//                    case R.id.issue:
//
//                        Intent issueIntent = new Intent(Select.this,ReportIssue.class);
//                        startActivity(issueIntent);
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        break;
//
//                    case R.id.contact:
//
//                        contactsRl.setVisibility(View.VISIBLE);
//                        addfilePage.setVisibility(View.INVISIBLE);
//
//                        num1 = findViewById(R.id.num1);
//                        num2 = findViewById(R.id.num2);
//
//                        num1.setOnClickListener(contactsPageListener);
//                        num2.setOnClickListener(contactsPageListener);
//                        back.setOnClickListener(contactsPageListener);
//
//                        break;
//
//                    case R.id.feedback:
//                        Intent feedbackIntent = new Intent(Select.this,Feedback.class);
//                        startActivity(feedbackIntent);
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        break;
//                }
//
//                return true;
//            }
//        });



    }

    public void setUpMenuButtonActions(){
        Button orderHistory,settings,feedback, contactUs,issue,needHelp,rateUs,tell_A_Friend,logout;

        orderHistory = findViewById(R.id.Menu_OrderHistory);
        settings = findViewById(R.id.Menu_Settings);
        contactUs = findViewById(R.id.Menu_ContactUs);
        issue = findViewById(R.id.Menu_ReportIssue);
        needHelp = findViewById(R.id.Menu_NeedHelp);
        rateUs = findViewById(R.id.Menu_RateUs);
        tell_A_Friend = findViewById(R.id.Menu_Tell_A_Friend);
        feedback = findViewById(R.id.Menu_Feedback);
        logout = findViewById(R.id.Menu_Logout);

        settings.setOnClickListener(MenuBtnListeners);
        orderHistory.setOnClickListener(MenuBtnListeners);
        contactUs.setOnClickListener(MenuBtnListeners);
        issue.setOnClickListener(MenuBtnListeners);
        feedback.setOnClickListener(MenuBtnListeners);
        needHelp.setOnClickListener(MenuBtnListeners);
        rateUs.setOnClickListener(MenuBtnListeners);
        tell_A_Friend.setOnClickListener(MenuBtnListeners);
        logout.setOnClickListener(MenuBtnListeners);

    }
    //     Create an anonymous implementation of OnClickListener
    private View.OnClickListener MenuBtnListeners = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onClick(View v) {

            if(v == findViewById(R.id.Menu_Settings)){

                Intent intent = new Intent(Select.this,settings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }else if(v == findViewById(R.id.Menu_OrderHistory)){
                mProgressDialog = new ProgressDialog(Select.this);
                // Set progressdialog title
                mProgressDialog.setTitle("Retreiving Orders");
                // Set progressdialog message
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setIndeterminate(false);
                // Show progressdialog
                mProgressDialog.show();
                Context context = getApplicationContext();
//                CharSequence text = "No order history to show.";
                CharSequence text = "Order Cnt" + orderCnt;
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);

                Intent orderHistoryIntent = new Intent(Select.this, YourOrders.class);
                startActivity(orderHistoryIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

//                finish();
                mProgressDialog.dismiss();
            }else if(v == findViewById(R.id.Menu_ContactUs)){

            }else if(v == findViewById(R.id.Menu_ReportIssue)){
                Intent issueIntent = new Intent(Select.this,ReportIssue.class);
                startActivity(issueIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }else if(v == findViewById(R.id.Menu_Feedback)){
                Intent feedbackIntent = new Intent(Select.this,Feedback.class);
                startActivity(feedbackIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }else if(v == findViewById(R.id.Menu_NeedHelp)){

            }else if(v == findViewById(R.id.Menu_RateUs)){

            }else if(v == findViewById(R.id.Menu_Tell_A_Friend)){

            }else if(v == findViewById(R.id.Menu_Logout)){
                FirebaseAuth.getInstance().signOut();
                Intent signOutIntent = new Intent(Select.this, MainActivity.class);
                signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                startActivity(signOutIntent);
                Toast.makeText(Select.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(addfilePage.getVisibility() == View.GONE) {
//            addfilePage.setVisibility(View.VISIBLE);
            contactsRl.setVisibility(View.GONE);

        }else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finish();
        }
    }

    public void currentOrderLV(){
        listView = findViewById(R.id.currentOrderLV);
        adapter = new Select.currentOrderAdapter();
        listView.setAdapter(adapter);

    }




     ArrayList<String> orderkey = new ArrayList<>();
    ArrayList<String> orderID = new ArrayList<>();

    ArrayList<String> shopKey = new ArrayList<>();
     ArrayList<String> shopNames = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> locations = new ArrayList<>();
     ArrayList<String> orderStatus = new ArrayList<>();

    //final ArrayList<String> orderkey = new ArrayList<>();

     ArrayList<String> orderDate = new ArrayList<>();
     ArrayList<String> paymentModes = new ArrayList<>();

    //final ArrayList<String> shopKey = new ArrayList<>();

     ArrayList<Double> shopLat = new ArrayList<>();
     ArrayList<Double> shopLong = new ArrayList<>();
     ArrayList<Integer> files = new ArrayList<>();
     ArrayList<Double> price = new ArrayList<>();
//    long cnt;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getOrders(){

//        setProgressForOrder();

        shopKey.clear();
        orderkey.clear();

        cnt = 0;
        ref.child("users").child(userId).child("Orders").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                for(DataSnapshot orderIDS: dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(!String.valueOf(map.get("orderStatus")).equals("Done")){
                        cnt = cnt + 1;

                        orderkey.add(dataSnapshot.getKey());
                        orderID.add(String.valueOf(map.get("orderID")));
                        shopKey.add(String.valueOf(map.get("storeId")));


                        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
                        sharedPreferences.edit().putInt("LiveOrderCnt",cnt).apply();

                        CONSTANTS obj = new CONSTANTS();
                        obj.cnt = cnt;

//                        setupLayoutParams();
//                        currentOrderLV();

                    }

//                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                setupLayoutParams();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void getCurrentOrderDetails(){
        ref.child("users").child(userId).child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot orderKeys) {

                files.clear();
                locations.clear();
                shopNames.clear();
                shopLat.clear();
                shopLong.clear();
                orderStatus.clear();
                price.clear();
                orderDate.clear();
                paymentModes.clear();

//                Toast.makeText(Select.this,"ORDERID"+orderKeys.getChildrenCount(),Toast.LENGTH_LONG).show();
//                Log.d("NEWORDERMAP",String.valueOf(orderKeys.getValue()));
                for(DataSnapshot values: orderKeys.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) values.getValue();

                    if (map != null) {
//                        Log.d("MAPVAL", String.valueOf(map));
                        if (!String.valueOf(map.get("orderStatus")).equals("Done")) {
//                            cnt = (int) orderKeys.getChildrenCount();

                            files.add(Integer.parseInt(String.valueOf(map.get("files") != null ? map.get("files") : 0)));
                            locations.add(String.valueOf(map.get("ShopsLocation") != null ? map.get("ShopsLocation"): "NA"));
                            shopNames.add(String.valueOf(map.get("ShopName") != null ? map.get("ShopName"): "NA"));
                            shopLat.add(Double.parseDouble(String.valueOf(map.get("ShopLat") != null ? map.get("ShopLat") : 0)));
                            shopLong.add(Double.parseDouble(String.valueOf(map.get("ShopLong") != null ? map.get("ShopLong") : 0)));
                            orderStatus.add(String.valueOf(map.get("orderStatus") != null ? map.get("orderStatus") : 0));
                            price.add(Double.parseDouble(String.valueOf(map.get("price") != null ? map.get("price") : 0)));
                            orderDate.add(String.valueOf(map.get("orderDateTime") != null ? map.get("orderDateTime") : 0));
                            paymentModes.add(String.valueOf(map.get("paymentMode") != null ? map.get("paymentMode") : 0));

//                            for (Map.Entry<String, Object> entry : map.entrySet())
//                                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

                        }
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


//                        Collections.reverse(orderkey);
//                        Collections.reverse(shopKey);
//
//                        Collections.reverse(files);
//                        Collections.reverse(locations);
//                        Collections.reverse(shopNames);
//                        Collections.reverse(shopLat);
//                        Collections.reverse(shopLong);
//                        Collections.reverse(paymentModes);
//                        Collections.reverse(price);
//                        Collections.reverse(orderDate);
//                        Collections.reverse(orderStatus);


                        home.setImageResource(R.drawable.home_grey);
                        cart.setImageResource(R.drawable.supermarket);
                        liveorders.setVisibility(View.VISIBLE);
                        homepage.setVisibility(View.GONE);
                        currentOrderLV();
                    }
                },500);


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

//                shopKey.add(dataSnapshot.getKey());
//                for(DataSnapshot orderIDS: dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(String.valueOf(map.get("orderStatus")).equals("Done")){
//                        cnt = (int) (dataSnapshot.getChildrenCount()+cnt);
                        orderCnt = orderCnt + 1;

                        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
                        sharedPreferences.edit().putInt("OrderHistoryCnt",orderCnt).apply();

                        CONSTANTS obj = new CONSTANTS();
                        obj.orderHistoryCnt = orderCnt;
//                        setupLayoutParams();
                    }
//                }


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

    Boolean isTop1=false,isTop2=false;




    DatabaseReference orderDb = FirebaseDatabase.getInstance().getReference();
//    ArrayList<String> orderKeys = new ArrayList<>();



    private void createNotificationChannel() {


    }


    ////////Declaring variables for more details of orders/////////
//    String shopKey,orderKey,shopName,loc,orderStatus;
//    int files;
//    Double shopLat,shopLong,price;
//    boolean fromYourOrders = false;

    int notifyCnt = 0;


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
                    for (DataSnapshot orderKeys : dataSnapshot.getChildren()) {

//                            Log.d("ORDERID",order.getKey());



                            boolean RTnotifyStatus = true;
                            boolean IPnotifyStatus = true;
                            boolean RnotifyStatus = true,doneNotifyStatus = true;

                            Map<String, Object> map = (Map<String, Object>) orderKeys.getValue();
                            String orderStatus;
                            Double price;
                            String orderDateTime;
                            RTnotifyStatus = Boolean.parseBoolean(String.valueOf(map.get("RT_Notified")));
                            IPnotifyStatus = Boolean.parseBoolean(String.valueOf(map.get("IP_Notified")));
                            RnotifyStatus =  Boolean.parseBoolean(String.valueOf(map.get("R_Notified")));
                            doneNotifyStatus =  Boolean.parseBoolean(String.valueOf(map.get("D_Notified")));
                             orderStatus = String.valueOf(map.get("orderStatus"));

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
                                                        .setContentText("Order ID: " + orderKeys.getKey() + " " + finalStatus)
                                                        .setGroup(CHANNEL_ID)
                                                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Order ID: " + orderKeys.getKey()))
                                                        .setContentIntent(resultPendingIntent)
                                                        .addAction(R.drawable.notify, "Check order Status", resultPendingIntent)
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                                builder.setSound(alarmSound);

                                            }else{
                                                builder = new NotificationCompat.Builder(Select.this, CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.notify)
                                                        .setLargeIcon(icon)
                                                        .setContentTitle("Order ID: " + orderKeys.getKey())
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


                                            if (finalOrderStatus.equals("Received") && !finalRTnotifyStatus) {

                                                notified.put("RT_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(orderKeys.getKey()).updateChildren(notified);
                                                notificationManager.notify(1, builder.build());
                                                notifyCnt = 0;

                                            } else if (finalOrderStatus.equals("In Progress") && !finalIPnotifyStatus) {

                                                notified.put("IP_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(orderKeys.getKey()).updateChildren(notified);
                                                notificationManager.notify(1, builder.build());

                                            } else if (finalOrderStatus.equals("Ready") && !finalRnotifyStatus) {


                                                notified.put("R_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(orderKeys.getKey()).updateChildren(notified);
                                                notificationManager.notify(1, builder.build());
                                            }
                                            else if (finalOrderStatus.equals("Done") && !finalDoneNotifyStatus) {


                                                notified.put("D_Notified", true);
                                                orderDb.child("users").child(userId).child("Orders").child(orderKeys.getKey()).updateChildren(notified);
                                                notificationManager.notify(1, builder.build());
                                            }

//                                            if(finalOrderStatus.equals("Placed")||finalOrderStatus.equals("Retrieved")||finalOrderStatus.equals("In Progress")){
//
//                                            }

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



    public void moreDetails(String shopKey,String orderKey,String shopName,Double shopLat, Double shopLong, String loc,int files,String orderStatus, Double price, String orderID){

        Intent intent = new Intent(Select.this, OrderPlaced.class);
        Bundle extras = new Bundle();
//                        Log.d("LAT", String.valueOf(shopLat.get(position)));
        extras.putString("ShopKey",shopKey);
        extras.putString("OrderKey", orderKey);
        extras.putString("ShopName", shopName);
        extras.putDouble("ShopLat", shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putString("Location", loc);
        extras.putInt("Files", files);
        extras.putString("OrderStatus", orderStatus);
        extras.putDouble("Price", (Double) price);
        extras.putString("OrderID",orderID);
        extras.putBoolean("FromYourOrders", false);
        extras.putBoolean("FromAddFilesPage", true);

        intent.putExtras(extras);
        startActivity(intent);

    }



  public class currentOrderAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return cnt;
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
                CurrentOrderRowRL = convertView.findViewById(R.id.CurrentOrderRowRL);
                currentOrderLayout = convertView.findViewById(R.id.OrderRelativeL);

                currentOrderShopName = convertView.findViewById(R.id.currentOrderShopName);
                currentOrderShopLoc = convertView.findViewById(R.id.currentOrderShopLoc);
                currentOrderStatus = convertView.findViewById(R.id.currentOrderStatus);
                currentOrderDateTime = convertView.findViewById(R.id.currentOrderDateTime);
                currentOrderID = convertView.findViewById(R.id.currentOrderID);
                currentOrderPrice = convertView.findViewById(R.id.currentOrderPrice);

                pickedUpOrderView = convertView.findViewById(R.id.PickedUpOrderRL);
                orderPickBtn = convertView.findViewById(R.id.pickedOrderYes);
                orderPickedTV = convertView.findViewById(R.id.orderPickedTV);

                //Current Order Status Text View//
                stat1 = convertView.findViewById(R.id.stat1);
                stat2 = convertView.findViewById(R.id.stat2);
                stat3 = convertView.findViewById(R.id.stat3);
                stat4 = convertView.findViewById(R.id.stat4);

                ///Current order status view(balls)//
                stat1view = convertView.findViewById(R.id.statball1);
                stat2view = convertView.findViewById(R.id.statball2);
                stat3view = convertView.findViewById(R.id.statball3);
                stat4view = convertView.findViewById(R.id.statball4);

                currentOrderID.setShadowLayer(10, 10, 10, Color.parseColor("#52BAB8B8"));

                final Handler handler = new Handler();
                final View finalConvertView = convertView;

                final Handler handler1 = new Handler();
//                handler1.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        finalConvertView.setAlpha(0f);
                        finalConvertView.setVisibility(View.VISIBLE);
                        finalConvertView.animate()
                                .alpha(1f)
                                .setDuration(shortAnimationDuration)
                                .setListener(null);
//                                            if (!orderStatus.get(position).equals("Done")) {

                        if (position < locations.size() && position < shopNames.size() && position < orderStatus.size() && position < price.size() && position < orderDate.size() && position < orderkey.size()) {


                            currentOrderID.setText("ID: " + orderID.get(position));
                            currentOrderShopLoc.setText(locations.get(position));
                            currentOrderShopName.setText(shopNames.get(position));
                            currentOrderPrice.setText("Price : ₹ " + price.get(position));
                            currentOrderDateTime.setText(orderDate.get(position));
                            currentOrderStatus.setText(orderStatus.get(position));


//                            currentOrderID.setText("ID " + orderkey.get(orderkey.size()-(position+1)));
//                            currentOrderShopLoc.setText(locations.get(locations.size()-(position+1)));
//                            currentOrderShopName.setText(shopNames.get(shopNames.size()-(1+position)));
//                            currentOrderPrice.setText("Price : ₹ " + price.get(price.size()-(1+position)));
//                            currentOrderDateTime.setText(orderDate.get(orderDate.size()-(1+position)));
//                            currentOrderStatus.setText(orderStatus.get(orderStatus.size()-(1+position)));

                            if ((orderStatus.get(position)).equals("Placed")) {
//                                currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview1);

                            }

                            if ((orderStatus.get(position)).equals("Received")) {
//                                currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview2);
                                stat1.setTextColor(Color.parseColor("#227093"));
                                stat1view.setBackgroundResource(R.drawable.orderstatballs);

                            }

                            if ((orderStatus.get(position)).equals("In Progress")) {
//                                currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview3);
                                stat2.setTextColor(Color.parseColor("#227093"));
                                stat2view.setBackgroundResource(R.drawable.orderstatballs);
                            }

                            if ((orderStatus.get(position)).equals("Ready")) {
//                                currentOrderStatus.setBackgroundResource(R.drawable.orderstatusview4);
                                stat3.setTextColor(Color.parseColor("#227093"));
                                stat3view.setBackgroundResource(R.drawable.orderstatballs);


                                Log.d("HEIGHTIS ",String.valueOf(CurrentOrderRowRL.getHeight()));
                                expandView(CurrentOrderRowRL,0,900);
                                expandView(pickedUpOrderView,0,300);

//                                shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
//                                pickedUpOrderView.setAlpha(0f);
//                                pickedUpOrderView.setVisibility(View.VISIBLE);
//                                pickedUpOrderView.animate()
//                                        .alpha(1f)
//                                        .setDuration(shortAnimationDuration)
//                                        .setListener(null);

                                orderPickBtn.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onClick(View v) {
                                        final HashMap<String, Object> orderStatusUpdate = new HashMap<String, Object>();
                                        orderStatusUpdate.put("orderStatus", "Done");
                                        ref.child("users").child(userId).child("Orders").child(orderkey.get(position)).updateChildren(orderStatusUpdate);

                                        orderPickedTV.setText("Thank you for using Sleefax. 😄 \n We hope you had a great experience.");
                                        orderPickBtn.setVisibility(View.INVISIBLE);

                                        stat4.setTextColor(Color.parseColor("#227093"));
                                        stat4view.setBackgroundResource(R.drawable.orderstatballs);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                getOrders();
                                                getOrderCntForOrderHistory();
                                                getCurrentOrderDetails();
                                            }
                                        }, 2000);

                                    }
                                });
                            }
                        }
//                       }
//                    }
//                }, 100);

                finalConvertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position < locations.size() && position < shopNames.size() && position < orderStatus.size() && position < price.size() && position < orderDate.size() && position < orderkey.size() && position < orderID.size() && position < files.size()) {
                            moreDetails(shopKey.get(position), orderkey.get(position), shopNames.get(position), shopLat.get(position), shopLong.get(position), locations.get(position), files.get(position), orderStatus.get(position), price.get(position), orderID.get(position));
                        }

                    }
                });


            }
            return convertView;
        }
    }


}

