package com.adiga.easypitch.pitch;

import com.adiga.easypitch.utils.GraphCoordinate;
import com.adiga.easypitch.utils.PeakFind;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 * Created by dbajj on 2017-11-27.
 */

public class PitchDetector {

    public static final double CUTOFF = 0.3;

    public static double findPitch(double[] audioInput, final int SAMPLE_RATE) {

        double[] inputSDF = calculateSDF(audioInput);

        GraphCoordinate[] maxima = PeakFind.findMaxima(inputSDF);

        for (int i = 0; i < maxima.length; i++) {
            System.out.println("X: " + maxima[i].getX() + "Y: " + maxima[i].getY());
            if(maxima[i].getY() > CUTOFF) {
                return SAMPLE_RATE/maxima[i].getX();
            }
        }
        // TODO figure out wtf this will return if there is no viable max found.
        System.out.println("Crap");
        return 0;

    }


    private static double[] fftAutoCorrelation(double[] input) {
        double[] inputCentered = centerDoubleArray(input);

        double[] zeroPadding  = new double[inputCentered.length];
        for(int i = 0; i < zeroPadding.length; i++) {
            zeroPadding[i] = 0.0;
        }

        double[] inputCenteredPadded = ArrayUtils.addAll(inputCentered,zeroPadding);

        FastFourierTransformer FFTCalc = new FastFourierTransformer(DftNormalization.STANDARD);

        Complex[] inputFFT = FFTCalc.transform(inputCenteredPadded, TransformType.FORWARD);


        for (int i = 0; i < inputFFT.length; i ++) {
            inputFFT[i] = new Complex(inputFFT[i].getReal()*inputFFT[i].getReal()+inputFFT[i].getImaginary()*inputFFT[i].getImaginary(),0.0);
        }

        Complex[] autoCovariance = FFTCalc.transform(inputFFT,TransformType.INVERSE);

        double[] autoCovarianceReal = new double[autoCovariance.length/2];

        for (int i = 0; i < autoCovariance.length/2; i++) {
            autoCovarianceReal[i] = autoCovariance[i].getReal();
        }

        return autoCovarianceReal;



    }


    // Produces divisor terms from McLeod Pitch method by sequentially subtracting terms from auto covariance
    // (which is taken to be sum of squares across entire input)
    private static double[] sdfDivisor(double[] input ,double[] inputACV) {
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

    // !!! add description
    private static double[] calculateSDF(double[] input) {

        double[] inputACV = fftAutoCorrelation(input);

        double[] inputDivisors = sdfDivisor(input,inputACV);

        double[] sdf = new double[inputACV.length];

        for (int i = 0 ; i < inputACV.length; i++) {
            sdf[i] = 2*inputACV[i]/inputDivisors[i];
        }

        return sdf;
    }

    private static double[] centerDoubleArray(double[] input) {
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
