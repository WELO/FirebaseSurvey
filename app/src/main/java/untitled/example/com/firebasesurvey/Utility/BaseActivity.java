package untitled.example.com.firebasesurvey.Utility;

import com.google.firebase.analytics.FirebaseAnalytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

/**
 * Created by Amy on 2019/4/1
 */

public class BaseActivity extends AppCompatActivity {
    protected Context context;
    protected Activity activity;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        this.activity = this;
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    protected void setUserId(String id) {
        Timber.d("setEvent id-> "+id);
        firebaseAnalytics.setUserId(id);
    }

    protected void setUserProperty(String property, String content) {
        Timber.d("setEvent property-> "+property+" content -> "+content);
        firebaseAnalytics.setUserProperty(property, content);
    }

    protected void setEvent(String action,String label) {
        Timber.d("setEvent action-> "+action+" label -> "+label);
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        bundle.putString("label", label);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    protected void setUidEvent(String action,String label) {
        Timber.d("setEvent action-> "+action+" label -> "+label);
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        bundle.putString("label", label);
        firebaseAnalytics.logEvent("uid", bundle);
    }

    protected void setScreen(String screenName) {
        Timber.d("setScreen ->"+screenName);
        firebaseAnalytics.setCurrentScreen(this, screenName, null /* class override */);
    }
}
