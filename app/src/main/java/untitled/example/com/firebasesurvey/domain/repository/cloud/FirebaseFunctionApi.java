package untitled.example.com.firebasesurvey.domain.repository.cloud;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import untitled.example.com.firebasesurvey.domain.repository.databean.FirebaseGetTokenFromUidBean;
import untitled.example.com.firebasesurvey.domain.repository.databean.FirebaseTokenResponseBean;
import untitled.example.com.firebasesurvey.domain.repository.databean.LineProfileResponseBean;

/**
 * Created by Amy on 2019/4/3
 */

public interface FirebaseFunctionApi {
    @POST("/getFirebaseTokenFromUid")
    Single<FirebaseTokenResponseBean> getFirebaseTokenFromUid(@Body FirebaseGetTokenFromUidBean firebaseGetTokenFromUidBean);
}
