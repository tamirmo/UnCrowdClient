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
import tamirmo.uncrowd.logic.UncrowdManager;
import tamirmo.uncrowd.data.Business;

public class AlternativesActivity extends AppCompatActivity implements BusinessesFragment.OnBusinessListItemClickedListener, SearchView.OnQueryTextListener, View.OnClickListener {

    private BusinessesFragment businessesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternatives);
        businessesFragment = (BusinessesFragment) getSupportFragmentManager().findFragmentById(R.id.businesses_fragment);
        BusinessView businessView = findViewById(R.id.original_business_view);

        Business selectedBusiness = UncrowdManager.getInstance().getSelectedBusiness();

        // Setting the original business as the selected one
        businessView.setBusiness(selectedBusiness);
        businessView.setOnClickListener(this);

        BusinessesFragmentViewModel model =
                ViewModelProviders.of(this).get(BusinessesFragmentViewModel.class);

        // Loading the alternatives for this business:
        model.getAlternativeBusinesses(selectedBusiness.getId()).observe(this, new Observer<List<Business>>() {
            @Override
            public void onChanged(@Nullable List<Business> businesses) {
                // TODO: Stop loading
                Toast.makeText(getApplicationContext(),"onChanged from alternatives", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBusinessListItemClicked(Business businessSelected) {
        UncrowdManager.getInstance().setSelectedBusiness(businessSelected);
        Intent detailedBusinessIntent = new Intent(this, BusinessExtendedDetailsActivity.class);
        startActivity(detailedBusinessIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
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
            // Moving to the detailed view
            Intent detailedBusinessIntent = new Intent(this, BusinessExtendedDetailsActivity.class);
            startActivity(detailedBusinessIntent);
        }
    }
}
