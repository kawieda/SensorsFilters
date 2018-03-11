package com.example.damian.sensorsfilters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RawData extends Activity implements SensorEventListener {

    private TextView xAcc;
    private TextView yAcc;
    private TextView zAcc;
    private TextView xGyr;
    private TextView yGyr;
    private TextView zGyr;
    private TextView xMag;
    private TextView yMag;
    private TextView zMag;

    private Sensor accSensor;
    private Sensor gyrSensor;
    private Sensor magSensor;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_data);

        xAcc = (TextView) findViewById(R.id.xAcc);
        yAcc = (TextView) findViewById(R.id.yAcc);
        zAcc = (TextView) findViewById(R.id.zAcc);
        xGyr = (TextView) findViewById(R.id.xGyr);
        yGyr = (TextView) findViewById(R.id.yGyr);
        zGyr = (TextView) findViewById(R.id.zGyr);
        xMag = (TextView) findViewById(R.id.xMag);
        yMag = (TextView) findViewById(R.id.yMag);
        zMag = (TextView) findViewById(R.id.zMag);

        Button goB = (Button) findViewById(R.id.button4);
        goB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyrSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAcc.setText(String.valueOf(event.values[0]));
            yAcc.setText(String.valueOf(event.values[1]));
            zAcc.setText(String.valueOf(event.values[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            xGyr.setText(String.valueOf(event.values[0]));
            yGyr.setText(String.valueOf(event.values[1]));
            zGyr.setText(String.valueOf(event.values[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            xMag.setText(String.valueOf(event.values[0]));
            yMag.setText(String.valueOf(event.values[1]));
            zMag.setText(String.valueOf(event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, gyrSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
