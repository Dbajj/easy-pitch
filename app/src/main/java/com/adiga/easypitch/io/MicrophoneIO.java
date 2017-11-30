package com.adiga.easypitch.io;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by dbajj on 2017-11-28.
 */

public class MicrophoneIO {

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNELS = AudioFormat.CHANNEL_IN_MONO;

    private static final int ENCODING  = AudioFormat.ENCODING_PCM_16BIT;

    // TODO make this a bit more robust (what if encoding changes?
    private static final int BYTES_PER_ELEMENT = 2;

    private static final int BUFFER_SIZE = 4096;

    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private static final int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,CHANNELS,ENCODING);

    private LinkedBlockingQueue<Double> audioInputQueue = new LinkedBlockingQueue<Double>();

    // TODO figure out what the constructor might need to do here, choose audio formats maybe?
    public MicrophoneIO() {

    }


    public LinkedBlockingQueue<Double> getAudioInput() {
            return audioInputQueue;
    }


    public void startRecording() {
        recorder = new AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(new AudioFormat.Builder()
                    .setEncoding(ENCODING)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(CHANNELS)
                    .build())
                .setBufferSizeInBytes(2*BUFFER_SIZE)
                .build();

        recorder.startRecording();
        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            public void run() {
                readAudioDataToQueue();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();

    }

    public void stopRecording() {

        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }

    }

    int bufferElementsToRecord = 1024;

    private void readAudioDataToQueue() {

        byte[] byteInputArray =  new byte[bufferElementsToRecord];

        while (isRecording) {
            recorder.read(byteInputArray,0,bufferElementsToRecord);

            double[] doubleInputArray = convertByteToDouble(byteInputArray);

            try {
             for (double d : doubleInputArray) {
                audioInputQueue.put(d);

                if(audioInputQueue.size() > 4096) {
                    for (int i = 0; i < 4096; i++) {
                        audioInputQueue.take();
                    }
                }
            }

            } catch(InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    //TODO make this work with a variety of encoding formats
    private double[] convertByteToDouble(byte[] inputAudioByteArray) {

        double[] doubleAudioOutput = new double[inputAudioByteArray.length/BYTES_PER_ELEMENT];

        for (int i = 0; i < inputAudioByteArray.length-1; i++) {
            double value = 0;

            int byteVal = ((inputAudioByteArray[i] & 0xFF) | (inputAudioByteArray[i+1] << 8));

            doubleAudioOutput[i/2] = value;

        }

        return doubleAudioOutput;
    }






}
