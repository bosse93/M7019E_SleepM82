package com.example.crill.m7019e_sleepm8;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.abs;

/**
 * Created by crill on 2017-05-15.
 */

public class Accelerometer extends MainActivity implements SensorEventListener {
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    float [] history = new float[2];
    private long sensorTimeMs;
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



    /** Register & unregister needed to override paus and resume functions by handling listeners **/
    public void register() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister() {
        mSensorManager.unregisterListener(this);
    }


    /** Getters and setters **/
    public Long getSensorTimeMs() {
        return sensorTimeMs;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    /** On change
     *  Checks for delta change
     *  acts depending on sensitivity
     *  updates date
     *
     *  **/

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
                sensorTimeMs = Calendar.getInstance().getTimeInMillis();
                Log.d("test", "xChanged" + sensorTimeMs);

            }

            if (abs(yChange) > sensitivity){
                sensorTimeMs = Calendar.getInstance().getTimeInMillis();
                Log.d("test", "yChanged" + sensorTimeMs);
            }
        }

    }

    /** needed **/
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
