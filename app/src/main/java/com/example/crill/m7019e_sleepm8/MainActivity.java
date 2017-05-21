package com.example.crill.m7019e_sleepm8;

import android.Manifest;
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
    private int startAlarmTimeHour = 0;
    private int startAlarmTimeMinute = 30;

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
            int receiveCase = intent.getIntExtra("message",1);
            switch (receiveCase) {
                case 1:
                    //FAIL
                case 2:
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    wakeLockAlarm = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
                    wakeLockAlarm.acquire();
                    startSnoozeAlarm(0);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_READ_PERMISSION);

        settings = new Intent(this, Settings.class);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("executeAlarm"));

        handlerStartAlarm = new Handler();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        /** Sleep history button
        Button sleepHistory = (Button) findViewById(R.id.sleepHistoryButton);
        sleepHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Click event trigger here
            }
        });**/

        /** Active button **/
        ToggleButton toggle = (ToggleButton) findViewById(R.id.activeButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startSensorBackground(sensitivity);
                } else {
                    stopService(sensors);
                    handlerStartAlarm.removeCallbacks(runnableExecuteAlarm);
                    if(wakeLockAlarm != null) {
                        wakeLockAlarm.release();
                    }
                }
            }
        });



    }


    public void startSensorBackground(int sensitivity) {
        sensors = new Intent(this, Accelerometer.class);
        sensors.putExtra("sensitivity", sensitivity);
        startService(sensors);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case (1):
                if (resultCode == RESULT_OK) {
                    sensitivity = intent.getIntExtra("sensitivity", 50);
                    startAlarmTimeHour = intent.getIntExtra("hour", 0);
                    startAlarmTimeMinute = intent.getIntExtra("minute", 30);
                } else {
                    Log.d("test", "SettingSetFailed" + resultCode);
                }
                break;
            case (2):
                if (resultCode == RESULT_OK) {
                    if(intent.getBooleanExtra("snooze", false)) {
                        startSnoozeAlarm(convertMinuteToMilli(5));
                    } else {
                        //KNAPPEN FRÅN GRÖN TILL RÖD??
                        stopService(sensors);
                        if(wakeLockAlarm != null) {
                            wakeLockAlarm.release();
                        }
                    }
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
                settings.putExtra("sensitivity", sensitivity);
                settings.putExtra("hour", startAlarmTimeHour);
                settings.putExtra("minute", startAlarmTimeMinute);
                startActivityForResult(settings, 1);
                return true;

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
        if (!permissionToReadAccepted){
            finish();
        }

    }

    private long convertHourToMilli(int hour) {
        return hour * (3600*1000);
    }
    private long convertMinuteToMilli(int minute) {
        return minute * (60 * 1000);
    }
}
