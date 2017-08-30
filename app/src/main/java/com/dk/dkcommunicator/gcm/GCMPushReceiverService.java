package com.dk.dkcommunicator.gcm;


import android.content.Intent;

import android.os.Bundle;

import android.util.Log;

import com.dk.dkcommunicator.SmsService;
import com.google.android.gms.gcm.GcmListenerService;


/**
 * Created by Sushant Chauhan on 10/13/2016.
 */

public class GCMPushReceiverService extends GcmListenerService {

    Intent intent;

    public void onMessageReceived(String from, Bundle data) {
        Log.d("MessageReceive", "Called");

        String message = data.getString("message");
        Log.d("messageReceived", message);
        String title = data.getString("title");

        sendNotification(message, title);
    }

    public void sendNotification(String message, String title) {
        if (message != null){
            intent = new Intent(this,SmsService.class);
            startService(intent);
        }
//        if (share.checkData("Installed")) {
//            intent = new Intent(this, DashboardActivity.class);
//            Log.d("loggedin", "logged");
//        } else {
//            intent = new Intent(this, LoginActivity.class);
//            Log.d("loggedout", "logged out");
//        }

//            intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//
//        int requestCode = 0;
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
//
//
//        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
////            // Deprecated with API 23
////            NotificationCompat.Builder noBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
////                    .setSmallIcon(R.drawable.notione)
////                    .setColor(this.getResources().getColor(R.color.colorPrimary))
////                    .setContentText(message)
////                    .setContentTitle(title)
////                    .setContentIntent(pendingIntent)
////                    .setSound(sound)
////                    .setAutoCancel(true);
////
////            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////            notificationManager.notify(0, noBuilder.build());
////        }else{
//            NotificationCompat.Builder noBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
//
//                    .setContentText(message)
//                    .setContentTitle(title)
//                    .setContentIntent(pendingIntent)
//                    .setSound(sound)
//                    .setStyle(new NotificationCompat.BigTextStyle()
//                            .bigText(message))
//                    .setAutoCancel(true);
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(0, noBuilder.build());
//        }

    }
}