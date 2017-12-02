package com.adiga.easypitch.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.adiga.easypitch.R;
import com.adiga.easypitch.io.MicrophoneIO;
import com.adiga.easypitch.pitch.PitchDetector;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.nio.Buffer;
import java.util.Queue;

/**
 * Created by dbajj on 2017-11-30.
 */

public class AudioFragment extends Fragment {

    TextView audioOutputTextID;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        MicrophoneIO audioInput = new MicrophoneIO();

        audioOutputTextID = (TextView) getActivity().findViewById(R.id.audio_sample);


        Handler handler = new Handler();
        int delay = 1;

        audioInput.startRecording();


        //TODO test what the SDF of the microphone inputs are looking like (currently not responding to guitar samples)

        Queue audioInputQueue = audioInput.getAudioInput();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                if(audioInputQueue.size() >= 4096) {

                    double[] waveInput = new double[audioInputQueue.size()];
                    for(int i = 0; i < audioInputQueue.size(); i ++) {
                        waveInput[i] = (double) audioInputQueue.poll();
                    }
                    Double foundPitch = (PitchDetector.findPitch(waveInput,44100));

                    if (foundPitch != 0) {
                        String foundPitchString = foundPitch.toString();
                        audioOutputTextID.setText(foundPitchString);
                    }


                }


                handler.postDelayed(this,delay);

            }
        },delay);


    }
}
