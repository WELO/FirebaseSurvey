package untitled.example.com.firebasesurvey.domain.model;

import android.graphics.Bitmap;

/**
 * Created by Amy on 2019/4/2
 */

public class AppIcon {
    private Bitmap bitmap;
    private String imageName;
    private String packageName;

    private AppIcon(Builder builder) {
        bitmap = builder.bitmap;
        imageName = builder.imageName;
        packageName = builder.packageName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private Bitmap bitmap;
        private String imageName;
        private String packageName;

        private Builder() {
        }

        public Builder setBitmap(Bitmap val) {
            bitmap = val;
            return this;
        }

        public Builder setImageName(String val) {
            imageName = val;
            return this;
        }

        public Builder setPackageName(String val) {
            packageName = val;
            return this;
        }

        public AppIcon build() {
            return new AppIcon(this);
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getImageName() {
        return imageName;
    }

    public String getPackageName() {
        return packageName;
    }
}
