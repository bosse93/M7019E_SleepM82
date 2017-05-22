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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {
    boolean development = false;

    private static final int REQUEST_READ_PERMISSION = 200;
    private boolean permissionToReadAccepted = false;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    Intent sensors = null;
    Intent settings = null;

    EditText hoursTxt = null;
    EditText minTxt = null;

    Handler handlerStartAlarm = null;

    private PowerManager.WakeLock wakeLockAlarm = null;

    private int sensitivity = 50;
    private int startAlarmTimeHour = 0;
    private int startAlarmTimeMinute = 30;
    private int alarmTimeHours = 8;
    private int alarmTimeMinutes = 0;
    private boolean sensorStarted = false;

    private Runnable runnableExecuteAlarm = new Runnable() {
        @Override
        public void run() {
            Log.d("test", "Alarm Started Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
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

        hoursTxt = (EditText) findViewById(R.id.editTextHours);
        minTxt = (EditText) findViewById(R.id.editTextMinutes);
        timeFix(hoursTxt, alarmTimeHours);
        timeFix(minTxt, alarmTimeMinutes);


        /** Active button **/
        ToggleButton toggle = (ToggleButton) findViewById(R.id.activeButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int tempHour = isValidHour(hoursTxt);
                    int tempMin = isValidMinute(minTxt);
                    if(tempHour >= 0 && tempMin >= 0) {
                        alarmTimeHours = tempHour;
                        alarmTimeMinutes = tempMin;
                        timeFix(hoursTxt, alarmTimeHours);
                        timeFix(minTxt, alarmTimeMinutes);
                        startSensorBackground();
                    } else {
                        invalidNumbers(hoursTxt, minTxt);
                        ToggleButton toggle = (ToggleButton) findViewById(R.id.activeButton);
                        toggle.toggle();
                    }

                } else {
                    if(sensorStarted) {
                        stopService(sensors);
                        sensorStarted = false;
                        handlerStartAlarm.removeCallbacks(runnableExecuteAlarm);
                        if (wakeLockAlarm != null) {
                            wakeLockAlarm.release();
                            wakeLockAlarm = null;
                        }
                    }
                }
            }
        });
    }


    public void startSensorBackground() {
        sensors = new Intent(this, Accelerometer.class);
        sensors.putExtra("sensitivity", sensitivity);
        sensors.putExtra("startalarmtime", (convertHourToMilli(startAlarmTimeHour) + convertMinuteToMilli(startAlarmTimeMinute)));
        sensors.putExtra("alarmtime", (convertHourToMilli(alarmTimeHours) + convertMinuteToMilli(alarmTimeMinutes)));
        startService(sensors);
        sensorStarted = true;
        makeToastAlarmStarted();
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
                        //toggles the button in order to kill sensor thread.
                        ToggleButton toggle = (ToggleButton) findViewById(R.id.activeButton);
                        toggle.toggle();
                    }
                } else {
                    Log.d("test", "AlarmReturnFailed" + resultCode);
                }
                break;
        }
    }

    //Inflate the default menu forcing it to obey my rules.
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

    private int isValidHour(EditText hoursTxt) {
        int tempHour;
        try {
            if(hoursTxt.getText().length() != 0) {
                tempHour = Integer.valueOf(hoursTxt.getText().toString());
                if(tempHour < 0 || tempHour > 23) {
                    return -1;
                }
            } else {
                tempHour = Integer.valueOf(hoursTxt.getHint().toString());
            }
        } catch(NumberFormatException e) {
            return -1;
        }
        return tempHour;
    }
    private int isValidMinute(EditText minTxt) {
        int tempMin;
        try {
            if(minTxt.getText().length() != 0) {
                tempMin = Integer.valueOf(minTxt.getText().toString());
                if(tempMin < 0 || tempMin > 59) {
                    return -1;
                }
            } else {
                tempMin = Integer.valueOf(minTxt.getHint().toString());
            }
        } catch(NumberFormatException e) {
            return -1;
        }
        return tempMin;
    }

    private void invalidNumbers(EditText hoursTxt, EditText minTxt){
        Toast.makeText(this, "Not a valid time. Enter 00:00 - 23.59.", Toast.LENGTH_LONG).show();
        hoursTxt.getText().clear();
        minTxt.getText().clear();
    }

    private long convertHourToMilli(int hour) {
        if(development) {
            return 2500;
        } else {
            return hour * (3600*1000);
        }
    }
    private long convertMinuteToMilli(int minute) {
        if(development) {
            return 2500;
        } else {
            return minute * (60 * 1000);
        }
    }
    private void timeFix(EditText edit, int time) {
        if(time < 10) {
            edit.setHint(0 + String.valueOf(time));
        } else {
            edit.setHint(String.valueOf(time));
        }
    }

    private void makeToastAlarmStarted(){
        String tempAlarmTimeHours;
        String tempAlarmTimeMinutes;
        if(alarmTimeHours < 10) {
            tempAlarmTimeHours = "0" + String.valueOf(alarmTimeHours);
        } else {
            tempAlarmTimeHours = String.valueOf(alarmTimeHours);
        }
        if(alarmTimeMinutes < 10) {
            tempAlarmTimeMinutes = "0" + String.valueOf(alarmTimeMinutes);
        } else {
            tempAlarmTimeMinutes = String.valueOf(alarmTimeMinutes);
        }
        Toast.makeText(this, "Alarm set! \nWe will wake you up in " + tempAlarmTimeHours + ":" + tempAlarmTimeMinutes + ".\nFrom when you fall asleep!", Toast.LENGTH_LONG).show();
    }
}
