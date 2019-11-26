package com.example.cargotransportationdriverapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class CustomNotificationGenerator {

    private static final String TAG = CustomNotificationGenerator.class.getName();
    private Context context;

    //for Ride notification
    private static final String PREFERENCE_LAST_NOTIF_ID = "PREFERENCE_LAST_NOTIF_ID";

    public static CustomNotificationGenerator getInstance(Context context){
        return new CustomNotificationGenerator(context);
    }

    public CustomNotificationGenerator(Context context) {
        this.context = context;
    }

    //to receive Ride notification
    public void createNotification(int noti_id, String noti_title, String noti_message, Bitmap postImageBitmap) {

        //final Intent intent = new Intent(context, Home.class);
        final Intent intent = new Intent();
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, noti_id, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myChannel";
            String description = "myChannelDescription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this 1551379411
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                    .setSmallIcon(R.drawable.avatar)
                    .setContentText(noti_message)
                    .setContentTitle(noti_title)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(postImageBitmap)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(postImageBitmap).bigLargeIcon(null))
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(context);
            notificationManager1.notify(noti_id, mBuilder.build());

        } else {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.avatar)
                    .setContentText(noti_message)
                    .setContentTitle(noti_title)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(postImageBitmap)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(noti_message))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(postImageBitmap).bigLargeIcon(null))
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(context);
            notificationManager1.notify(noti_id, mBuilder.build());

        }

    }

    //to receive Ride notification
    public static int getNextNotifId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int id = sharedPreferences.getInt(PREFERENCE_LAST_NOTIF_ID, 0) + 1;
        if (id == Integer.MAX_VALUE) {
            id = 0;
        } // isn't this over kill ??? hahaha!!  ^_^
        sharedPreferences.edit().putInt(PREFERENCE_LAST_NOTIF_ID, id).apply();
        Log.e("PREFERENCE_NOTIF_ID", "getNextNotifId: " + id);
        return id;
    }

    //to receive Ride notification
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //to receive Ride notification
    private void getImageBitmapAndCreateNotification(
            final int noti_id, final String noti_title, final String noti_message,String imageUrl){

        if (imageUrl != null && imageUrl.length() > 0) {
            @SuppressLint("StaticFieldLeak") AsyncClassTotalNotifs notifs = new AsyncClassTotalNotifs() {
                @Override
                public void receiveData(Object object) {

                    createNotification(noti_id, noti_title, noti_message, StringToBitMap((String) object));
                }
            };
            notifs.execute(imageUrl);
        }else
            createNotification(noti_id, noti_title, noti_message, null);
    }

}
