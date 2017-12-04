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

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by dbajj on 2017-11-30.
 */

public class AudioFragment extends Fragment {

    TextView audioOutputTextID;

    private int testFileNum = 0;

    private String pitch = "";

    PitchDetector pitchDetector;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        MicrophoneIO audioInput = new MicrophoneIO();

        audioOutputTextID = (TextView) getActivity().findViewById(R.id.audio_sample);


        pitchDetector = new PitchDetector(44100);
        audioInput.startRecording();



        Handler handler = new Handler();
        int delay = 500;

        Runnable pitchCycle =  new Runnable() {
            int delay = 0;
            double[] audioBuffer;
            @Override
            public void run() {
                audioBuffer = audioInput.getSample();

                //writeSDFDebug(audioBuffer);


                pitch = String.valueOf(pitchDetector.findPitch(audioBuffer)) + "\n";

                //pitch = pitch + String.valueOf(delay) + "\n";

                audioOutputTextID.setText(pitch);

                handler.postDelayed(this, delay);
            }
        };

        handler.post(pitchCycle);

    }

    /*private void writeSDFDebug(double[] audioInput) {

        try {
            String output = "";
            double[] sdf = PitchDetector.findSDF(audioInput);

            for (double d : sdf) {
                output = output + d + " ";
            }

            String output2 = "";
            for (double d : audioInput) {
                output2 = output2 + d + " ";
            }




            String outputSDFFileName = "SDFOut" + testFileNum + ".txt";
            String outputAudioFileName = "AudioOUT" + testFileNum + ".txt";
           File file = new File(getActivity().getExternalFilesDir(null),outputSDFFileName);

           FileOutputStream fileOutput = new FileOutputStream(file);
           OutputStreamWriter outPutStreamWriter = new OutputStreamWriter(fileOutput);
           outPutStreamWriter.write(output);
           outPutStreamWriter.flush();
           fileOutput.getFD().sync();
           outPutStreamWriter.close();

           File file2 = new File(getActivity().getExternalFilesDir(null),outputAudioFileName);

           FileOutputStream fileOutput2 = new FileOutputStream(file2);
           OutputStreamWriter outPutStreamWriter2 = new OutputStreamWriter(fileOutput2);
           outPutStreamWriter2.write(output2);
           outPutStreamWriter2.flush();
           fileOutput2.getFD().sync();
           outPutStreamWriter2.close();

            MediaScannerConnection.scanFile(getActivity(),new String[]{file.getAbsolutePath()},null,null);
            MediaScannerConnection.scanFile(getActivity(),new String[]{file2.getAbsolutePath()},null,null);


            testFileNum++;


        } catch(IOException e) {

           e.printStackTrace();
        }




    } */



}
