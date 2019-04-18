package untitled.example.com.firebasesurvey.domain.interactor.storage;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import untitled.example.com.firebasesurvey.Utility.Untility;
import untitled.example.com.firebasesurvey.domain.model.AppIcon;

/**
 * Created by Amy on 2019/4/2
 */

public class IconStorage implements DataStorage<Bitmap> {
    private Context context;
    private FirebaseStorage storage;

    public IconStorage(Context context) {
        this.context = context;
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public void update(Bitmap object) {
        getPackageNameList().doOnSuccess(strings -> uploadImage(strings)).subscribe();
    }

    private void uploadImage(List<String> packageNameList) {
        PackageManager packageManager = context.getPackageManager();
        Observable.fromIterable(packageNameList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(s -> getAppIconDrawable(s, packageManager))
                .doFinally(() -> Timber.d("upload app icon done"))
                .subscribe(appIcon -> {
                    updateFirebaseStorage(appIcon);
                });
    }

    private void updateFirebaseStorage(AppIcon appIcon) {
        StorageReference storageRef = storage.getReference();
        StorageReference image = storageRef.child(appIcon.getImageName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        appIcon.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = image.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Timber.d(appIcon.getImageName() + " onFailure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Timber.d(appIcon.getImageName() + " onSuccess");

                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .setCustomMetadata("app_name", appIcon.getImageName())
                        .setCustomMetadata("package_name", appIcon.getPackageName())
                        .build();
                image.updateMetadata(metadata)
                        .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                            @Override
                            public void onSuccess(StorageMetadata storageMetadata) {
                                Timber.d("updateMetadata "+ appIcon.getImageName() + " onSuccess");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Timber.d("updateMetadata "+ appIcon.getImageName() + " onFailure");
                            }
                        });
            }
        });
    }

    @Override
    public Single<Bitmap> download(String fileName) {
        return null;
    }

    private AppIcon getAppIconDrawable(String packageName, PackageManager packageManager) {
        Drawable drawable = null;
        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            drawable = packageManager.getApplicationIcon(app);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String imageName = Untility.getIconName(packageName).trim();
        AppIcon appIcon = AppIcon.newBuilder()
                .setBitmap(drawableToBitmap(drawable))
                .setImageName(imageName)
                .setPackageName(packageName).build();

        return appIcon;
    }

    public Single<List<String>> getPackageNameList() {
        return Single.defer(() -> {
                    List<String> packageNameList = Untility.getInstalledApps(context);
                    return Single.just(packageNameList);
                }
        ).subscribeOn(Schedulers.io());
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }
}
