package untitled.example.com.firebasesurvey.domain.repository.databean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Amy on 2019/4/9
 */

public class NotificationDatabean {
    @SerializedName("data_title")
    String title = "";
    @SerializedName("data_content")
    String content = "";

    private NotificationDatabean(Builder builder) {
        title = builder.title;
        content = builder.content;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String title;
        private String content;

        private Builder() {
        }

        public Builder setTitle(String val) {
            title = val;
            return this;
        }

        public Builder setContent(String val) {
            content = val;
            return this;
        }

        public NotificationDatabean build() {
            return new NotificationDatabean(this);
        }
    }
}
