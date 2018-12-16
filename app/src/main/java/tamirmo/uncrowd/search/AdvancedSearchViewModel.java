package tamirmo.uncrowd.search;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import java.util.ArrayList;
import java.util.List;

import tamirmo.uncrowd.data.BusinessType;

public class AdvancedSearchViewModel extends ViewModel {
    private List<Long> selectedTypesList;
    //private Map<Long, BusinessType> selectedTypesMap;
    public ObservableInt radius;
    public ObservableField<String> name;

    public void setSelectedTypesList(List<Long> selectedTypesList) {
        this.selectedTypesList = selectedTypesList;
    }

    AdvancedSearchViewModel(){
        selectedTypesList = new ArrayList<>();
        this.name = new ObservableField<>();
        this.radius = new ObservableInt();
        //selectedTypesMap = new HashMap<>();
    }

    List<Long> getSelectedTypesList(){
        return selectedTypesList;
    }

    void addSelectedType(long id, BusinessType type){
        if(!selectedTypesList.contains(id)) {
            selectedTypesList.add(id);
        }
        //selectedTypesMap.put(id, type);
    }

    void removeSelectedType(long id){
        selectedTypesList.remove(id);
        //selectedTypesMap.remove(id);
    }

    AdvancedSearchInput toAdvancedSearchInput(){
        return new AdvancedSearchInput(this.selectedTypesList,
                this.radius.get(),
                this.name.get());
    }
}
