package untitled.example.com.firebasesurvey.domain.interactor.login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.SingleSubject;
import timber.log.Timber;

/**
 * Created by Amy on 2019/4/10
 */

public class EmailLogin {
    private final FirebaseAuth firebaseAuth;
    private SingleSubject<FirebaseUser> registerCompletableSubject = SingleSubject.create();
    private SingleSubject<FirebaseUser> verifyCompletableSubject = SingleSubject.create();
    private SingleSubject<FirebaseUser> loginCompletableSubject = SingleSubject.create();
    private SingleSubject<FirebaseUser> linkWithCredentialCompletableSubject = SingleSubject.create();


    public EmailLogin(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void init() {

    }

    public Single<FirebaseUser> register(String account, String password) {
        firebaseAuth.createUserWithEmailAndPassword(account, password)
                .addOnCompleteListener(registerOnCompleteListener);
        return registerCompletableSubject;
    }

    public Single<FirebaseUser> verify() {
        if (null != firebaseAuth.getCurrentUser()) {
            firebaseAuth.getCurrentUser()
                    .sendEmailVerification()
                    .addOnCompleteListener(verifyOnCompleteListener);
            return verifyCompletableSubject;
        } else {
            return Single.error(new Exception("current user null"));
        }
    }

    public Single<FirebaseUser> login(String account, String password) {
        firebaseAuth.signInWithEmailAndPassword(account, password)
                .addOnCompleteListener(loginOnCompleteListener);
        return loginCompletableSubject;
    }

    public Single<FirebaseUser> linkCredential(String account, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(account, password);
        firebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(linkCredentialOnCompleteListener);
        return linkWithCredentialCompletableSubject;
    }

    public Completable logout() {
        return Completable.fromAction(() -> {
            firebaseAuth.signOut();
        });
    }

    OnCompleteListener<AuthResult> registerOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Timber.d("createUserWithEmail:success");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                registerCompletableSubject.onSuccess(user);
            } else {
                Timber.w("createUserWithEmail:failure", task.getException());
                registerCompletableSubject.onError(task.getException());
            }
        }
    };

    OnCompleteListener<Void> verifyOnCompleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                verifyCompletableSubject.onSuccess(firebaseAuth.getCurrentUser());
            } else {
                verifyCompletableSubject.onError(task.getException());
            }
        }
    };

    OnCompleteListener<AuthResult> loginOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Timber.d("signInWithEmail:success");
                FirebaseUser user = firebaseAuth.getCurrentUser();

                Timber.d("getProviders size = " + user.getProviders().size());
                for (String providerId : user.getProviders()) {
                    Timber.d("getProviders = " + providerId);
                }

                loginCompletableSubject.onSuccess(user);
            } else {

                Timber.w("signInWithEmail:failure", task.getException());
                loginCompletableSubject.onError(task.getException());
            }
        }
    };

    OnCompleteListener<AuthResult> linkCredentialOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Timber.d("signInWithEmail:success");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                linkWithCredentialCompletableSubject.onSuccess(user);
            } else {

                Timber.w("signInWithEmail:failure", task.getException());
                linkWithCredentialCompletableSubject.onError(task.getException());
            }
        }
    };
}
