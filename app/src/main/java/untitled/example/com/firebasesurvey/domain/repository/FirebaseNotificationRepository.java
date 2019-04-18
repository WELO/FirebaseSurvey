package untitled.example.com.firebasesurvey.domain.repository;

import io.reactivex.Completable;
import untitled.example.com.firebasesurvey.domain.repository.databean.Notification;

/**
 * Created by Amy on 2019/4/12
 */

public interface FirebaseNotificationRepository {
    Completable sendNotification(String to, String title, String content);

    Completable sendNotification(Notification notification);
}
