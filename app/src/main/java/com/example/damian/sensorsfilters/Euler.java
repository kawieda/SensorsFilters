package com.example.damian.sensorsfilters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class Euler extends Activity implements SensorEventListener {

    private BarGraphSeries<DataPoint> series;
    private SensorManager sensorManager;
    private Sensor gyr;
    private Sensor acc;
    private Sensor mag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_euler);

        Button b1 = (Button)findViewById(R.id.button);
        b1.setOnClickListener(click);
        Button b2 = (Button)findViewById(R.id.button2);
        b2.setOnClickListener(click);
        Button b3 = (Button)findViewById(R.id.button3);
        b3.setOnClickListener(click);
        Button b4 = (Button)findViewById(R.id.button4);
        b4.setOnClickListener(click);
        GraphView graph = (GraphView)findViewById(R.id.graph);
        graph.setOnClickListener(click);

        series = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
        });
        graph.addSeries(series);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        series.setSpacing(20);
        CalEulers.resetValues();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        gyr = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        double[] values = CalEulers.getEulers(event);

        if (values != null) {
            series.resetData(new DataPoint[]{
                    new DataPoint(0, 0),
                    new DataPoint(1, values[0] * 180 / Math.PI),
                    new DataPoint(2, values[1] * 180 / Math.PI),
                    new DataPoint(3, values[2] * 180 / Math.PI),
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        CalEulers.resetValues();

        sensorManager.registerListener(this, gyr, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause(){
        super.onPause();
        CalEulers.filtersOff();
        sensorManager.unregisterListener(this);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.button:
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.button2:
                    CalEulers.filtersOff();
                    break;
                case R.id.button3:
                    CalEulers.setComplementaryFilter();
                    break;
                case R.id.button4:
                    break;
                case R.id.graph:
                    CalEulers.resetValues();
                    break;
            }
        }
    };

}
