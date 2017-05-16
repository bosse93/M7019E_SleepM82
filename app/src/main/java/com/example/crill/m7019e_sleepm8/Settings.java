package com.example.crill.m7019e_sleepm8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class Settings extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SeekBar seekbar = (SeekBar) findViewById(R.id.sensetivitySeekbar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("test", String.valueOf(progress));
                Intent returnIntent = new Intent();
                returnIntent.putExtra("sensitivity", progress);
                setResult(RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("test", "started tracking seekbar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("test", "stopped tracking seekbar");
            }
        });{

        }
    }
}
