package com.example.crill.m7019e_sleepm8;

import android.content.Intent;
import android.os.Looper;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "om.example.crill.m7019e_sleepm8.MESSAGE";
    Intent sensors = null;
    private Date currentTime = Calendar.getInstance().getTime();
    private boolean isSleeping = false;
    float x = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("test", "Start Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
        //starting new sensor instance
        //sensors = new Accelerometer(this);
        sensors = new Intent(this, Accelerometer.class);
        String message = "Meddelande";
        sensors.putExtra(EXTRA_MESSAGE, message);
        startService(sensors);



        Log.d("test", "Thread: " + Looper.getMainLooper().getThread().getName() + " " + Looper.getMainLooper().getThread().getId());
        /** intent för att göra larm, bara test **/
        final Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(EXTRA_MESSAGE, "New Alarm");
        i.putExtra(AlarmClock.EXTRA_HOUR, 10);
        i.putExtra(AlarmClock.EXTRA_MINUTES, 30);


        Button alarmButton = (Button) findViewById(R.id.AlarmButton);
        alarmButton.setOnClickListener((
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        boolean a = stopService(sensors);
                        Log.d("test", "Service stoped? " + a);
                    }
                }
        ));
/*
        Button test = (Button) findViewById(R.id.button2);
        test.setOnClickListener((
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("test", String.valueOf(sensors.getSensorTimeMs()));
                        getAlarmTime();
                    }
                }
        ));
*/
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
                    // The toggle is enabled
                    Log.d("test", "toggle - ENABLED");
                } else {
                    // The toggle is disabled
                    Log.d("test", "toggle - DISABLED");
                }
            }
        });




    }

    public long getAlarmTime(){
        EditText datePicker = (EditText)findViewById(R.id.alarmTime);
        int alarmTime = Integer.parseInt(datePicker.getText().toString());
        long alarmTimeMS = alarmTime*60*60*1000; // hh*mm*ss*ms
        Log.d("test ALARMTIME", String.valueOf(alarmTime));
        return alarmTimeMS;
    }

/*
    public boolean checkSleep(){
        if ((sensors.getSensorTimeMs() + getAlarmTime()) >= System.currentTimeMillis()) {
            return isSleeping = true;
        }
        return isSleeping = false;
    }
*/

}
