package tamirmo.uncrowd.communication;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import java.util.List;

import tamirmo.uncrowd.data.Business;

public abstract class BusinessesLiveData extends LiveData<List<Business>> {
    private final Context context;
    //private final FileObserver fileObserver;

    public BusinessesLiveData(Context context) {
        this.context = context;
        //String path = new File(context.getFilesDir(),
        //       "downloaded.json").getPath();
        /*fileObserver = new FileObserver(path) {
            @Override
            public void onEvent(int event, String path) {
                // The file has changed, so let’s reload the data
                loadData();
            }
        };*/
    }

    public abstract void loadBusinesses();

    @Override
    protected void onActive() {
        //fileObserver.startWatching();
    }
    @Override
    protected void onInactive() {
        //fileObserver.stopWatching();
    }

}
