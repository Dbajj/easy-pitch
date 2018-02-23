package com.adiga.easypitch.pitch;

import com.adiga.easypitch.utils.GraphCoordinate;
import com.adiga.easypitch.utils.PeakFind;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.List;

/**
 * Created by dbajj on 2018-01-24.
 */

public class MPMCalculator implements PitchCalculator {
    private final double CUTOFF;
    private static final double MATCH_ALLOWANCE = 0.2;
    private static final double MATCH_TOLERANCE = 0.1;
    private final int SAMPLE_RATE;

    private double[] inputSDF;
    private double[] inputACV;
    private double[] inputCentered;
    private double[] inputDivisors;
    private double[] zeroPadding;
    private double[][] inputCenteredPaddedComplex;
    private FastFourierTransformer mFFTCalc;
    private double mPreviousPitch;

    /**
     * Initializes new PitchCalculator, by setting pitch cutoff, sample rate and FFT calculator
     * @param sampleRate the sampling rate used to collect audio input data that will be given
     *                   to the calculator (used to convert frequency to Hz)
     */
    public MPMCalculator(int sampleRate, int sampleLength) {
        mFFTCalc = new FastFourierTransformer(DftNormalization.STANDARD);
        CUTOFF = PitchDetector.CUTOFF;

        SAMPLE_RATE = sampleRate;

        inputACV = new double[sampleLength];
        inputCentered = new double[sampleLength];
        inputDivisors = new double[sampleLength];
        inputSDF = new double[sampleLength];
        zeroPadding = new double[sampleLength];
        inputCenteredPaddedComplex = new double[2][sampleLength*2];
        mPreviousPitch = 0.0;
    }


    /**
     *
     * @param audioInput an array of audio data, sampled at SAMPLE_RATE and of any length >=0
     * @return           the calculated pitch using the McLeod Pitch autocorrelation method
     */
    @Override
    public double findPitch(double[] audioInput) {

        calculateSDF(audioInput);

        try {
            List<GraphCoordinate> mMaxima = PeakFind.getInstance().findMaxima(inputSDF);

            for (int i = 0; i < mMaxima.size()/2; i++) {
                if(mMaxima.get(i).getY() > CUTOFF) {
                    double output = SAMPLE_RATE/mMaxima.get(i).getX();
                    mPreviousPitch = output;
                    return output;
                } else if (mPreviousPitch != 0.0 && Math.abs((mPreviousPitch-mMaxima.get(i).getY())/mPreviousPitch) <= MATCH_TOLERANCE
                        && mMaxima.get(i).getY() > CUTOFF*MATCH_ALLOWANCE) {
                    double output = SAMPLE_RATE/mMaxima.get(i).getX();
                    mPreviousPitch = output;
                    return output;
                }
            }
        } finally {
            PeakFind.getInstance().clearMaxima();
        }

        // TODO figure out what this will return if there is no viable max found.
        return 0;
    }


    /**
     * @param input an array of data to be inserted into the autocorrelation function
     * @return the autocorrelation function of the input, calculated using FFT
     */
    private void fftAutoCorrelationFast(double[] input) {
        centerDoubleArray(input);


        for (int i = 0; i < inputCentered.length; i++) {
            inputCenteredPaddedComplex[0][i] = inputCentered[i];
            inputCenteredPaddedComplex[1][i] = 0;
        }

        for (int i = 0; i < zeroPadding.length; i++) {
            inputCenteredPaddedComplex[0][i+inputCentered.length] = zeroPadding[i];
            inputCenteredPaddedComplex[1][i+inputCentered.length] = zeroPadding[i];
        }


        mFFTCalc.transformInPlace(inputCenteredPaddedComplex,DftNormalization.STANDARD, TransformType.FORWARD);

        calculateNormInPlace(inputCenteredPaddedComplex);

        mFFTCalc.transformInPlace(inputCenteredPaddedComplex,DftNormalization.STANDARD, TransformType.INVERSE);



        for (int i = 0; i < inputACV.length; i++) {
            inputACV[i] = inputCenteredPaddedComplex[0][i];
        }

    }

    private void autoCorrelationSlow(double[] input) {
        centerDoubleArray(input);

        for(int i = 0; i < input.length; i++) {
            double acf = 0;

            for(int j = 0; j < input.length-i; j++) {
                acf += input[j]*input[j+i];
            }

            inputACV[i] = acf;
        }
    }



    /**
     * Calculates the norm of the given values in-place (modifies input array).
     * The norm is calculated as the sum of squares of the real and complex components of each value
     *
     * The resulting array will have the norm of element i at input[0][i], and all values of
     * input[1] will be set to zero.
     *
     * @param input a matrix containing the real component of element i at input[0][i] and the
     *              complex component of element i at input[1][i]
     */
    private void calculateNormInPlace(double[][] input) {

        for (int i = 0; i < input[0].length; i++) {
            input[0][i] = input[0][i]*input[0][i]+input[1][i]*input[1][i];
            input[1][i] = 0;
        }
    }

    /**
     * Calculates the squared difference function divisor term for each element i in input
     *
     * @param input an array of real numbers with length >= 0
     * @param inputACV the autocorrelation function of input
     * @return an array of SDF divisor terms corresponding to elements in input
     */
    private void sdfDivisor(double[] input ,double[] inputACV) {
        centerDoubleArray(input);

        double term_0  = inputACV[0];

        inputDivisors[0] = 2*term_0;

        double currentTerm = 2*term_0;

        for (int i = 1; i < input.length; i++) {
            currentTerm = currentTerm - Math.pow(inputCentered[i-1],2) - Math.pow(inputCentered[input.length-i],2);

            inputDivisors[i] = currentTerm;
        }

    }

    /**
     * Calculates the squared difference function for input, defined as
     *
     * 2*Autocorrelation(i)/SDFDivisor(i) for each element i of input
     *
     * See Mcleod Pitch reference for more information
     * @param input an array of real valued elements
     * @return the squared difference function for input
     */
    private void calculateSDF(double[] input) {

        fftAutoCorrelationFast(input);

        sdfDivisor(input,inputACV);

        for (int i = 0 ; i < inputACV.length/2; i++) {
            inputSDF[i] = 2*inputACV[i]/inputDivisors[i];
        }
    }


    /**
     * Centers an array of double values by subtracting the average value of all elements
     * from each element in the array
     *
     * @param input an array of input values of length >=0
     * @return an array of values in input centered around the average value of input
     */
    private void centerDoubleArray(double[] input) {
        double sum = 0;

        for (double d : input) {
            sum += d;
        }

        double mean = sum/(double)input.length;

        for (int i = 0; i < inputCentered.length; i ++) {
            inputCentered[i] = input[i] - mean;
        }
    }


}
