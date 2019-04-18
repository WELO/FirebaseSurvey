package untitled.example.com.firebasesurvey.domain.repository.databean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Amy on 2019/4/9
 */

public class Notification {
    @SerializedName("to")
    String to = "";
    @SerializedName("data")
    NotificationDatabean notificationDatabean;
    @SerializedName("notification")
    NotificationBodybean notificationBodybean;

    private Notification(Builder builder) {
        to = builder.to;
        notificationDatabean = builder.notificationDatabean;
        notificationBodybean = builder.notificationBodybean;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String to;
        private NotificationDatabean notificationDatabean;
        private NotificationBodybean notificationBodybean;

        private Builder() {
        }

        public Builder setTo(String val) {
            to = val;
            return this;
        }

        public Builder setNotificationDatabean(NotificationDatabean val) {
            notificationDatabean = val;
            return this;
        }

        public Builder setNotificationBodybean(NotificationBodybean val) {
            notificationBodybean = val;
            return this;
        }

        public Notification build() {
            return new Notification(this);
        }
    }

    public String getTo() {
        return to;
    }

    public NotificationDatabean getNotificationDatabean() {
        return notificationDatabean;
    }

    public NotificationBodybean getNotificationBodybean() {
        return notificationBodybean;
    }
}
