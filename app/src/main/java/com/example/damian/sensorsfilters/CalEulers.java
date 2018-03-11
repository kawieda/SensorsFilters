package com.example.damian.sensorsfilters;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.Timer;
import java.util.TimerTask;

public class CalEulers {

    private static final float time = 1.0f / 1000000000.0f;
    private static float timestamp;

    private static double prevPhi;
    private static double prevTheta;
    private static double prevPsi;

    private static float[] magnet = new float[3];
    private static float[] accel = new float[3];
    private static float[] accMagOrientation = new float[3];
    private static float[] rotationMatrix = new float[9];

    private static Timer timer = new Timer();
    public static final float FILTER_COEFFICIENT = 0.98f;


    public static double[] getEulers(SensorEvent event){
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // copy new accelerometer data into accel array then calculate new orientation
                System.arraycopy(event.values, 0, accel, 0, 3);
                return null;

            case Sensor.TYPE_GYROSCOPE:
                return calGyro(event);

            case Sensor.TYPE_MAGNETIC_FIELD:
                // copy new magnetometer data into magnet array
                System.arraycopy(event.values, 0, magnet, 0, 3);
                return null;
        }
        return null;
    }

    public static void setComplementaryFilter(){

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
                    SensorManager.getOrientation(rotationMatrix, accMagOrientation);
                }

                final double oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
                prevPhi = FILTER_COEFFICIENT * prevPhi + oneMinusCoeff * (-accMagOrientation[1]);
                prevTheta = FILTER_COEFFICIENT * prevTheta + oneMinusCoeff * accMagOrientation[2];
                prevPsi = FILTER_COEFFICIENT * prevPsi + oneMinusCoeff * (-accMagOrientation[0]);

            }
        }, 1000, 30);
    }

    public static void filtersOff(){
        timer.cancel();
    }

    public static double[] calGyro(SensorEvent event){
        if (timestamp != 0) {
            final float dt = (event.timestamp - timestamp) * time;

            double wx = event.values[0];
            double wy = event.values[1];
            double wz = event.values[2];

            double sinPhi = Math.sin(prevPhi);
            double cosPhi = Math.cos(prevPhi);
            double cosTheta = Math.cos(prevTheta);
            double tanTheta = Math.tan(prevTheta);

            double phi = prevPhi + dt * (wx + wy * sinPhi * tanTheta + wz * cosPhi * tanTheta);
            double theta = prevTheta + dt * (wy * cosPhi - wz * sinPhi);
            double psi = prevPsi + dt * (wy * sinPhi / cosTheta + wz * cosPhi / cosTheta);

            prevPhi = phi;
            prevTheta = theta;
            prevPsi = psi;

        }
        timestamp = event.timestamp;

        double[] values = new double[3];
        values[0] = prevPhi;
        values[1] = prevTheta;
        values[2] = prevPsi;

        return values;
    }

    public static void resetValues(){
        prevPhi = 0;
        prevTheta = 0;
        prevPsi = 0;
    }

    public static float[] getAngles(SensorEvent event){

        final float angles[] = new float[3];
        final double[] values = getEulers(event);

        if (values != null){
            angles[0] = (float) values[0];
            angles[1] = (float) values[1];
            angles[2] = (float) values[2];

            return angles;
        }
        else {
            return null;
        }
    }

}

