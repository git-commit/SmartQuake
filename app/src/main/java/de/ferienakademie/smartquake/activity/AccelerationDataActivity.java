package de.ferienakademie.smartquake.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.excitation.AccelData;
import de.ferienakademie.smartquake.excitation.FileAccelerationProvider;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by root on 28.09.16.
 */
public class AccelerationDataActivity extends AppCompatActivity {

    private String equationData;
    private FileInputStream fileInputStream = null;
    private ArrayList<AccelData> list;
    //AccelData currentReading;
    private Long time;
    private Integer time_int;
    private Double xAcceleration;
    private Double yAcceleration;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.accdata_graph_view);

        LayoutInflater i = this.getLayoutInflater();

        final View view = i.inflate(R.layout.accdata_graph_view, null);

        equationData = getIntent().getExtras().getString("eqDataFile");
        equationData = equationData + ".earthquake";
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series_x = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> series_y = new LineGraphSeries<DataPoint>();
        //File file = new File(getFilesDir().getAbsoluteFile() + "/" + equationData);
        try {

            FileAccelerationProvider fileAccelerationProvider = new FileAccelerationProvider();
            fileInputStream = openFileInput(equationData);
            fileAccelerationProvider.load(fileInputStream);
            if (!fileAccelerationProvider.isEmpty()) {
                list = fileAccelerationProvider.readData();
                Log.v("IVANA:ACCELERATION", "i riched this");
                for (AccelData currentReading : list)  {
                    time = currentReading.timestamp;
                    if(time>0) {
                        xAcceleration = currentReading.xAcceleration;
                        yAcceleration = currentReading.yAcceleration;
                        series_x.appendData(new DataPoint(time, xAcceleration), true, 100);
                        series_y.appendData(new DataPoint(time, yAcceleration), true, 100);
                    }
                    }
                Viewport viewport = graph.getViewport();
                //viewport.setYAxisBoundsManual(true);
                //viewport.setMinY(0);
                //viewport.setMaxY(10);
                //viewport.setScrollable(true);
                graph.setTitle(equationData.substring(0, equationData.length() - 11));
                graph.setTitleTextSize(30);
                graph.setBackgroundColor(Color.WHITE);
                graph.getLegendRenderer().setVisible(true);
                graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
                graph.getLegendRenderer().setBackgroundColor(Color.WHITE);
                series_x.setColor(Color.GREEN);
                series_y.setColor(Color.RED);
                series_x.setThickness(5);
                series_y.setThickness(5);
                series_x.setTitle("X Acce");
                series_y.setTitle("Y Acce");
                graph.addSeries(series_x);
                graph.addSeries(series_y);
                //StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
                //staticLabelsFormatter.setHorizontalLabels(new String[] {"0","End"});
                //graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                }

             else {
                Toast.makeText(getApplicationContext(),
                        "No acceleration data", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException e) {
            Log.e("ACCEL READ", "file not found", e);
        } catch (IOException e) {
            Log.e("ACCEL READ", "error writing", e);
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            } catch (IOException e) {
                Log.e("ACCEL READ", "error closing", e);
            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
