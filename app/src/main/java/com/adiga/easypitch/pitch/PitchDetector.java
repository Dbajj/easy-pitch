package com.adiga.easypitch.pitch;


import com.adiga.easypitch.io.MicrophoneIO;
import java.util.ArrayList;

/**
 * Created by dbajj on 2017-11-27.
 */

public class PitchDetector {

    public static final double CUTOFF = 0.83;
    private static final int RUNNING_AVERAGE_SIZE = 32;

    private PitchCalculator mPitchCalculator;
    private MicrophoneIO mMicrophoneIO;
    private double mCurrentPitch;
    private double[] mAudioBuffer;
    private ArrayList<Double> mPitchHistory;

    /**
     * Initializes new PitchDetector with given microphone input
     *
     * @param io - valid MicrophoneIO object corresponding to device microphone in
     */
    public PitchDetector(MicrophoneIO io) {
        mPitchCalculator = new PitchCalculator(io.getSampleRate());
        mMicrophoneIO = io;
        mCurrentPitch = 0;
        mPitchHistory = new ArrayList<Double>();
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
     *
     * Then recalculates the average pitch over the last RUNNING_AVERAGE_SIZE observations,
     * and assigns that value to be the new current pitch.
     */
    public void processPitch() {
        if(!mMicrophoneIO.isRecording()) startDetection();

        mAudioBuffer = mMicrophoneIO.getSample();

        double pitch = mPitchCalculator.findPitch(mAudioBuffer);

        if(pitch  == 0) {
            mCurrentPitch = 0;
            return;
        } else if (mPitchHistory.size() == 0) {
            mCurrentPitch = pitch;
            mPitchHistory.add(mCurrentPitch);
        } else if (mPitchHistory.size() < RUNNING_AVERAGE_SIZE) {
            mPitchHistory.add(pitch);
            mCurrentPitch = getAveragePitch();
        } else {
            mPitchHistory.remove(0);
            mPitchHistory.add(pitch);
            mCurrentPitch = getAveragePitch();
        }

    }

    /**
     *
     * @return the current average pitch from all entries in mPitchHistory
     */
    private double getAveragePitch() {
        double sum = 0;
        int count = 0;

        for(double d : mPitchHistory) {
            sum += d;
            count++;
        }

        return sum/count;
    }





}
