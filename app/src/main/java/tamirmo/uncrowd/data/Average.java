package tamirmo.uncrowd.data;

public class Average implements Comparable<Average>{
    Integer Day;
    Integer dateTime;
    Integer Average;
    Long businessId;

    public int getDay() {
        return Day;
    }

    public void setDay(int day) {
        Day = day;
    }

    public int getDateTime() {
        return dateTime;
    }

    public void setDateTime(int dateTime) {
        this.dateTime = dateTime;
    }

    public int getAverage() {
        return Average;
    }

    public void setAverage(int average) {
        Average = average;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    @Override
    public int compareTo(tamirmo.uncrowd.data.Average o) {
        int dayCompareTo = this.Day.compareTo(o.Day);
        int dateTimeCompareTo = this.dateTime.compareTo(o.dateTime);
        if(dayCompareTo == 0) {
            return dateTimeCompareTo;
        }

        return dayCompareTo;
    }
}
