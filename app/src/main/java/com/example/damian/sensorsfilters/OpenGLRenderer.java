package com.example.damian.sensorsfilters;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRenderer implements GLSurfaceView.Renderer, SensorEventListener {

    private Cube mCube = new Cube();
    private Context context;

    private float[] angles = new float[3];


    OpenGLRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -10.0f);

        double xrot = angles[0] * 180 / Math.PI;
        double yrot = angles[1] * 180 / Math.PI;
        double zrot = angles[2] * 180 / Math.PI;

        gl.glRotatef((float) -xrot, 1.0f, 0.0f, 0.0f);   //X
        gl.glRotatef((float) -yrot, 0.0f, 1.0f, 0.0f);   //Y
        gl.glRotatef((float) -zrot, 0.0f, 0.0f, 1.0f);   //Z

        //gl.glRotatef(-angles[0], angles[1], angles[2], angles[3]);

        mCube.draw(gl);
        gl.glLoadIdentity();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = CalEulers.getAngles(event);

        if (values != null){
            angles = values;
        }

    }

  /*  @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = CalQuanternions.getAngles(event);

        if (values != null){
            angles = values;
        }

    }*/

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}