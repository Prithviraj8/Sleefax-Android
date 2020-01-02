package com.Prithviraj8.Sleefax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
//import com.paytm.pg.merchant.CheckSumServiceHelper;
import com.paytm.pgsdk.Log;
import com.paytm.pgsdk.PaytmClientCertificate;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.TreeMap;

public class Payments extends AppCompatActivity {
    String CHANNEL_ID = "UsersChannel";


    PaytmPGService Service = PaytmPGService.getProductionService();


    String ShopName,loc,orderKey,orderStatus,storeID,userID;
    LatLng shopLoc, userLoc;
    double shopLat;
    double shopLong;
    double userLat,userLong;
    int files, price;
    boolean FromYourOrders =false;
    long num;

    Button paytm,card,payOnPickup,upi;
    TextView amount;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        getSupportActionBar().hide();


        paytm = findViewById(R.id.paytm);
        upi = findViewById(R.id.upi);
        payOnPickup = findViewById(R.id.pickup);
        card = findViewById(R.id.card);
        amount = findViewById(R.id.amount);
        amount.setText((""+price));

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        shopLat = extras.getDouble("ShopLat");
        shopLong = extras.getDouble("ShopLong");
        ShopName = extras.getString("ShopName");
        loc = extras.getString("Location");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
        price = extras.getInt("Price");
        FromYourOrders = extras.getBoolean("FromYourOrders");
        storeID = extras.getString("ShopKey");
        orderKey = extras.getString("OrderKey");
        userLat = extras.getDouble("User Lat");
        userLong = extras.getDouble("User Long");
        userID = extras.getString("UserID");
        num = extras.getLong("Number");


//        paytm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                paytmPay(orderKey, userID,num,CHANNEL_ID,price,Service);
//
//
//            }
//        });


        payOnPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Payments.this,OrderPlaced.class);
                Bundle extras = new Bundle();
//        ShopInfo info = new ShopInfo();
//        info.latitude = shopLat;
//        info.longitude = shopLong;

                extras.putString("ShopName",ShopName);
                extras.putString("Location",loc);
                extras.putString("OrderKey",orderKey);
                extras.putDouble("ShopLat",shopLat);
                extras.putDouble("ShopLong", shopLong);
                extras.putInt("Files",files);
                extras.putInt("Price",price);
                extras.putString("ShopKey",storeID);
                extras.putString("UserID",userID);
                extras.putLong("Number",num);
                extras.putDouble("User Lat",userLat);
                extras.putDouble("User Long",userLong);

                intent1.putExtras(extras);
                startActivity(intent1);
                finish();
            }
        });


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
