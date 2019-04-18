package untitled.example.com.firebasesurvey.domain.repository.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import untitled.example.com.firebasesurvey.Utility.RetrofitGsonTypeAdapterFactory;

/**
 * Created by Amy on 2019/4/3
 */

public class FirebaseCloudMessageApiClient {

    private final static String FIREBASE_DOMAIN = "https://fcm.googleapis.com";

    private static final FirebaseCloudMessageApiClient instance = new FirebaseCloudMessageApiClient();
    private FirebaseCloudMessageApi mFirebaseCloudMessageApi;

    public static FirebaseCloudMessageApiClient getInstance() {
        return instance;
    }

    public FirebaseCloudMessageApiClient() {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(RetrofitGsonTypeAdapterFactory.create()).create();
        //Gson gson = new GsonBuilder().setLenient().create();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(loggingInterceptor);
        builder.connectTimeout(4, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(FIREBASE_DOMAIN)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        mFirebaseCloudMessageApi = retrofit.create(FirebaseCloudMessageApi.class);
    }

    public static FirebaseCloudMessageApi getFirebaseFunctionApi() {
        return getInstance().mFirebaseCloudMessageApi;
    }
}
