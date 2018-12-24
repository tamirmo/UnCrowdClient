package tamirmo.uncrowd.business.view;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.data.CrowdHistory;

final class BusinessViewsUtilities {
    static void showTrendGraph(LineChart lineChartView, Business business, boolean showYAxis){
        if(business.getCrowdHistory() != null) {
            float[] lastCrowdCounts = new float[business.getCrowdHistory().size()];

            int i = 0;
            for (CrowdHistory crowdHistory : business.getCrowdHistory()) {
                lastCrowdCounts[i++] = crowdHistory.getCrowdCount();
            }

            // If all the last counts are the same the lime will not be visible (LineChartView bug)
        /*boolean isCountsTheSame = true;
        if(lastCrowdCounts.length > 0){
            float firstVal = lastCrowdCounts[0];
            for(float count : lastCrowdCounts){
                if(count != firstVal){
                    isCountsTheSame = false;
                    break;
                }
            }

            if(isCountsTheSame){
                lastCrowdCounts[0] += 1;
            }
        }*/


            List<Entry> entries = new ArrayList<>();

            for (i = 0; i < lastCrowdCounts.length; i++) {

                // turn your data into Entry objects
                entries.add(new Entry(business.getCrowdHistory().get(i).getDateTime(), lastCrowdCounts[i]));
            }

            LineDataSet dataSet = new LineDataSet(entries, null); // add entries to dataset
            dataSet.setLineWidth(3);
            //dataSet.setColor();
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
            lineChartView.getXAxis().setEnabled(showYAxis);

            LineData lineData = new LineData(dataSet);
            lineChartView.setData(lineData);
            lineChartView.invalidate();
        }
    }
}
