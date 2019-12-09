package untitled.example.com.firebasesurvey.domain.interactor;

import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.SingleSubject;
import untitled.example.com.firebasesurvey.Utility.Define;
import untitled.example.com.firebasesurvey.domain.interactor.login.EmailLinkLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.EmailLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.FacebookLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.GoogleLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.LineLogin;
import untitled.example.com.firebasesurvey.domain.interactor.login.SocialLogin;

/**
 * Created by Amy on 2019/4/1
 */

public class Authentication {

    private final @Define.LoginType
    int loginType;
    private SocialLogin socialLogin;
    private EmailLogin emailLogin;
    private EmailLinkLogin emailLinkLogin;

    public Authentication(@Define.LoginType int loginType, EmailLogin emailLogin) {
        this.loginType = loginType;
        this.emailLogin = emailLogin;
        this.emailLogin.init();
    }

    public Authentication(@Define.LoginType int loginType, EmailLinkLogin emailLinkLogin) {
        this.loginType = loginType;
        this.emailLinkLogin = emailLinkLogin;
        this.emailLinkLogin.init();
    }

    public Authentication(@Define.LoginType int loginType, SocialLogin socialLogin) {
        this.loginType = loginType;
        this.socialLogin = socialLogin;
        this.socialLogin.init();
    }

    public Single<String> verify(String account) {
        return socialLogin.verify(account);
    }

    public Single<FirebaseUser> register(String account, String password) {
        if (null != emailLogin && loginType == Define.EMAIL) {
            return emailLogin.register(account, password);
        }
        return Single.error(new Exception("Not Email type"));
    }

    public Single<FirebaseUser> verify() {
        if (null != emailLogin && loginType == Define.EMAIL) {
            return emailLogin.verify();
        }
        return Single.error(new Exception("Not Email type"));
    }

    public Single<FirebaseUser> linkEmailCredential(String account, String password) {
        if (null != emailLogin && loginType == Define.EMAIL) {
            return emailLogin.linkCredential(account, password);
        }
        return Single.error(new Exception("Not Email type"));
    }

    public Completable sendEmailLink(String email) {
        if (null != emailLinkLogin && loginType == Define.EMAIL_LINK) {
            return emailLinkLogin.sendLink(email);
        }
        return Completable.error(new Exception("Not Email type"));
    }

    public Single<FirebaseUser> login(String account, String password) {
        if (loginType == Define.EMAIL) {
            return emailLogin.login(account, password);
        } else if (loginType == Define.EMAIL_LINK) {
            return emailLinkLogin.login(account, password);
        }
        return socialLogin.login(account, password);
    }

    public Single<FirebaseUser> linkSocailCredential() {
        if (loginType == Define.EMAIL) {
            return Single.error(new Exception("no email"));
        }
        return socialLogin.linkCredential();
    }

    public Completable logout() {
        if (loginType == Define.EMAIL) {
            return emailLogin.logout();
        } else if (loginType == Define.EMAIL_LINK) {
            return emailLinkLogin.logout();
        }
        return socialLogin.logout();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (loginType) {
            case Define.LINE:
                ((LineLogin) socialLogin).onActivityResult(requestCode, resultCode, data);
                break;
            case Define.FACEBOOK:
                ((FacebookLogin) socialLogin).onActivityResult(requestCode, resultCode, data);
                break;
            case Define.GOOGLE:
                ((GoogleLogin) socialLogin).onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}
