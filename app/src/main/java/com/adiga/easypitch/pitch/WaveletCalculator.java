package com.adiga.easypitch.pitch;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbajj on 2018-02-22.
 */

public class WaveletCalculator implements PitchCalculator {

    public static final int LEVEL_NUMBER = 5;
    public static final double PEAK_CUTOFF = 0.50;
    public static final double MAX_FREQUENCY = 1000;
    public static final int NEIGHBOUR_COUNT = 3;

    private final int SAMPLE_RATE;
    private final int SAMPLE_LENGTH;

    private List<double[]> mApproxWavelets;
    private List<double[]> mDetailWavelets;

    private List<List<Integer>> mMaxIndices;
    private List<List<Integer>> mMinIndices;

    private List<List<Integer>> mDifferences;
    private List<Double> mModes;

    private double mSampleAverage;
    private double mMinCutoff;
    private double mMaxCutoff;
    private int mMinPeakDistance;

    private int mCurrentLevel;

    public WaveletCalculator(int sampleRate, int sampleLength) {
        SAMPLE_RATE = sampleRate;

        if(sampleLength % 16 != 0) {
            throw new IllegalArgumentException("Wavelet calculator sample length must be divisble by 16");
        }

        SAMPLE_LENGTH = sampleLength;

        mApproxWavelets = new ArrayList<double[]>();
        mDetailWavelets = new ArrayList<double[]>();

        mMaxIndices = new ArrayList<List<Integer>>();
        mMinIndices = new ArrayList<List<Integer>>();

        mDifferences = new ArrayList<List<Integer>>();
        mModes = new ArrayList<Double>();

        for(int i = 0; i < LEVEL_NUMBER; i++) {
            int width = sampleLength/(int)Math.pow(2,i+1);

            mApproxWavelets.add(new double[width]);
            mDetailWavelets.add(new double[width]);

            mMaxIndices.add(new ArrayList<Integer>());
            mMinIndices.add(new ArrayList<Integer>());

            mDifferences.add(new ArrayList<Integer>());
            mModes.add(0.0);
        }
        mCurrentLevel = 0;
    }

    @Override
    public double findPitch(double[] audioInput) {

        mCurrentLevel = 0;

        if(audioInput == null) {
            throw new IllegalArgumentException("findPitch given a null audio input");
        } else if (audioInput.length != SAMPLE_LENGTH) {
            throw new IllegalArgumentException("findPitch given input of length not matching SAMPLE_LENGTH");
        }

        initializeCutoffs(audioInput);


        while(mCurrentLevel < LEVEL_NUMBER) {
            calculateWavelet(audioInput);
            findExtrema();
            findDistances();
            findMode();

            if(mCurrentLevel != 0 && mModes.get(mCurrentLevel-1) != 0 && mMinIndices.get(mCurrentLevel).size() >= 2
                    && mMaxIndices.get(mCurrentLevel).size() >= 2) {
                if(mModes.get(mCurrentLevel-1) - 2*mModes.get(mCurrentLevel) <= mMinPeakDistance) {
                    return SAMPLE_RATE/mModes.get(mCurrentLevel-1)/Math.pow(2,mCurrentLevel);
                }
            }

            Log.d("PitchWavelet",String.valueOf(mModes.get(mCurrentLevel)));
            mCurrentLevel++;
        }




        return 0.0;
    }

    private void calculateWavelet(double[] audioInput) {
        int width = SAMPLE_LENGTH/(int)Math.pow(2,mCurrentLevel+1);

        double[] currentDetail = mDetailWavelets.get(mCurrentLevel);
        double[] currentApprox = mApproxWavelets.get(mCurrentLevel);

        for (int j = 0; j < width; j++) {
            currentDetail[j] = audioInput[2*j+1]-audioInput[2*j];
            currentApprox[j] = audioInput[2*j]+currentDetail[j]/2;
        }
    }

    private void findExtrema() {
        mMaxIndices.get(mCurrentLevel).clear();
        mMinIndices.get(mCurrentLevel).clear();

        boolean goingUp = true;
        mMinPeakDistance = (int)Math.max(Math.floor(((double)SAMPLE_RATE)/MAX_FREQUENCY/Math.pow(2,mCurrentLevel+1)),
                1);

        double[] currentApprox = mApproxWavelets.get(mCurrentLevel);

        if(currentApprox[1] > currentApprox[0]) {
            goingUp = true;
        } else {
            goingUp = false;
        }

        boolean findable = true;
        int spaceRemaining = 0;

        for (int i = 2; i < currentApprox.length;i++) {
            double difference = currentApprox[i]-currentApprox[i-1];

            if(goingUp && difference > 0) {
                if(currentApprox[i-1] >= mMaxCutoff && findable && spaceRemaining == 0) {
                    mMaxIndices.get(mCurrentLevel).add(i);
                    spaceRemaining = mMinPeakDistance;
                    findable = false;
                }

                goingUp = false;
            } else if(!goingUp && difference > 0) {
                if(currentApprox[i-1] <= mMinCutoff && findable && spaceRemaining == 0) {
                    mMinIndices.get(mCurrentLevel).add(i);
                    spaceRemaining = mMinPeakDistance;
                    findable = false;
                }

                goingUp = true;
            }

            if((currentApprox[i] >= mSampleAverage && currentApprox[i-1] < mSampleAverage) ||
                    (currentApprox[i] <= mSampleAverage && currentApprox[i-1] > mSampleAverage)) {
                findable = true;
            }

            if(spaceRemaining != 0) {
                spaceRemaining -= 1;
            }
        }

    }

    private void findDistances() {
        mDifferences.get(mCurrentLevel).clear();

        List<Integer> currentMinIndices = mMinIndices.get(mCurrentLevel);
        List<Integer> currentMaxIndices = mMaxIndices.get(mCurrentLevel);

        for(int diff = 1; diff < NEIGHBOUR_COUNT+1; diff++) {
            for (int i = 0; i < currentMinIndices.size()-diff; i++) {
                int curr_diff = Math.abs(currentMinIndices.get(i+diff)-currentMinIndices.get(i));
                mDifferences.get(mCurrentLevel).add(curr_diff);
            }

            for(int i = 0; i < currentMaxIndices.size()-diff; i++) {
                int curr_diff = Math.abs(currentMaxIndices.get(i+diff)-currentMaxIndices.get(i));
                mDifferences.get(mCurrentLevel).add(curr_diff);
            }
        }
    }

    private void findMode() {

        mModes.set(mCurrentLevel,0.0);

        double greatestMode = 1;

        List<Integer> currentDifferences = mDifferences.get(mCurrentLevel);

        for(int i = 0; i < currentDifferences.size(); i++) {
            double currentMode = 0;

            for(int j = i+1; j < currentDifferences.size(); j++) {
                if(Math.abs(currentDifferences.get(i)-currentDifferences.get(j)) < mMinPeakDistance &&
                        i != j) {

                    currentMode += 1;
                }
            }

            if (currentMode > greatestMode) {
                greatestMode = currentMode;

                mModes.set(mCurrentLevel,(double)currentDifferences.get(i));
            }
        }

        if(mModes.get(mCurrentLevel) != 0.0) {
            double sum = 0;
            int count = 0;

            for(int x : currentDifferences) {
                if(Math.abs(mModes.get(mCurrentLevel)-x) <= mMinPeakDistance) {
                    sum += x;
                    count++;
                }
            }

            mModes.set(mCurrentLevel,sum/count);
        }

    }

    private void initializeCutoffs(double[] audioInput) {
        mSampleAverage = findAverage(audioInput);

        double minValue = findMinimum(audioInput);
        double maxValue = findMaximum(audioInput);

        mMinCutoff = PEAK_CUTOFF*(minValue-mSampleAverage)+mSampleAverage;
        mMaxCutoff = PEAK_CUTOFF*(maxValue-mSampleAverage)+mSampleAverage;
    }

    private double findAverage(double[] input) {
        double sum = 0;
        int count = 0;

        for(double d : input) {
            sum += d;
            count++;
        }

        return sum/count;
    }

    private double findMinimum(double[] input) {
        double min = Double.MAX_VALUE;

        for(double d : input) {
            if (d < min) min = d;
        }

        return min;
    }

    private double findMaximum(double[] input) {
        double max = Double.MIN_VALUE;

        for(double d : input) {
            if (d > max) max = d;
        }

        return max;
    }
}
