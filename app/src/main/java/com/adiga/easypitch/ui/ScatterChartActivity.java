package com.adiga.easypitch.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.adiga.easypitch.R;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.ArrayList;

/**
 * Created by dbajj on 2017-12-03.
 */

public class ScatterChartActivity extends AppCompatActivity {

    private ScatterChart mChart;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scatterchart);

        mChart  = findViewById(R.id.chart1);

        ArrayList<Entry> yVal = new ArrayList<Entry>();

        for (int i = 1; i < 6; i++) {
            yVal.add(new Entry(i,(float) Math.pow(i,2)));
        }

        ScatterDataSet dataSet = new ScatterDataSet(yVal,"DS 1");

        ScatterData data = new ScatterData(dataSet);

        mChart.setData(data);

        mChart.invalidate();







    }


}
