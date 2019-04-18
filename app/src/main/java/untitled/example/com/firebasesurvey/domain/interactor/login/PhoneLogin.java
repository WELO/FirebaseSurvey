package untitled.example.com.firebasesurvey.domain.interactor.login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.SingleSubject;
import timber.log.Timber;

/**
 * Created by Amy on 2019/4/1
 */

public class PhoneLogin implements SocialLogin {

    private SingleSubject<String> verifySingleSubject = SingleSubject.create();
    private CompletableSubject loginCompletableSubject = CompletableSubject.create();
    private Activity activity;
    private final FirebaseAuth firebaseAuth;

    public PhoneLogin(Activity activity, FirebaseAuth firebaseAuth) {
        this.activity = activity;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void init() {
    }

    @Override
    public Single<String> verify(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                verificationStateChangedCallbacks);
        return verifySingleSubject;
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Timber.d(String.valueOf(phoneAuthCredential));

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            verifySingleSubject.onError(e);
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            verifySingleSubject.onSuccess(verificationId);
            // Save verification ID and resending token so we can use them later
            Timber.d("verificationId = " + verificationId + " \nforceResendingToken = " + forceResendingToken);
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String s) {
            Timber.d("onCodeAutoRetrievalTimeOut: " + s);
            //mVerificationId = s;
            super.onCodeAutoRetrievalTimeOut(s);

        }
    };

    @Override
    public Completable login(String verificationId, String code) {
        Timber.d("login mVerificationId = " + verificationId);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(authResultOnCompleteListener);
        return loginCompletableSubject;
    }

    OnCompleteListener<AuthResult> authResultOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Timber.d("signInWithCredential:success");

                FirebaseUser user = task.getResult().getUser();
                Timber.d("user = " + String.valueOf(user));
                loginCompletableSubject.onComplete();
                // ...
            } else {
                // Sign in failed, display a message and update the UI
                Timber.w("signInWithCredential:failure", task.getException());
                loginCompletableSubject.onError(task.getException());
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                }
            }
        }
    };

    @Override
    public Completable logout() {
        return Completable.fromAction(() -> firebaseAuth.signOut());
    }
}
