package untitled.example.com.firebasesurvey.domain.interactor.login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.subjects.CompletableSubject;
import timber.log.Timber;
import untitled.example.com.firebasesurvey.BuildConfig;

/**
 * Created by Amy on 2019/4/10
 */

public class EmailLinkLogin {
    private final FirebaseAuth firebaseAuth;
    private CompletableSubject sendLinkCompletableSubject = CompletableSubject.create();
    private CompletableSubject loginCompletableSubject = CompletableSubject.create();
    private ActionCodeSettings actionCodeSettings;

    public EmailLinkLogin(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void init() {
        actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://firebasesurvey.page.link/emaillink")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                BuildConfig.APPLICATION_ID,
                                false, /* installIfNotAvailable */
                                null    /* minimumVersion */)
                        .build();


    }

    public Completable sendLink(String email) {
        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(sendLinkOnCompleteListener);
        return sendLinkCompletableSubject;
    }

    public Completable login(String email, String emailLink) {
        if (firebaseAuth.isSignInWithEmailLink(emailLink)) {
            firebaseAuth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(loginOnCompleteListener);
        } else {
            return Completable.error(new Exception("emailLink is invalid"));
        }
        return loginCompletableSubject;
    }

    public Completable logout() {
        return Completable.fromAction(() -> {
            firebaseAuth.signOut();
        });
    }

    OnCompleteListener<Void> sendLinkOnCompleteListener = new OnCompleteListener<Void>() {

        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                Timber.d("Email sent.");
                sendLinkCompletableSubject.onComplete();
            } else {
                sendLinkCompletableSubject.onError(task.getException());
            }
        }
    };

    OnCompleteListener<AuthResult> loginOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Timber.d("signInWithEmailLink:success");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Timber.d("user isEmailVerified="+user.isEmailVerified());
                loginCompletableSubject.onComplete();
            } else {

                Timber.w("signInWithEmailLink:failure", task.getException());
                loginCompletableSubject.onError(task.getException());
            }
        }
    };
}
