package com.adiga.easypitch.ui;

import android.animation.ObjectAnimator;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.adiga.easypitch.R;
import com.adiga.easypitch.io.MicrophoneIO;
import com.adiga.easypitch.pitch.PitchDetector;

/**
 * Created by dbajj on 2017-11-30.
 */

public class AudioFragment extends Fragment {

    TextView audioOutputTextID;

    private static final int PITCH_QUERY_DELAY = 100;

    private String pitchString;
    private PitchDetector pitchDetector;

    private Handler audioHandler;
    private MicrophoneIO audioInput;

    private StringView string;
    private float currentOffset;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        audioInput = new MicrophoneIO();

        audioOutputTextID = getActivity().findViewById(R.id.audio_sample);
        pitchDetector = new PitchDetector(audioInput.getSampleRate());

        audioHandler = new Handler();

        string = getActivity().findViewById(R.id.guitar_string);

        currentOffset = 0;

    }

    @Override
    public void onStart() {
        super.onStart();
        audioInput.startRecording();
        audioHandler.post(new Runnable() {
            double[] audioBuffer;

            @Override
            public void run() {
                ObjectAnimator animation = ObjectAnimator.ofFloat(string,"CurveOffset",currentOffset%1);
                currentOffset += 0.1;
                animation.setDuration(1000);
                animation.start();
                audioBuffer = audioInput.getSample();
                pitchString = String.valueOf(pitchDetector.findPitch(audioBuffer)) + "\n";

                audioOutputTextID.setText(pitchString);

                audioHandler.postDelayed(this, PITCH_QUERY_DELAY);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        audioHandler.removeCallbacksAndMessages(null);
        audioInput.stopRecording();
    }
}
