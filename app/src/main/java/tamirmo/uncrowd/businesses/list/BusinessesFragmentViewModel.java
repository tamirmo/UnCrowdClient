package tamirmo.uncrowd.businesses.list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.location.Location;
import android.support.annotation.NonNull;

import tamirmo.uncrowd.location.LocationHandler;
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

    LiveData<List<Business>> getAllBusinesses() {
        if (businessesLiveData == null) {
            businessesLiveData = new AllBusinessesLiveData(getApplication());
            businessesLiveData.loadBusinesses();
        }
        return businessesLiveData;
    }

    public LiveData<List<Business>> getAlternativeBusinesses(Business originalBusiness) {
        if (businessesLiveData == null) {
            businessesLiveData = new AlternativesBusinessesLiveData(getApplication(), originalBusiness);
            businessesLiveData.loadBusinesses();
        }
        return businessesLiveData;
    }

    LiveData<List<Business>> getAdvancedSearchBusinesses(AdvancedSearchInput advancedSearchInput) {
        if (businessesLiveData == null) {
            businessesLiveData = new AdvancedSearchLiveData(getApplication(), advancedSearchInput);
            businessesLiveData.loadBusinesses();
        }
        return businessesLiveData;
    }

    public LiveData<List<Business>> getBusinesses(){
        return businessesLiveData;
    }

    LiveData<List<Business>> refresh(){
        businessesLiveData.loadBusinesses();
        return businessesLiveData;
    }

    void sortByCrowd(){
        businessesLiveData.sortByCrowd();
    }

    void sortByLocation(final LocationHandler locationHandler){
        businessesLiveData.sortByLocation(locationHandler);
    }
}
