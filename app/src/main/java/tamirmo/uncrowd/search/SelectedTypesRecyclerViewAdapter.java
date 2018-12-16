package tamirmo.uncrowd.search;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tamirmo.uncrowd.R;
import tamirmo.uncrowd.logic.UncrowdManager;
import tamirmo.uncrowd.data.BusinessType;
import tamirmo.uncrowd.databinding.BusinessTypeItemListViewLayoutBinding;

public class SelectedTypesRecyclerViewAdapter extends RecyclerView.Adapter<SelectedTypesRecyclerViewAdapter.BusinessTypeViewHolder> {

    private final IOnBusinessTypeRemoveClick mListener;
    private AdvancedSearchViewModel advancedSearchViewModel;

    SelectedTypesRecyclerViewAdapter(IOnBusinessTypeRemoveClick listener,
                                     AdvancedSearchViewModel advancedSearchViewModel) {
        mListener = listener;
        this.advancedSearchViewModel = advancedSearchViewModel;
    }

    @NonNull
    @Override
    public BusinessTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        BusinessTypeItemListViewLayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.business_type_item_list_view_layout, parent,
                false);

        return new BusinessTypeViewHolder(itemBinding.getRoot(), itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final BusinessTypeViewHolder holder, int position) {
        holder.bind(getItem(position));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, AdvancedSearchActivity)
                    // that an item has been selected.
                    mListener.onBusinessTypeRemoveClick(holder.type);
                }
            }
        });
    }

    private BusinessType getItem(int index){
        long itemId = advancedSearchViewModel.getSelectedTypesList().get(index);

        return UncrowdManager.getInstance().getBusinessTypesHandler().getBusinessTypeById(itemId);
    }

    @Override
    public int getItemCount() {
        return advancedSearchViewModel.getSelectedTypesList().size();
    }

    class BusinessTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        BusinessType type;
        BusinessTypeItemListViewLayoutBinding itemBinding;
        final View view;

        BusinessTypeViewHolder(View view, BusinessTypeItemListViewLayoutBinding itemBinding) {
            super(view);
            this.view = view;
            this.itemBinding = itemBinding;
            view.findViewById(R.id.remove_type_btn).setOnClickListener(this);
        }

        void bind(BusinessType type) {
            this.type = type;
            itemBinding.setType(type);
            itemBinding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                // Notify the active callbacks interface (the activity, AdvancedSearchActivity)
                // that an item has been selected.
                mListener.onBusinessTypeRemoveClick(type);
            }
        }
    }
}
