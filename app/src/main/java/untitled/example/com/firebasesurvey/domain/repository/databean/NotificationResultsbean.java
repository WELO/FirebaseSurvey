package untitled.example.com.firebasesurvey.domain.repository.databean;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Amy on 2019/4/9
 */
public class NotificationResultsbean {
    @SerializedName("message_id")
    public String multicastId;

    @SerializedName("error")
    public String error;

    public NotificationResultsbean(String multicastId, String error) {
        this.multicastId = multicastId;
        this.error = error;
    }

    private NotificationResultsbean(Builder builder) {
        multicastId = builder.multicastId;
        error = builder.error;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String multicastId;
        private String error;

        private Builder() {
        }

        public Builder setMulticastId(String val) {
            multicastId = val;
            return this;
        }

        public Builder setError(String val) {
            error = val;
            return this;
        }

        public NotificationResultsbean build() {
            return new NotificationResultsbean(this);
        }
    }

    public String getMulticastId() {
        return multicastId;
    }

    public String getError() {
        return error;
    }
}
