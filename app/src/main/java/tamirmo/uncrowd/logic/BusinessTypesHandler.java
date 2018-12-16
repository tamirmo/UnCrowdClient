package tamirmo.uncrowd.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.data.BusinessType;

public class BusinessTypesHandler {
    private Map<Long, BusinessType> businessTypes;
    private List<BusinessType> businessTypesList;

    BusinessTypesHandler(){
        businessTypes = new HashMap<>();
        businessTypesList = new ArrayList<>();

        /*BusinessType type = new BusinessType();
        type.setName("Toys");
        type.setId(1);
        businessTypes.put(type.getId(), type);
        businessTypesList.add(type);

        type = new BusinessType();
        type.setName("Paper");
        type.setId(2);
        businessTypes.put(type.getId(), type);
        businessTypesList.add(type);

        type = new BusinessType();
        type.setName("Colors");
        type.setId(3);
        businessTypes.put(type.getId(), type);
        businessTypesList.add(type);

        type = new BusinessType();
        type.setName("Kids");
        type.setId(4);
        businessTypes.put(type.getId(), type);
        businessTypesList.add(type);*/
    }

    public BusinessType getBusinessTypeById(long id){
        return businessTypes.get(id);
    }

    public List<BusinessType> getBusinessTypesList(){
        return this.businessTypesList;
    }
    public BusinessType getBusinessTypeByIndex(int index){
        return this.businessTypesList.get(index);
    }

    public void setTypes(BusinessType[] types){
        if(types != null) {
            for (BusinessType type : types){
                businessTypes.put(type.getId(), type);
                businessTypesList.add(type);
            }
        }
    }
}
