package untitled.example.com.firebasesurvey.domain.interactor.database;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.CompletableSubject;
import timber.log.Timber;
import untitled.example.com.firebasesurvey.domain.model.User;

/**
 * Created by Amy on 2019/4/2
 */

public class CloudFirestore implements FirebaseDatabasePresenter {
    private String COLLECTION_PARENT = "Parent";
    private String COLLECTION_KID = "kid";
    private FirebaseAuth mFirebaseAuth;
    private String tokenId;


    private CompletableSubject addDataCompletableSubject = CompletableSubject.create();
    private PublishProcessor<User> getDataPublishProcessor = PublishProcessor.create();

    FirebaseFirestore db;

    public CloudFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void init() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth != null && mFirebaseAuth.getCurrentUser() != null) {
            mFirebaseAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    tokenId = getTokenResult.getToken();
                    Timber.d("tokenId = " + tokenId);
                }
            });
        }
    }

    @Override
    public Completable addData(User user) {
        db.collection(COLLECTION_PARENT).document(user.getUid()).set(user)
                .addOnSuccessListener(mOnSuccessListener)
                .addOnFailureListener(mOnFailureListener);
        return addDataCompletableSubject;
    }

    @Override
    public Completable setData(User user) {
        db.collection(COLLECTION_PARENT).document(user.getUid()).set(user)
                .addOnSuccessListener(mOnSuccessListener)
                .addOnFailureListener(mOnFailureListener);

        return addDataCompletableSubject;
    }

    OnSuccessListener<Void> mOnSuccessListener = new OnSuccessListener<Void>() {

        @Override
        public void onSuccess(Void aVoid) {
            addDataCompletableSubject.onComplete();
        }
    };

    OnFailureListener mOnFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            addDataCompletableSubject.onError(e);
        }
    };

    @Override
    public Flowable<User> getData(String uid) {
//        final DocumentReference docRef = db.collection(COLLECTION_PARENT).document(uid);
//        docRef.addSnapshotListener(mEventListener);
      //  Query query = db.collection(COLLECTION_PARENT).whereGreaterThan("age",25).orderBy("name");
        Query query = db.collection(COLLECTION_PARENT).orderBy("phone");

        query.get().addOnSuccessListener(mSnapshotOnSuccessListener);
        return getDataPublishProcessor;
    }

    OnSuccessListener<QuerySnapshot> mSnapshotOnSuccessListener = new OnSuccessListener<QuerySnapshot>() {

        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                User user = User.newBuilder()
                        .setAge(Integer.parseInt(documentSnapshot.getData().get("age").toString()))
                        .setName(documentSnapshot.getData().get("name").toString())
                        .setPhone(documentSnapshot.getData().get("phone").toString())
                        .setUid(documentSnapshot.getData().get("uid").toString()).build();
                getDataPublishProcessor.onNext(user);
                Timber.d("Current data: " + documentSnapshot.getData());
            }
        }
    };

    EventListener<QuerySnapshot> mQuerySnapshotEventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                User user = User.newBuilder()
                        .setAge(Integer.parseInt(documentSnapshot.getData().get("age").toString()))
                        .setName(documentSnapshot.getData().get("name").toString())
                        .setPhone(documentSnapshot.getData().get("phone").toString())
                        .setUid(documentSnapshot.getData().get("uid").toString()).build();
                getDataPublishProcessor.onNext(user);
                Timber.d("Current data: " + documentSnapshot.getData());
            }
        }
    };

    EventListener<DocumentSnapshot> mEventListener = new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot snapshot,
                            @Nullable FirebaseFirestoreException e) {
            if (e != null) {
                Timber.w("Listen failed.", e);
                getDataPublishProcessor.onError(e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                User user = User.newBuilder()
                        .setAge(Integer.parseInt(snapshot.getData().get("age").toString()))
                        .setName(snapshot.getData().get("name").toString())
                        .setPhone(snapshot.getData().get("phone").toString())
                        .setUid(snapshot.getData().get("uid").toString()).build();
                getDataPublishProcessor.onNext(user);
                Timber.d("Current data: " + snapshot.getData());
            } else {
                getDataPublishProcessor.onError(new Exception("null"));
                Timber.d("Current data: null");
            }
        }
    };

}
