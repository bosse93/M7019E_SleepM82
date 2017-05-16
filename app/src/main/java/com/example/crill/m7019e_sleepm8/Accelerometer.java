package com.example.crill.m7019e_sleepm8;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.Calendar;
import java.util.Date;
import static java.lang.Math.abs;


public class Accelerometer extends Service implements SensorEventListener {
    HandlerThread handlerThread = null;

    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Handler handler = null;

    float [] history = new float[2];
    private long sensorTimeMs;
    private float sensitivity = 5;

    String message = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test", "Start Thread: " + Looper.getMainLooper().getThread().getName() + " " + Looper.getMainLooper().getThread().getId());
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();

        mInitialized = false;
        // Instantiate SensorManager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Get Accelerometer sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d("test onCreateSensor", "det körs automagiskt när jag gör klassen");
        handler = new Handler(handlerThread.getLooper());
        register();

        return START_STICKY;
    }

    public void register() {
        Log.d("test", "Register Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL, handler);
    }

    public void unregister() {
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("test", "Destroy Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
        Log.d("test", "Service Destroyed");
        handlerThread.quitSafely();
        Log.d("test", "ThreadRemoved");
        unregister();
        Log.d("test", "SensorRemoved");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                sensorTimeMs = Calendar.getInstance().getTimeInMillis();
                Log.d("test", "xChanged" + sensorTimeMs);
                Log.d("test", "Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                Log.d("test", "" + message);


            }

            if (abs(yChange) > sensitivity){
                sensorTimeMs = Calendar.getInstance().getTimeInMillis();
                Log.d("test", "yChanged" + sensorTimeMs);
                Log.d("test", "Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                Log.d("test", "" + message);


            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
}
