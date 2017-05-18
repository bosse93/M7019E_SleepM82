package com.example.crill.m7019e_sleepm8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "om.example.crill.m7019e_sleepm8.MESSAGE";
    Intent sensors = null;
    Intent settings = null;
    private Date currentTime = Calendar.getInstance().getTime();
    private boolean isSleeping = false;
    private int sensitivity = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("test", "Start Thread " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
        //starting new sensor instance
        //sensors = new Accelerometer(this);
        settings = new Intent(this, Settings.class);

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
                    //PUTEXTRA

                    stopService(sensors);
                    // The toggle is disabled
                    Log.d("test", "toggle - DISABLED");
                }
            }
        });



    }

    public long getAlarmTime() {
        EditText datePicker = (EditText) findViewById(R.id.alarmTime);
        int alarmTime = Integer.parseInt(datePicker.getText().toString());
        long alarmTimeMS = alarmTime * 60 * 60 * 1000; // hh*mm*ss*ms
        Log.d("test ALARMTIME", String.valueOf(alarmTime));
        return alarmTimeMS;
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
        }
    }


/*
    public boolean checkSleep(){
        if ((sensors.getSensorTimeMs() + getAlarmTime()) >= System.currentTimeMillis()) {
            return isSleeping = true;
        }
        return isSleeping = false;
    }
*/


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

}
