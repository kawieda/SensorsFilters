package com.example.damian.sensorsfilters;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class OpenGLDemoActivity extends Activity implements View.OnClickListener {

    private OpenGLView view;
    private Enum filter = Enum.NOFILTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gldemo);

        // Go fullscreen
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /*GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(new OpenGLRenderer(getApplicationContext()));*/
        view = new OpenGLView(this);
        setContentView(view);
        view.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        view.onResume();
        //CalEulers.setComplementaryFilter();

        //CalQuanternions.setComplementaryFilter();

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyr = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        OpenGLRenderer renderer = view.getRenderer();
        sensorManager.registerListener(renderer, gyr, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(renderer, acc, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(renderer, mag, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        view.onPause();
        CalEulers.filtersOff();
        //CalQuanternions.filtersOff();

        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).unregisterListener(view.getRenderer());
    }

    @Override
    public void onClick(View v) {
        //CalEulers.resetValues();
        //CalQuanternions.resetValues();
        switch (filter) {
            case NOFILTER:
                filter = Enum.COMPLEMENTARY;
                CalEulers.resetValues();
                CalEulers.setComplementaryFilter();
                Toast.makeText(getApplicationContext(), "Complementary filter on", Toast.LENGTH_SHORT).show();
                break;
            case COMPLEMENTARY:
                filter = Enum.NOFILTER;
                CalEulers.resetValues();
                CalEulers.filtersOff();
                Toast.makeText(getApplicationContext(), "Filters off", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    enum Enum {
        NOFILTER, COMPLEMENTARY, KALMAN
    }
}

