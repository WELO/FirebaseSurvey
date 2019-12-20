package untitled.example.com.firebasesurvey.domain.interactor.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import java.util.Map;

import timber.log.Timber;
import untitled.example.com.firebasesurvey.R;
import untitled.example.com.firebasesurvey.Utility.Define;
import untitled.example.com.firebasesurvey.Utility.SharedPrefMgr;
import untitled.example.com.firebasesurvey.presetation.MainActivity;


/**
 * Created by Amy on 2019/4/9
 */

public class FirebaseMessageReceiver extends FirebaseMessagingService {

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
        Timber.d("Status: onNewToken = %s", newToken);
        SharedPrefMgr.saveSharedPref(getApplicationContext(), Define.SPFS_FCM_TOKEN, newToken, Define.SPFS_CATEGORY);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        String body = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : null;
        Map<String, String> data = remoteMessage.getData();
        Timber.d("Status: onMessageReceived with from = %s\nmessage = %s\ndata = %s", from, body, data);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.app_name))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(data.get("data_title"))
                .setContentText(data.get("data_content"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.app_name), "Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Notification");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, builder.build());
    }
}
