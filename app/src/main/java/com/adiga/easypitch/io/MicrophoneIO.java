package com.adiga.easypitch.io;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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


    private double[] audioBuffer = new double[BUFFER_SIZE/2];
    private boolean bufferRead = false;

    // TODO figure out what the constructor might need to do here, choose audio formats maybe?
    public MicrophoneIO() {

    }


    public double[] getSample() {
        recordingThread = new Thread(new Runnable() {
            public void run() {
                readAudioDataToQueue();

            }
        }, "AudioRecorder Thread");
        recordingThread.start();

        while(recordingThread.isAlive()) {

        }

        bufferRead = false;
        return audioBuffer;
    }

    public void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLE_RATE,CHANNELS,ENCODING,bufferSize);

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

    int bufferElementsToRecord = BUFFER_SIZE/2;

    private void readAudioDataToQueue() {

        short[] shortInputArray = new short[bufferElementsToRecord];

        recorder.read(shortInputArray,0,bufferElementsToRecord,AudioRecord.READ_BLOCKING);


        for (int i = 0; i < shortInputArray.length; i++) {
            audioBuffer[i] = shortInputArray[i];
        }

        bufferRead = true;

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
