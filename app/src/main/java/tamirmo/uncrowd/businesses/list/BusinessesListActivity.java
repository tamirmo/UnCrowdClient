package tamirmo.uncrowd.businesses.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import tamirmo.uncrowd.location.LocationHandler;
import tamirmo.uncrowd.R;
import tamirmo.uncrowd.business.view.BusinessExtendedDetailsActivity;
import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.logic.UncrowdManager;
import tamirmo.uncrowd.map.MapActivity;
import tamirmo.uncrowd.search.AdvancedSearchActivity;
import tamirmo.uncrowd.search.AdvancedSearchInput;

public class BusinessesListActivity extends AppCompatActivity implements BusinessesFragment.OnBusinessListItemClickedListener, SearchView.OnQueryTextListener, View.OnClickListener {

    public final static String ADVANCED_SEARCH_INPUT_KEY = "ADVANCED_SEARCH_INPUT_KEY";
    private final static String SERVER_IP_KEY = "SERVER_IP";


    private BusinessesFragment businessesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_list);
        businessesFragment = (BusinessesFragment) getSupportFragmentManager().findFragmentById(R.id.businesses_fragment);
        findViewById(R.id.advanced_search_btn).setOnClickListener(this);
        findViewById(R.id.map_btn).setOnClickListener(this);

        // Checking which list of businesses to load
        actUponIntent(getIntent());

        LocationHandler.getInstance().init(this);
        LocationHandler.getInstance().getCurrLocation(null);

        // Pulling the saved server ip from shared preferences
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        UncrowdManager.getInstance().setServerIp(sharedPref.getString(SERVER_IP_KEY, "10.100.102.2"));
    }

    /**
     * Called when there is a new intent or it is the first intent received
     * @param intent Intent, The intent to act upon
     */
    private void actUponIntent(Intent intent){
        BusinessesFragmentViewModel model =
                ViewModelProviders.of(this).get(BusinessesFragmentViewModel.class);

        boolean isAdvancedSearchIntent = false;

        if(intent != null){
            // Trying to see if we came from the advanced search activity and need to perform a search
            AdvancedSearchInput advancedSearchInput = intent.getParcelableExtra(ADVANCED_SEARCH_INPUT_KEY);
            if(advancedSearchInput != null){

                // Indicating the list of businesses was loaded and there is no need to load all businesses
                isAdvancedSearchIntent = true;
                model.getAdvancedSearchBusinesses(advancedSearchInput).observe(this, new Observer<List<Business>>() {
                    @Override
                    public void onChanged(@Nullable List<Business> businesses) {
                        Toast.makeText(getApplicationContext(),"onChanged from search", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        // The intent is not advanced search intent
        if(!isAdvancedSearchIntent){
            // Loading all businesses
            model.getAllBusinesses().observe(this, new Observer<List<Business>>() {
                @Override
                public void onChanged(@Nullable List<Business> businesses) {
                    Toast.makeText(getApplicationContext(),"onChanged from all", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Checking which list of businesses to load
        actUponIntent(intent);
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
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                displayAppSettings();
                return true;
            case R.id.action_sort_crowded:
                businessesFragment.sortByCrowd();
                return true;
            case R.id.action_sort_location:
                businessesFragment.sortByLocation(LocationHandler.getInstance());
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void displayAppSettings(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Server Ip");
        alertDialog.setMessage("Enter IP");

        final EditText input = new EditText(this);
        input.setText(UncrowdManager.getInstance().getServerIp());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input); // uncomment this line

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UncrowdManager.getInstance().setServerIp(input.getText().toString());
                        dialog.cancel();

                        // Saving the ip entered for next time
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(SERVER_IP_KEY, UncrowdManager.getInstance().getServerIp());
                        editor.apply();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
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
        if(v.getId() == R.id.map_btn){
            if (UncrowdManager.getInstance().getBusinessesList() != null &&
                    UncrowdManager.getInstance().getBusinessesList().size() > 0) {
                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, R.string.map_click_no_businesses, Toast.LENGTH_LONG).show();
            }
        }else if (v.getId() == R.id.advanced_search_btn) {
            Intent mapIntent = new Intent(this, AdvancedSearchActivity.class);
            startActivity(mapIntent);
        }
    }
}
