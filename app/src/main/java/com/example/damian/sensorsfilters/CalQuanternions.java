package com.example.damian.sensorsfilters;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.Timer;
import java.util.TimerTask;

import Jama.Matrix;

public class CalQuanternions {

    private static final float time = 1.0f / 1000000000.0f;
    private static float timestamp;

    private static double prevQ1;
    private static double prevQ2;
    private static double prevQ3;
    private static double prevQ4 = 1;

    private static Timer timer = new Timer();
    public static final float FILTER_COEFFICIENT = 0.98f;

    // for Complementary filter
    private static float[] magnet = new float[3];
    private static float[] accel = new float[3];
    private static float[] accMagOrientation = new float[3];
    private static float[] rotationMatrix = new float[9];

    // for Kalman Filter
    private static double[][] vals = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
    private static Matrix matrixA = new Matrix(4, 4);
    private static Matrix matrixH = new Matrix(vals);
    private static Matrix matrixP = new Matrix(vals);
    private static Matrix matrixQ = new Matrix(vals);
    private static Matrix matrixR = new Matrix(vals);
    private static Matrix matrixX = new Matrix(new double[][]{{1},{0},{0},{0}});
    private static Matrix xp = new Matrix(4, 4);
    private static Matrix pp = new Matrix(4, 4);
    private static Matrix k = new Matrix(4, 4);
    private static double[] kalmans = new double[4];


    public static double[] getQuanternions(SensorEvent event){

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // copy new accelerometer data into accel array then calculate new orientation
                System.arraycopy(event.values, 0, accel, 0, 3);
                return null;

            case Sensor.TYPE_GYROSCOPE:
                return calGyro(event);
                //return setKalmanFilter(event);

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

                final double[] filteredQuat = eulersToQuaternions();

                prevQ1 = prevQ1 * FILTER_COEFFICIENT + filteredQuat[3] * oneMinusCoeff;
                prevQ2 = prevQ2 * FILTER_COEFFICIENT + filteredQuat[1] * oneMinusCoeff;
                prevQ3 = prevQ3 * FILTER_COEFFICIENT + filteredQuat[2] * oneMinusCoeff;
                prevQ4 = prevQ4 * FILTER_COEFFICIENT + filteredQuat[0] * oneMinusCoeff;

            }
        }, 1000, 30);
    }

    public static double[] setKalmanFilter(SensorEvent event){

        if (timestamp != 0) {
            final float dt = (event.timestamp - timestamp) * time;
            //SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet);
            //SensorManager.getOrientation(rotationMatrix, accMagOrientation);

            final double[] filteredQuat = eulersToQuaternions();
            final double[] pqr = CalEulers.calGyro(event);

            Matrix z = new Matrix(new double[][]{{filteredQuat[3]},{filteredQuat[1]},{filteredQuat[2]},{filteredQuat[0]}});

            if (pqr != null){
                Matrix matrixPQR = new Matrix(new double[][]{{0, -pqr[0], -pqr[1], -pqr[2]},
                        {pqr[0], 0, pqr[2], -pqr[1],},
                        {pqr[1], -pqr[2], 0, pqr[0]},
                        {pqr[2], pqr[1], -pqr[0], 0}
                });
                matrixA = matrixH.plus(matrixPQR.times(dt * 0.5));
            }

            xp = matrixA.times(matrixX);
            pp = ((matrixA.times(matrixP)).times(matrixA.transpose())).plus(matrixQ);

            k = ((pp.times(matrixH)).times(((matrixH.times(pp)).plus(matrixR)).inverse()));

            matrixX = xp.plus((k.times(z)).minus((k.times(matrixH)).times(xp)));
            matrixP = pp.minus((k.times(matrixH)).times(pp));

        }
        timestamp = event.timestamp;

        kalmans = matrixX.getRowPackedCopy();

        final double Nq = Math.pow(kalmans[0], 2) + Math.pow(kalmans[1], 2) + Math.pow(kalmans[2], 2) + Math.pow(kalmans[3], 2);

        kalmans[0] = kalmans[0] / Math.sqrt(Nq);
        kalmans[1] = kalmans[1] / Math.sqrt(Nq);
        kalmans[2] = kalmans[2] / Math.sqrt(Nq);
        kalmans[3] = kalmans[3] / Math.sqrt(Nq);

        for (int i = 0; i < 4; i++){
            matrixX.set(i, 0, kalmans[i]);
        }

        return kalmans;
    }

    public static double[] eulersToQuaternions() {
        if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        }

        final double a1 = Math.cos((-1) * accMagOrientation[0] / 2);
        final double b1 = Math.sin((-1) * accMagOrientation[0] / 2);
        final double a2 = Math.cos((-1) * accMagOrientation[1] / 2);
        final double b2 = Math.sin((-1) * accMagOrientation[1] / 2);
        final double a3 = Math.cos(accMagOrientation[2] / 2);
        final double b3 = Math.sin(accMagOrientation[2] / 2);
        final double a1a2 = a1 * a2;
        final double b1b2 = b1 * b2;

        final double[] quanternions = new double[4];

        quanternions[0] = a1a2 * a3 - b1b2 * b3;
        quanternions[1] = a1a2 * b3 + b1b2 * a3;
        quanternions[2] = b1 * a2 * a3 + a1 * b2 * b3;
        quanternions[3] = a1 * b2 * a3 - b1 * a2 * b3;

        return quanternions;
    }

    public static void filtersOff(){
        timer.cancel();
    }

    private static double[] calGyro(SensorEvent event){
        if (timestamp != 0) {
            final float dt = (event.timestamp - timestamp) * time;

            double wx = event.values[0];
            double wy = event.values[1];
            double wz = event.values[2];

            double q1 = prevQ1 + (dt * 0.5) * ((prevQ4 * wx) - (prevQ3 * wy) + (prevQ2 * wz));
            double q2 = prevQ2 + (dt * 0.5) * ((prevQ3 * wx) + (prevQ4 * wy) - (prevQ1 * wz));
            double q3 = prevQ3 + (dt * 0.5) * (-(prevQ2 * wx) + (prevQ1 * wy) + (prevQ4 * wz));
            double q4 = prevQ4 + (dt * 0.5) * (-(prevQ1 * wx) - (prevQ2 * wy) - (prevQ3 * wz));

            final double Nq = Math.pow(q1, 2) + Math.pow(q2, 2) + Math.pow(q3, 2) + Math.pow(q4, 2);

            q1 = q1 / Math.sqrt(Nq);
            q2 = q2 / Math.sqrt(Nq);
            q3 = q3 / Math.sqrt(Nq);
            q4 = q4 / Math.sqrt(Nq);

            prevQ1 = q1;
            prevQ2 = q2;
            prevQ3 = q3;
            prevQ4 = q4;
        }
        timestamp = event.timestamp;

        double[] values = new double[4];
        values[0] = prevQ1;
        values[1] = prevQ2;
        values[2] = prevQ3;
        values[3] = prevQ4;

        return values;
    }

    public static void resetValues(){
        prevQ1 = 0;
        prevQ2 = 0;
        prevQ3 = 0;
        prevQ4 = 1;
    }

    public static void resetKalmanValues(){

        matrixX.set(0, 0, 0);
        matrixX.set(1, 0, 0);
        matrixX.set(2, 0, 0);
        matrixX.set(3, 0, 1);

        for (int i = 0; i<4; i++){

            matrixQ.set(i, i, 0.0001f);
            matrixR.set(i, i, 10f);

            matrixH.set(i, i, 1);
            matrixP.set(i, i, 1);

        }

    }

    public static float[] getAngles(SensorEvent event){

        final float angles[] = new float[4];
        final double[] values = getQuanternions(event);

        if (values != null){
            angles[0] = (float) Math.toDegrees((2 * Math.acos(values[3])));
            angles[1] = (float) values[0];
            angles[2] = (float) values[1];
            angles[3] = (float) values[2];

            return angles;
        }
        else {
            return null;
        }

    }

}