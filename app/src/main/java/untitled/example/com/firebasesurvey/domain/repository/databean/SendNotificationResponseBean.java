package untitled.example.com.firebasesurvey.domain.repository.databean;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Amy on 2019/4/9
 */
@AutoValue
public abstract class SendNotificationResponseBean {

    @SerializedName("multicast_id")
    public abstract long multicast_id();
    @SerializedName("success")
    public abstract int success();
    @SerializedName("failure")
    public abstract int failure();
    @SerializedName("canonical_ids")
    public abstract String canonical_ids();
    @SerializedName("results")
    public abstract ArrayList<NotificationResultsbean> results();

    public static TypeAdapter<SendNotificationResponseBean> typeAdapter(Gson gson) {
        return new AutoValue_SendNotificationResponseBean.GsonTypeAdapter(gson);
    }
}
