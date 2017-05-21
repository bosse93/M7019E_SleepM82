package com.example.crill.m7019e_sleepm8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    private int sensitivity = 50;
    EditText hoursTxt = null;
    EditText minTxt = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        hoursTxt = (EditText) findViewById(R.id.editText2);
        minTxt = (EditText) findViewById(R.id.editText4);
        timeFix(hoursTxt, getIntent().getIntExtra("hour", 0));
        timeFix(minTxt, getIntent().getIntExtra("minute", 30));


        SeekBar seekbar = (SeekBar) findViewById(R.id.sensetivitySeekbar);
        sensitivity = getIntent().getIntExtra("sensitivity", 50);
        seekbar.setProgress(sensitivity);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivity = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        Button angryButton = (Button) findViewById(R.id.angry_btn);
        angryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int tempHour = isValidHour(hoursTxt);
                int tempMin = isValidMinute(minTxt);
                if((tempHour > 0) && (tempMin > 0)){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("sensitivity", sensitivity);
                    returnIntent.putExtra("hour", tempHour);
                    returnIntent.putExtra("minute", tempMin);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    invalidNumbers(hoursTxt, minTxt);
                }
            }
        });


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

    private void timeFix(EditText edit, int time) {
        if(time < 10) {
            edit.setHint(0 + String.valueOf(time));
        } else {
            edit.setHint(String.valueOf(time));
        }
    }
}
