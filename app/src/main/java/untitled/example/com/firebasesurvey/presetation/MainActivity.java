package untitled.example.com.firebasesurvey.presetation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import com.android.databinding.library.baseAdapters.BR;

import android.Manifest;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;
import untitled.example.com.firebasesurvey.R;
import untitled.example.com.firebasesurvey.Utility.BaseActivity;
import untitled.example.com.firebasesurvey.Utility.Define;
import untitled.example.com.firebasesurvey.Utility.SharedPrefMgr;
import untitled.example.com.firebasesurvey.domain.model.User;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    FirebaseViewModel viewModel;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;

    String verificationId = "";

    private final int PERMISSION_RESULT_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding binding = DataBindingUtil.setContentView(activity, R.layout.activity_main);
        ButterKnife.bind(this, binding.getRoot());
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FirebaseViewModel(context, activity);
            }
        }).get(FirebaseViewModel.class);

        binding.setVariable(BR.firebaseViewModel, viewModel);
        getPermission();
        setScreen(getClass().getSimpleName());
    }

    private void getPermission() {
        String[] perms = {Manifest.permission.GET_ACCOUNTS};
        if (EasyPermissions.hasPermissions(activity, perms)) {

        } else {
            EasyPermissions.requestPermissions(activity, "need permission",
                    PERMISSION_RESULT_CODE, perms);
        }
    }

    @Override
    protected void onResume() {
        if (getIntent() != null && getIntent().getData() != null) {
            Timber.d("getIntent().getData = " + getIntent().getData().toString());
        }
        if (null != getIntent() && null != getIntent().getData()) {
            Timber.d("EmailLinkLogin onResume");
            viewModel.initLoginType(Define.LoginType.EMAIL_LINK);
            viewModel.login(etAccount.getText().toString(), getIntent().getData().toString())
                    .subscribe(() -> {
                        setUserUid();
                        Timber.d("EmailLinkLogin subscribe");
                        Toast.makeText(context, "EmailLinkLogin subscribe", Toast.LENGTH_LONG).show();

                    }, this::errorhandle);
        }
        super.onResume();
    }

    public void EmailRegister(View view) {
        Timber.d("EmailRegister");

        setEvent("EmailRegister", etAccount.getText().toString() + " " + etPassword.getText().toString());

        viewModel.initLoginType(Define.LoginType.EMAIL);
        viewModel.register(etAccount.getText().toString(), etPassword.getText().toString())
                .subscribe(() -> {
                    setUserUid();
                    Timber.d("EmailRegister subscribe");
                    Toast.makeText(context, "EmailRegister subscribe", Toast.LENGTH_LONG).show();

                }, this::errorhandle);
    }

    public void EmailLogin(View view) {
        Timber.d("EmailLogin");

        setEvent("EmailLogin", etAccount.getText().toString() + " " + etPassword.getText().toString());

        viewModel.initLoginType(Define.LoginType.EMAIL);
        viewModel.login(etAccount.getText().toString(), etPassword.getText().toString())
                .subscribe(() -> {
                    setUserUid();
                    Timber.d("EmailLogin subscribe");
                    Toast.makeText(context, "EmailLogin subscribe", Toast.LENGTH_LONG).show();
                }, this::errorhandle);
    }

    public void sendEmailLink(View view) {
        Timber.d("sendEmailLink");
        setEvent("sendEmailLink", etAccount.getText().toString());
        viewModel.initLoginType(Define.LoginType.EMAIL_LINK);
        viewModel.sendEmailLink(etAccount.getText().toString())
                .subscribe(() -> {
                    Timber.d("sendEmailLink subscribe");
                    Toast.makeText(context, "sendEmailLink subscribe", Toast.LENGTH_LONG).show();
                }, this::errorhandle);
    }

    public void GoogleLogin(View view) {
        Timber.d("GoogleLogin");

        setEvent("GoogleLogin", "");

        viewModel.initLoginType(Define.LoginType.GOOGLE);
        viewModel.login("", "")
                .subscribe(() -> {
                    setUserUid();
                    Timber.d("GoogleLogin subscribe");
                    Toast.makeText(context, "GoogleLogin subscribe", Toast.LENGTH_LONG).show();
                }, this::errorhandle);
    }

    public void FBLogin(View view) {
        Timber.d("FBLogin");

        setEvent("FBLogin", "");

        viewModel.initLoginType(Define.LoginType.FACEBOOK);
        viewModel.login("", "")
                .subscribe(() -> {
                    setUserUid();
                    Timber.d("FBLogin subscribe");
                    Toast.makeText(context, "FBLogin subscribe", Toast.LENGTH_LONG).show();
                }, this::errorhandle);
    }

    public void lineLogin(View view) {
        Timber.d("lineLogin");
        setEvent("lineLogin", "");

        viewModel.initLoginType(Define.LoginType.LINE);
        viewModel.login("", "")
                .subscribe(() -> {
                    setUserUid();
                    Timber.d("lineLogin subscribe");
                    Toast.makeText(context, "lineLogin subscribe", Toast.LENGTH_LONG).show();
                }, this::errorhandle);

//        viewModel.login("eyJhbGciOiJIUzI1NiJ9.htVFn2fhm1z3dsKNeYkyB_AgFo9fwVQRsGzK856kMytJrFnD2T6oTlhFNRF9OgR04cmmPOBGd2t28N0rWqEiSTbuNMTr9TCnxXm222ewhJAQDeKW8gzjjwTpF1uIN1ubVbbBQ1MIl7fSxtfLr8zwmepP2u2mT14BizTKld2dyj0.ZX3sPKEAsBGWHJXYhFq2SoMmkoHi79NnTY8NQLgfdvo", "")
//                .subscribe(() -> Timber.d("lineLogin subscribe"), this::errorhandle);
        //viewModel.login("", "").subscribe(() -> Timber.d("lineLogin subscribe"), this::errorhandle);
    }

    public void phoneVerify(View view) {
        Timber.d("phoneVerify");
        setEvent("phoneVerify", "");
        viewModel.initLoginType(Define.LoginType.PHONE);
        viewModel.verify(etPhone.getText().toString())
                .subscribe(verificationId -> {
                    Timber.d("phoneVerify subscribe");
                    Toast.makeText(context, "phoneVerify subscribe", Toast.LENGTH_LONG).show();
                    this.verificationId = verificationId;
                });//,this::errorhandle);
    }

    public void phoneLogin(View view) {
        Timber.d("phoneLogin verificationId = " + verificationId);
        setEvent("phoneLogin", verificationId);

        if (!verificationId.isEmpty()) {
            viewModel.login(verificationId, "123456")
                    .subscribe(() -> {
                        setUserUid();
                        Timber.d("phoneLogin subscribe");
                        Toast.makeText(context, "phoneLogin subscribe", Toast.LENGTH_LONG).show();
                        verificationId = "";
                    }, this::errorhandle);
        } else {
            Toast.makeText(context, "verificationId is empty", Toast.LENGTH_LONG).show();
        }
    }

    public void addPhone(View view) {
        Timber.d("phoneLogin verificationId = " + verificationId);

        setEvent("addPhone", verificationId);

        if (!verificationId.isEmpty()) {
            viewModel.addPhone(verificationId, "123456")
                    .subscribe(() -> {
                        Timber.d("addPhone subscribe");
                        Toast.makeText(context, "addPhone subscribe", Toast.LENGTH_LONG).show();
                        verificationId = "";
                    }, this::errorhandle);
        } else {
            Toast.makeText(context, "verificationId is empty", Toast.LENGTH_LONG).show();
        }
    }


    public void logout(View view) {
        Timber.d("logout");

        setEvent("logout", "");

        viewModel.logout().subscribe(() -> {
            Timber.d("logout");
            Toast.makeText(context, "logout", Toast.LENGTH_LONG).show();

        }, this::errorhandle);
    }

    public void getUserCF(View view) {
        Timber.d("getUserCF");

        setEvent("getUserCF", "");

        viewModel.initDBType(Define.DBType.CLOUD_FIRESTORE);
        viewModel.getUser("FHJdaLcxoRej6WSlipXY").subscribe(user -> {
            Timber.d(String.valueOf(user));
        }, this::errorhandle);
    }

    public void addUserCF(View view) {
        Timber.d("addUserCF");
        setEvent("addUserCF", "");

        viewModel.initDBType(Define.DBType.CLOUD_FIRESTORE);
        viewModel.addUser(User.newBuilder().setUid("123124124").setName("shszd").setPhone("+88691111111").setAge(20).build()).subscribe(() -> {
            Timber.d("addUserCF subscribe");
        }, this::errorhandle);
    }

    public void setUserCF(View view) {
        Timber.d("setUserCF");
        setEvent("setUserCF", "");

        viewModel.initDBType(Define.DBType.CLOUD_FIRESTORE);
        viewModel.setUser(User.newBuilder().setUid("123124124").setName("shszd").setPhone("+88600000000").setAge(20).build()).subscribe(() -> {
            Timber.d("setUserCF subscribe");
        }, this::errorhandle);
    }

    public void getUserRD(View view) {
        Timber.d("getUserRD");
        setEvent("getUserRD", "");

        viewModel.initDBType(Define.DBType.REALTIME_DATABASE);
        viewModel.getUser("FHJdaLcxoRej6WSlipXY").subscribe(user -> {
            Timber.d(String.valueOf(user));
        }, this::errorhandle);
    }

    public void updateIcon(View view) {
        Timber.d("updateIcon");
        setEvent("updateIcon", "");

        viewModel.initStorageType(Define.StorageType.ICON);
        viewModel.updateIcon();
    }

    public void sendNotification(View view) {
        Timber.d("sendNotification");
        setEvent("sendNotification", "");
        String fcmToken = SharedPrefMgr.loadSharedPref(context, Define.SPFS_FCM_TOKEN, "", Define.SPFS_CATEGORY);
        viewModel.sendNotification(fcmToken, "test title", "test content")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Timber.d("sendNotification subscribe");
                }, this::errorhandle);
    }

    private void setUserUid() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //setUserId(firebaseAuth.getCurrentUser().getUid());
        setUserId(firebaseAuth.getCurrentUser().getUid());
        //setUserProperty("uid", firebaseAuth.getCurrentUser().getUid());
        setUserProperty("email", firebaseAuth.getCurrentUser().getEmail());
        setUidEvent("uid", firebaseAuth.getCurrentUser().getUid());
    }

    private void errorhandle(Throwable throwable) {
        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_LONG).show();
        throwable.printStackTrace();
        if (throwable instanceof FirebaseAuthInvalidCredentialsException) {
            // Toast.makeText(context, "[+][country code][subscriber number including area code]", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
