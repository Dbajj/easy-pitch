package com.adiga.easypitch.ui;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.adiga.easypitch.R;
import com.adiga.easypitch.io.MicrophoneIO;

import org.w3c.dom.Text;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView audioOutputTextID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MicrophoneIO audioInput = new MicrophoneIO();

        audioOutputTextID = (TextView) findViewById(R.id.audio_sample);


        Handler handler = new Handler();
        int delay = 1000;

        audioInput.startRecording();

        //TODO figure out why this queue is only ever outputting 0.0;
        LinkedBlockingQueue<Double> audioInputQueue = audioInput.getAudioInput();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                 audioOutputTextID.setText(audioInputQueue.take().toString());
                handler.postDelayed(this,delay);

                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },delay);


    }
}
