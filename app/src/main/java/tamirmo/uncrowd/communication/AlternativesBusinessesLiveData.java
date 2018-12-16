package tamirmo.uncrowd.communication;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import tamirmo.uncrowd.data.Business;

public class AlternativesBusinessesLiveData extends BusinessesLiveData {
    private long originalBusinessId;

    public AlternativesBusinessesLiveData(Context context, long originalBusinessId) {
        super(context);
        this.originalBusinessId = originalBusinessId;
    }

    @Override
    public void loadBusinesses() {
        new LoadAlternativesTask(this, originalBusinessId).execute();
    }

    private static class LoadAlternativesTask extends AsyncTask<Void,Void,List<Business>> {

        WeakReference<AlternativesBusinessesLiveData> loadBusinessesWeakReference;
        long originalBusinessId;

        LoadAlternativesTask(AlternativesBusinessesLiveData loadBusinesses, long originalBusinessId){
            this.loadBusinessesWeakReference = new WeakReference<>(loadBusinesses);
            this.originalBusinessId = originalBusinessId;
        }

        @Override
        protected List<Business> doInBackground(Void... voids) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // TODO: Load from server
            if(loadBusinessesWeakReference.get() != null){
                /*List<Business> list = new ArrayList<>();
                Business b = new Business();
                b.setName("Alternative 1");
                b.setLat(32.114825);
                b.setLon(34.816782);
                list.add(b);

                b = new Business();
                b.setName("Alternative 2");
                b.setLat(32.113587);
                b.setLon(34.817685);
                list.add(b);

                UncrowdManager.getInstance().updateBusinesses(list);
                return list;*/
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
