package com.example.muszakibolt.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.muszakibolt.R;

public class NotificationHelper {
        private static final String CHANNEL_ID = "cart_channel";
        private final int NOTIFICATION_ID = 0;
        private static final String LOG_TAG = NotificationHelper.class.getName();

        private final NotificationManager notificationManager;
        private final Context context;


        public NotificationHelper(Context context) {
            this.context = context;
            this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            createChannel();
        }

        private void createChannel() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                return;
            Log.d(LOG_TAG,"createChannel");
            NotificationChannel channel = new NotificationChannel
                    (CHANNEL_ID, "MűszakiBolt", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setDescription("A MűszakiBolt alkalmazás üzenete");

            notificationManager.createNotificationChannel(channel);
        }

        public void send(String message) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("MűszakiBolt")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.icon_shopping_cart);
            builder.setChannelId(CHANNEL_ID);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

        public void cancel() {
            notificationManager.cancel(NOTIFICATION_ID);
        }
}
