package untitled.example.com.firebasesurvey.domain.repository.cloud;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import untitled.example.com.firebasesurvey.domain.repository.databean.LineProfileResponseBean;

/**
 * Created by Amy on 2019/4/3
 */

public interface LineApi {
    @GET("/v2/profile")
    Single<LineProfileResponseBean> getProfile(@Header("Authorization") String authHeader);
}
