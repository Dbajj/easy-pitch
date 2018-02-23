package com.adiga.easypitch.io;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;


/**
 * Created by dbajj on 2017-11-28.
 */

public class MicrophoneIO {

    private int mSampleRate;
    private int mRecordBufferSize;
    private static final int CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING  = AudioFormat.ENCODING_PCM_16BIT;
    public static final int OUTPUT_SAMPLE_SIZE = 2048*2;
    public static final double SAMPLE_OVERLAP = 0.50;

    // TODO make this a bit more robust (what if encoding changes?
    private static final int BYTES_PER_ELEMENT = 2;

    private AudioRecord recorder;
    private Thread recordingThread;
    private boolean isRecording = false;
    private boolean emptyBuffer = true;

    private double[] audioBuffer = new double[OUTPUT_SAMPLE_SIZE];

    public MicrophoneIO() {
        mSampleRate = findSampleRate();
        mRecordBufferSize = AudioRecord.getMinBufferSize(mSampleRate,CHANNELS,ENCODING);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,mSampleRate,CHANNELS,ENCODING, mRecordBufferSize);
        emptyBuffer = true;
    }

    private int findSampleRate() {
        int max_rate = 0;
        int[] rates = new int[]{8000,11025,16000,22050,44100,48000};

        for(int rate : rates) {
            int bufferSize = AudioRecord.getMinBufferSize(rate,CHANNELS,ENCODING);
            if(bufferSize > 0 && rate > max_rate) {
                max_rate = rate;
            }
        }

        if(max_rate == 0) throw new UnsupportedOperationException("No valid audio sampling rate found");

        return max_rate;
    }

    public int getSampleRate() {
        return mSampleRate;
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



    public void startRecording() {

        recorder.startRecording();
        isRecording = true;
    }


    public void stopRecording() {

        if (null != recorder) {
            isRecording = false;
            recorder.stop();
        }

    }

    public boolean isRecording() {
        return isRecording;
    }


    private void readAudioDataToQueue() {

        if(emptyBuffer) {
            short[] shortInputArray = new short[OUTPUT_SAMPLE_SIZE];

            recorder.read(shortInputArray,0,OUTPUT_SAMPLE_SIZE,AudioRecord.READ_BLOCKING);


            for (int i = 0; i < shortInputArray.length; i++) {
                audioBuffer[i] = shortInputArray[i];
            }

            emptyBuffer = false;
        } else {
            int overlap_offset = (int)Math.floor(OUTPUT_SAMPLE_SIZE*(1-SAMPLE_OVERLAP));
            short[] shortInputArray = new short[overlap_offset];

            recorder.read(shortInputArray,0,shortInputArray.length,AudioRecord.READ_BLOCKING);

            for(int i = 0; i < OUTPUT_SAMPLE_SIZE-overlap_offset; i++) {
                audioBuffer[i] = audioBuffer[overlap_offset+i];
            }

            for(int i = 0; i < overlap_offset; i++) {
                audioBuffer[OUTPUT_SAMPLE_SIZE-overlap_offset+i] = shortInputArray[i];
            }
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
