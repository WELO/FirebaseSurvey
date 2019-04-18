package untitled.example.com.firebasesurvey.domain.interactor;

import android.content.Intent;

import io.reactivex.Completable;
import io.reactivex.Single;
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

    private final Define.LoginType loginType;
    private SocialLogin socialLogin;
    private EmailLogin emailLogin;
    private EmailLinkLogin emailLinkLogin;

    public Authentication(Define.LoginType loginType, EmailLogin emailLogin) {
        this.loginType = loginType;
        this.emailLogin = emailLogin;
        this.emailLogin.init();
    }

    public Authentication(Define.LoginType loginType, EmailLinkLogin emailLinkLogin) {
        this.loginType = loginType;
        this.emailLinkLogin = emailLinkLogin;
        this.emailLinkLogin.init();
    }

    public Authentication(Define.LoginType loginType, SocialLogin socialLogin) {
        this.loginType = loginType;
        this.socialLogin = socialLogin;
        this.socialLogin.init();
    }

    public Single<String> verify(String account) {
        return socialLogin.verify(account);
    }

    public Completable register(String account, String password) {
        if (null != emailLogin && loginType.equals(Define.LoginType.EMAIL)) {
            return emailLogin.register(account, password);
        }
        return Completable.error(new Exception("Not Email type"));
    }

    public Completable sendEmailLink(String email) {
        if (null != emailLinkLogin && loginType.equals(Define.LoginType.EMAIL_LINK)) {
            return emailLinkLogin.sendLink(email);
        }
        return Completable.error(new Exception("Not Email type"));
    }

    public Completable login(String account, String password) {
        if (loginType.equals(Define.LoginType.EMAIL)) {
            return emailLogin.login(account, password);
        } else if (loginType.equals(Define.LoginType.EMAIL_LINK)) {
            return emailLinkLogin.login(account, password);
        }
        return socialLogin.login(account, password);
    }

    public Completable linkCredential(String account, String password) {
        if (loginType.equals(Define.LoginType.EMAIL)) {
            return emailLogin.login(account, password);
        }
        return socialLogin.login(account, password);
    }

    public Completable logout() {
        if (loginType.equals(Define.LoginType.EMAIL)) {
            return emailLogin.logout();
        } else if (loginType.equals(Define.LoginType.EMAIL_LINK)) {
            return emailLinkLogin.logout();
        }
        return socialLogin.logout();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (loginType) {
            case LINE:
                ((LineLogin) socialLogin).onActivityResult(requestCode, resultCode, data);
                break;
            case FACEBOOK:
                ((FacebookLogin) socialLogin).onActivityResult(requestCode, resultCode, data);
                break;
            case GOOGLE:
                ((GoogleLogin) socialLogin).onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}
