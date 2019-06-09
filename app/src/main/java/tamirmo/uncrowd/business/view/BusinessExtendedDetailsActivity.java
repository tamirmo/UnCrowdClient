package tamirmo.uncrowd.business.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import tamirmo.uncrowd.R;
import tamirmo.uncrowd.alternatives.AlternativesActivity;
import tamirmo.uncrowd.communication.HttpUtilities;
import tamirmo.uncrowd.data.Average;
import tamirmo.uncrowd.databinding.ActivityExtendedBusinessBinding;

import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.logic.UncrowdManager;
import tamirmo.uncrowd.service.TrackBusinessService;
import tamirmo.uncrowd.utilities.NavigationActivityStarter;

public class BusinessExtendedDetailsActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String BUSINESS_ID = "BUSINESS_ID";

    private ActivityExtendedBusinessBinding businessExtendedDetailsBinding;

    private ImageButton expand;
    private ImageButton collapse;
    private TextView businessHoursExpanded;
    private ColumnChartView averagesGraph;
    private TextView averageDay;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Business business;

    private int currAverageDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        businessExtendedDetailsBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_extended_business);
        businessExtendedDetailsBinding.setBusiness(UncrowdManager.getInstance().getSelectedBusiness());

        expand = findViewById(R.id.expand);
        collapse = findViewById(R.id.collapse);
        businessHoursExpanded = findViewById(R.id.business_hours_expanded);

        expand.setOnClickListener(this);
        collapse.setOnClickListener(this);

        findViewById(R.id.average_left).setOnClickListener(this);
        findViewById(R.id.average_right).setOnClickListener(this);

        averageDay = findViewById(R.id.average_day);

        long businessId = getIntent().getLongExtra(BUSINESS_ID, 0);

        averagesGraph = findViewById(R.id.averages_graph);

        averagesGraph.setZoomEnabled(false);
        currAverageDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

        findViewById(R.id.on_my_way_btn).setOnClickListener(this);
        findViewById(R.id.alternatives_btn).setOnClickListener(this);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        setBusiness(businessId);
    }

    private void setBusiness(long businessId) {
        if (UncrowdManager.getInstance().getBusinessesMap().get(businessId) != null) {
            business = UncrowdManager.getInstance().getBusinessesMap().get(businessId);
        }

        // Populating the trend graph with the data from the Business object
        BusinessViewsUtilities.showTrendGraph((LineChart) findViewById(R.id.trend_graph),
                business);

        setDayAverage(averagesGraph,
                currAverageDay,
                true);

        businessExtendedDetailsBinding.setBusiness(business);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.expand){
            expand.setVisibility(View.GONE);
            collapse.setVisibility(View.VISIBLE);
            businessHoursExpanded.setVisibility(View.VISIBLE);
        }else if(v.getId() == R.id.collapse){
            expand.setVisibility(View.VISIBLE);
            collapse.setVisibility(View.GONE);
            businessHoursExpanded.setVisibility(View.GONE);
        }else if(v.getId() == R.id.average_left){
            if(--currAverageDay < 0) {
                currAverageDay = 6;
            }

            setDayAverage(averagesGraph, currAverageDay,
                    Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 == currAverageDay);
        }
        else if(v.getId() == R.id.average_right){
            if(++currAverageDay > 6) {
                currAverageDay = 0;
            }
            setDayAverage(averagesGraph, currAverageDay,
                    Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 == currAverageDay);
        }
        else if(v.getId() == R.id.on_my_way_btn){
            // Forwarding to navigation activity:
            NavigationActivityStarter.startNavigationActivity(this, business.getLat(), business.getLon());

            // Start service with notification to track crowd updates
            Intent intent = new Intent(this, TrackBusinessService.class);
            intent.setAction(TrackBusinessService.ACTION_START_FOREGROUND_SERVICE);
            intent.putExtra(TrackBusinessService.BUSINESS_ID_EXTRA, business.getId());
            startService(intent);
        }
        else if(v.getId() == R.id.alternatives_btn){
            Intent alternativesIntent = new Intent(this, AlternativesActivity.class);
            alternativesIntent.putExtra(AlternativesActivity.BUSINESS_ID, business.getId());
            startActivity(alternativesIntent);
        }
    }

    private void setDayAverage(ColumnChartView chart, int day, boolean highlightCurrHour) {
        // Getting the averages array for the given day:

        Integer[] dayAverages;
        List<Integer> dayAveragesList = new ArrayList<>();

        // Creating a list of averages of the given day only:
        for(Average average : business.getAverages()){
            if(average.getDay() == day){
                dayAveragesList.add(average.getAverage());
            }
        }

        // Converting the list to array
        dayAverages = new Integer[dayAveragesList.size()];
        dayAveragesList.toArray(dayAverages);

        // Calculating the hours the business is open in the given day
        int openingHour = business.getOpeningHours().get(day).getOpen() / 100;
        int closingHour = business.getOpeningHours().get(day).getClose() / 100;

        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<>();

        for (int i = openingHour; i < closingHour; ++i) {
            // The label of the values are the hours
            axisValues.add(new AxisValue(i - openingHour).setLabel(String.valueOf(i)));

            values = new ArrayList<>();

            // Getting the columns color from resources:
            int columnColor  = ContextCompat.getColor(this, R.color.extender_business_view_averages_column_color);

            // If the current column is the of the current hour
            if(highlightCurrHour && i == Calendar.getInstance().getTime().getHours()){
                // Getting the current column color
                columnColor = ContextCompat.getColor(this, R.color.extender_business_view_averages_current_column_color);;
            }

            values.add(new SubcolumnValue(dayAverages[i], columnColor));

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);

        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis();
        axisY.setHasLines(true);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        chart.setColumnChartData(data);

        // Updating the text of the current day
        averageDay.setText(getResources().getStringArray(R.array.days)[day]);
    }
    @Override
    public void onRefresh() {
        new RefreshBusinessTask(business.getId(), this).execute();
    }

    private void onRefreshFinished() {
        swipeRefreshLayout.setRefreshing(false);
        setBusiness(business.getId());
    }

    private static class RefreshBusinessTask extends AsyncTask<Void, Void, Void> {

        private long businessId;
        private WeakReference<BusinessExtendedDetailsActivity> activity;

        RefreshBusinessTask(long businessId, BusinessExtendedDetailsActivity activity) {
            this.businessId = businessId;
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = HttpUtilities.getBaseServerUrl() + String.format(Locale.US, "Businessinfo/%d/",
                        businessId);

                RestTemplate restTemplate = HttpUtilities.createRestTemplate();
                ResponseEntity<Business> responseEntity =
                        restTemplate.getForEntity(url, tamirmo.uncrowd.data.Business.class);

                // Updating the manager ad the notification
                UncrowdManager.getInstance().updateBusiness(responseEntity.getBody());
                return null;
            }catch(Exception ex) {
                ex.printStackTrace();
                // null indicating an error
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(this.activity.get() != null) {
                this.activity.get().onRefreshFinished();
            }
        }
    }
}
