package untitled.example.com.firebasesurvey.domain.repository.databean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Amy on 2019/4/9
 */

public class NotificationBodybean {
    @SerializedName("title")
    String title = "";
    @SerializedName("body")
    String body = "";

    private NotificationBodybean(Builder builder) {
        title = builder.title;
        body = builder.body;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String title;
        private String body;

        private Builder() {
        }

        public Builder setTitle(String val) {
            title = val;
            return this;
        }

        public Builder setBody(String val) {
            body = val;
            return this;
        }

        public NotificationBodybean build() {
            return new NotificationBodybean(this);
        }
    }
}
