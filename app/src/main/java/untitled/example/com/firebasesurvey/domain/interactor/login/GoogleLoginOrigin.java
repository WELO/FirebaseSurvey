package untitled.example.com.firebasesurvey.domain.interactor.login;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.CompletableSubject;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Amy on 2019/4/10
 */

public class GoogleLoginOrigin implements SocialLogin
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Activity activity;
    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private final int PERMISSION_RESULT_CODE = 100;


    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;

    private CompletableSubject loginCompletableSubject = CompletableSubject.create();
    private final FirebaseAuth firebaseAuth;

    public GoogleLoginOrigin(Activity activity, FirebaseAuth firebaseAuth) {
        this.activity = activity;
        this.firebaseAuth = firebaseAuth;
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void init() {

    }

    @Override
    public Single<String> verify(String account) {
        return null;
    }

    @Override
    public Completable login(String account, String password) {
        if (!(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            mSignInClicked = true;
            resolveSignInError();
        }

        return null;
    }

    @Override
    public Completable logout() {
        return null;
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(activity, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String[] perms = {Manifest.permission.GET_ACCOUNTS};

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), activity, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            mConnectionResult = connectionResult;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
}
