package tamirmo.uncrowd.businesses.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tamirmo.uncrowd.R;
import tamirmo.uncrowd.businesses.list.BusinessesFragment.OnBusinessListItemClickedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import tamirmo.uncrowd.business.view.BusinessView;
import tamirmo.uncrowd.data.Business;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Business} and makes a call to the
 * specified {@link OnBusinessListItemClickedListener}.
 */
public class BusinessRecyclerViewAdapter extends RecyclerView.Adapter<BusinessRecyclerViewAdapter.BusinessViewHolder> {

    private List<Integer> mFilteredIndexes;
    private String currSearchPhrase = null;
    private final OnBusinessListItemClickedListener mListener;
    private List<Business> businesses;

    BusinessRecyclerViewAdapter(OnBusinessListItemClickedListener listener) {
        mListener = listener;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
        // Filtering the new list according to the current search phrase
        onSearchPhraseEntered(this.currSearchPhrase);
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        View root = layoutInflater.inflate(R.layout.business_list_item, parent, false);

        BusinessView businessView = root.findViewById(R.id.business_view_list_item);

        return new BusinessViewHolder(root, businessView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BusinessViewHolder holder, int position) {
        holder.bind(getItem(position));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onBusinessListItemClicked(holder.mBusiness);
                }
            }
        });
    }

    private Business getItem(int index){
        if(businesses == null){
            return null;
        }else {
            // If there is no filter (the user has not yet entered a search phrase)
            if (this.mFilteredIndexes == null)
                // Returning the usual value
                return businesses.get(index);
            else
                // Returning the next item from the filtered list
                return businesses.get(this.mFilteredIndexes.get(index));
        }
    }

    @Override
    public int getItemCount() {
        if(businesses == null) {
            return 0;
        }else {
            if (this.mFilteredIndexes == null)
                return businesses.size();
            else
                return this.mFilteredIndexes.size();
        }
    }

    void onSearchPhraseEntered(String searchPhrase){
        if(searchPhrase == null || searchPhrase.equals("")){
            this.mFilteredIndexes = null;
        }else{
            if(this.businesses != null) {
                this.mFilteredIndexes = new ArrayList<>();
                ListIterator<Business> iterator = this.businesses.listIterator();
                while (iterator.hasNext()) {
                    if (iterator.next().containsSearchPhrase(searchPhrase)) {
                        this.mFilteredIndexes.add(iterator.nextIndex() - 1);
                    }
                }
            }
        }

        this.currSearchPhrase = searchPhrase;

        notifyDataSetChanged();
    }

    class BusinessViewHolder extends RecyclerView.ViewHolder {
        Business mBusiness;
        BusinessView businessView;
        final View view;

        BusinessViewHolder(View view, BusinessView businessView) {
            super(view);
            this.view = view;
            this.businessView = businessView;
        }

        void bind(Business business) {
            this.mBusiness = business;
            businessView.setBusiness(business);
        }
    }
}
