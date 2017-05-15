package com.example.crill.m7019e_sleepm8;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by crill on 2017-05-15.
 */

public class Accelerometer extends MainActivity implements SensorEventListener {
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    float [] history = new float[2];
    private boolean changed = false;
    private float sensitivity = 5;


    //constructor
    public Accelerometer(Context context) {
        mInitialized = false;
        // Instantiate SensorManager
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // Get Accelerometer sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d("test onCreateSensor", "det körs automagiskt när jag gör klassen");
    }




    public void register() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister() {
        mSensorManager.unregisterListener(this);
    }

    public void setChanged(){
        changed = false;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!mInitialized) {
            Log.d("test", "Initalizing sensors");
            mInitialized = true;
        } else {
            float xChange = history[0] - event.values[0];
            float yChange = history[1] - event.values[1];

            history[0] = event.values[0];
            history[1] = event.values[1];

            if (abs(xChange) > sensitivity){
                changed = true;
                Log.d("test", "xChanged");
            }

            if (abs(yChange) > sensitivity){
                changed = true;
                Log.d("test", "yChanged");
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
