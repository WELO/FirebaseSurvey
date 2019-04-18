package untitled.example.com.firebasesurvey.domain.interactor.login;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Amy on 2019/4/1
 */

public interface SocialLogin {
    void init();

    Single<String> verify(String account);

    Completable login(String account, String password);

    Completable logout();


}
