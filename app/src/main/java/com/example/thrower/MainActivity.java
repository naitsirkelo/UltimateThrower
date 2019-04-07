package com.example.thrower;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView minAccText, currentAccText, thrownAccText, heightText, recordHeightText;
    public long totalSeconds = 30, intervalSeconds = 1;
    String timeCounter = "0.0";
    CountDownTimer timer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    ImageView animationTarget;
    Animation animation;
    MediaPlayer sound;
    public float minAcc = 15.f, heightRecord = 0.f, height = 0.f;
    public float gravity = SensorManager.GRAVITY_EARTH, timeSetting = 1.5f;
    public boolean onGround = true;


    public static final int REQUEST_SETTINGS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        currentAccText = findViewById(R.id.currentAccText);

        thrownAccText = findViewById(R.id.thrownAccText);

        heightText = findViewById(R.id.heightText);

        recordHeightText = findViewById(R.id.recordHeightText);

        animationTarget = findViewById(R.id.testImage);
        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_around_center_point);

        minAccText = findViewById(R.id.minAccTextView);
        minAccText.setText(String.valueOf(minAcc));

        sound = MediaPlayer.create(this, R.raw.pling);


        /* Allowing user to reset to original state. */
        final Button resetButton = findViewById(R.id.btn_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });


        /* Resetting values except record before new throw. */
        final Button newButton = findViewById(R.id.btn_new);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetThrow();
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

                timeCounter = String.format("%.1f", t);
                timeCounter = timeCounter.replace(",", ".");
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

            float acc = (float) Math.sqrt(x * x + y * y + z * z) - gravity;

            String a = String.format("%.3f", acc);
            a = a.replace(",", ".");
            currentAccText.setText(String.valueOf(a));

            /* Is the current acc larger than set minimum value? */
            if (acc >= minAcc) {

                a = String.format("%.3f", acc);
                a = a.replace(",", ".");
                thrownAccText.setText(a);

                if (onGround) {
                    onGround = false;
                    newThrow(acc);
                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void newThrow(float acc) {

        animationTarget.startAnimation(animation);

        height = (acc * 2) / (gravity * 2);

        if (height > heightRecord) {
            heightRecord = height;
        }

        String h = String.format("%.3f", height);
        h = h.replace(",", ".");
        heightText.setText(h);

        String hr = String.format("%.3f", heightRecord);
        hr = hr.replace(",", ".");
        recordHeightText.setText(hr);


        final float timeToTop = acc / gravity / timeSetting;

        /* Playing sound when then ball is at the top of the trajectory. */
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                sound.start();

                /* Stop animation when ball reaches the ground again. */
                handler.postDelayed(new Runnable() {
                    public void run() {

                        animationTarget.clearAnimation();
                        onGround = true;

                    }
                }, (long) timeToTop * 1000);    /* Time to top. */
            }
        }, (long) timeToTop * 1000);    /* Time to bottom. */
    }


    public void resetThrow() {

        height = 0.f;
        String h = String.format("%.3f", height);
        h = h.replace(",", ".");
        heightText.setText(h);

        thrownAccText.setText("-");

        animationTarget.clearAnimation();

        onGround = true;
    }
}
