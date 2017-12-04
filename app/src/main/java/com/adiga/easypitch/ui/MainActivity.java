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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adiga.easypitch.R;
import com.adiga.easypitch.io.MicrophoneIO;
import com.adiga.easypitch.pitch.PitchDetector;

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

    }

    @Override
    public void onResume() {
       super.onResume();

        AudioFragment myAudio = (AudioFragment) getSupportFragmentManager().findFragmentByTag("audio_processor");

    }

    public void openPlot(View view) {
        Intent intent = new Intent(this, ScatterChartActivity.class);
        startActivity(intent);

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
