package com.adiga.easypitch.ui;

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
//
//public class AudioFragment extends Fragment {
//
//    TextView audioOutputTextID;
//
//    private static final int PITCH_QUERY_DELAY = 100;
//
//    private String pitchString;
//    private PitchDetector pitchDetector;
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        MicrophoneIO audioInput = new MicrophoneIO();
//
//        audioOutputTextID = getActivity().findViewById(R.id.audio_sample);
//        pitchDetector = new PitchDetector(44100);
//
//        Handler handler = new Handler();
//
//        handler.post(new Runnable() {
//            double[] audioBuffer;
//
//            @Override
//            public void run() {
//                audioBuffer = audioInput.getSample();
//                pitchString = String.valueOf(pitchDetector.findPitch(audioBuffer)) + "\n";
//
//                audioOutputTextID.setText(pitchString);
//
//                handler.postDelayed(this, PITCH_QUERY_DELAY);
//            }
//        });
//
//
//    }
//
//
//
//
//
//}
