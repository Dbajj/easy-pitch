package com.adiga.easypitch.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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

import com.adiga.easypitch.R;
import com.adiga.easypitch.io.MicrophoneIO;
import com.adiga.easypitch.pitch.PitchDetector;

import org.w3c.dom.Text;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView audioOutputTextID;
    Button responseButton;
    double mPitch;
    MicrophoneIO microphoneIO;
    PitchDetector pitchDetector;

    private Handler mPitchHandler;
    private PitchRunnable mPitchRunnable;


    private static final int PITCH_QUERY_DELAY = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermissions();

        audioOutputTextID = (TextView) findViewById(R.id.audio_sample);
        responseButton = (Button) findViewById(R.id.response_button);

        responseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Heyo",Toast.LENGTH_SHORT).show();
            }
        });

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
    }

    private void getPermissions() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},5);
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
