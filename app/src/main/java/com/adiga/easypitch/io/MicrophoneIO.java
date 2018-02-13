package com.adiga.easypitch.io;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;


/**
 * Created by dbajj on 2017-11-28.
 */

public class MicrophoneIO {

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING  = AudioFormat.ENCODING_PCM_16BIT;
    private static final int RECORD_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,CHANNELS,ENCODING);
    public static final int OUTPUT_SAMPLE_SIZE = 4096;

    // TODO make this a bit more robust (what if encoding changes?
    private static final int BYTES_PER_ELEMENT = 2;


    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private double[] audioBuffer = new double[OUTPUT_SAMPLE_SIZE];

    public MicrophoneIO() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLE_RATE,CHANNELS,ENCODING, RECORD_BUFFER_SIZE);
    }

    // Retrieves a sample from recordingThread, using a new thread
    public double[] getSample() {

        if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            startRecording();
        }

        recordingThread = new Thread(new Runnable() {
            public void run() {
                readAudioDataToQueue();

            }
        }, "AudioRecorder Thread");
        recordingThread.start();

        while(recordingThread.isAlive()) {

        }
        return audioBuffer;
    }

    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    public void startRecording() {

        recorder.startRecording();
        isRecording = true;
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

    public boolean isRecording() {
        return isRecording;
    }


    private void readAudioDataToQueue() {

        short[] shortInputArray = new short[OUTPUT_SAMPLE_SIZE];

        recorder.read(shortInputArray,0,OUTPUT_SAMPLE_SIZE,AudioRecord.READ_BLOCKING);


        for (int i = 0; i < shortInputArray.length; i++) {
            audioBuffer[i] = shortInputArray[i];
        }

    }

    //TODO make this work with a variety of encoding formats
    private double[] convertByteToDouble(byte[] inputAudioByteArray) {

        double[] doubleAudioOutput = new double[inputAudioByteArray.length/BYTES_PER_ELEMENT];

        for (int i = 0; i < inputAudioByteArray.length-1; i++) {
            double value = 0;

            int byteVal = ((inputAudioByteArray[i] & 0xFF) | (inputAudioByteArray[i+1] << 8));

            doubleAudioOutput[i/2] = byteVal;

        }

        return doubleAudioOutput;
    }






}
