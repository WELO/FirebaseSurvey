package untitled.example.com.firebasesurvey.presetation;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import untitled.example.com.firebasesurvey.Utility.Define;
import untitled.example.com.firebasesurvey.domain.interactor.Authentication;
import untitled.example.com.firebasesurvey.domain.interactor.Storage;
import untitled.example.com.firebasesurvey.domain.interactor.TestDatabase;
import untitled.example.com.firebasesurvey.domain.interactor.database.CloudFirestore;
import untitled.example.com.firebasesurvey.domain.interactor.database.RealtimeDatabasePresenter;
import untitled.example.com.firebasesurvey.domain.interactor.login.EmailLinkLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.EmailLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.FacebookLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.FirebaseUserFile;
import untitled.example.com.firebasesurvey.domain.interactor.login.GoogleLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.LineLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.PhoneLogin;
import untitled.example.com.firebasesurvey.domain.interactor.storage.IconStorage;
import untitled.example.com.firebasesurvey.domain.model.User;
import untitled.example.com.firebasesurvey.domain.repository.FirebaseNotificationRepository;
import untitled.example.com.firebasesurvey.domain.repository.imp.FirebaseNotificationRepositoryImp;

/**
 * Created by Amy on 2019/4/1
 */

public class FirebaseViewModel extends ViewModel {

    private final Activity activity;
    private final Context context;
    private Authentication authentication;
    private TestDatabase testDatabase;
    private FirebaseAuth firebaseAuth;
    private Storage storage;
    private FirebaseUserFile firebaseUserFile;
    private FirebaseNotificationRepository firebaseNotificationRepository;
    private Define.LoginType loginType;

    public FirebaseViewModel(Context context, Activity activity) {
        this.activity = activity;
        this.context = context;
        FirebaseApp.initializeApp(context);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.useAppLanguage();
        firebaseUserFile = new FirebaseUserFile(firebaseAuth);
        firebaseNotificationRepository = new FirebaseNotificationRepositoryImp();

    }

    public void initLoginType(Define.LoginType loginType) {
        this.loginType = loginType;
        switch (loginType) {
            case EMAIL:
                authentication = new Authentication(loginType, new EmailLogin(firebaseAuth));
                break;
            case EMAIL_LINK:
                authentication = new Authentication(loginType, new EmailLinkLogin(firebaseAuth));
                break;
            case LINE:
                authentication = new Authentication(loginType, new LineLogin(activity, firebaseAuth));
                break;
            case GOOGLE:
                authentication = new Authentication(loginType, new GoogleLogin(activity, firebaseAuth));
                break;
            case PHONE:
                authentication = new Authentication(loginType, new PhoneLogin(activity, firebaseAuth));
                break;
            case FACEBOOK:
                authentication = new Authentication(loginType, new FacebookLogin(activity, firebaseAuth));
                break;
        }

    }

    public Define.LoginType getLoginType() {
        return loginType;
    }

    public void initDBType(Define.DBType dbType) {
        switch (dbType) {
            case CLOUD_FIRESTORE:
                testDatabase = new TestDatabase(new CloudFirestore());
                break;
            case REALTIME_DATABASE:
                testDatabase = new TestDatabase(new RealtimeDatabasePresenter());
                break;
        }

    }

    public void initStorageType(Define.StorageType storageType) {
        switch (storageType) {
            case ICON:
                storage = new Storage(storageType, new IconStorage(context));
                break;
            case OTHER:

                break;
        }

    }

    public Single<String> verify(String account) {
        if (null != authentication) {
            return authentication.verify(account);
        } else {
            return Single.error(new Exception("authentication is null"));
        }
    }

    public Completable register(String account, String password) {
        if (null != authentication) {
            return authentication.register(account, password);
        } else {
            return Completable.error(new Exception("authentication is null"));
        }
    }

    public Completable login(String account, String password) {
        if (null != authentication) {
            return authentication.login(account, password);
        } else {
            return Completable.error(new Exception("authentication is null"));
        }
    }

    public Completable sendEmailLink(String email) {
        if (null != authentication) {
            return authentication.sendEmailLink(email);
        } else {
            return Completable.error(new Exception("authentication is null"));
        }
    }

    public Completable addPhone(String verificationId, String code) {
        return firebaseUserFile.updatePhone(verificationId, code);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != authentication) {
            authentication.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Completable logout() {
        if (null != authentication) {
            return authentication.logout();
        } else {
            return Completable.error(new Exception("authentication is null"));
        }
    }

    public Completable setUser(User user) {
        if (null != testDatabase) {
            return testDatabase.setData(user);
        } else {
            return Completable.error(new Exception("testDatabase is null"));
        }
    }

    public Completable addUser(User user) {
        if (null != testDatabase) {
            return testDatabase.addData(user);
        } else {
            return Completable.error(new Exception("testDatabase is null"));
        }
    }

    public Flowable<User> getUser(String uid) {
        if (null != testDatabase) {
            return testDatabase.getData(uid);
        } else {
            return Flowable.error(new Exception("testDatabase is null"));
        }
    }

    public void updateIcon() {
        storage.update(null);
    }

    public Completable sendNotification(String to, String title, String content) {
        return firebaseNotificationRepository.sendNotification(to, title, content);
    }
}
