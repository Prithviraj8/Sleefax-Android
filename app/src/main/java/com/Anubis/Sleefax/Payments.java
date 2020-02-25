package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;
import java.util.UUID;

public class Payments extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DatabaseReference storeDb = FirebaseDatabase.getInstance().getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    PaytmPGService Service = PaytmPGService.getProductionService();


    String name,loc,orderKey,orderStatus,shopKey,fileType,pagesize,orientation,username,email;
    LatLng shopLoc, userLoc;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    long shopNum;
    int files;
    double price;
    int copy;
    int resultCode;
    int requestCode;
    double numberOfPages;
    String color,custom,orderDateTime;
    String CHANNEL_ID = "UsersChannel",shopType;
    boolean FromYourOrders =false, bothSides,isTester;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> downloadUrls = new ArrayList<>();

    long usernum;
    Button paytm,card,payOnPickup,upi;
    TextView amount,tv;
    ProgressBar mProgress;
    View view3,orderProcessAnime;


    ArrayList<String> pageURL = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
//        getSupportActionBar().hide();


        orderProcessAnime = findViewById(R.id.orderProcessAnime);
        view3 = findViewById(R.id.view3);
        mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        tv = findViewById(R.id.tv);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //////////////////////////////////////////////////Shop Info//////////////////////////////////////////
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        name = extras.getString("ShopName");
        loc = extras.getString("Location");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
        price = extras.getDouble("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        shopKey = extras.getString("ShopKey");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");


        /////////////////////////////////////////////////Order info////////////////////////////////////////


//        orderKey = extras.getString("OrderKey");
        fileType = extras.getString("FileType");
        pagesize = extras.getString("PageSize");
        orientation = extras.getString("Orientation");
        username = extras.getString("Username");
        email = extras.getString("email");
        usernum = extras.getLong("UserNumber");
        shopNum = extras.getLong("ShopNum");
        urls = extras.getStringArrayList("URLS");
        copy = extras.getInt("Copies");
        color = extras.getString("ColorType");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
//        pageURL = extras.getStringArrayList("URLS");
        bothSides = extras.getBoolean("BothSides");
        custom = extras.getString("Custom");
        numberOfPages = extras.getDouble("Pages");
        isTester = extras.getBoolean("IsTester");

        if(isTester){
            shopType = "TestStores";
        }else{
            shopType = "Stores";
        }


//        paytm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                paytmPay(orderKey, userID,num,CHANNEL_ID,price,Service);
//
//
//            }
//        });
        paytm = findViewById(R.id.paytm);
        upi = findViewById(R.id.upi);
        payOnPickup = findViewById(R.id.pickup);
        card = findViewById(R.id.card);
        amount = findViewById(R.id.amount);
        amount.setText(("₹"+price));

        upi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingUpi(String.valueOf(price),String.valueOf(shopNum)+"@paytm","Order","");
            }
        });


        payOnPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setVisibilities();
            }
        });

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


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(Payments.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

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

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(Payments.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                android.util.Log.d("UPI", "responseStr: "+approvalRefNo);

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

            sendData();

        }

    }

    public void setVisibilities(){
        payOnPickup.setVisibility(View.INVISIBLE);
        upi.setVisibility(View.INVISIBLE);
        card.setVisibility(View.INVISIBLE);
        paytm.setVisibility(View.INVISIBLE);

        view3.setVisibility(View.VISIBLE);
        orderProcessAnime.setVisibility(View.VISIBLE);


        Resources res = getResources();
        Drawable drawable;
        drawable = res.getDrawable(R.drawable.circular);

        mProgress.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);

        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

        //Gathering ids of pervious orders
        getId();
    }

    public void sendData(){
//        view3.setVisibility(View.GONE);
//        orderProcessAnime.setVisibility(View.GONE);
//        mProgress.setVisibility(View.GONE);
//        tv.setVisibility(View.GONE);

        Intent intent = new Intent(Payments.this, OrderPlaced.class);
        Bundle extras = new Bundle();

        extras.putStringArrayList("URLS", urls);
        extras.putString("ShopName", name);
        extras.putString("Location", loc);
        extras.putDouble("ShopLat", shopLat);
        extras.putDouble("ShopLong", shopLong);
        extras.putInt("Files", files);
        extras.putDouble("Price", price);
        android.util.Log.d("PRICE", String.valueOf(price));
        extras.putString("FileType", fileType);
        extras.putString("PageSize", pagesize);
        extras.putString("Orientation", orientation);
        extras.putBoolean("IsTester", isTester);
        extras.putLong("ShopNum", shopNum);

        if (username != null && email != null && usernum > 0) {
            extras.putString("Username", username);
            extras.putString("email", email);
            extras.putLong("UserNumber", usernum);
        }

        extras.putInt("Copies", copy);
        extras.putString("ColorType", color);
        extras.putBoolean("BothSides", bothSides);
        extras.putString("Custom", custom);
        extras.putString("OrderKey", orderKey);
        extras.putString("ShopKey", shopKey);
//        extras.putString("UserID", userID);
        extras.putDouble("User Lat", userLat);
        extras.putDouble("User Long", userLong);
        extras.putInt("RequestCode", requestCode);
        extras.putInt("ResultCode", resultCode);
        extras.putDouble("Pages", numberOfPages);

        intent.putExtras(extras);
        startActivity(intent);


    }


    ArrayList<Integer> ids = new ArrayList<>();
    public void getId(){



        ref.child(shopType).child(shopKey).child("Orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                android.util.Log.d("cust",dataSnapshot.getKey());

                for(DataSnapshot orders: dataSnapshot.getChildren()){
                    for(DataSnapshot values: orders.getChildren()) {
                        if (values.getKey().equals("id")) {
                            ids.add(Integer.parseInt(values.getValue().toString()));
//                            android.util.Log.d("IDS",values.getValue().toString());
                            Collections.sort(ids);

                        }
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

        new uploadFile().execute();

    }

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
            Log.d("UPLOADINGGGG","DATA");
            Uri uri;
//            statusPercent.setText("15%");
//            final ArrayList<String> urls = pageURL;

            for (int i = 0; i < urls.size(); i++) {
                String file = urls.get(i);
                uri = Uri.parse(file);

                final String uniqueID = UUID.randomUUID().toString();
                final StorageReference filesRef = storageRef.child(uniqueID);

//        if (Build.VERSION.SDK_INT < 19) {

//      getContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                final UploadTask uploadTask = filesRef.putFile(uri);
//        final UploadTask uploadTask = filesRef.putFile(changeExtension(new File(file.getPath()),"pdf"));
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

                        progressAnimator[0] = ObjectAnimator.ofInt(mProgress,"Progress",0,30);
                        progressAnimator[0].setDuration(2000);
                        progressAnimator[0].start();

//                        progressAnimator[0] = ObjectAnimator.ofInt(tv,"ProgressTV",3,35);
//                        progressAnimator[0].setDuration(2000);
//                        progressAnimator[0].start();
                        tv.setText(30 + "%");

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        android.util.Log.d("UPLOAD", "SUCCESSFULL");
                        Toast.makeText(Payments.this, "Files are being sent", Toast.LENGTH_SHORT).show();

                        progressAnimator[0].end();

                        android.util.Log.d("UNIQUE", uniqueID);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                android.util.Log.d("URIID", String.valueOf(finalUri));

                                progressAnimator[0] = ObjectAnimator.ofInt(mProgress,"Progress",30,55);
                                progressAnimator[0].setDuration(2000);
                                progressAnimator[0].start();

                                tv.setText(55 + "%");
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

                                    progressAnimator[0].end();

//                                    mProgress.setProgress(65);
//                                    tv.setText(65 + "%");
                                    if (urls.size() == downloadUrls.size()) {


//                                        Log.d("PAGESIZEGEE",pagesize);
//                                        Log.d("CUSTOM",custom);
//                                        Log.d("ORIENTATION",orientation);
//                                        Log.d("COLOR",color);
//                                        Log.d("COPY", String.valueOf(copy));
                                        String orderID = UUID.randomUUID().toString();


                                        int id = 0;
                                        if (ids.size() > 0) {
//                                            Log.d("IDS",String.valueOf(ids.get(2)));
                                            id = ids.get(ids.size() - 1) + 1;
                                        }

                                        if (custom == "" || custom == null) {
                                            custom = "All";
                                        }

                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                        android.util.Log.d("ORDERPBOTHSIDES", String.valueOf(bothSides));
                                        shopinfo orderInfo = new shopinfo(loc, name, "Placed", shopLat, shopLong, shopNum, files, fileType, pagesize, orientation, price, custom, orderDateTime, false, false, false, false);
                                        info userinfo = new info(username, email, usernum, "android", "Placed", fileType, copy, orderDateTime, id, custom, price, bothSides);

                                        String storeID = shopKey;

                                        storeDb = storeDb.child(shopType).child(storeID).child("Orders").child(userId).child(orderID);
                                        storeDb.setValue(userinfo);
                                        db = db.child("users").child(userId).child("Orders").child(storeID).child(orderID);
                                        db.setValue(orderInfo);
//                                        orderid.setText("Order ID: "+orderID);


                                        for (int k = 0; k < downloadUrls.size(); k++) {

                                            progressAnimator[0] = ObjectAnimator.ofInt(mProgress,"Progress",55,75);
                                            progressAnimator[0].setDuration(2000);
                                            progressAnimator[0].start();
                                            tv.setText(75 + "%");


                                            singlePageInfo single = new singlePageInfo(downloadUrls.get(k), color, copy, fileType, pagesize, orientation);
                                            db.push().setValue(single);
                                            storeDb.push().setValue(single);
                                            orderKey = orderID;


                                            if (k == urls.size() - 1) {
                                                Toast.makeText(Payments.this, "Files are being sent", Toast.LENGTH_SHORT).show();
//                                                new setProgressForOrder().execute(orderKey);
//                                                setProgressForOrder(orderKey);
                                                progressAnimator[0].end();

                                                progressAnimator[0] = ObjectAnimator.ofInt(mProgress,"Progress",75,100);
                                                progressAnimator[0].setDuration(2000);
                                                progressAnimator[0].start();
                                                progressAnimator[0].end();
                                                tv.setText(100 + "%");
                                                showNotification(orderKey);



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



    public void paytmPay(String orderID, String userID, Long num, String CHANNEL_ID, int price, PaytmPGService Service) {

        Log.d("Payment","PAYTM");

        HashMap<String, String> paramMap = new HashMap<String,String>();
        paramMap.put( "MID" , "EyJcsf77777626853128");
// Key in your staging and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , orderID);
        paramMap.put( "CUST_ID" , userID);
        paramMap.put( "MOBILE_NO" , String.valueOf(num));
        paramMap.put( "EMAIL" , "username@emailprovider.com");
        paramMap.put( "CHANNEL_ID" , "WAP");
        paramMap.put( "TXN_AMOUNT" , "100.12");
        paramMap.put( "WEBSITE" , "WEBSTAGING");
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=order1");
        paramMap.put( "CHECKSUMHASH" , "w2QDRMgp1234567JEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4=");
        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);

        PaytmClientCertificate Certificate = new PaytmClientCertificate( "1234567",  "File");
        Service.initialize(Order, Certificate);

        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
            /*Call Backs*/
            public void someUIErrorOccurred(String inErrorMessage) {}
            public void onTransactionResponse(Bundle inResponse) {}
            public void networkNotAvailable() {}
            public void clientAuthenticationFailed(String inErrorMessage) {}
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {}
            public void onBackPressedCancelTransaction() {}
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {}
        });


        /* initialize a TreeMap object */
        TreeMap<String, String> paytmParams = new TreeMap<String, String>();

        /* put checksum parameters in TreeMap */
        paytmParams.put("MID", "EyJcsf77777626853128");
        paytmParams.put("ORDERID", orderID);

/**
 * Generate checksum by parameters we have
 * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
 */
//        try{
//            String checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("u8PqHNTiQrHlIPXs", paytmParams);
//            paramMap.put("CHECKSUMHASH" , checkSum);
//
//            System.out.println("Paytm Payload: "+ paramMap);
//
//
//            /* string we need to verify against checksum */
//            String body = "{\"mid\":\"EyJcsf77777626853128\",\"orderId\":\"YOUR_ORDER_ID_HERE\"}";
//
//            /* checksum that we need to verify */
//            String checksum = "CHECKSUM_VALUE";
//
///**
// * Verify Checksum
// * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
// */
//            boolean isValidChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum("YOUR_KEY_HERE", body, checksum);
//            if (isValidChecksum) {
//                System.out.append("Checksum Matched");
//            } else {
//                System.out.append("Checksum Mismatched");
//            }
//
//        }catch(Exception e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }


}
