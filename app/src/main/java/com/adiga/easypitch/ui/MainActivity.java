package com.adiga.easypitch.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.adiga.easypitch.R;
import com.adiga.easypitch.io.MicrophoneIO;
import com.adiga.easypitch.pitch.PitchDetector;

import org.apache.commons.collections.Buffer;
import org.w3c.dom.Text;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView audioOutputTextID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermissions();

        addAudioFragment();

        //TODO add this functionality into the AudioFragment
        /*MicrophoneIO audioInput = new MicrophoneIO();

        audioOutputTextID = (TextView) findViewById(R.id.audio_sample);


        Handler handler = new Handler();
        int delay = 1000;

        audioInput.startRecording();


        //TODO figure out why this queue is only ever outputting 0.0;

        Buffer audioInputQueue = audioInput.getAudioInput();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                double[] waveInput = new double[audioInputQueue.size()];


                if(audioInputQueue.size() >= 4096) {

                    for(int i = 0; i < audioInputQueue.size(); i ++) {
                        waveInput[i] = (double) audioInputQueue.get();
                    }
                    String foundPitch = String.valueOf(PitchDetector.findPitch(waveInput,44100));
                    audioOutputTextID.setText(foundPitch);

                }


                handler.postDelayed(this,delay);

            }
        },delay); */


    }

    @Override
    public void onResume() {
       super.onResume();

        AudioFragment myAudio = (AudioFragment) getSupportFragmentManager().findFragmentByTag("audio_processor");

    }

    private void getPermissions() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},5);
        }
    }

    private void addAudioFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AudioFragment audioFragment = new AudioFragment();

        fragmentTransaction.add(audioFragment,"audio_processor");

        fragmentTransaction.commit();

    }
}
