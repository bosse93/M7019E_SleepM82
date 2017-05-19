package com.example.crill.m7019e_sleepm8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class Settings extends AppCompatActivity {
    private int sensitivity = 50;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SeekBar seekbar = (SeekBar) findViewById(R.id.sensetivitySeekbar);
        sensitivity = getIntent().getIntExtra("sensitivity", 50);
        seekbar.setProgress(sensitivity);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivity = progress;
                Log.d("test", String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("test", "started tracking seekbar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("test", "stopped tracking seekbar");
            }
        });

        Button angryButton = (Button) findViewById(R.id.angry_btn);
        angryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("sensitivity", sensitivity);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });


    }
}
