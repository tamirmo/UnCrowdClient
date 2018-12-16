package tamirmo.uncrowd.businesses.list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import tamirmo.uncrowd.communication.AdvancedSearchLiveData;
import tamirmo.uncrowd.communication.AllBusinessesLiveData;
import tamirmo.uncrowd.communication.AlternativesBusinessesLiveData;
import tamirmo.uncrowd.communication.BusinessesLiveData;
import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.search.AdvancedSearchInput;

import java.util.List;

public class BusinessesFragmentViewModel extends AndroidViewModel {

    private BusinessesLiveData businessesLiveData;


    public BusinessesFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Business>> getAllBusinesses() {
        if (businessesLiveData == null) {
            businessesLiveData = new AllBusinessesLiveData(getApplication());
            businessesLiveData.loadBusinesses();
        }
        return businessesLiveData;
    }

    public LiveData<List<Business>> getAlternativeBusinesses(long originalBusinessId) {
        if (businessesLiveData == null) {
            businessesLiveData = new AlternativesBusinessesLiveData(getApplication(), originalBusinessId);
            businessesLiveData.loadBusinesses();
        }
        return businessesLiveData;
    }

    public LiveData<List<Business>> getAdvancedSearchBusinesses(AdvancedSearchInput advancedSearchInput) {
        if (businessesLiveData == null) {
            businessesLiveData = new AdvancedSearchLiveData(getApplication(), advancedSearchInput);
            businessesLiveData.loadBusinesses();
        }
        return businessesLiveData;
    }

    public LiveData<List<Business>> getBusinesses(){
        return businessesLiveData;
    }

    public LiveData<List<Business>> refresh(){
        businessesLiveData.loadBusinesses();
        return businessesLiveData;
    }
}
