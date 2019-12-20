package untitled.example.com.firebasesurvey.domain.interactor.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import untitled.example.com.firebasesurvey.domain.model.User;

/**
 * Created by Amy on 2019/4/2
 */

public class RealtimeDatabasePresenter implements FirebaseDatabasePresenter {
    /*Implement in parental control */
    private DatabaseReference database;

    public RealtimeDatabasePresenter() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void init() {

    }

    @Override
    public Completable addData(User user) {
        return null;
    }

    @Override
    public Completable setData(User user) {
        return null;
    }

    @Override
    public Flowable<User> getData(String uid) {
        return null;
    }
}
