package com.example.thrower;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import static android.hardware.SensorManager.GRAVITY_EARTH;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView minAccText, currentAccText, thrownAccText, timeText, heightText, recordHeightText, throwOrGroundText;
    public float vel = 0.f, acc = 0.f, minAcc = 5.f, heightRecord = 0.f, height = 0.f;
    long totalSeconds = 30, intervalSeconds = 1;
    String timeCounter = "0.0";
    CountDownTimer timer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    MediaPlayer sound;
    ProgressBar timeBar, heightBar;
    boolean thrown = false;

    public static final int REQUEST_SETTINGS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager     = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer     = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        currentAccText    = findViewById(R.id.currentAccText);

        thrownAccText     = findViewById(R.id.thrownAccText);

        timeText          = findViewById(R.id.timeText);

        heightText        = findViewById(R.id.heightText);

        recordHeightText  = findViewById(R.id.recordHeightText);

        throwOrGroundText = findViewById(R.id.throwOrGroundText);

        timeBar           = findViewById(R.id.timeBar);

        heightBar         = findViewById(R.id.heightBar);

        minAccText        = findViewById(R.id.minAccTextView);
        minAccText.setText(String.valueOf(minAcc));


        /* Defining the sound to be played at the highest point of the throw. */
        sound = MediaPlayer.create(this, R.raw.pling);
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer sound) {
                sound.release();
            }
        });


        /* Allowing user to reset to original state. */
        final Button resetButton = findViewById(R.id.btn_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        /* Resetting values except record before new throw. */
        final Button newButton = findViewById(R.id.btn_new);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newThrow();
            }
        });


        /* Starting settings activity to get an updated minimum acceleration. */
        final Button settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent toPreferences = new Intent(MainActivity.this, Settings.class);
                toPreferences.putExtra("minAcc", minAcc);
                startActivityForResult(toPreferences, REQUEST_SETTINGS);
            }
        });


        /* New timer to count while the ball is in the air. */
        timer = new CountDownTimer(totalSeconds * 1000, intervalSeconds) {
            public void onTick(long millisUntilFinished) {

                float n = Float.valueOf(millisUntilFinished / 1000 + "." + ((millisUntilFinished / 100) % 10));
                float t = ((float) totalSeconds - n);
                timeBar.setProgress((int) t);

                timeCounter = String.format("%.1f", t);
                timeCounter = timeCounter.replace(",", ".");

                timeText.setText(timeCounter);

            }

            public void onFinish() {
                Log.d("Done!", "Time's up!");
            }

        };

    }


    /* Get result from Settings activity. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_SETTINGS) {

                if (resultCode == RESULT_OK) {

                    if (data.getExtras() != null) {

                        minAcc = (float) data.getIntExtra("update", 0);
                        minAccText.setText(String.valueOf(minAcc));

                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void newThrow() {
        height = 0.f;
        String h = String.format("%.3f", height);
        h = h.replace(",", ".");
        heightText.setText(h);

        timeCounter = "0.0";
        timeText.setText(String.valueOf(timeCounter));

        timer.cancel();
        throwOrGroundText.setText("On the ground.");

        thrownAccText.setText("-");

        timeBar.setProgress(0);
        heightBar.setProgress(0);
    }


    /* Send imageView to back of screen. */
    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    /* Get values from the built in accelerometer while they are being updated. */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[0];
            float z = event.values[0];

            acc = (float) Math.sqrt(x * x + y * y + z * z) - GRAVITY_EARTH;
            String a = String.format("%.3f", acc);
            a = a.replace(",", ".");
            currentAccText.setText(String.valueOf(a));

            /* Is the current acc larger than set minimum value? */
            if (acc >= minAcc) {

                /* Has the timer not been started yet? */
                if (timeCounter.equals("0.0")) {

                    timer.start();
                    a = String.format("%.3f", acc);
                    a = a.replace(",", ".");
                    thrownAccText.setText(a);

                    String hr = String.format("%.3f", heightRecord);
                    hr = hr.replace(",", ".");
                    recordHeightText.setText(hr);

                }

            }

            vel = acc + (-GRAVITY_EARTH * Float.parseFloat(timeCounter));


            height += vel;
            heightBar.setProgress((int) height);

            if (vel < 0.01f && vel > -0.01f) sound.start();
            if (height > heightRecord) heightRecord = height;

            if (height < 0.01f) height = 0.f;
            String h = String.format("%.3f", height);
            h = h.replace(",", ".");
            heightText.setText(h);

            if (height > 0.f) throwOrGroundText.setText("Throw!");
            else throwOrGroundText.setText("On the ground.");

        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


}
