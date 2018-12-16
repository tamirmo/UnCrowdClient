package tamirmo.uncrowd.business.view;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import tamirmo.uncrowd.data.Business;
import tamirmo.uncrowd.data.CrowdHistory;

public final class BusinessViewsUtilities {
    public static void showTrendGraph(LineChartView lineChartView, Business business, boolean showYAxis){
        List<PointValue> yAxisValues = new ArrayList<>();

        float[] lastCrowdCounts = new float[business.getCrowdHistory().size()];

        int i = 0;
        for (CrowdHistory crowdHistory : business.getCrowdHistory()) {
            lastCrowdCounts[i++] = crowdHistory.getCrowdCount();
        }

        // If all the last counts are the same the lime will not be visible (LineChartView bug)
        boolean isCountsTheSame = true;
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
        }



        for (i = 0; i < lastCrowdCounts.length; i++){
            yAxisValues.add(new PointValue(i, lastCrowdCounts[i]));
        }

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        lineChartView.setLineChartData(data);

        if(showYAxis) {
            Axis yAxis = new Axis();
            data.setAxisYLeft(yAxis);
        }
    }
}
