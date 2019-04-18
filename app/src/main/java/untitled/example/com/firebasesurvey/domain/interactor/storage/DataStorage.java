package untitled.example.com.firebasesurvey.domain.interactor.storage;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Amy on 2019/4/2
 */

public interface DataStorage<T> {

    void update(T object);

    Single<T> download(String fileName);
}
