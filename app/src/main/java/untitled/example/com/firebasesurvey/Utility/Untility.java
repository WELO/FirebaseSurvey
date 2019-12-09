package untitled.example.com.firebasesurvey.Utility;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amy on 2019/4/2
 */

public class Untility {

    public static List<String> getInstalledApps(Context context) {
        List<String> packageNameList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        for (PackageInfo packageInfo : packageInfoList) {
            String packageName = packageInfo.packageName;
            String name = (String) packageInfo.applicationInfo.loadLabel(packageManager);
            if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(name) || packageName.getBytes().length > 255 || name.getBytes().length > 255 || packageName.equals(context.getPackageName()))
                continue;

            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
                intent = packageManager.getLaunchIntentForPackage(packageName);
            }
            if (intent == null)
                continue;

            packageNameList.add(packageName);
        }

        return packageNameList;
    }

    public static String getIconName(String iconName) {
        return "images/" + iconName.replace(".", "_").split("---")[0] + ".png";
    }

    public static String getFirebaseCustomToken(String uid) {

        return "";
    }

    public static <T>String objectToString(T object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
