package tamirmo.uncrowd.communication;

import android.content.Context;
import android.os.AsyncTask;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tamirmo.uncrowd.logic.UncrowdManager;
import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.data.BusinessType;

public class AllBusinessesLiveData extends BusinessesLiveData {

    public AllBusinessesLiveData(Context context) {
        super(context);
    }

    @Override
    public void loadBusinesses() {
        new LoadAllBusinessesTask(this).execute();
    }

    private static class LoadAllBusinessesTask extends AsyncTask<Void,Void,List<Business>> {

        WeakReference<AllBusinessesLiveData> loadBusinessesWeakReference;

        LoadAllBusinessesTask(AllBusinessesLiveData loadBusinesses){
            this.loadBusinessesWeakReference = new WeakReference<>(loadBusinesses);
        }

        @Override
        protected List<Business> doInBackground(Void... voids) {

            if(loadBusinessesWeakReference.get() != null) {

                try {

                    // Checking if we need to get all the types
                    if (UncrowdManager.getInstance().initializeTypesHandlerIfNeeded()) {
                        RestTemplate restTemplate = HttpUtilities.createRestTemplate();

                        // Getting the types from the server:
                        final String url = HttpUtilities.getBaseServerUrl() + "AllBusinessTypes";
                        ResponseEntity<BusinessType[]> responseEntity = restTemplate.getForEntity(url, BusinessType[].class);

                        // Updating the types in the types handler
                        UncrowdManager.getInstance().getBusinessTypesHandler().setTypes(responseEntity.getBody());
                    }

                    RestTemplate restTemplate = HttpUtilities.createRestTemplate();
                    // TODO: Add the user's location
                    final String url2 = HttpUtilities.getBaseServerUrl() + "AllBusinesses/2.5/3.4?size=5&page=0";

                    ResponseEntity<tamirmo.uncrowd.data.Business[]> responseEntity2 =
                            restTemplate.getForEntity(url2, tamirmo.uncrowd.data.Business[].class);

                    List<Business> businesses = new ArrayList<>(Arrays.asList(responseEntity2.getBody()));
                    UncrowdManager.getInstance().updateBusinesses(businesses);

                    return businesses;
                } catch (ResourceAccessException ex){
                    ex.printStackTrace();
                    // null indicating a connection error
                    return null;
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                    // TODO: Differentiate connection error and this error
                    // null indicating an error
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Business> data) {
            if(loadBusinessesWeakReference.get() != null) {
                loadBusinessesWeakReference.get().setValue(data);
            }
        }
    }
}
