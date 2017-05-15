package com.example.crill.m7019e_sleepm8;

import android.content.Intent;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Accelerometer sensors = null;
    float x = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //starting new sensor instance
        sensors = new Accelerometer(this);

        /** intent för att göra larm, bara test **/
        final Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "New Alarm");
        i.putExtra(AlarmClock.EXTRA_HOUR, 10);
        i.putExtra(AlarmClock.EXTRA_MINUTES, 30);

        Button alarmButton = (Button) findViewById(R.id.AlarmButton);
        alarmButton.setOnClickListener((
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(i);
                    }
                }
        ));

        Button test = (Button) findViewById(R.id.button2);
        test.setOnClickListener((
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        sensors.setChanged();
                    }
                }
        ));
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensors.register();
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensors.unregister();
    }

}
