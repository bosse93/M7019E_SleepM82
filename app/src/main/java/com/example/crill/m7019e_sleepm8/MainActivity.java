package com.example.crill.m7019e_sleepm8;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_READ_PERMISSION = 200;
    private boolean permissionToReadAccepted = false;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    Intent sensors = null;
    Intent settings = null;

    Handler handlerStartAlarm = null;

    private PowerManager.WakeLock wakeLockAlarm = null;

    private int sensitivity = 50;

    private Runnable runnableExecuteAlarm = new Runnable() {
        @Override
        public void run() {
            Log.d("test", "Larm Runnable Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
            startAlarm();
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            int intCase = intent.getIntExtra("message",1);
            switch (intCase) {
                case 1:
                    //FAIL
                case 2:
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    wakeLockAlarm = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
                    wakeLockAlarm.acquire();
                    Log.d("test", "" + intCase);
                    //Första gången. 0 är rätt.
                    startSnoozeAlarm(0);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("test", "Start Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
        ActivityCompat.requestPermissions(this, permissions, REQUEST_READ_PERMISSION);

        settings = new Intent(this, Settings.class);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("executeAlarm"));

        handlerStartAlarm = new Handler();

        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        Button test = (Button) findViewById(R.id.button2);
        test.setOnClickListener((
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        Log.d("test", "jag har ingen funktionalitet atm");

                    }
                }
        ));


        /** Sleep history button **/
        Button sleepHistory = (Button) findViewById(R.id.sleepHistoryButton);
        sleepHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Click event trigger here
            }
        });

        /** Active button **/
        ToggleButton toggle = (ToggleButton) findViewById(R.id.activeButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startSensorBackground(sensitivity);
                    // The toggle is enabled
                    Log.d("test", "toggle - ENABLED");
                } else {
                    stopService(sensors);
                    handlerStartAlarm.removeCallbacks(runnableExecuteAlarm);
                    if(wakeLockAlarm != null) {
                        wakeLockAlarm.release();
                    }
                    // The toggle is disabled
                    Log.d("test", "toggle - DISABLED");
                }
            }
        });



    }


    public void startSensorBackground(int sensitivity) {
        sensors = new Intent(this, Accelerometer.class);
        sensors.putExtra("sensitivity", sensitivity);
        Log.d("test", "startSensorBackground :" + sensitivity);
        startService(sensors);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case (1):
                if (resultCode == RESULT_OK) {
                    sensitivity = intent.getIntExtra("sensitivity", 50);
                    Log.d("test", "onActivityResult x:" + sensitivity);
                } else {
                    Log.d("test", "SettingSetFailed" + resultCode);
                }
                break;
            case (2):
                if (resultCode == RESULT_OK) {
                    if(intent.getBooleanExtra("snooze", false)) {
                        //ÄNDRA 10000 TILL SNOOZE TID
                        startSnoozeAlarm(10000);
                    } else {
                        //KNAPPEN FRÅN GRÖN TILL RÖD??
                        stopService(sensors);
                        if(wakeLockAlarm != null) {
                            wakeLockAlarm.release();
                        }
                    }
                    Log.d("test", "AlarmReturn");
                } else {
                    Log.d("test", "AlarmReturnFailed" + resultCode);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Log.d("test", "Clicked toolbar action_settings");
                settings.putExtra("sensitivity", sensitivity);
                startActivityForResult(settings, 1);
                return true;

            /**case R.id.action_test:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Log.d("test", "Toolbar action_test");
                return true; **/

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void startAlarm(){
        Intent alarmIntent = new Intent(this, Alarm.class);
        startActivityForResult(alarmIntent, 2);
    }

    public void startSnoozeAlarm(long snoozeTime) {
        handlerStartAlarm.postDelayed(runnableExecuteAlarm, snoozeTime);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PERMISSION:
                permissionToReadAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToReadAccepted) finish();

    }
}
