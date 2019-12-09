package untitled.example.com.firebasesurvey.domain.interactor.login;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.SingleSubject;
import timber.log.Timber;
import untitled.example.com.firebasesurvey.Utility.Define;
import untitled.example.com.firebasesurvey.Utility.Untility;

/**
 * Created by Amy on 2019/4/10
 */

public class GoogleLogin implements SocialLogin {

    private final Activity activity;
    private final FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1001;
    private int RC_LINK_CREDENTAIL = 1002;
    private SingleSubject<FirebaseUser> loginSingleSubject = SingleSubject.create();
    private SingleSubject<FirebaseUser> linkSingleSubject = SingleSubject.create();
    private CompletableSubject logoutCompletableSubject = CompletableSubject.create();


    public GoogleLogin(Activity activity, FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        this.activity = activity;
    }

    @Override
    public void init() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Define.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    @Override
    public Single<String> verify(String account) {
        return null;
    }

    @Override
    public Single<FirebaseUser> login(String account, String password) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
        return loginSingleSubject;
    }

    @Override
    public Completable logout() {
        firebaseAuth.signOut();
        googleLogout();
        return logoutCompletableSubject;
    }

    @Override
    public Single<FirebaseUser> linkCredential() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_LINK_CREDENTAIL);
        return linkSingleSubject;
    }

    private void googleLogout() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, logoutOnCompleteListener);
    }

    private OnCompleteListener<Void> logoutOnCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            logoutCompletableSubject.onComplete();
            if (task.isSuccessful()) {
                Timber.d("googleLogout Success");
            } else {
                Timber.d("googleLogout failed");
                Timber.w(task.getException());
                logoutCompletableSubject.onError(task.getException());
            }
        }
    };


    private void firebaseLogin(GoogleSignInAccount account) {
        Timber.d("GoogleSignInAccount = " + Untility.objectToString(account));
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        Timber.d("GoogleSignInAccount getEmail= " + account.getEmail());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Timber.d("signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            for (int i = 0; i < user.getProviderData().size(); i++) {
                                Timber.d("getProviderData = " + Untility.objectToString(user.getProviderData().get(i)));
                            }
                            loginSingleSubject.onSuccess(user);
                        } else {
                            Timber.w("signInWithCredential:failure", task.getException());
                            loginSingleSubject.onError(task.getException());
                        }
                    }
                });
    }

    private void linkCredential(AuthCredential credential) {
        firebaseAuth.getCurrentUser()
                .linkWithCredential(credential).addOnCompleteListener(mAuthResultOnCompleteListener);
    }

    private OnCompleteListener<AuthResult> mAuthResultOnCompleteListener = new OnCompleteListener<AuthResult>() {

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Timber.d("signInWithCredential:success");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                linkSingleSubject.onSuccess(user);
            } else {
                Timber.w("signInWithCredential:failure", task.getException());
                linkSingleSubject.onError(task.getException());
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (requestCode == RC_SIGN_IN) {
                firebaseLogin(account);
            } else if (requestCode == RC_LINK_CREDENTAIL) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                linkCredential(credential);
            }
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Timber.w("Google sign in failed");
            e.printStackTrace();
            loginSingleSubject.onError(e);
            // ...
        }
    }
}
