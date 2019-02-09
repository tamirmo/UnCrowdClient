package tamirmo.uncrowd.data;

import android.databinding.ObservableDouble;
import android.databinding.ObservableInt;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import tamirmo.uncrowd.location.LocationUtils;

public class Business {
    //
    public enum AlternativeRelation {BETTER, SAME, WORSE}

    Long id;
    String name;
    String address;
    Double lat;
    Double lon;
    // currCrowdLevel
    ObservableInt crowdLevel;
    // currCrowdCount
    ObservableInt crowdCount;
    // expectedCrowdCount
    ObservableInt expectedCrowdCount;
    ObservableDouble distance;
    List<Average> averages;
    // The last couple of samples
    List<CrowdHistory> crowdHistory;
    List<OpeningHours> openingHours;
    List<BusinessType> types;

    // TODO: Think of a way to save this
    private int crowdCountTime;
    private int expectedCountTime;

    // Indicating the relation of the this business to the original one
    // (which this business is an alternative for)
    private AlternativeRelation alternativaRelation = AlternativeRelation.SAME;

    public Business(){
        this.crowdLevel = new ObservableInt();
        this.crowdCount = new ObservableInt();
        this.expectedCrowdCount = new ObservableInt();
        this.distance = new ObservableDouble();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OpeningHours> getWorkingHours() {
        return this.openingHours;
    }

    public void setWorkingHours(List<OpeningHours> openingHours) {
        this.openingHours = openingHours;
    }

    public List<BusinessType> getTypes() {
        return types;
    }

    public void setTypes(List<BusinessType> types) {
        this.types = types;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getCrowdLevel() {
        return crowdLevel.get();
    }

    public void setCrowdLevel(int crowdLevel) {
        this.crowdLevel.set(crowdLevel);
    }

    public int getCrowdCount() {
        return crowdCount.get();
    }

    public void setCrowdCount(int crowdCount) {
        this.crowdCount.set(crowdCount);
    }

    public int getExpectedCrowdCount() {
        return expectedCrowdCount.get();
    }

    public void setExpectedCrowdCount(int expectedCrowdCount) {
        this.expectedCrowdCount.set(expectedCrowdCount);
    }

    public List<Average> getAverages() {
        return averages;
    }

    public void setAverages(List<Average> averages) {
        this.averages = averages;
    }

    public List<CrowdHistory> getCrowdHistory() {
        return crowdHistory;
    }

    public void setCrowdHistory(List<CrowdHistory> crowdHistory) {
        this.crowdHistory = crowdHistory;
    }

    public double getDistance() {
        return distance.get();
    }

    public void setDistance(double distance) {
        this.distance.set(distance);
    }

    public List<OpeningHours> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(List<OpeningHours> openingHours) {
        this.openingHours = openingHours;
    }

    public int getCrowdCountTime() {
        return crowdCountTime;
    }

    public void setCrowdCountTime(int crowdCountTime) {
        this.crowdCountTime = crowdCountTime;
    }

    public int getExpectedCountTime() {
        return expectedCountTime;
    }

    public void setExpectedCountTime(int expectedCountTime) {
        this.expectedCountTime = expectedCountTime;
    }

    public AlternativeRelation getAlternativaRelation() {
        return alternativaRelation;
    }

    public void setAlternativaRelation(AlternativeRelation alternativaRelation) {
        this.alternativaRelation = alternativaRelation;
    }

    // Indicating if the business is open at the moment
    public boolean isOpen() {
        boolean isOpen = false;

        // Getting the current time
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int currHour = calendar.getTime().getHours();

        if(openingHours != null){
            // Checking if the current hour is between the opening hour
            // and closing hour:
            if(currHour >= openingHours.get(day-1).open / 100 &&
                    currHour < openingHours.get(day-1).close / 100 ){
                isOpen = true;
            }
        }

        return isOpen;
    }

    /**
     * A method assembling the names of the types for this mBusiness (with "," in between types).
     * @return String, The type's names with "," in between types.
     */
    public String getTypesString(){
        StringBuilder types = new StringBuilder();

        String prefix = "";

        for(BusinessType type : this.types){
            //BusinessType type = UncrowdManager.getInstance().getBusinessTypesHandler().getBusinessTypeById(typeId);

            types.append(prefix);
            prefix = ", ";
            types.append(type.getName());
        }

        return types.toString();
    }

    public String getTodayOpeningHoursString(){
        String openingHoursString = "";
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);


        if(openingHours != null){
            openingHoursString = String.format("%02d:%02d - %02d:%02d", openingHours.get(day - 1).open / 100,
                    openingHours.get(day - 1).open  % 100,
                    openingHours.get(day - 1).close / 100,
                    openingHours.get(day - 1).close % 100);
        }

        return openingHoursString;
    }

    public String getAllOpeningHoursString(){
        StringBuilder openingHoursString = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String prefix = "";

        if(openingHours != null) {
            for (int i = day; i < 7; i++){
                openingHoursString.append(prefix);
                prefix = "\r\n";
                openingHoursString.append(String.format("%02d:%02d - %02d:%02d", openingHours.get(i).open / 100,
                        openingHours.get(i).open % 100,
                        openingHours.get(i).close / 100,
                        openingHours.get(i).close % 100));
            }

            for (int i = 0; i < day - 1; i++){
                openingHoursString.append(prefix);
                prefix = "\r\n";
                openingHoursString.append(String.format("%02d:%02d - %02d:%02d", openingHours.get(i).open / 100,
                        openingHours.get(i).open % 100,
                        openingHours.get(i).close / 100,
                        openingHours.get(i).close % 100));
            }
        }

        return openingHoursString.toString();
    }

    public boolean containsSearchPhrase(String searchPhrase){
        return this.name.toLowerCase().contains(searchPhrase.toLowerCase()) ||
                this.address.toLowerCase().contains(searchPhrase.toLowerCase());
    }

    @Override
    public boolean equals(Object o){
        boolean isEqual = false;
        if(o instanceof Business){
            if(((Business) o).id.equals(this.id)){
                isEqual = true;
            }
        }
        return isEqual;
    }

    public static class BusinessCrowdComparator implements Comparator<Business> {
        @Override
        public int compare(Business business1, Business business2) {
            return business1.crowdLevel.get() - business2.crowdLevel.get();
        }
    }

    public static class BusinessLocationComparator implements Comparator<Business> {

        private double userLat;
        private double userLon;

        public BusinessLocationComparator(double lat, double lon){
            this.userLat = lat;
            this.userLon = lon;
        }

        @Override
        public int compare(Business business1, Business business2) {
            return (int)(-LocationUtils.distance(userLat, business2.lat, userLon, business2.lon,0 ,0 ) +
                            LocationUtils.distance(userLat, business1.lat, userLon, business1.lon,0 ,0 ));
        }
    }
}
