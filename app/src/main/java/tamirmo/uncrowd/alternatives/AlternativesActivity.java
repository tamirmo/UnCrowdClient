package tamirmo.uncrowd.alternatives;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import tamirmo.uncrowd.business.view.BusinessExtendedDetailsActivity;
import tamirmo.uncrowd.business.view.BusinessView;
import tamirmo.uncrowd.businesses.list.BusinessesFragment;
import tamirmo.uncrowd.businesses.list.BusinessesFragmentViewModel;
import tamirmo.uncrowd.R;
import tamirmo.uncrowd.location.LocationHandler;
import tamirmo.uncrowd.logic.UncrowdManager;
import tamirmo.uncrowd.data.Business;

public class AlternativesActivity extends AppCompatActivity implements BusinessesFragment.OnBusinessListItemClickedListener, SearchView.OnQueryTextListener, View.OnClickListener {

    public static final String BUSINESS_ID = "BUSINESS_ID";

    private BusinessesFragment businessesFragment;
    // The business to calculate alternatives
    private Business originalBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternatives);
        businessesFragment = (BusinessesFragment) getSupportFragmentManager().findFragmentById(R.id.businesses_fragment);
        BusinessView businessView = findViewById(R.id.original_business_view);

        long businessId = getIntent().getLongExtra(BUSINESS_ID, 0);

        if (UncrowdManager.getInstance().getBusinessesMap().get(businessId) != null) {
            originalBusiness = UncrowdManager.getInstance().getBusinessesMap().get(businessId);
        }

        // Setting the original business as the selected one
        businessView.setBusiness(originalBusiness);
        businessView.setOnClickListener(this);

        BusinessesFragmentViewModel model =
                ViewModelProviders.of(this).get(BusinessesFragmentViewModel.class);

        // Loading the alternatives for this business:
        model.getAlternativeBusinesses(originalBusiness).observe(this, new Observer<List<Business>>() {
            @Override
            public void onChanged(@Nullable List<Business> businesses) {
                Toast.makeText(getApplicationContext(),"onChanged from alternatives", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBusinessListItemClicked(Business businessSelected) {
        UncrowdManager.getInstance().setSelectedBusiness(businessSelected);
        Intent detailedBusinessIntent = new Intent(this, BusinessExtendedDetailsActivity.class);
        detailedBusinessIntent.putExtra(BusinessExtendedDetailsActivity.BUSINESS_ID, businessSelected.getId());
        startActivity(detailedBusinessIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alternatives_activity_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_crowded:
                businessesFragment.sortByCrowd();
                return true;
            case R.id.action_sort_location:
                businessesFragment.sortByLocation(LocationHandler.getInstance());
                return true;
            case R.id.action_sort_relevance:
                // TODO: Sort by relevance? save the server's original sorting ?
                businessesFragment.onRefresh();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO: Go to server ?
        businessesFragment.onSearchPhraseEntered(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        businessesFragment.onSearchPhraseEntered(newText);
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.original_business_view){
            UncrowdManager.getInstance().setSelectedBusiness(originalBusiness);
            // Moving to the detailed view
            Intent detailedBusinessIntent = new Intent(this, BusinessExtendedDetailsActivity.class);
            startActivity(detailedBusinessIntent);
        }
    }
}
