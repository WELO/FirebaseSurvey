package untitled.example.com.firebasesurvey.domain.repository.cloud;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import untitled.example.com.firebasesurvey.domain.repository.databean.FirebaseGetTokenFromUidBean;
import untitled.example.com.firebasesurvey.domain.repository.databean.FirebaseTokenResponseBean;
import untitled.example.com.firebasesurvey.domain.repository.databean.Notification;
import untitled.example.com.firebasesurvey.domain.repository.databean.SendNotificationResponseBean;

/**
 * Created by Amy on 2019/4/3
 */

public interface FirebaseCloudMessageApi {
    @POST("/fcm/send")
    Single<SendNotificationResponseBean> sendNotification(@Header("Authorization") String authHeader, @Body Notification notification);
}
