package untitled.example.com.firebasesurvey.domain.interactor;

import io.reactivex.Single;
import untitled.example.com.firebasesurvey.Utility.Define;
import untitled.example.com.firebasesurvey.domain.interactor.storage.DataStorage;

/**
 * Created by Amy on 2019/4/2
 */

public class Storage {

    private final Define.StorageType storageType;
    private final DataStorage dataStorage;

    public Storage(Define.StorageType storageType, DataStorage dataStorage) {
        this.storageType = storageType;
        this.dataStorage = dataStorage;
    }

    public <T> void update(T object) {
        dataStorage.update(object);
    }

    public <T> Single<T> download() {
        return dataStorage.download(null);
    }
}
