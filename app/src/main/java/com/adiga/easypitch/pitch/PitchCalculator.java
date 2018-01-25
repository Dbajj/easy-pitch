package com.adiga.easypitch.pitch;

import com.adiga.easypitch.utils.GraphCoordinate;
import com.adiga.easypitch.utils.PeakFind;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 * Created by dbajj on 2018-01-24.
 */

public class PitchCalculator {
    private final double CUTOFF;
    private final int SAMPLE_RATE;

    private double[] inputSDF;
    private FastFourierTransformer mFFTCalc;

    /**
     * Initializes new PitchCalculator, by setting pitch cutoff, sample rate and FFT calculator
     * @param sampleRate the sampling rate used to collect audio input data that will be given
     *                   to the calculator (used to convert frequency to Hz)
     */
    public PitchCalculator(int sampleRate) {
        mFFTCalc = new FastFourierTransformer(DftNormalization.STANDARD);
        CUTOFF = PitchDetector.CUTOFF;
        SAMPLE_RATE = sampleRate;
    }


    /**
     *
     * @param audioInput an array of audio data, sampled at SAMPLE_RATE and of any length >=0
     * @return           the calculated pitch using the McLeod Pitch autocorrelation method
     */
    public double findPitch(double[] audioInput) {

        inputSDF = calculateSDF(audioInput);
        GraphCoordinate[] maxima = PeakFind.findMaxima(inputSDF);


        inputSDF = null;

        for (int i = 0; i < maxima.length/2; i++) {
            if(maxima[i].getY() > CUTOFF) {
                return SAMPLE_RATE/maxima[i].getX();
            }
        }
        // TODO figure out wtf this will return if there is no viable max found.
        return 0;

    }


    /**
     * @param input an array of data to be inserted into the autocorrelation function
     * @return the autocorrelation function of the input, calculated using FFT
     */
    private double[] fftAutoCorrelationFast(double[] input) {
        double[] inputCentered = centerDoubleArray(input);

        double[] zeroPadding  = new double[inputCentered.length];
        for(int i = 0; i < zeroPadding.length; i++) {
            zeroPadding[i] = 0.0;
        }

        double[] inputCenteredPadded = ArrayUtils.addAll(inputCentered,zeroPadding);

        double[][] inputCenteredPaddedComplex = new double[2][inputCenteredPadded.length];


        for (int i = 0; i < inputCenteredPadded.length; i++) {
            inputCenteredPaddedComplex[0][i] = inputCenteredPadded[i];
            inputCenteredPaddedComplex[1][i] = 0;
        }


        mFFTCalc.transformInPlace(inputCenteredPaddedComplex,DftNormalization.STANDARD, TransformType.FORWARD);

        calculateNormInPlace(inputCenteredPaddedComplex);

        mFFTCalc.transformInPlace(inputCenteredPaddedComplex,DftNormalization.STANDARD, TransformType.INVERSE);



        double[] autoCovarianceReal = new double[inputCenteredPaddedComplex[0].length/2];

        for (int i = 0; i < autoCovarianceReal.length; i++) {
            autoCovarianceReal[i] = inputCenteredPaddedComplex[0][i];
        }

        return autoCovarianceReal;

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
    private double[] sdfDivisor(double[] input ,double[] inputACV) {
        double[] centeredInput = centerDoubleArray(input);

        double term_0  = inputACV[0];

        double[] divisorTerms = new double[input.length];

        divisorTerms[0] = 2*term_0;

        double currentTerm = 2*term_0;

        for (int i = 1; i < input.length; i++) {
            currentTerm = currentTerm - Math.pow(centeredInput[i-1],2) - Math.pow(centeredInput[input.length-i],2);

            divisorTerms[i] = currentTerm;
        }

        return divisorTerms;
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
    private double[] calculateSDF(double[] input) {

        double[] inputACV = fftAutoCorrelationFast(input);

        double[] inputDivisors = sdfDivisor(input,inputACV);

        double[] sdf = new double[inputACV.length/2];

        for (int i = 0 ; i < inputACV.length/2; i++) {
            sdf[i] = 2*inputACV[i]/inputDivisors[i];

        }

        return sdf;
    }


    /**
     * Centers an array of double values by subtracting the average value of all elements
     * from each element in the array
     *
     * @param input an array of input values of length >=0
     * @return an array of values in input centered around the average value of input
     */
    private double[] centerDoubleArray(double[] input) {
        double sum = 0;

        for (double d : input) {
            sum += d;
        }

        double mean = sum/(double)input.length;

        double[] centeredInput = input.clone();

        for (int i = 0; i < centeredInput.length; i ++) {
            centeredInput[i] = centeredInput[i] - mean;
        }
         return centeredInput;
    }


}
