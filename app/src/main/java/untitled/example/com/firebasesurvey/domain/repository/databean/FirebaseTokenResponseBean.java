package untitled.example.com.firebasesurvey.domain.repository.databean;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Amy on 2019/4/3
 */
@AutoValue
public abstract class FirebaseTokenResponseBean {
    @SerializedName("firebase_token")
    public abstract String firebaseToken();

    public static TypeAdapter<FirebaseTokenResponseBean> typeAdapter(Gson gson) {
        return new AutoValue_FirebaseTokenResponseBean.GsonTypeAdapter(gson);
    }
}
