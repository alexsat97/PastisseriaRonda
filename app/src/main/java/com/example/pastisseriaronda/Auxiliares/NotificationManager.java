package com.example.pastisseriaronda.Auxiliares;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.pastisseriaronda.Actividades.Compartidas.MainActivity;
import com.example.pastisseriaronda.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class NotificationManager extends FirebaseMessagingService {
    private static final String TAG = "M -> ";

    public NotificationManager() { }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        getString(R.string.default_notification_channel_id), "notificaciones", android.app.NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplication(), getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.drawable.titulo)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setVibrate(new long[]{100, 250})
                    .setLights(Color.RED, 500, 5000)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(getApplication(), R.color.colorPrimary));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplication());
            stackBuilder.addNextIntent(new Intent(getApplication(), MainActivity.class));
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            notificationManager.notify( Integer.parseInt(new SimpleDateFormat("ddHHmmss").format(new Date())), mBuilder.build());
        }
    }


}