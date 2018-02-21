package com.adiga.easypitch.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.adiga.easypitch.R;
import com.adiga.easypitch.io.MicrophoneIO;
import com.adiga.easypitch.pitch.PitchDetector;

import org.w3c.dom.Text;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView audioOutputTextID;
    private static final int AUDIO_PERMISSION_REQUEST_CODE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermissions();
    }

    @Override
    public void onResume() {
       super.onResume();

        AudioFragment myAudio = (AudioFragment) getSupportFragmentManager().findFragmentByTag("audio_processor");

    }

    private void getPermissions() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},AUDIO_PERMISSION_REQUEST_CODE);
            }
        } else {
            addAudioFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case AUDIO_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addAudioFragment();
                } else {
                    Toast.makeText(this,
                            "Permission for audio not granted. Please grant permission to use EasyPitch",
                            Toast.LENGTH_LONG).show();
                            finish();
                }
            }
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
