package tamirmo.uncrowd.search;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchInput implements Parcelable {
    private List<Long> selectedTypesList;
    private int radius;
    private String name;

    public List<Long> getSelectedTypesList() {
        return selectedTypesList;
    }

    public void setSelectedTypesList(List<Long> selectedTypesList) {
        this.selectedTypesList = selectedTypesList;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    AdvancedSearchInput(List<Long> selectedTypesList, int radius, String name){
        this.selectedTypesList = selectedTypesList;
        this.radius = radius;
        this.name = name;
    }

    //constructor used for parcel
    private AdvancedSearchInput(Parcel parcel){
        String selectedTypes = parcel.readString();
        this.radius = parcel.readInt();
        this.name = parcel.readString();

        // Assembling the list of types ids from the types string
        if(selectedTypes != null && !selectedTypes.equals("")){
            this.selectedTypesList = new ArrayList<>();
            String[] selectedTypesSeparated = selectedTypes.split(",");
            for(String type : selectedTypesSeparated){
                this.selectedTypesList.add(Long.parseLong(type));
            }
        }
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Creating a String with the types ids:
        StringBuilder selectedTypes = new StringBuilder();
        String separator = "";
        for(Long typeId : selectedTypesList){
            selectedTypes.append(separator);
            separator = ",";
            selectedTypes.append(typeId);
        }

        dest.writeString(selectedTypes.toString());
        dest.writeInt(radius);
        dest.writeString(name);
    }

    // Used when un-parceling our parcel (creating the object)
    public static final Parcelable.Creator<AdvancedSearchInput> CREATOR = new Parcelable.Creator<AdvancedSearchInput>(){

        @Override
        public AdvancedSearchInput createFromParcel(Parcel parcel) {
            return new AdvancedSearchInput(parcel);
        }

        @Override
        public AdvancedSearchInput[] newArray(int size) {
            return new AdvancedSearchInput[0];
        }
    };
}
