package tamirmo.uncrowd.logic;

import android.location.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.location.LocationHandler;
import tamirmo.uncrowd.location.LocationUtils;

public class UncrowdManager {
    private static final UncrowdManager ourInstance = new UncrowdManager();

    public static UncrowdManager getInstance() {
        return ourInstance;
    }

    private BusinessTypesHandler businessTypesHandler;
    private Business selectedBusiness;
    private Map<Long, Business> businessesMap;
    private List<Business> businessesList;
    private String serverIp;

    public void setSelectedBusiness(Business business){
        this.selectedBusiness = business;
    }

    public Business getSelectedBusiness() {
        return selectedBusiness;
    }

    public Map<Long, Business> getBusinessesMap() {
        return businessesMap;
    }

    public List<Business> getBusinessesList() {
        return businessesList;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    private UncrowdManager() {
        this.businessesMap = new HashMap<>();
        this.businessesList = new ArrayList<>();
    }

    /**
     * Initializing the types handler if needed and returning true if was initialized,
     * false if not.
     * @return boolean, true if was initialized, false if not.
     */
    public boolean initializeTypesHandlerIfNeeded(){
        if(businessTypesHandler == null){
            businessTypesHandler = new BusinessTypesHandler();
            return true;
        }
        return false;
    }

    public BusinessTypesHandler getBusinessTypesHandler() {
        return businessTypesHandler;
    }

    public void updateBusinesses(Collection<Business> businesses){

        // If the list was not yet initialized
        if(businessesList.size() == 0){
            for(Business business : businesses){
                setBusinessDistance(business);
                this.businessesMap.put(business.getId(), business);
                this.businessesList.add(business);
            }
        }else {
            for (Business business : businesses) {
                setBusinessDistance(business);
                businessesMap.put(business.getId(), business);

                // Adding or updating the business on the list:
                int indexOfBusiness = businessesList.indexOf(business);
                if(indexOfBusiness != -1){
                    businessesList.set(indexOfBusiness, business);
                }else {
                    businessesList.add(business);
                }

                // Updating the list
                /*ListIterator<Business> businessListIterator = this.businessesList.listIterator();
                while (businessListIterator.hasNext()) {
                    Business listBusiness = businessListIterator.next();
                    if(listBusiness.equals(business)){
                        businessListIterator.remove();
                        businessListIterator.add(business);
                    }
                }*/
            }
        }
    }

    public void updateBusiness(Business business) {
        setBusinessDistance(business);
        this.businessesMap.put(business.getId(), business);
    }

    private void setBusinessDistance(Business business) {
        Location myLocation = LocationHandler.getInstance().getLastLocation();
        if (myLocation != null) {
            double distanceMeters = LocationUtils.distance(business.getLat(), myLocation.getLatitude(),
                    business.getLon(), myLocation.getLongitude(),
                    0, 0);
            business.setDistance(distanceMeters / 1000.0);
        }
    }
}
