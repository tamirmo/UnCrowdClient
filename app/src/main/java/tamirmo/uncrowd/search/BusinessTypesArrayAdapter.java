package tamirmo.uncrowd.search;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import tamirmo.uncrowd.logic.BusinessTypesHandler;
import tamirmo.uncrowd.R;
import tamirmo.uncrowd.data.BusinessType;
import tamirmo.uncrowd.logic.UncrowdManager;

public class BusinessTypesArrayAdapter extends ArrayAdapter<BusinessType> implements View.OnClickListener {

    private Context context;
    // The list of types id's that fits the string entered by the user in the AutoCompleteTextView
    private List<Long> filtered = null;
    private BusinessTypesHandler businessTypesHandler;
    // A listener for a business type add click (should be the AdvancedSearchActivity)
    private IOnBusinessTypeAdded typeAddedListener;

    BusinessTypesArrayAdapter(Context context, IOnBusinessTypeAdded typeAddedListener) {
        super(context, R.layout.business_type_item_text_view_layout);
        this.context = context;
        this.businessTypesHandler = UncrowdManager.getInstance().getBusinessTypesHandler();
        this.typeAddedListener = typeAddedListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                view = inflater.inflate(R.layout.business_type_item_text_view_layout, parent, false);
            }

            BusinessType businessType = getItem(position);
            TextView name = view.findViewById(R.id.business_type_name);
            ImageButton addButton = view.findViewById(R.id.add_type_btn);
            addButton.setOnClickListener(this);

            if(businessType != null) {
                // Adding the id of the type as tag to identify on "onClick" method
                addButton.setTag(businessType.getId());
                name.setText(businessType.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Nullable
    @Override
    public BusinessType getItem(int position) {
        // If there is no filter yet
        if(filtered == null){
            // Going with the original list of types
            return businessTypesHandler.getBusinessTypeByIndex(position);
        }
        // If there are filtered types
        else{
            // Going with the filtered list of types
            return businessTypesHandler.getBusinessTypeById(filtered.get(position));
        }
    }

    @Override
    public int getCount() {
        // If there is no filter yet
        if(filtered == null){
            // Going with the original list of types
            return businessTypesHandler.getBusinessTypesList().size();
        }
        // If there are filtered types
        else{
            // Going with the filtered list of types
            return filtered.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return fruitFilter;
    }

    @Override
    public void onClick(View v) {
        // Alerting of a type added
        if(typeAddedListener != null){
            long typeId = (Long)v.getTag();
            typeAddedListener.onBusinessTypeAdded(businessTypesHandler.getBusinessTypeById(typeId));
        }
    }

    private Filter fruitFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            BusinessType type = (BusinessType) resultValue;
            return type.getName();
        }
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                // Clearing the list of the last search:
                if(filtered == null){
                    filtered = new ArrayList<>();
                }else{
                    filtered.clear();
                }
                // Searching for types that starts with the given string and adding to the filtered list:
                for (BusinessType type: UncrowdManager.getInstance().getBusinessTypesHandler().getBusinessTypesList()) {
                    if (type.getName().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        filtered.add(type.getId());
                    }
                }

                // Creating a FilterResults from the found types
                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                filterResults.count = filtered.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) { }
    };
}
