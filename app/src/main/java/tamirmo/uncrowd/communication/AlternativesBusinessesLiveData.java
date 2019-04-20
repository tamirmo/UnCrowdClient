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

import tamirmo.uncrowd.data.Business;

public class AlternativesBusinessesLiveData extends BusinessesLiveData {
    private Business originalBusiness;

    public AlternativesBusinessesLiveData(Context context, Business originalBusiness) {
        super(context);
        this.originalBusiness = originalBusiness;
    }

    @Override
    public void loadBusinesses() {
        new LoadAlternativesTask(this, originalBusiness).execute();
    }

    private static class LoadAlternativesTask extends AsyncTask<Void,Void,List<Business>> {

        WeakReference<AlternativesBusinessesLiveData> loadBusinessesWeakReference;
        Business originalBusiness;

        LoadAlternativesTask(AlternativesBusinessesLiveData loadBusinesses, Business originalBusiness){
            this.loadBusinessesWeakReference = new WeakReference<>(loadBusinesses);
            this.originalBusiness = originalBusiness;
        }

        @Override
        protected List<Business> doInBackground(Void... voids) {

            try {
                // TODO: Load from server
                if (loadBusinessesWeakReference.get() != null) {

                    String url = HttpUtilities.getBaseServerUrl() + String.format("Alternatives/%d/",
                            originalBusiness.getId());

                    RestTemplate restTemplate = HttpUtilities.createRestTemplate();

                    ResponseEntity<Business[]> responseEntity =
                            restTemplate.getForEntity(url, tamirmo.uncrowd.data.Business[].class);

                    List<Business> alternatives = new ArrayList<>(Arrays.asList(responseEntity.getBody()));

                    for(Business business : alternatives){
                        if (business.getCrowdLevel() < this.originalBusiness.getCrowdLevel()) {
                            business.setAlternativeRelation(Business.AlternativeRelation.BETTER);
                        }else if (business.getCrowdLevel() > this.originalBusiness.getCrowdLevel()) {
                            business.setAlternativeRelation(Business.AlternativeRelation.WORSE);
                        } else {
                            business.setAlternativeRelation(Business.AlternativeRelation.SAME);
                        }
                    }
                    return alternatives;
                }
            } catch (ResourceAccessException ex){
                ex.printStackTrace();
                // null indicating a connection error
                return null;
            }
            catch(Exception ex) {
                ex.printStackTrace();
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
