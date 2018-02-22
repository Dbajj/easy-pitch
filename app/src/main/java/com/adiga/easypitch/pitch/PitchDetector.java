package com.adiga.easypitch.pitch;


import com.adiga.easypitch.io.MicrophoneIO;

/**
 * Created by dbajj on 2017-11-27.
 */

public class PitchDetector {

    public static final double CUTOFF = 0.83;
    private static final int RUNNING_AVERAGE_SIZE = 16;

    private PitchCalculator mPitchCalculator;
    private MicrophoneIO mMicrophoneIO;
    private double mCurrentPitch;
    private double[] mAudioBuffer;
    private PitchHistory mPitchHistory;

    /**
     * Initializes new PitchDetector with given microphone input
     *
     * @param io - valid MicrophoneIO object corresponding to device microphone in
     */
    public PitchDetector(MicrophoneIO io) {
        mPitchCalculator = new MPMCalculator(io.getSampleRate(),MicrophoneIO.OUTPUT_SAMPLE_SIZE);
        mMicrophoneIO = io;
        mCurrentPitch = 0;
        mPitchHistory = new PitchHistory(RUNNING_AVERAGE_SIZE);
    }

    /**
     *
     * @return the current averaged pitch detected.
     */
    public double getCurrentPitch() {
        return mCurrentPitch;
    }

    /**
     * Initializes the microphone object, and performs any other operations neccesary
     * to begin audio input collection before pitch processing.
     */
    public void startDetection() {
        if(mMicrophoneIO == null) {
            throw(new NullPointerException());
        }

        if(!mMicrophoneIO.isRecording()) mMicrophoneIO.startRecording();


    }

    /**
     * Stops collecting audio data and resets all pitch information to default values.
     */
    public void stopDetection() {
        if(mMicrophoneIO == null) {
            throw(new NullPointerException());
        }

        if(mMicrophoneIO.isRecording()) mMicrophoneIO.stopRecording();
    }


    /**
     * Collects a sample of audio from mMicrophoneIO, calculates the pitch, and adds
     * pitch to the running average.
     */
    public void processPitch() {
        if(!mMicrophoneIO.isRecording()) startDetection();

        mAudioBuffer = mMicrophoneIO.getSample();

        double pitch = mPitchCalculator.findPitch(mAudioBuffer);

        mCurrentPitch = mPitchHistory.add(pitch);
    }


}
