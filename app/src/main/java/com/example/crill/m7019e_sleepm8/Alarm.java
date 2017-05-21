package com.example.crill.m7019e_sleepm8;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;


public class Alarm extends AppCompatActivity {
    private MediaPlayer mPlayer = null;
    private static String mFileName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        //SETTING LAYOUT. launches the alarm activity when alarm is executed
        setContentView(R.layout.activity_alarm);



        startPlayingAlarm();


        Button angryButton = (Button) findViewById(R.id.angry_btn);
        angryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Click event trigger here
                endAlarmActivity(false);
            }
        });

        /*  KOD TILL AVSLUTA LARM KNAPP
            endAlarmActivity(false);
         */



        /*
            MEDIA PLAYER. LJUD? RENSA UPP I endAlarm.
         */


        View view = this.findViewById(android.R.id.content);
        view.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                // Whatever
                endAlarmActivity(true);
                Toast.makeText(Alarm.this, "Alarm Snoozed.", Toast.LENGTH_SHORT).show();
                Log.d("test", "Swipe left");
            }
            @Override
            public void onSwipeRight() {
                endAlarmActivity(false);
                Toast.makeText(Alarm.this, "Alarm stopped.", Toast.LENGTH_SHORT).show();
                Log.d("test", "Swipe right");
            }
        });



    }

    private void startPlayingAlarm(){
        mPlayer = new MediaPlayer();
        mFileName = Environment.getExternalStorageDirectory().getPath();
        mFileName += "/count_down.mp3";

        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (IOException e) {
            Log.e("test", "prepare() failed\n" + e);
        }
    }
    private void endAlarmActivity(boolean snooze){
        mPlayer.release();
        mPlayer = null;
        Intent alarmReturnIntent = new Intent();
        alarmReturnIntent.putExtra("snooze", snooze);
        setResult(RESULT_OK, alarmReturnIntent);
        finish();
    }


}
