package com.example.thrower;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    TextView progressText;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        progressText = findViewById(R.id.barTextView);
        seekBar = findViewById(R.id.minAccBar);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            int minAcc = (int) extras.getFloat("minAcc");
            seekBar.setProgress(minAcc);
            progressText.setText("Minimum acceleration required: " + minAcc);

        }


        /* Defining the slider used to choose a minAcc value. */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                seekBar.setProgress(progress);
                progressText.setText("Minimum acceleration required: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // called when the user first touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // called after the user finishes moving the SeekBar
            }
        });


        /* Return to main without updating value. */
        final Button homeButton = findViewById(R.id.btn_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


        /* Store the updated value and return to Main. */
        final Button saveAndReturn = findViewById(R.id.btn_save);
        saveAndReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent toMain = new Intent(Settings.this, MainActivity.class);
                toMain.putExtra("update", seekBar.getProgress());

                setResult(RESULT_OK, toMain);
                finish();
            }

        });

    }

}
