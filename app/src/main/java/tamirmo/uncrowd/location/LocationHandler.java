package tamirmo.uncrowd.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

@SuppressLint("MissingPermission")
public class LocationHandler extends LocationCallback {

    private static final int FINE_LOCATION_REQUEST_ID = 4;

    // Singleton:
    private static LocationHandler instance;
    public static LocationHandler getInstance(){
        if(instance == null){
            instance = new LocationHandler();
        }
        return instance;
    }

    private boolean isLocationPermGranted;
    private Location lastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private WeakReference<IOnLocationReceived> locationReceivedListener;

    private LocationHandler(){}

    public void init(Activity activity) {
        if(isLocationPermGranted || !askPermissionIfNeeded(activity)) {
            getLastLocation(activity);
        }
    }

    private void getLastLocation(Activity activity){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lastLocation = location;
                        }
                    }
                });
    }

    public void getCurrLocation(IOnLocationReceived locationReceivedListener){
        if(isLocationPermGranted) {
            this.locationReceivedListener = new WeakReference<>(locationReceivedListener);
            LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
                    setInterval(1000).
                    setFastestInterval(1000).
                    setNumUpdates(1);
            mFusedLocationClient.requestLocationUpdates(locationRequest, this, null);
        }
    }

    private boolean askPermissionIfNeeded(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    FINE_LOCATION_REQUEST_ID);

            return true;
        } else {
            isLocationPermGranted = true;
            // Permission has already been granted
            return false;
        }
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull int[] grantResults){
        switch (requestCode) {
            case FINE_LOCATION_REQUEST_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    isLocationPermGranted = true;
                    // Getting the last location
                    getLastLocation(activity);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    isLocationPermGranted = false;
                }
            }
        }
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            return;
        }
        lastLocation = locationResult.getLastLocation();
        if(locationReceivedListener != null && locationReceivedListener.get() != null){
            locationReceivedListener.get().onLocationReceived(lastLocation);
        }
    }

    public Location getLastLocation(){
        return lastLocation;
    }

    public interface IOnLocationReceived{
        void onLocationReceived(Location lastLocation);
    }
}
