package com.NewApp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import com.androidplot.xy.*;

import java.util.ArrayList;
import java.util.List;

 
/**
 * A straightforward example of using AndroidPlot to plot some data.
 */
public class PlotActivity extends Activity
{
 
    private XYPlot plot;
 
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
 
        // fun little snippet that prevents users from taking screenshots
        // on ICS+ devices :-)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                                 WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.plot);
 
       // plotInGraph(0, "posture");
       // plotInGraph(1, "heart_rate");
       // plotInGraph(2, "activity");

        
 
    }
    
    private void plotInGraph(int gId, String colVal ){
        
        // initialize our XYPlot reference:
    	switch (gId){
    	case 0:
        	plot = (XYPlot) findViewById(R.id.plotPosture);
        	break;
    	case 1:
    		plot = (XYPlot) findViewById(R.id.plotHR);
        	break;
    	case 2:
    		plot = (XYPlot) findViewById(R.id.plotActivity);
    		break;
        default:
        	break;
    		
    	}
 
        // Create a couple arrays of y-values to plot:
        List<Number> numbers = getValuesToPlot(colVal);
        
        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, colVal);    // Y_VALS_ONLY means use the element index as the x value
 
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.rgb(0, 0, 200),
        	    Color.rgb(0, 0, 100),  null, null);
        //series1Format.setPointLabelFormatter(new PointLabelFormatter());
        //series1Format.configure(getApplicationContext(), R.xml.line_point_formatter_with_plf1);
 
        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
 
 
        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.getGraphWidget().setDomainLabelOrientation(-45);
        
    	
    }
    
    private List<Number> getValuesToPlot(String col){
    	List<Number> result = new ArrayList();
    	/*TODO List<String > values = Database.readFromDatabase(this, "zephyr", col);
    	for (int i=0; i < values.size(); i++){
    		result.add( Float.parseFloat(values.get(i)));

    	}*/
    	return result;
    	
    }
}
