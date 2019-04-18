package untitled.example.com.firebasesurvey.domain.interactor.login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.CompletableSubject;
import timber.log.Timber;

/**
 * Created by Amy on 2019/4/1
 */

public class FacebookLogin implements SocialLogin {

    private Activity activity;
    private CallbackManager mCallbackManager;
    private CompletableSubject loginCompletableSubject = CompletableSubject.create();
    private ProfileTracker mProfileTracker;
    private String FACEBOOK_PHOTO_URL = "http://graph.facebook.com";
    private final FirebaseAuth firebaseAuth;


    public FacebookLogin(Activity activity, FirebaseAuth firebaseAuth) {
        this.activity = activity;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void init() {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(activity, getFbPermissions());
    }

    @Override
    public Single<String> verify(String account) {
        return null;
    }

    @Override
    public Completable login(String account, String password) {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(mCallbackManager,mLoginResultFacebookCallback);
        return loginCompletableSubject;
    }

    @Override
    public Completable logout() {
        return Completable.fromAction(() -> {
            LoginManager.getInstance().logOut();
            firebaseAuth.signOut();
        });
    }

    public List<String> getFbPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");
        return permissions;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    FacebookCallback<LoginResult> mLoginResultFacebookCallback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (Profile.getCurrentProfile() == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        Profile.setCurrentProfile(profile2);
                        mProfileTracker.stopTracking();
                        handleLogInResult(loginResult);
                    }
                };
                // no need to call startTracking() on mProfileTracker
                // because it is called by its constructor, internally.
            } else {
                handleLogInResult(loginResult);
            }
        }

        @Override
        public void onCancel() {
            loginCompletableSubject.onError(new Exception("onCancel"));

        }

        @Override
        public void onError(FacebookException error) {
            loginCompletableSubject.onError(error);

        }
    };

    /**
     * Handle the Facebook login result
     *
     * @param loginResult the login result
     */
    private void handleLogInResult(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                (JSONObject object, GraphResponse response) -> {
                    try {
                        // get current fb profile
                        Profile profile = Profile.getCurrentProfile();
                        if (profile != null) {
                            String email = object.has("email") ? object.getString("email") : null;
                            String name = profile.getName();
                            String uid = profile.getId();
                            String token = loginResult.getAccessToken().getToken();
                            Uri avatar = profile.getProfilePictureUri(100, 100);
                            String avatarImage = FACEBOOK_PHOTO_URL + avatar.getPath();
                            firebaseLogin(token);

                        } else {
                            loginCompletableSubject.onError(new Exception("profile is null"));
                        }

                    } catch (JSONException e) {
                        loginCompletableSubject.onError(e);
                    }
                });
        // launch graph request
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,id,first_name,last_name,name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void firebaseLogin(String accessToken){
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken);
        //linkCredential(credential);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Timber.d("signInWithCredential:success");
                            loginCompletableSubject.onComplete();
                        } else {
                            Timber.w("signInWithCredential:failure", task.getException());
                            linkCredential(credential);
                        }
                    }
                });
    }

    public void linkCredential(AuthCredential credential){
        firebaseAuth.getCurrentUser()
                .linkWithCredential(credential).addOnCompleteListener(mAuthResultOnCompleteListener);
    }

    OnCompleteListener<AuthResult> mAuthResultOnCompleteListener = new OnCompleteListener<AuthResult>(){

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Timber.d("signInWithCredential:success");
                loginCompletableSubject.onComplete();
            } else {
                Timber.w("signInWithCredential:failure", task.getException());
                loginCompletableSubject.onError(task.getException());
            }
        }
    };
}