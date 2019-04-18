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
import com.google.firebase.auth.GoogleAuthProvider;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.CompletableSubject;
import timber.log.Timber;
import untitled.example.com.firebasesurvey.Utility.Define;

/**
 * Created by Amy on 2019/4/10
 */

public class GoogleLogin implements SocialLogin {

    private final Activity activity;
    private final FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 100;
    private CompletableSubject loginCompletableSubject = CompletableSubject.create();
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
    public Completable login(String account, String password) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
        return loginCompletableSubject;
    }

    @Override
    public Completable logout() {
        firebaseAuth.signOut();
        googleLogout();
        return logoutCompletableSubject;
    }

    private void googleLogout() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, logoutOnCompleteListener);
    }

    OnCompleteListener<Void> logoutOnCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            logoutCompletableSubject.onComplete();
            if (task.isSuccessful()) {
                Timber.d("googleLogout Success");
            } else {
                Timber.d("googleLogout failed");
                Timber.w(task.getException());
            }
        }
    };



    private void firebaseLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        //linkCredential(credential);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseLogin(account);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Timber.w("Google sign in failed");
            e.printStackTrace();
            // ...
        }
    }
}
