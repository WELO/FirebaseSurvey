package untitled.example.com.firebasesurvey.domain.repository.databean;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Amy on 2019/4/3
 */

@AutoValue
public abstract class LineProfileResponseBean {
    @SerializedName("userId")
    public abstract String userId();

    @SerializedName("displayName")
    public abstract String displayName();

    @SerializedName("pictureUrl")
    public abstract String pictureUrl();

    @SerializedName("statusMessage")
    public abstract String statusMessage();

    public static TypeAdapter<LineProfileResponseBean> typeAdapter(Gson gson) {
        return new AutoValue_LineProfileResponseBean.GsonTypeAdapter(gson);
    }
}
