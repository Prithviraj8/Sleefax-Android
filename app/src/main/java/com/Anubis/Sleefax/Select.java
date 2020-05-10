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
import android.os.Handler;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Anubis.Sleefax.CONSTANTS.CONSTANTS;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    Button orders,orderPickBtn;
    ImageButton selectFilesBtn,more ,setting,sideMenu,selectPhotos,selectAttachment;

    View pickedUpOrderView,addFileView,blurrView;

    int PICK_IMAGE_MULTIPLE = 1;
    final static int PICK_PDF_CODE = 2342;
    final static int PICK_IMAGE_CODE = 100;
    int orderCnt = 0,mScreenHeight;

    String imageEncoded;
    List<String> imagesEncodedList;
    private boolean isMenuShown = false;
    int cnt;
    boolean network,isTester,newUser;
    PaytmPGService Service = PaytmPGService.getProductionService();

    RelativeLayout contactsRl,addfilePage,addfileTVRL;

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



    //////// Live orders Listview /////////
    ListView listView;
    currentOrderAdapter adapter;

    //    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_files);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;

        //View that will come from the top of the screen when user clicks on add file btn///
        addfileTVRL = findViewById(R.id.AddfileTVRL);
        blurrView = findViewById(R.id.blurr);

        //Buttons for image and files
        selectAttachment = findViewById(R.id.SelectFile);
        selectPhotos = findViewById(R.id.SelectImage);

        addFileView = findViewById(R.id.liveordersTopView);
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        selectFilesBtn = findViewById(R.id.AddFilesButton);

        setting = findViewById(R.id.settings);
        orders = findViewById(R.id.YourOrders);
        sideMenu = findViewById(R.id.SideMenu);
        more = findViewById(R.id.more);
        contactsRl = findViewById(R.id.contactsRelativeL);
        addfilePage = findViewById(R.id.addfilesRL);
        back = findViewById(R.id.contactspageback);

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
                orders.setOnClickListener(Listener);
                if(FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
                    if (FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("tester") || FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("Tester")) {
                        isTester = true;
                    } else {
                        isTester = false;
                    }
                }else{
                    if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).contains("909")){
                        isTester = true;
                    }else {
                        isTester = false;
                    }

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
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.RefreshLayout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("REFRESHING", "onRefresh called from SwipeRefreshLayout");

//                getOrders();
                currentOrderLV();
                pullToRefresh.setRefreshing(false);

            }
        });

        selectFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles(view);



                addfileTVRL.setVisibility(View.VISIBLE);
                blurrView.setVisibility(View.VISIBLE);

                if(addfileTVRL.getHeight() == 0) {
                    expandView(addfileTVRL, 0, mScreenHeight / 4);
//                    selectFilesBtn.animate()
//                            .alpha(0f)
//                            .setDuration(100)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    super.onAnimationStart(animation);
//                                    selectFilesBtn.setAlpha(1f);
//
//                                }
//                            });
                }else{
//                    selectFilesBtn.setBackgroundResource(R.drawable.addfilebtnshadow);
                }



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




        ////setting size of add file view to full page if cnt == 0  ////////
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                setupLayoutParams();
            }
        },200);


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
    private void selectFiles(final View v) {
        final CoordinatorLayout mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        int transitionId;
        Intent pop = new Intent(Select.this,Pop.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsTester",isTester);
        bundle.putBoolean("NewUser",newUser);
        pop.putExtras(bundle);
        startActivity(pop);


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
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.openmenublack,getTheme());
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

                        contactsRl.setVisibility(View.VISIBLE);
                        addfilePage.setVisibility(View.INVISIBLE);

                        num1 = findViewById(R.id.num1);
                        num2 = findViewById(R.id.num2);

                        num1.setOnClickListener(contactsPageListener);
                        num2.setOnClickListener(contactsPageListener);
                        back.setOnClickListener(contactsPageListener);

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
//                CharSequence text = "No order history to show.";
                CharSequence text = "Order Cnt"+orderCnt;
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);

                Intent orderHistoryIntent = new Intent(Select.this, YourOrders.class);
                startActivity(orderHistoryIntent);
                finish();
                mProgressDialog.dismiss();

            }
        }
    };



     ArrayList<String> orderkey = new ArrayList<>();
     ArrayList<String> shopKey = new ArrayList<>();
     ArrayList<String> shopNames = new ArrayList<>();
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
                        shopKey.add(String.valueOf(map.get("storeId")));


                        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
                        sharedPreferences.edit().putInt("LiveOrderCnt",cnt).apply();

                        CONSTANTS obj = new CONSTANTS();
                        obj.cnt = cnt;



//                        setupLayoutParams();
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
                Log.d("NEWORDERMAP",String.valueOf(orderKeys.getValue()));
                for(DataSnapshot values: orderKeys.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) values.getValue();

                    if (map != null) {
                        Log.d("MAPVAL", String.valueOf(map));
                        if (!String.valueOf(map.get("orderStatus")).equals("Done")) {
//                            cnt = (int) orderKeys.getChildrenCount();

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
//         * @param name Used to name the worker thread, important only for debugging.
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
                    for (DataSnapshot orderKeys : dataSnapshot.getChildren()) {

//                            Log.d("ORDERID",order.getKey());



                            boolean RTnotifyStatus = true;
                            boolean IPnotifyStatus = true;
                            boolean RnotifyStatus = true,doneNotifyStatus = true;

                            Map<String, Object> map = (Map<String, Object>) orderKeys.getValue();

//                            orderDateTime = String.valueOf(map.get("orderDateTime"));
//                            price = Double.parseDouble(String.valueOf(map.get("price")));
//                            shopName = String.valueOf(map.get("ShopName"));
//                            shopLat = Double.parseDouble(String.valueOf(map.get("ShopLat")));
//                            shopLong = Double.parseDouble(String.valueOf(map.get("ShopLong")));
//                            loc = String.valueOf(map.get("ShopsLocation"));
//                            files = Integer.parseInt(String.valueOf(map.get("files")));



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



    public void moreDetails(String shopKey,String orderKey,String shopName,Double shopLat, Double shopLong, String loc,int files,String orderStatus, Double price){

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


                            currentOrderID.setText("ID " + orderkey.get(position));
                            currentOrderShopLoc.setText(locations.get(position));
                            currentOrderShopName.setText(shopNames.get(position));
                            currentOrderPrice.setText("Price : ₹ " + price.get(position));
                            currentOrderDateTime.setText(orderDate.get(position));
                            currentOrderStatus.setText(orderStatus.get(position));

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
                        if (position < locations.size() && position < shopNames.size() && position < orderStatus.size() && position < price.size() && position < orderDate.size() && position < orderkey.size()) {
                            moreDetails(shopKey.get(position), orderkey.get(position), shopNames.get(position), shopLat.get(position), shopLong.get(position), locations.get(position), files.get(position), orderStatus.get(position), price.get(position));
                        }
                    }
                });


            }
            return convertView;
        }
    }
}

