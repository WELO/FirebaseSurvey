package untitled.example.com.firebasesurvey.Utility;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Amy on 2019/4/1
 */

public class Define {

    public static final int FACEBOOK =      0;
    public static final int LINE =          1;
    public static final int PHONE =         2;
    public static final int GOOGLE =        3;
    public static final int EMAIL =         4;
    public static final int EMAIL_LINK =    5;

    @IntDef({FACEBOOK, LINE, PHONE, GOOGLE, EMAIL, EMAIL_LINK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginType {
    }

    public enum DBType {
        CLOUD_FIRESTORE, REALTIME_DATABASE
    }

    public enum StorageType {
        ICON, OTHER
    }

    public static final String LINE_CHENNAL_ID = "1560540369";
    public static final String GOOGLE_CLIENT_ID = "177602515675-ql6cnhhvqn7iov985uc9smu8s5chkl1f.apps.googleusercontent.com";
    public static final String FIREBASE_SERVER_KEY = "AAAAKVnvUts:APA91bFndz9qI2fL0QEGDWWoOaqiNzDuHEW_0umFKDd2fqW6II35bkHixkbKKWb5e4WsO4-_3qDDtKKCRduIeXP_YyldvTb8i9IkmccNIkh39T-67phdhAHpyf3FrB2PlIZT33CsgbHp";

    //Sharepref
    public static final String SPFS_FCM_TOKEN = "SpfsFcmToken";
    public static final String SPFS_CURRENT_UID = "SpfsCurrentUid";
    public static final String SPFS_CATEGORY = "SpfsCategory";

}
