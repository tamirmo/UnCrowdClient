package tamirmo.uncrowd.location;

public final class LocationUtils {
    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. To ignore height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * @param lat1 double, Start point latitude
     * @param lon1 double, Start point longitude
     * @param lat2 double, End point latitude
     * @param lon2 double, End point longitude
     * @param el1 double, Elevation of start point (0 for ignoring)
     * @param el2 double, Elevation of end point (0 for ignoring)
     * @returns double, Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
