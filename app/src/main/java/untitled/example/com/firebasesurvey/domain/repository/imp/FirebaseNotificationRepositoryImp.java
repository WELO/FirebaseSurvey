package untitled.example.com.firebasesurvey.domain.repository.imp;

import io.reactivex.Completable;
import untitled.example.com.firebasesurvey.Utility.Define;
import untitled.example.com.firebasesurvey.domain.repository.FirebaseNotificationRepository;
import untitled.example.com.firebasesurvey.domain.repository.cloud.FirebaseCloudMessageApiClient;
import untitled.example.com.firebasesurvey.domain.repository.databean.Notification;
import untitled.example.com.firebasesurvey.domain.repository.databean.NotificationBodybean;
import untitled.example.com.firebasesurvey.domain.repository.databean.NotificationDatabean;

/**
 * Created by Amy on 2019/4/12
 */

public class FirebaseNotificationRepositoryImp implements FirebaseNotificationRepository {

    private String FIREBASE_SEND_NOTIFICATION_PREFIX = "key=";

    @Override
    public Completable sendNotification(String to, String title, String content) {
        return FirebaseCloudMessageApiClient.getFirebaseFunctionApi()
                .sendNotification(FIREBASE_SEND_NOTIFICATION_PREFIX + Define.FIREBASE_SERVER_KEY, Notification.newBuilder().setTo(to)
                        .setNotificationBodybean(NotificationBodybean.newBuilder().setTitle(title).setBody(content).build())
                        .setNotificationDatabean(NotificationDatabean.newBuilder().setTitle(title).setContent(content).build())
                        .build())
                .flatMapCompletable(sendNotificationResponseBean -> {
                    if (sendNotificationResponseBean.success() > 0) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new Exception(sendNotificationResponseBean.results().get(0).getError()));
                    }

                });
    }

    @Override
    public Completable sendNotification(Notification notification) {
        return FirebaseCloudMessageApiClient.getFirebaseFunctionApi()
                .sendNotification(FIREBASE_SEND_NOTIFICATION_PREFIX + Define.FIREBASE_SERVER_KEY, notification)
                .flatMapCompletable(sendNotificationResponseBean -> {
                    if (sendNotificationResponseBean.success() > 0) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new Exception(sendNotificationResponseBean.results().get(0).getError()));
                    }

                });
    }
}
