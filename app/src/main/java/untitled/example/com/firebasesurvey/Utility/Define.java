package untitled.example.com.firebasesurvey.Utility;

/**
 * Created by Amy on 2019/4/1
 */

public class Define {
    public enum LoginType {
        FACEBOOK, LINE, PHONE, GOOGLE, EMAIL,EMAIL_LINK
    }

    public enum DBType {
        CLOUD_FIRESTORE, REALTIME_DATABASE
    }

    public enum StorageType {
        ICON, OTHER
    }

    public static final String LINE_CHENNAL_ID = "";
    public static final String GOOGLE_CLIENT_ID = "";
    public static final String FIREBASE_SERVER_KEY = "";

    //Sharepref
    public static final String SPFS_FCM_TOKEN = "fcm_token";
    public static final String SPFS_CATEGORY = "SpfsCategory";

}
