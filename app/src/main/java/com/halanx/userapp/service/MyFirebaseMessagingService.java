package com.halanx.userapp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.halanx.userapp.Activities.HomeActivity;
import com.halanx.userapp.Activities.RatingActivity;
import com.halanx.userapp.R;
import com.halanx.userapp.app.Config;
import com.halanx.userapp.util.NotificationUtils;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Nishant on 15/07/17.
 */


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private android.support.v4.app.NotificationCompat.Style bigPictureStyle;
    long[] pattern = {0, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000, 500};
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage);

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            String data = remoteMessage.getData().toString();
//            Log.d("dataat16", String.valueOf(data.charAt(157))+String.valueOf(data.charAt(158)));
//            data = "[" +data + "]";
//            Log.d("final String",data);
//            String data_value = remoteMessage.getData().get("data");
//            Log.d("getacces", String.valueOf(data_value.charAt(156)));
//
//            char[] value = data_value.toCharArray();
//            value[150] = 'k';
//            String new_string = String.valueOf(value);
//            Log.d("getacces",new_string);


            try {
                JSONObject json = new JSONObject(data);
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception:" + e);
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            String batch_id = json.getString("BatchId");
            Log.d("batch_id",batch_id);
            getSharedPreferences("BatchData",Context.MODE_PRIVATE).edit().putString("BatchID", batch_id).apply();


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                startActivity(new Intent(MyFirebaseMessagingService.this,RatingActivity.class));

                // app is in foreground, broadcast the push message
            } else {
                Intent resultIntent = new Intent(this, HomeActivity.class);

                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent piResult = PendingIntent.getActivity(this,
                        (int) Calendar.getInstance().getTimeInMillis(), resultIntent, 0);

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);

                Intent resultIntenta = new Intent(this, RatingActivity.class);

                resultIntenta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent piResulta = PendingIntent.getActivity(this,
                        (int) Calendar.getInstance().getTimeInMillis(), resultIntenta, 0);


// Assign big picture notification

                NotificationCompat.Builder builder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.logochange)
                                .setContentTitle("Halanx")
                                .setContentText("Hey Buddy Shopper has Recieved your order")
                                .setSound(RingtoneManager.getValidRingtoneUri(getApplicationContext()))
                                .setStyle(bigPictureStyle)
                                .setContentIntent(piResulta)
                                .setVibrate(pattern)
                                .setOngoing(true)
                                .setAutoCancel(true);
//set intents and pending intents to call activity on click of "show activity" action button of notification

// Gets an instance of the NotificationManager service
                NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                builder.build().flags |= builder.build().FLAG_INSISTENT;

//to post your notification to the notification bar
                notificationManager.notify(0, builder.build());



//                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                resultIntent.putExtra("message", message);
//
//                // check for image attachment
//                if (TextUtils.isEmpty("data")) {
//                    showNotificationMessage(getApplicationContext(), title, message, resultIntent);
//                } else {
//                    // image is present, show notification with image
//                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, resultIntent);
//                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
//    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage("data", message, intent);
//    }
//
//    /**
//     * Showing notification with text and image
//     */
//    private void showNotificationMessageWithBigImage(Context context, String title, String message, Intent intent) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, intent);
//    }
}