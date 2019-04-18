package untitled.example.com.firebasesurvey.presetation;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.crashlytics.android.Crashlytics;

import android.app.Application;
import android.widget.Toast;

import io.fabric.sdk.android.Fabric;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

/**
 * Created by Amy on 2019/4/1
 */

public class MainApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        FirebaseApp.initializeApp(this);

        Fabric.with(this, new Crashlytics());



        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
