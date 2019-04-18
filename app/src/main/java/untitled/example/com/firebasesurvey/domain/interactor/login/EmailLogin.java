package untitled.example.com.firebasesurvey.domain.interactor.login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.subjects.CompletableSubject;
import timber.log.Timber;

/**
 * Created by Amy on 2019/4/10
 */

public class EmailLogin {
    private final FirebaseAuth firebaseAuth;
    private CompletableSubject registerCompletableSubject = CompletableSubject.create();
    private CompletableSubject loginCompletableSubject = CompletableSubject.create();


    public EmailLogin(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void init() {

    }

    public Completable register(String account, String password) {
        firebaseAuth.createUserWithEmailAndPassword(account, password)
                .addOnCompleteListener(registerOnCompleteListener);
        return registerCompletableSubject;
    }

    public Completable login(String account, String password) {
        firebaseAuth.signInWithEmailAndPassword(account, password)
                .addOnCompleteListener(loginOnCompleteListener);
        return loginCompletableSubject;
    }

    public Completable logout() {
        return Completable.fromAction(() -> {
            firebaseAuth.signOut();
        });
    }
    OnCompleteListener<AuthResult> registerOnCompleteListener = new OnCompleteListener<AuthResult>(){
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Timber.d("createUserWithEmail:success");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                registerCompletableSubject.onComplete();
            } else {
                Timber.w("createUserWithEmail:failure", task.getException());
                registerCompletableSubject.onError(task.getException());
            }
        }
    };
    OnCompleteListener<AuthResult> loginOnCompleteListener = new OnCompleteListener<AuthResult>(){
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Timber.d("signInWithEmail:success");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                loginCompletableSubject.onComplete();
            } else {

                Timber.w("signInWithEmail:failure", task.getException());
                loginCompletableSubject.onError(task.getException());
            }
        }
    };
}
