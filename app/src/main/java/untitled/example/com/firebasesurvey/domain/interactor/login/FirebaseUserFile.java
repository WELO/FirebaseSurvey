package untitled.example.com.firebasesurvey.domain.interactor.login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.subjects.CompletableSubject;
import timber.log.Timber;

/**
 * Created by Amy on 2019/4/12
 */

public class FirebaseUserFile {

    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private CompletableSubject updatePhoneCompletableSubject = CompletableSubject.create();
    private CompletableSubject updateEmailCompletableSubject = CompletableSubject.create();
    private CompletableSubject updateProfileCompletableSubject = CompletableSubject.create();


    public FirebaseUserFile(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        user = firebaseAuth.getCurrentUser();
    }

    public Completable updateProfile(String displayname, String photoUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayname)
                .setPhotoUri(Uri.parse(photoUri))
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(updateProfileOnCompleteListener)
                .addOnFailureListener(updateProfileOnFailureListener);
        return updateProfileCompletableSubject;
    }

    private OnCompleteListener<Void> updateProfileOnCompleteListener = new OnCompleteListener<Void>() {

        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                updateProfileCompletableSubject.onComplete();
            } else {
                updateProfileCompletableSubject.onError(task.getException());
            }
        }
    };
    private OnFailureListener updateProfileOnFailureListener = new OnFailureListener() {

        @Override
        public void onFailure(@NonNull Exception e) {
            updateProfileCompletableSubject.onError(e);
        }
    };


    public Completable updateEmail(String email) {
        user.updateEmail(email)
                .addOnCompleteListener(updateEmailOnCompleteListener)
                .addOnFailureListener(updateEmailOnFailureListener);
        return updateEmailCompletableSubject;
    }

    private OnCompleteListener<Void> updateEmailOnCompleteListener = new OnCompleteListener<Void>() {

        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                Timber.d(" firebaseAuth.getCurrentUser() = " + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber());
                updateEmailCompletableSubject.onComplete();
            } else {
                updateEmailCompletableSubject.onError(task.getException());
            }
        }
    };
    private OnFailureListener updateEmailOnFailureListener = new OnFailureListener() {

        @Override
        public void onFailure(@NonNull Exception e) {
            updateEmailCompletableSubject.onError(e);
        }
    };

    public Completable updatePhone(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        user.updatePhoneNumber(credential)
                .addOnCompleteListener(updatePhoneOnCompleteListener)
                .addOnFailureListener(updatePhoneOnFailureListener);
        return updatePhoneCompletableSubject;
    }

    private OnCompleteListener<Void> updatePhoneOnCompleteListener = new OnCompleteListener<Void>() {

        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                Timber.d(" firebaseAuth.getCurrentUser() = " + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber());
                updatePhoneCompletableSubject.onComplete();
            } else {
                updatePhoneCompletableSubject.onError(task.getException());
            }
        }
    };
    private OnFailureListener updatePhoneOnFailureListener = new OnFailureListener() {

        @Override
        public void onFailure(@NonNull Exception e) {
            updatePhoneCompletableSubject.onError(e);
        }
    };
}
