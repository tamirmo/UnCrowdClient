package tamirmo.uncrowd.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * A broadcast receiver invoked when the user chooses the "end" action on the business notification.
 */
public class EndBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Uncrowd end broadcast receiver");
        Intent stopServiceIntent = new Intent(context, TrackBusinessService.class);
        stopServiceIntent.setAction(TrackBusinessService.ACTION_STOP_FOREGROUND_SERVICE);
        context.startService(stopServiceIntent);
    }
}
