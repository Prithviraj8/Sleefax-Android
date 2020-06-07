package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Anubis.Sleefax.Animations.ProgressBarAnimation;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
//import com.paytm.pg.merchant.CheckSumServiceHelper;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paytm.pgsdk.Log;
import com.paytm.pgsdk.PaytmClientCertificate;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import ng.max.slideview.SlideView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class info{

    public String name,orderStatus;
    public String email,device,orderDateTime,paymentMode,userId,orderID,otp;
    public long num;
    public int id,files;
    public double price;
    public boolean confirm,otpUsed;

    public info(String name, String email, long num, String device, String orderStatus,String orderDateTime, int id, double price,String paymentMode, String userId, int files,boolean confirm,String orderID, String otp, boolean otpUsed){
        this.email = email;
        this.name = name;
        this.num = num;
        this.device = device;
        this.orderStatus = orderStatus;
        this.orderDateTime = orderDateTime;
        this.id = id;
        this.price = price;
        this.paymentMode = paymentMode;
        this.userId = userId;
        this.files = files;
        this.confirm = confirm;
        this.orderID = orderID;
        this.otp = otp;
        this.otpUsed = otpUsed;
    }
//    public info(String name, String email, long num, String device, String placed, String fileType, int copy, String orderDateTime, int id, String custom, double price, boolean bothSides, String paymentMode,String userId){
//        this.email = email;
//        this.name = name;
//        this.num = num;
//        this.device = device;
//        this.orderStatus = placed;
//        this.fileType = fileType;
//        this.copies = copy;
//        this.orderDateTime = orderDateTime;
//        this.id = id;
//        this.custom = custom;
//        this.price = price;
//        this.bothSides = bothSides;
//        this.paymentMode = paymentMode;
//        this.userId = userId;
//    }

}
public class Payments extends AppCompatActivity implements PaymentResultListener {

    public String SharedPrefs = "Data";


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DatabaseReference storeDb = FirebaseDatabase.getInstance().getReference();
    String userId;


    PaytmPGService Service = PaytmPGService.getProductionService();
    private static final String TAG = MainActivity.class.getSimpleName();
    private int shortAnimationDuration;


    String shopName,loc,orderKey,orderStatus,shopKey,fileType,pagesize,orientation,username,email;
    LatLng shopLoc, userLoc;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    long shopNum;
    int files;
    double price;
    int mScreenHeight;

    String color,custom,orderDateTime,paymentMode,paymentUPImode;
    String CHANNEL_ID = "UsersChannel",shopType;
    boolean FromYourOrders =false,isTester,newUser;
    ArrayList<String> downloadUrls = new ArrayList<>();

    long usernum;
    RelativeLayout paytm,otherPayments,payOnPickup,upi,gpay,phonepay,topView;
    RelativeLayout slidelayout;
    int idOfPaymentMode;
    TextView amount,tv;
    ProgressBar mProgress;
    View view3,orderProcessAnime;
    ImageButton back;
    RelativeLayout relativeLForUPI,progressRL;
    ArrayList<String> pageURL = new ArrayList<>();

    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<String> customValues = new ArrayList<>();
    ArrayList<Integer> numberOfPages = new ArrayList<>();

    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> fileSizes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
//        getSupportActionBar().hide();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;


        //    orderProcessAnime = findViewById(R.id.orderProcessAnime);
        // view3 = findViewById(R.id.view3);
        mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        tv = findViewById(R.id.tv);
        back = findViewById(R.id.paymentBackBtn);
        //  relativeLForUPI = findViewById(R.id.relativeLForUPI);
        gpay = findViewById(R.id.gpay);
        progressRL = findViewById(R.id.LoadingScreenRL);

        phonepay = findViewById(R.id.phonepay_upi);
        topView = findViewById(R.id.view);



        getOrderInfo();

        if(!newUser) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getCurrentUserInfo();
            Log.d("ISTESTER",String.valueOf(isTester));

            if(isTester){
                shopType = "TestStores";
            }else{
                shopType = "Stores";
            }
            //Gathering ids of pervious orders

            getId();


        }else{

            sendData();
        }

        paytm = findViewById(R.id.paytm);
        //  upi = findViewById(R.id.upi);
        payOnPickup = findViewById(R.id.pickup);
        //    otherPayments = findViewById(R.id.otherPayments);
        amount = findViewById(R.id.amount);
        amount.setText(""+price);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

       /* upi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                relativeLForUPI.setAlpha(0f);
                relativeLForUPI.setVisibility(View.VISIBLE);
                relativeLForUPI.animate()
                        .alpha(1f)
                        .setDuration(shortAnimationDuration + 500)
                        .setListener(null);
//                easyUPI(String.valueOf(price),"7875210665"+"@paytm","Order");


            }
        });*/




        payOnPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idOfPaymentMode = 1;

                slidelayout.setVisibility(View.VISIBLE);
                Drawable drawable = getResources().getDrawable(R.drawable.outline);
                payOnPickup.setBackground(drawable);
                Drawable drawable1 = getResources().getDrawable(R.drawable.white_bg_layout);
                phonepay.setBackground(drawable1);
                paytm.setBackground(drawable1);
                gpay.setBackground(drawable1);

                // paymentMode = "Pay on Pickup";
                // setVisibilities();
            }
        });
        phonepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idOfPaymentMode = 2;

                slidelayout.setVisibility(View.VISIBLE);
                Drawable drawable = getResources().getDrawable(R.drawable.outline);
                phonepay.setBackground(drawable);
                Drawable drawable1 = getResources().getDrawable(R.drawable.white_bg_layout);
                payOnPickup.setBackground(drawable1);
                paytm.setBackground(drawable1);
                gpay.setBackground(drawable1);

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idOfPaymentMode = 3;

                slidelayout.setVisibility(View.VISIBLE);
                Drawable drawable = getResources().getDrawable(R.drawable.outline);
                paytm.setBackground(drawable);
                Drawable drawable1 = getResources().getDrawable(R.drawable.white_bg_layout);
                payOnPickup.setBackground(drawable1);
                phonepay.setBackground(drawable1);
                gpay.setBackground(drawable1);
                // paymentUPImode = "paytm";
                //payUsingUpi(String.valueOf(price),"7875210665"+"@paytm","Order","");
            }
        });

        gpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idOfPaymentMode = 4;
                slidelayout.setVisibility(View.VISIBLE);
                Drawable drawable = getResources().getDrawable(R.drawable.outline);
                gpay.setBackground(drawable);
                Drawable drawable1 = getResources().getDrawable(R.drawable.white_bg_layout);
                payOnPickup.setBackground(drawable1);
                paytm.setBackground(drawable1);
                phonepay.setBackground(drawable1);

                // paymentUPImode = "com.google.android.apps.nbu.paisa.user";
                //payUsingUpi(String.valueOf(price),"7875210665"+"@paytm","Order","");
            }
        });


        slidelayout = findViewById(R.id.slideviewlayout);

        SlideView slideView = findViewById(R.id.slideView);
        slideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {

                switch (idOfPaymentMode){
                    case 1:
                        paymentMode = "Pay on Pickup";
                        setVisibilities();
                        break;
                    case 2:
                        Toast.makeText(Payments.this, "PhonePay to be added sson..", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        paymentUPImode = "paytm";
                        payUsingUpi(String.valueOf(price),"7875210665"+"@paytm","Order","");
                        break;
                    case 4:
                        paymentUPImode = "com.google.android.apps.nbu.paisa.user";
                        payUsingUpi(String.valueOf(price),"7875210665"+"@paytm","Order","");
                        break;



                }

            }
        });




//        Checkout.preload(getApplicationContext());
//        otherPayments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startPayment();
//            }
//        });

    }


    private void getCurrentUserInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs,0);
        username = sharedPreferences.getString("DisplayName",null);
        email = sharedPreferences.getString("Email",null);
        usernum = sharedPreferences.getLong("UserNumber",0);

    }

    public void getOrderInfo(){
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //////////////////////////////////////////////////Shop Info//////////////////////////////////////////
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        shopName = extras.getString("ShopName");
        loc = extras.getString("Location");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
        price = extras.getDouble("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        shopKey = extras.getString("ShopKey");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");


        /////////////////////////////////////////////////Order info////////////////////////////////////////




        shopNum = extras.getLong("ShopNum");
        fileNames = extras.getStringArrayList("FileNames");
        fileSizes = extras.getStringArrayList("FileSizes");

        urls = extras.getStringArrayList("URLS");
        fileTypes = extras.getStringArrayList("FileType");
        pageSize = extras.getStringArrayList("PageSize");
        orientations = extras.getStringArrayList("Orientation");
        copies = extras.getIntegerArrayList("Copies");
        colors = extras.getStringArrayList("ColorType");
        bothSides = extras.getBooleanArray("BothSides");
        customPages = extras.getStringArrayList("Custom");
        numberOfPages = extras.getIntegerArrayList("Pages");

        isTester = extras.getBoolean("IsTester");
        newUser = extras.getBoolean("NewUser");
        Toast.makeText(this, "FILETPYE "+fileType, Toast.LENGTH_SHORT).show();

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

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setImage(R.drawable.appicon);
//        co.setFullScreenDisable(true);


        try {
            JSONObject options = new JSONObject();
            options.put("name", "Sleefax");
            options.put("description", "Order amount");
            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */

            options.put("amount", (int) (price)+"00");

            JSONObject preFill = new JSONObject();
            preFill.put("email", email);
            preFill.put("contact", "91"+usernum);

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            paymentMode = "Online";
            setVisibilities();
        } catch (Exception e) {
            android.util.Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            android.util.Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


    final int UPI_PAYMENT = 0;
    void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();




//        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
//        upiPayIntent.setData(uri);
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
//        upiPayIntent.setType("text/plain");

        /////Limiting number of apps /////
        List<Intent> targetShareIntents=new ArrayList<Intent>();
        List<ResolveInfo> resInfos=getPackageManager().queryIntentActivities(upiPayIntent, 0);
        if(!resInfos.isEmpty()) {
            System.out.println("Have package");
            for(ResolveInfo resInfo : resInfos){
                String packageName=resInfo.activityInfo.packageName;
                Log.i("Package Name", packageName);


                if(packageName.contains(paymentUPImode)){
                    Intent intent=new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_TEXT, "Text");
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    intent.setData(uri);
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if(!targetShareIntents.isEmpty()){
                System.out.println("Have Intent");
                Intent chooserIntent=Intent.createChooser(targetShareIntents.remove(0), "Choose app to pay");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
//                startActivity(chooserIntent);
                startActivityForResult(chooserIntent, UPI_PAYMENT);

            }else{
                System.out.println("Do not Have Intent");
            }
        }

//            // will always show a dialog to user to choose an app
//        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
//
//        // check if intent resolves
//        if(null != chooser.resolveActivity(getPackageManager())) {
//            startActivityForResult(chooser, UPI_PAYMENT);
//        } else {
//            Toast.makeText(Payments.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
//        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        android.util.Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        android.util.Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    android.util.Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(Payments.this)) {
            String str = data.get(0);
            android.util.Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";

            if(str == null) str = "discard";

            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");

            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
//                Log.d("EQUALSTRE",equalStr[i]);
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            Log.d("PAYMENTSTATUS",status);
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(Payments.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                android.util.Log.d("UPI", "responseStr: "+approvalRefNo);
                paymentMode = "UPI";

                /////////////////////////////Placing order////////////////////
                setVisibilities();
            }

            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(Payments.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Payments.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Payments.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public void showNotification(String orderKey){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "Order Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.appicon);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(Payments.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notify)
                    .setLargeIcon(icon)
                    .setContentTitle("Yayy!!😁 Your order has been placed successfully.")
                    .setContentText("Order ID: " + orderKey)
                    .setGroup(CHANNEL_ID)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Order ID: " + orderKey))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(1, builder.build());
//            sendData();
        }
        sendData();

    }





    public void sendData(){

        Intent intent;
        Bundle extras = new Bundle();

        if (newUser) {
            intent = new Intent(Payments.this,SignInActivity.class);
            extras.putBoolean("SignUp",true);
            extras.putBoolean("NewUser",true);
        }else{
            intent = new Intent(Payments.this,OrderPlaced.class);
        }


        extras.putString("ShopName", shopName);
        extras.putString("Location", loc);
        extras.putDouble("ShopLat", shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putInt("Files", files);
        extras.putDouble("Price", price);
        android.util.Log.d("PAYMMPRICE", String.valueOf(price));

        extras.putBoolean("IsTester", isTester);
        extras.putLong("ShopNum", shopNum);

        if (username != null && email != null && usernum > 0) {
            extras.putString("Username", username);
            extras.putString("email", email);
            extras.putLong("UserNumber", usernum);
        }

        extras.putStringArrayList("URLS", urls);
        extras.putIntegerArrayList("Pages", numberOfPages);
        extras.putBooleanArray("BothSides", bothSides);
        extras.putIntegerArrayList("Copies", copies);
        extras.putStringArrayList("ColorType", colors);
        extras.putStringArrayList("FileType", fileTypes);
        extras.putStringArrayList("PageSize", pageSize);
        extras.putStringArrayList("Orientation", orientations);
        extras.putStringArrayList("Custom", customPages);

        extras.putString("OrderKey", orderKey);
        extras.putString("ShopKey", shopKey);
        extras.putDouble("User Lat", userLat);
        extras.putDouble("User Long", userLong);
        extras.putString("PaymentMode",paymentMode);
        extras.putStringArrayList("FileNames",fileNames);
        extras.putStringArrayList("FileSizes",fileSizes);


        intent.putExtras(extras);
        startActivity(intent);


    }


    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<Integer> custOrderIDS = new ArrayList<>();

    public void getId(){

        Log.d("SHOPTYPEID",shopType);
        Log.d("SHOPKEYID",shopKey);
        ref.child(shopType).child(shopKey).child("Orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                for(DataSnapshot orders: dataSnapshot.getChildren()){
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map != null) {
                    if(map.get("id")!=null){
                        ids.add(Integer.parseInt(String.valueOf(map.get("id"))));
                    }
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
        ref.child("users").child(userId).child("Orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map.get("id")!=null) {
                    custOrderIDS.add(Integer.parseInt(String.valueOf(map.get("id"))));
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



    ImageView rocket;
    RoundCornerProgressBar roundCornerProgressBar;
    MediaPlayer ring;
    public void setVisibilities(){

        int topViewInitHT = topView.getLayoutParams().height;
        collapseView(topView,topViewInitHT,0);

        payOnPickup.setVisibility(View.INVISIBLE);
//        upi.setVisibility(View.INVISIBLE);
//        otherPayments.setVisibility(View.INVISIBLE);
        paytm.setVisibility(View.INVISIBLE);

        rocket = findViewById(R.id.rocket);
        roundCornerProgressBar = findViewById(R.id.progressBarCenteredCustom);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(TransitionInflater.from(Payments.this).inflateTransition(R.transition.slide_from_bottom));
        }

//        view3.setVisibility(View.VISIBLE);
//        view3.setAlpha(0f);
//        view3.setVisibility(View.VISIBLE);
//        view3.animate()
//                .alpha(1f)
//                .setDuration(shortAnimationDuration)
//                .setListener(null);
//
//        orderProcessAnime.setAlpha(0f);
//        orderProcessAnime.setVisibility(View.VISIBLE);
//        orderProcessAnime.animate()
//                .alpha(1f)
//                .setDuration(shortAnimationDuration)
//                .setListener(null);
//
////        orderProcessAnime.setVisibility(View.VISIBLE);
//
//        Resources res = getResources();
//        Drawable drawable;
//        drawable = res.getDrawable(R.drawable.circular);
//
//
//        mProgress.setAlpha(0f);
//        mProgress.setVisibility(View.VISIBLE);
//        mProgress.animate()
//                .alpha(1f)
//                .setDuration(shortAnimationDuration)
//                .setListener(null);




//        mProgress.setProgress(0);   // Main Progress
//        mProgress.setSecondaryProgress(100); // Secondary Progress
//        mProgress.setMax(100); // Maximum Progress
//        mProgress.setProgressDrawable(drawable);

        expandView(progressRL,0,mScreenHeight);
        ring = MediaPlayer.create(Payments.this, R.raw.rocketlaunchaudio);
//        tv.setAlpha(0f);
//        tv.setVisibility(View.VISIBLE);
//        tv.animate()
//                .alpha(1f)
//                .setDuration(shortAnimationDuration)
//                .setListener(null);


        new uploadFile().execute();

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

    int id = 0,custorderID=0;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    public class uploadFile extends AsyncTask<ArrayList<String>,Void,Void> {
        final int[] uploadCnt = {0};


        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(final ArrayList<String>... arrayLists) {

            String currentDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
            orderDateTime = currentTime +" " +currentDate;

            final ObjectAnimator[] progressAnimator = new ObjectAnimator[1];
            Uri uri;
            for (int i = 0; i < urls.size(); i++) {
                final String file = urls.get(i);
                uri = Uri.parse(file);

                final String uniqueID = UUID.randomUUID().toString();
                final StorageReference filesRef;
                Log.d("FILETYPESELECTED",String.valueOf(fileTypes.get(0)));
                if(fileTypes.get(i).contains("document")){
                    filesRef = storageRef.child(uniqueID+".docx");
                    fileTypes.add(i,"DOCX");
                }else if(fileTypes.get(i).contains("powerpoint")){
                    fileTypes.add(i,"PowerPoint");
                    filesRef = storageRef.child(uniqueID+".pptx");
                }else if(fileTypes.get(i).equals("application/msword")){
                    fileTypes.add(i,"Word");
                    filesRef = storageRef.child(uniqueID+".doc");
                } else {
                    fileTypes.add(i,"PDF");
                    filesRef = storageRef.child(uniqueID+".pdf");
                }


                final UploadTask uploadTask = filesRef.putFile(uri);
                final int finalI = i;
                final Uri finalUri = uri;

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        android.util.Log.d("UPLOAD", "Not successfull");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressLint("ObjectAnimatorBinding")
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        android.util.Log.d("UPLOADPROGRESS ", String.valueOf(progress));

//                        progressAnimator[0] = ObjectAnimator.ofInt(mProgress,"Progress", (int) progress, (int) progress);
//                        progressAnimator[0].setDuration(300);
//                        progressAnimator[0].start();

//                        progressAnimator[0] = ObjectAnimator.ofInt(roundCornerProgressBar,"Progress", (int) progress, (int) progress);
//                        progressAnimator[0].setDuration(300);
//                        progressAnimator[0].start();

                        ProgressBarAnimation anim = new ProgressBarAnimation(roundCornerProgressBar, progress, progress);
                        anim.setDuration(1000);

                        roundCornerProgressBar.startAnimation(anim);

//                        }


                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        android.util.Log.d("UPLOAD", "SUCCESSFULL");
                        Toast.makeText(Payments.this, "Files are being sent", Toast.LENGTH_SHORT).show();

//                        progressAnimator[0].end();

                        android.util.Log.d("UNIQUE", uniqueID);
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                uploadCnt[0]++;
                                // Continue with the task to get the download URL
                                return filesRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()) {
                                    String url;
                                    Uri downloadUri = task.getResult();
                                    url = String.valueOf(downloadUri);
                                    downloadUrls.add(url);
                                    android.util.Log.d("DOWNLOADURL", String.valueOf(url));

                                    if (urls.size() == downloadUrls.size()) {

                                        String orderID = UUID.randomUUID().toString();
//                                        orderKey = orderID.substring(orderID.length()-8,orderID.length());

                                        if(custOrderIDS != null && custOrderIDS.size()>0){
                                            Collections.sort(custOrderIDS);
                                            custorderID = (custOrderIDS.get(custOrderIDS.size()-1)) + 1;

                                        }

                                        if(ids != null && ids.size() >0){
                                            Collections.sort(ids);
                                            id = ((ids.get(ids.size()-1)) + 1);
                                        }

//                                        Toast.makeText(Payments.this, "IDSIZE "+id, Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(Payments.this, "CUSORDERIDSIZE "+custOrderIDS.size(), Toast.LENGTH_SHORT).show();

                                        if (custom == "" || custom == null) {
                                            custom = "All";
                                        }

                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                        String storeID = shopKey;
                                        orderID = uniqueID.substring(uniqueID.length()-8);
                                        shopinfo orderInfo = new shopinfo(storeID,loc, shopName, "Placed", shopLat, shopLong, shopNum, files, price, orderDateTime, false, false, false, false, false, paymentMode, custorderID,orderID);

                                        Random random = new Random();
                                        String otp = String.format("%04d", random.nextInt(10000));

                                        info userinfo = new info(username, email, usernum, "android", "Placed", orderDateTime, id,  price, paymentMode,userId,files,false,orderID,otp,false);





//                                        db = db.child("users").child(userId).child("Orders").child(storeID).child(orderKey);
//                                        db = db.child("users").child(userId).child("Orders").child(orderKey);
                                        db = db.child("users").child(userId).child("Orders").push();
                                        db.setValue(orderInfo);

                                        orderKey = db.getKey();

                                        //Savind order on the selected shop
                                        storeDb = storeDb.child(shopType).child(storeID).child("Orders").child(orderKey);
                                        storeDb.setValue(userinfo);

//                                        orderKey = orderKey.substring(orderKey.length()-orderKey.length()+1,orderKey.length());


                                        for (int k = 0; k < downloadUrls.size(); k++) {

                                            eachFileInfo single;
                                            page_INFO pageInfo;


                                            if(fileTypes.get(0).contains("image")){
                                                pageInfo = new page_INFO(downloadUrls.get(k), colors.get(0), copies.get(0), "Image", pageSize.get(0), orientations.get(0));
                                                db.push().setValue(pageInfo);
                                                storeDb.push().setValue(pageInfo);
                                            }else{
                                                if(fileSizes.get(k).length()>=4){
                                                    fileSizes.set(k,fileSizes.get(k)+"MB");
                                                }else {
                                                    fileSizes.set(k,fileSizes.get(k)+"KB");
                                                }
                                                if(fileNames.size() <= k){
                                                    single = new eachFileInfo(downloadUrls.get(k), colors.get(k), copies.get(k), fileTypes.get(k), pageSize.get(k), orientations.get(k),"Unknown File Name",customPages.get(k),fileSizes.get(k));
                                                }else {
                                                    single = new eachFileInfo(downloadUrls.get(k), colors.get(k), copies.get(k), fileTypes.get(k), pageSize.get(k), orientations.get(k), fileNames.get(k), customPages.get(k),fileSizes.get(k));
                                                }
                                                db.push().setValue(single);
                                                storeDb.push().setValue(single);
                                            }


                                            if (k == urls.size() - 1) {
//                                                progressAnimator[0].end();
                                                ///Starting rocket launch audio countdown
                                                if (roundCornerProgressBar.getProgress() == 100) {
//                                                  if(!ring.isPlaying()) {
                                                    Toast.makeText(Payments.this, "Starting audio", Toast.LENGTH_SHORT).show();
//                                                    ring.start();
                                                    launchRocket();
                                                }

//                                                if(!ring.isPlaying()) {
//                                                    showNotification(orderKey);
//                                                }
                                            }
                                        }
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
            return null;

        }
    }


    public void launchRocket(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, -mScreenHeight);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                rocket.setTranslationY(value);
            }
        });

        valueAnimator.setInterpolator(new AccelerateInterpolator(5f));
//        valueAnimator.setInterpolator(new AccelerateInterpolator(2f));
        valueAnimator.setDuration(2000);
        valueAnimator.start();

        /// Proceeding with placing order
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                ring.stop();
                showNotification(orderKey);
            }
        },6000);

    }


    ProgressDialog progress;

    public void startPageCountApi(final Uri returnUri, final String contentType){
        progress = new ProgressDialog(Payments.this);
        progress.setTitle("Uploading");
        progress.setMessage("Please wait...");
        progress.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                android.util.Log.d("STARTINGAPI","YES");
                File f  = new File(returnUri.getPath());
//                    Toast.makeText(MainActivity.this, "PATH "+FilePickerActivity.RESULT_FILE_PATH, Toast.LENGTH_SHORT).show();
//                    File f= new File(data.getDataString());
                String content_type  = contentType;
//                    String content_type  = getMimeType(data.getData().toString());

                String file_path = f.getAbsolutePath();
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type",content_type)
                        .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                        .build();

                Request request = new Request.Builder()
                        .url("https://www.sleefax.com/test/upload.php")
                        .post(request_body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    if(!response.isSuccessful()){
                        throw new IOException("Error : "+response);
                    }

                    progress.dismiss();
                    sendjsonrequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        t.start();

    }

    String url = "https://www.sleefax.com/test/page_count.php";
    String count;
    RequestQueue rq;

    public void sendjsonrequest(){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    count = response.getString("PageCount");
                    android.util.Log.i("PageCount",count);
//                    countText.setText(count);
                    android.util.Log.d("COUNTIS ",String.valueOf(count));
                    android.util.Log.d("RESPONSE ",String.valueOf(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                count = error.getMessage();

            }
        });
    };


}
