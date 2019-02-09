package tamirmo.uncrowd.search;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import tamirmo.uncrowd.location.LocationHandler;
import tamirmo.uncrowd.R;
import tamirmo.uncrowd.businesses.list.BusinessesListActivity;
import tamirmo.uncrowd.data.BusinessType;
import tamirmo.uncrowd.databinding.ActivityAdvancedSearchBinding;

public class AdvancedSearchActivity extends AppCompatActivity implements IOnBusinessTypeAdded, IOnBusinessTypeRemoveClick, View.OnClickListener, LocationHandler.IOnLocationReceived {

    private AppCompatAutoCompleteTextView businessTypeTextView;
    private RecyclerView selectedBusinessTypesRecyclerView;
    private SelectedTypesRecyclerViewAdapter selectedTypesAdapter;
    private AdvancedSearchViewModel viewModel;
    private BusinessTypesArrayAdapter typesTextViewAdapter;
    private EditText businessNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAdvancedSearchBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this),
                    R.layout.activity_advanced_search, null,
                      false);
        setContentView(binding.getRoot());

        businessTypeTextView =
                findViewById(R.id.business_type_text_view);
        typesTextViewAdapter = new BusinessTypesArrayAdapter(this, this);
        businessTypeTextView.setAdapter(typesTextViewAdapter);
        businessTypeTextView.setThreshold(0);

        this.viewModel = ViewModelProviders.of(this).get(AdvancedSearchViewModel.class);

        selectedBusinessTypesRecyclerView = findViewById(R.id.selected_business_types_list);
        selectedTypesAdapter = new SelectedTypesRecyclerViewAdapter(this,viewModel);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,
                false);
        selectedBusinessTypesRecyclerView.setLayoutManager(layoutManager);
        selectedBusinessTypesRecyclerView.setAdapter(selectedTypesAdapter);

        businessNameEditText = findViewById(R.id.business_name_edit_text);

        binding.setViewModel(this.viewModel);

        findViewById(R.id.advanced_search_btn).setOnClickListener(this);
        LocationHandler.getInstance().getCurrLocation(this);
    }

    @Override
    public void onBusinessTypeAdded(BusinessType typeSelected) {
        // Clearing the text for the next type
        businessTypeTextView.setText("");
        typesTextViewAdapter.notifyDataSetChanged();

        // Adding to the view model
        viewModel.addSelectedType(typeSelected.getId(), typeSelected);
        // Updating the recycler view
        selectedTypesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBusinessTypeRemoveClick(BusinessType removedType) {
        viewModel.removeSelectedType(removedType.getId());
        // Updating the adapter
        typesTextViewAdapter.notifyDataSetChanged();
        selectedTypesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.advanced_search_btn){
            // Starting the search results activity to perform the search and display the results:
            Intent intent = new Intent(this, BusinessesListActivity.class);

            // Creating the input of the search with the last known device location:
            AdvancedSearchInput asi = viewModel.toAdvancedSearchInput();
            asi.setCurrUserLocation(LocationHandler.getInstance().getLastLocation());

            // Passing the search results activity the search input
            intent.putExtra(BusinessesListActivity.ADVANCED_SEARCH_INPUT_KEY, asi);
            startActivity(intent);
        }
    }

    @Override
    public void onLocationReceived(Location lastLocation) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationHandler.getInstance().onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
