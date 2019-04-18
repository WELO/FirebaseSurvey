package untitled.example.com.firebasesurvey.domain.repository.databean;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Amy on 2019/4/3
 */

@AutoValue
public abstract class FirebaseGetTokenFromUidBean {
    @SerializedName("userId")
    public abstract String userId();

    public static TypeAdapter<FirebaseGetTokenFromUidBean> typeAdapter(Gson gson) {
        return new AutoValue_FirebaseGetTokenFromUidBean.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_FirebaseGetTokenFromUidBean.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder userId(String userId);

        public abstract FirebaseGetTokenFromUidBean build();
    }
}
