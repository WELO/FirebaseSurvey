package untitled.example.com.firebasesurvey.domain.interactor.login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import java.util.Arrays;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.SingleSubject;
import timber.log.Timber;
import untitled.example.com.firebasesurvey.domain.repository.cloud.FirebaseFunctionApiClient;
import untitled.example.com.firebasesurvey.domain.repository.cloud.LineApiClient;
import untitled.example.com.firebasesurvey.domain.repository.databean.FirebaseGetTokenFromUidBean;
import untitled.example.com.firebasesurvey.domain.repository.databean.LineProfileResponseBean;

import static untitled.example.com.firebasesurvey.Utility.Define.LINE_CHENNAL_ID;

/**
 * Created by Amy on 2019/4/1
 */

public class LineLogin implements SocialLogin {

    private static final int REQUEST_CODE = 1;
    private final FirebaseAuth firebaseAuth;
    private final Activity activity;
    private SingleSubject<FirebaseUser> loginCompletableSubject = SingleSubject.create();
    private String BEARER_AUTH_PREFIX = "Bearer ";
    private LineProfileResponseBean lineProfileResponseBean = null;

    public LineLogin(Activity activity, FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        this.activity = activity;
    }

    @Override
    public void init() {

    }

    @Override
    public Single<String> verify(String account) {
        return null;
    }

    @Override
    public Single<FirebaseUser> login(String token, String password) {
        Intent loginIntent = LineLoginApi.getLoginIntent(
                activity,
                LINE_CHENNAL_ID,
                new LineAuthenticationParams.Builder()
                        .scopes(Arrays.asList(Scope.PROFILE))
                        .build());
        activity.startActivityForResult(loginIntent, REQUEST_CODE);
        return loginCompletableSubject;
    }

    OnCompleteListener<AuthResult> mAuthResultOnCompleteListener = new OnCompleteListener<AuthResult>() {

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Timber.d("signInWithCustomToken:success");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Timber.d("getProviders size = " + user.getProviderData().size());
                for (UserInfo userInfo : user.getProviderData()) {
                    Timber.d("getProviders = " + userInfo.getProviderId());
                }
                loginCompletableSubject.onSuccess(user);
                //updateIdIdentifier(user);
            } else {
                // If sign in fails, display a message to the user.
                Timber.w("signInWithCustomToken:failure " + task.getException());
                loginCompletableSubject.onError(new Exception("signInWithCustomToken:failure"));

            }
        }
    };

    private void updateIdIdentifier(FirebaseUser user) {
        if (null != lineProfileResponseBean) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(lineProfileResponseBean.displayName())
                    .setPhotoUri(Uri.parse(lineProfileResponseBean.pictureUrl()))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Timber.d("User profile updated.");
                            }
                            FirebaseUser newUser = firebaseAuth.getCurrentUser();
                            Timber.d("user getDisplayName=  " + newUser.getDisplayName()
                                    + " getPhoneNumber = " + newUser.getPhoneNumber()
                                    + " getEmail = " + newUser.getEmail()
                                    + " getCreationTimestamp = " + newUser.getMetadata().getCreationTimestamp()
                                    + " getLastSignInTimestamp = " + newUser.getMetadata().getLastSignInTimestamp()
                                    + " getProviderId = " + newUser.getProviderId()
                                    + " getPhotoUrl = " + newUser.getPhotoUrl().toString());
                            loginCompletableSubject.onSuccess(newUser);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loginCompletableSubject.onError(new Exception("updateIdIdentifier:failure"));
                }
            });
            user.updateEmail(lineProfileResponseBean.displayName() + "@line.com")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Timber.d("updateEmail updated.");
                            }
                            loginCompletableSubject.onSuccess(user);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    loginCompletableSubject.onError(new Exception("updateIdIdentifier:failure"));
                }
            });
        }

    }

    @Override
    public Completable logout() {
        return Completable.fromAction(() -> {
            firebaseAuth.signOut();
        });
    }

    @Override
    public Single<FirebaseUser> linkCredential() {
        return null;
    }


    private Single<String> getLineUid(String token) {
        return LineApiClient.getLineAPI().getProfile(BEARER_AUTH_PREFIX + token).map(lineProfileResponseBean -> {
            Timber.d("userId = " + lineProfileResponseBean.userId() + " displayName = " + lineProfileResponseBean.displayName() + lineProfileResponseBean.displayName());
            this.lineProfileResponseBean = lineProfileResponseBean;
            return lineProfileResponseBean.userId();
        });
    }

    private Single<String> getFirebaseTokenFromUid(String uid) {
        return FirebaseFunctionApiClient.getFirebaseFunctionApi().getFirebaseTokenFromUid(FirebaseGetTokenFromUidBean.builder().userId(uid).build())
                .map(firebaseTokenResponseBean -> {
                    Timber.d("firebaseToken = " + firebaseTokenResponseBean.firebaseToken());
                    return firebaseTokenResponseBean.firebaseToken();
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);

        switch (result.getResponseCode()) {

            case SUCCESS:
                // Login successful
                String accessToken = result.getLineCredential().getAccessToken().getTokenString();
                Timber.d("accessToken = " + accessToken);
                getFirebaseTokenFromUid(result.getLineProfile().getUserId())
                        .subscribe(firebaseToken -> {
                            firebaseAuth.signInWithCustomToken(firebaseToken)
                                    .addOnCompleteListener(mAuthResultOnCompleteListener);
                        });

                break;
            case CANCEL:
                // Login canceled by user
                Timber.e("LINE Login Canceled by user.");
                break;

            default:
                // Login canceled due to other error
                Timber.e("Login FAILED!");
                Timber.e(result.getErrorData().toString());
        }
    }
}