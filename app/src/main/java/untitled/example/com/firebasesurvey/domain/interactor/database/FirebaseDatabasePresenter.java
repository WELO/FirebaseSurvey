package untitled.example.com.firebasesurvey.domain.interactor.database;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import untitled.example.com.firebasesurvey.domain.model.User;

/**
 * Created by Amy on 2019/4/2
 */

public interface FirebaseDatabasePresenter {

    void init();

    Completable addData(User user);

    Completable setData(User user);

    Flowable<User> getData(String uid);

}
