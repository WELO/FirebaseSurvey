package untitled.example.com.firebasesurvey.domain.interactor;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import untitled.example.com.firebasesurvey.domain.interactor.database.FirebaseDatabasePresenter;
import untitled.example.com.firebasesurvey.domain.model.User;

/**
 * Created by Amy on 2019/4/2
 */

public class TestDatabase {

    FirebaseDatabasePresenter mFirebaseDatabasePresenter;

    public TestDatabase(FirebaseDatabasePresenter firebaseDatabasePresenter) {
        this.mFirebaseDatabasePresenter = firebaseDatabasePresenter;
        firebaseDatabasePresenter.init();
    }

    public Completable addData(User user){
        return mFirebaseDatabasePresenter.addData(user);
    }

    public Completable setData(User user){
        return mFirebaseDatabasePresenter.addData(user);
    }

    public Flowable<User> getData(String uid) {
        return mFirebaseDatabasePresenter.getData(uid);
    }
}
