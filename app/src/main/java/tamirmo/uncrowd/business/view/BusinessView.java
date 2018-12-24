package tamirmo.uncrowd.business.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.github.mikephil.charting.charts.LineChart;

import tamirmo.uncrowd.R;
import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.databinding.BusinessViewBinding;

import lecho.lib.hellocharts.view.LineChartView;

public class BusinessView extends ConstraintLayout {
    private BusinessViewBinding binding;
    private Business business;
    private LineChart lineChartView;

    public BusinessView(Context context) {
        super(context);
        init(context);
    }

    public BusinessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BusinessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        binding = BusinessViewBinding.inflate(layoutInflater, this, true);
        lineChartView = binding.getRoot().findViewById(R.id.trend_graph);
    }

    public void setBusiness(Business business){
        this.business = business;

        if(binding != null){
            binding.setBusiness(this.business);
            binding.executePendingBindings();
        }

        if(lineChartView != null) {
            // Populating the trend graph with the data from the Business object
            // (the graph does not support binding)
            BusinessViewsUtilities.showTrendGraph(lineChartView,
                    business,
                    false);
        }
    }
}
