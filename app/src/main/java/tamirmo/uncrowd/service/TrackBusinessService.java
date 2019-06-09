package tamirmo.uncrowd.service;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import tamirmo.uncrowd.R;
import tamirmo.uncrowd.alternatives.AlternativesActivity;
import tamirmo.uncrowd.business.view.BusinessExtendedDetailsActivity;
import tamirmo.uncrowd.communication.HttpUtilities;
import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.logic.UncrowdManager;

/**
 * A service that starts when the user chooses "On my way" and starts tracking the chosen business's crowd.
 * The service has a notification for details containing the updates of the business.
 */
public class TrackBusinessService extends Service {
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String BUSINESS_ID_EXTRA = "BUSINESS_ID_EXTRA";
    private static final String NOTIFICATION_CHANNEL_ID = "BusinessNotification";
    private static final int NOTIFICATION_ID = 130;
    // The time in milliseconds to wait between checking the business's crowd.
    private static final int BUSINESS_QUERY_INTERVAL_MS = 10 * 1000;

    private NotificationCompat.Builder notification;
    private Timer trackBusinessTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand ");

        if(intent != null && intent.getAction() != null)
        {
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    System.out.println("onStartCommand start");
                    long businessId = intent.getLongExtra(BUSINESS_ID_EXTRA, -1);
                    startForegroundService(businessId);
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    System.out.println("onStartCommand stop");
                    stopForegroundService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void startForegroundService(long businessId) {
        Business business = UncrowdManager.getInstance().getBusinessesMap().get(businessId);

        // Create notification click intent:

        Intent notificationClickIntent = new Intent(this, BusinessExtendedDetailsActivity.class);
        notificationClickIntent .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationClickIntent .putExtra(BusinessExtendedDetailsActivity.BUSINESS_ID, businessId);
        PendingIntent notificationClickPendingIntent = PendingIntent.getActivity(this, 0,
                notificationClickIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create notification switch action intent:

        Intent switchIntent = new Intent(this, AlternativesActivity.class);
        switchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switchIntent.putExtra(AlternativesActivity.BUSINESS_ID, businessId);
        PendingIntent switchPendingIntent = PendingIntent.getActivity(this, 0,
                switchIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create notification end action intent:

        Intent endIntent = new Intent(this, EndBroadcastReceiver.class);
        PendingIntent endPendingIntent =
                PendingIntent.getBroadcast(this, 0, endIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel((NotificationManager) getSystemService(NOTIFICATION_SERVICE));

        notification =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.app_logo_144)
                        .setContentIntent(notificationClickPendingIntent)
                        .setContentTitle(business.getName())
                        // Switch action:
                        .addAction(R.drawable.app_logo_144,
                                getString(R.string.notification_action_switch),
                                switchPendingIntent)
                        // End action:
                        .addAction(R.drawable.app_logo_144,
                                getString(R.string.notification_action_end),
                                endPendingIntent);

        updateNotificationText(business, null);

        // Start foreground service with the created notification
        startForeground(NOTIFICATION_ID, notification.build());

        // Starting a timer to track the business:

        if (trackBusinessTimer != null) {
            trackBusinessTimer.cancel();
        }
        trackBusinessTimer = new Timer();
        trackBusinessTimer.schedule(new TrackBusinessTimer(businessId), 0, BUSINESS_QUERY_INTERVAL_MS);
    }

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager) {

        String description = "Live notification for a business crowd details.";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }

    private void stopForegroundService() {

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();

        // No need for the timer anymore
        if (trackBusinessTimer != null) {
            trackBusinessTimer.cancel();
        }
    }

    private void updateNotification(Business originalBusiness, Business updatedBusiness) {
        updateNotificationText(originalBusiness, updatedBusiness);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    /**
     * Update the notification cause there was an update for a business
     * @param originalBusiness The Business details for when the user choose to start tracking
     * @param updatedBusiness The updated business
     */
    private void updateNotificationText(Business originalBusiness, Business updatedBusiness) {
        notification.setContentText(getString((R.string.notification_neutral_notification)));

        if (updatedBusiness != null) {
            if (updatedBusiness.getCrowdCount() > originalBusiness.getCrowdCount()) {
                notification.setContentText(getString(R.string.notification_worst_notification,
                        updatedBusiness.getCrowdCount(), originalBusiness.getCrowdCount()));
            } else if (updatedBusiness.getCrowdCount() < originalBusiness.getCrowdCount()) {
                notification.setContentText(getString(R.string.notification_better_notification,
                        updatedBusiness.getCrowdCount(), originalBusiness.getCrowdCount()));
            }
        }
    }

    /**
     * A timer asking for an update of the business
     */
    private class TrackBusinessTimer extends TimerTask {

        private long businessId;
        private Business firstTimeBusinessDetails;

        TrackBusinessTimer(long businessId){
            this.businessId = businessId;
        }

        @Override
        public void run() {
            try{
                System.out.println("Uncrowd - timer start");

                String url = HttpUtilities.getBaseServerUrl() + String.format("Businessinfo/%d/",
                        businessId);

                RestTemplate restTemplate = HttpUtilities.createRestTemplate();
                ResponseEntity<Business> responseEntity =
                        restTemplate.getForEntity(url, tamirmo.uncrowd.data.Business.class);

                // Saving the first query to reference later
                // (alerting the user the business is more crowded [or the other way around])
                if (firstTimeBusinessDetails == null) {
                    firstTimeBusinessDetails = responseEntity.getBody();
                }

                // Updating the manager ad the notification
                UncrowdManager.getInstance().updateBusiness(responseEntity.getBody());
                updateNotification(firstTimeBusinessDetails, responseEntity.getBody());

                System.out.println("Uncrowd - timer end");
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
