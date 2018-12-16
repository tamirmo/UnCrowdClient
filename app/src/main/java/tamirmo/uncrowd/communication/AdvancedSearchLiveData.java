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

import tamirmo.uncrowd.search.AdvancedSearchInput;
import tamirmo.uncrowd.data.Business;

public class AdvancedSearchLiveData extends BusinessesLiveData {
    private AdvancedSearchInput advancedSearchInput;

    public AdvancedSearchLiveData(Context context, AdvancedSearchInput advancedSearchInput) {
        super(context);
        this.advancedSearchInput = advancedSearchInput;
    }

    @Override
    public void loadBusinesses() {
        new LoadAdvancedSearchTask(this, advancedSearchInput).execute();
    }

    private static class LoadAdvancedSearchTask extends AsyncTask<Void,Void,List<Business>> {

        WeakReference<AdvancedSearchLiveData> loadBusinessesWeakReference;
        AdvancedSearchInput advancedSearchInput;

        LoadAdvancedSearchTask(AdvancedSearchLiveData loadBusinesses, AdvancedSearchInput advancedSearchInput){
            this.loadBusinessesWeakReference = new WeakReference<>(loadBusinesses);
            this.advancedSearchInput = advancedSearchInput;
        }

        @Override
        protected List<Business> doInBackground(Void... voids) {

            try {
                // TODO: Load from server
                if (loadBusinessesWeakReference.get() != null) {

                    // Converting the types to one string separated by ','
                    StringBuilder typesStringBuilder = new StringBuilder();
                    String prefix = "";
                    if (advancedSearchInput.getSelectedTypesList() != null) {
                        for (Long id : advancedSearchInput.getSelectedTypesList()) {
                            typesStringBuilder.append(prefix);
                            prefix = ",";
                            typesStringBuilder.append(id);
                        }
                    }

                    String url = HttpUtilities.getBaseServerUrl() + String.format("BusinessWithParameters/%s/%d/%f/%f/",
                            advancedSearchInput.getName(),
                            advancedSearchInput.getRadius(),
                            // LAT
                            32.124825,
                            // LON
                            34.826782);

                    if (advancedSearchInput.getSelectedTypesList() != null && advancedSearchInput.getSelectedTypesList().size() > 0) {
                        url += "?types=" + typesStringBuilder.toString();
                    }
                    RestTemplate restTemplate = HttpUtilities.createRestTemplate();

                    ResponseEntity<Business[]> responseEntity =
                            restTemplate.getForEntity(url, tamirmo.uncrowd.data.Business[].class);

                    return new ArrayList<>(Arrays.asList(responseEntity.getBody()));
                }
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
