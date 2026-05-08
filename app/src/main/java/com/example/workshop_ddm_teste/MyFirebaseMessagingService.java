package com.example.workshop_ddm_teste;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "fcm_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String titulo = "Nova notificação";
        String corpo  = "";

        if (remoteMessage.getNotification() != null) {
            titulo = remoteMessage.getNotification().getTitle();
            corpo  = remoteMessage.getNotification().getBody();
        }

        criarCanalSeNecessario();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(corpo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onNewToken(String token) {
        // Aqui você pode salvar o token no Firestore se quiser
        android.util.Log.d("FCM", "Token: " + token);
    }

    private void criarCanalSeNecessario() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificações FCM",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }
    }
}