package com.adiga.easypitch.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;

import com.adiga.easypitch.R;
import com.adiga.easypitch.io.MicrophoneIO;
import com.adiga.easypitch.pitch.PitchDetector;

public class MainActivity extends AppCompatActivity {

    private static final int AUDIO_PERMISSION_REQUEST_CODE = 5;
    TextView audioOutputTextID;
    Button responseButton;
    double mPitch;
    MicrophoneIO microphoneIO;
    PitchDetector pitchDetector;

    private Handler mPitchHandler;
    private PitchRunnable mPitchRunnable;
    private StringView mGuitarString;

    private float offset = 0;

    private ObjectAnimator animator;


    private static final int PITCH_QUERY_DELAY = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermissions();

        audioOutputTextID = (TextView) findViewById(R.id.audio_sample);

        mGuitarString = findViewById(R.id.guitar_string);

    }

    private void setupAudio() {
        microphoneIO = new MicrophoneIO();
        pitchDetector = new PitchDetector(microphoneIO);

        mPitchHandler = new Handler();

        mPitchRunnable = new PitchRunnable();

        monitorPitch();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
       super.onResume();
       monitorPitch();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMonitorPitch();
    }

    private void monitorPitch() {
        pitchDetector.startDetection();

        mPitchHandler.post(mPitchRunnable);
    }

    private void stopMonitorPitch() {
        mPitchHandler.removeCallbacks(mPitchRunnable);
        pitchDetector.stopDetection();
    }

    private void updatePitch() {
        audioOutputTextID.setText(String.valueOf(mPitch));

        if(animator != null && animator.isRunning()) Log.d("MainActivity","Still running!");
        animator = ObjectAnimator.ofFloat(mGuitarString,"CurveOffset",offset%1);

        offset += 0.01;
        animator.setDuration(1000);

        animator.start();

    }

    private void getPermissions() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},AUDIO_PERMISSION_REQUEST_CODE);
            }
        } else {
            setupAudio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case AUDIO_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupAudio();
                } else {
                    Toast.makeText(this,
                            "Permission for audio not granted. Please grant permission to use EasyPitch",
                            Toast.LENGTH_LONG).show();
                            finish();
                }
            }
        }
    }

    public class PitchRunnable implements Runnable {

        @Override
        public void run() {
            pitchDetector.processPitch();

            mPitch = pitchDetector.getCurrentPitch();

            updatePitch();

            mPitchHandler.postDelayed(this,PITCH_QUERY_DELAY);
        }
    }



}
