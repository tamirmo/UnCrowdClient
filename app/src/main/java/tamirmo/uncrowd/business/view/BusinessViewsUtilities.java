package tamirmo.uncrowd.business.view;

import android.databinding.BindingAdapter;
import android.os.Build;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import tamirmo.uncrowd.R;
import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.data.CrowdHistory;

public final class BusinessViewsUtilities {
    static void showTrendGraph(LineChart lineChartView, Business business){
        if(business.getCrowdHistory() != null) {
            float[] lastCrowdCounts = new float[business.getCrowdHistory().size()];

            int i = 0;
            for (CrowdHistory crowdHistory : business.getCrowdHistory()) {
                lastCrowdCounts[i++] = crowdHistory.getCrowdCount();
            }

            List<Entry> entries = new ArrayList<>();

            for (i = 0; i < lastCrowdCounts.length; i++) {

                // turn your data into Entry objects
                entries.add(new Entry(business.getCrowdHistory().get(i).getDateTime(), lastCrowdCounts[i]));
            }

            LineDataSet dataSet = new LineDataSet(entries, null); // add entries to dataset
            dataSet.setLineWidth(3);
            dataSet.setValueTextSize(13);
            dataSet.setColor(0x0541a2, 255);
            dataSet.setCircleHoleRadius(8);
            dataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.format("%.0f", value);
                }
            });
            //dataSet.setValueTextColor(...); // styling, ...

            lineChartView.getXAxis().setEnabled(false);
            lineChartView.getXAxis().enableAxisLineDashedLine(0, 0, 0);
            lineChartView.getXAxis().setLabelCount(lastCrowdCounts.length);
            lineChartView.setDescription(null);
            lineChartView.setDrawGridBackground(false);
            lineChartView.getLegend().setEnabled(false);
            lineChartView.getAxisLeft().setEnabled(false);
            lineChartView.getAxisRight().setEnabled(false);

            lineChartView.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            lineChartView.getXAxis().setEnabled(true);

            lineChartView.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return (value % 100 < 60) ? String.format("%02d:%02d",(int)(value / 100), (int)(value % 100)) : "";
                }
            });

            LineData lineData = new LineData(dataSet);
            lineChartView.setData(lineData);
            lineChartView.invalidate();
        }
    }

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, int crowdLevel) {
        int drawableId = R.drawable.one_crowd_lvl;
        switch (crowdLevel) {
            case 1:
                drawableId = R.drawable.one_crowd_lvl;
                break;
            case 2:
                drawableId = R.drawable.two_crowd_lvl;
                break;
            case 3:
                drawableId = R.drawable.three_crowd_lvl;
                break;
            case 4:
                drawableId = R.drawable.four_crowd_lvl;
                break;
            case 5:
                drawableId = R.drawable.five_crowd_lvl;
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(view.getContext().getResources().getDrawable(drawableId, view.getContext().getTheme()));
        } else {
            view.setImageDrawable(view.getContext().getResources().getDrawable(drawableId));
        }

    }
}
