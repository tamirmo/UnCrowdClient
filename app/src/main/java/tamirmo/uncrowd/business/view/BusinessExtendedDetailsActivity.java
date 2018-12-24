package tamirmo.uncrowd.business.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import tamirmo.uncrowd.R;
import tamirmo.uncrowd.alternatives.AlternativesActivity;
import tamirmo.uncrowd.data.Average;
import tamirmo.uncrowd.databinding.ActivityExtendedBusinessBinding;

import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.logic.UncrowdManager;

public class BusinessExtendedDetailsActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ImageButton expand;
    private ImageButton collapse;
    private TextView businessHoursExpanded;
    private ColumnChartView averagesGraph;
    private TextView averageDay;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int currAverageDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityExtendedBusinessBinding businessExtendedDetailsBinding =
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

        // Populating the trend graph with the data from the Business object
        BusinessViewsUtilities.showTrendGraph((LineChart) findViewById(R.id.trend_graph),
                UncrowdManager.getInstance().getSelectedBusiness(),
                true);

        averagesGraph = findViewById(R.id.averages_graph);

        currAverageDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        setDayAverage(averagesGraph,
                currAverageDay,
                true);

        findViewById(R.id.on_my_way_btn).setOnClickListener(this);
        findViewById(R.id.alternatives_btn).setOnClickListener(this);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
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
            // TODO: Intent navigation & start service with notification
        }
        else if(v.getId() == R.id.alternatives_btn){
            Intent alternativesIntent = new Intent(this, AlternativesActivity.class);
            startActivity(alternativesIntent);

        }
    }

    private void setDayAverage(ColumnChartView chart, int day, boolean highlightCurrHour) {
        Business selectedBusiness = UncrowdManager.getInstance().getSelectedBusiness();

        // Getting the averages array for the given day:

        Integer[] dayAverages;
        List<Integer> dayAveragesList = new ArrayList<>();

        // Creating a list of averages of the given day only:
        for(Average average : selectedBusiness.getAverages()){
            if(average.getDay() == day){
                dayAveragesList.add(average.getAverage());
            }
        }

        // Converting the list to array
        dayAverages = new Integer[dayAveragesList.size()];
        dayAveragesList.toArray(dayAverages);

        // Calculating the hours the business is open in the given day
        int openingHour = selectedBusiness.getOpeningHours().get(day).getOpen() / 100;
        int closingHour = selectedBusiness.getOpeningHours().get(day).getClose() / 100;

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
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        chart.setColumnChartData(data);

        // Updating the text of the current day
        averageDay.setText(getResources().getStringArray(R.array.days)[day]);
    }
    @Override
    public void onRefresh() {
        // TODO: Load the details from the server
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }, 1000);
    }
}
