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
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import static java.lang.Math.abs;


public class Accelerometer extends Service implements SensorEventListener {
    HandlerThread handlerThread = null;

    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private Handler handlerSensor = null;
    private Handler handlerStartAlarm = null;
    private Handler handlerRunAlarm = null;

    private PowerManager.WakeLock wakeLockAlarm = null;

    private Runnable runnableStartAlarm = new Runnable() {
        @Override
        public void run() {
            Log.d("test", "Larm schema lagt");
            Log.d("test", "Runnable Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
            unregister();
            //ÄNDRA 10000 TILL LARM TID
            handlerRunAlarm = new Handler(handlerThread.getLooper());
            handlerRunAlarm.postDelayed(runnableRunAlarm, alarmTime);
        }
    };

    private Runnable runnableRunAlarm = new Runnable() {
        @Override
        public void run() {
            Log.d("test", "LarmLARM");
            sendMessage("executeAlarm", 2);
        }
    };

    float [] history = new float[2];
    private int sensitivityPercent;
    private double defaultSens = 10.0;
    private double sensitivity;
    private long alarmTime;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test", "Start Thread: " + Looper.getMainLooper().getThread().getName() + " " + Looper.getMainLooper().getThread().getId());

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLockAlarm = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
        wakeLockAlarm.acquire();

        //Fix seekbar
        sensitivityPercent = intent.getIntExtra("sensitivity", 0);
        sensitivity = ((defaultSens/100) * (100 - sensitivityPercent));

        //Fix alarm time
        alarmTime = intent.getLongExtra("alarmtime", 0);

        Log.d("test", "Changed sensitivty in acc "+sensitivity);
        handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();

        mInitialized = false;
        // Instantiate SensorManager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Get Accelerometer sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d("test onCreateSensor", "det körs automagiskt när jag gör klassen");
        handlerSensor = new Handler(handlerThread.getLooper());
        register();

        //ÄNDRA 10000 TILL TID FRÅN ACTIVATE TILL START LARM
        handlerStartAlarm = new Handler(handlerThread.getLooper());
        handlerStartAlarm.postDelayed(runnableStartAlarm, 10000); //Byt till något annat


        return START_STICKY;
    }

    public void register() {
        Log.d("test", "Register Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL, handlerSensor);
    }

    public void unregister() {
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerStartAlarm.removeCallbacks(runnableStartAlarm);
        wakeLockAlarm.release();
        Log.d("test", "Destroy Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
        Log.d("test", "Service Destroyed");
        handlerThread.quit();
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
                //ÄNDRA 10000 TILL TID FRÅN ACTIVATE TILL START LARM
                startAlarm(10000);
                Log.d("test", "Sensor Change Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
            }

            if (abs(yChange) > sensitivity){
                //ÄNDRA 10000 TILL TID FRÅN ACTIVATE TILL START LARM
                startAlarm(10000);
                Log.d("test", "Sensor Change Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startAlarm(long alarmTime) {
        handlerStartAlarm.removeCallbacks(runnableStartAlarm);
        handlerStartAlarm.postDelayed(runnableStartAlarm, alarmTime);
    }

    public void sendMessage (String intentName, int message) {
        Intent intent = new Intent(intentName);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
