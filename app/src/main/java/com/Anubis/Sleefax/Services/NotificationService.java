package com.Anubis.Sleefax.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.Anubis.Sleefax.R;
import com.Anubis.Sleefax.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NotificationService extends Service {


    DatabaseReference reference;
    int notificationId = 200;
    String CHANNEL_ID = "Sleefax";
    DatabaseReference orderDb = FirebaseDatabase.getInstance().getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    NotificationManagerCompat notificationManager;


    @Override
    public void onCreate() {

//            new createNotification().execute();
        // Create an Intent for the activity you want to start
        final Intent resultIntent = new Intent(getApplicationContext(), Select.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//          final PendingIntent resultPendingIntent = PendingIntent.getBroadcast(Select.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        orderDb.child("users").child(userId).child("Orders").addValueEventListener(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
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

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            CharSequence name = "Order Status";
                            String description = "Order Notifications";
                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                            channel.setDescription(description);

                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(channel);


                        }

                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        NotificationCompat.Builder builder;
                        notificationManager = NotificationManagerCompat.from(getApplicationContext());

                        if(!finalOrderStatus.equals("Done")){

                            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setSmallIcon(R.drawable.notify)
                                    .setLargeIcon(icon)
                                    .setContentTitle("Order Status :"+finalStatus)
                                    .setContentText("Order ID: " + orderKeys.getKey() + " " + finalStatus)
                                    .setGroup(CHANNEL_ID)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Order ID: " + orderKeys.getKey()))
                                    .setContentIntent(resultPendingIntent)
                                    .addAction(R.drawable.notify, "Check order Status", resultPendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH);
                            builder.setSound(alarmSound);

                        }else{
                            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setSmallIcon(R.drawable.notify)
                                    .setLargeIcon(icon)
                                    .setContentTitle("Order ID: " + orderKeys.getKey())
                                    .setGroup(CHANNEL_ID)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Thank You for using Sleefax 😁 \n We hope you had a great experience."))
                                    .setContentIntent(resultPendingIntent)
                                    .addAction(R.drawable.notify, "Check order Status", resultPendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH);
                            builder.setSound(alarmSound);

                        }

                        if (finalOrderStatus.equals("Received") && !finalRTnotifyStatus) {

                            notified.put("RT_Notified", true);
                            orderDb.child("users").child(userId).child("Orders").child(orderKeys.getKey()).updateChildren(notified);
                            notificationManager.notify(1, builder.build());
//                            notifyCnt = 0;

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


                    }


                }
            }
//                }

//                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }


    public NotificationService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
