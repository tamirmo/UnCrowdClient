package tamirmo.uncrowd.map;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import tamirmo.uncrowd.business.view.BusinessExtendedDetailsActivity;
import tamirmo.uncrowd.business.view.BusinessView;
import tamirmo.uncrowd.R;
import tamirmo.uncrowd.logic.UncrowdManager;
import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.utilities.NavigationActivityStarted;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private GoogleMap googleMap;
    private boolean hasMarkersLoadedToMap;
    // Mapping each marker with it's record id
    private Map<Long, Marker> markersMap;
    private BusinessView businessView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.businesses_map_fragment);

        if(mapFragment != null) {
            // Loading map and requesting notification when the map is ready to be used.
            mapFragment.getMapAsync(this);
        }

        markersMap = new HashMap<>();

        businessView = findViewById(R.id.selected_business_view);
        businessView.setOnClickListener(this);

        findViewById(R.id.take_me_there_btn).setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Saving the map to change markers after
        this.googleMap = googleMap;

        addMarkersToMap();
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(marker.getTag() != null) {
            // The tag holds the if of the business connected to this marker
            onBusinessSelected(Long.valueOf(marker.getTag().toString()), false);
        }

        return false;
    }

    private void addMarkersToMap() {
        // Checking we have a map at this point
        if (googleMap != null) {
            // Avoiding adding the same markers twice
            if (!hasMarkersLoadedToMap) {
                long firstMarkerAddedId = -1;
                hasMarkersLoadedToMap = true;
                // Getting all the businesses from the manager to add markers for each business
                for (Business currBusiness : UncrowdManager.getInstance().getBusinessesList()) {
                    // Creating a marker and putting it on the map:

                    LatLng businessLocation = new LatLng(currBusiness.getLat(), currBusiness.getLon());
                    MarkerOptions marker = new MarkerOptions().position(businessLocation)
                            .title(currBusiness.getName());
                    Marker markerCreated = googleMap.addMarker(marker);

                    // Saving the id of the marker business
                    markerCreated.setTag(currBusiness.getId());

                    // Saving the marker to update if needed
                    markersMap.put(currBusiness.getId(), markerCreated);

                    // Saving the id of the first business to zoom on later
                    if(firstMarkerAddedId == -1) {
                        firstMarkerAddedId = currBusiness.getId();
                    }
                }

                // Zooming on the first business added and setting it's view
                onBusinessSelected(firstMarkerAddedId, true);
            }
        }
    }

    private void onBusinessSelected(long businessId, boolean startShowInfoTimer){
        Business selectedBusiness = UncrowdManager.getInstance().getBusinessesMap().get(businessId);

        // Selecting the row on the table
        businessView.setBusiness(selectedBusiness);

        final Marker businessMarker = markersMap.get(businessId);

        if(businessMarker != null) {
            // Moving the camera to the business of the business selected and displaying it's info:

            businessMarker.hideInfoWindow();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(businessMarker.getPosition(), 15);
            googleMap.animateCamera(cameraUpdate);

            // Handling showing the info window after the map is loaded
            // (calling showInfoWindow immediately not working)
            if (startShowInfoTimer){
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                businessMarker.showInfoWindow();
                            }
                        });
                    }
                }, 100);
            }

            businessMarker.showInfoWindow();
        }

        // Updating the manager to prepare to a click on the business view
        UncrowdManager.getInstance().setSelectedBusiness(selectedBusiness);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.selected_business_view){
            // Moving to the detailed view
            Intent detailedBusinessIntent = new Intent(this, BusinessExtendedDetailsActivity.class);
            startActivity(detailedBusinessIntent);
        }else if(v.getId() == R.id.take_me_there_btn){
            // Forwarding to navigation activity
            Business business = UncrowdManager.getInstance().getSelectedBusiness();
            NavigationActivityStarted.startNavigationActivity(this, business.getLat(), business.getLon());
        }
    }
}
