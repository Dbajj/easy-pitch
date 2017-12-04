package com.adiga.easypitch.pitch;

import android.content.Context;
import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;

import com.adiga.easypitch.utils.GraphCoordinate;
import com.adiga.easypitch.utils.PeakFind;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;

/**
 * Created by dbajj on 2017-11-27.
 */

public class PitchDetector {

    public static final double CUTOFF = 0.8;

    private double[] inputSDF;
    private int sampleRate;

    private FastFourierTransformer FFTCalc;
    public PitchDetector(int SAMPLE_RATE) {
        sampleRate = SAMPLE_RATE;
        FFTCalc = new FastFourierTransformer(DftNormalization.STANDARD);
    }


    public double findPitch(double[] audioInput) {

        inputSDF = calculateSDF(audioInput);

        GraphCoordinate[] maxima = PeakFind.findMaxima(inputSDF);

        inputSDF = null;

        for (int i = 0; i < maxima.length/2; i++) {
            if(maxima[i].getY() > CUTOFF) {
                return sampleRate/maxima[i].getX();
            }
        }
        // TODO figure out wtf this will return if there is no viable max found.
        return 0;

    }

    public double[] findSDF(double[] audioInput) {
        return calculateSDF(audioInput);

    }


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


        FFTCalc.transformInPlace(inputCenteredPaddedComplex,DftNormalization.STANDARD, TransformType.FORWARD);

        calculateNormInPlace(inputCenteredPaddedComplex);

        FFTCalc.transformInPlace(inputCenteredPaddedComplex,DftNormalization.STANDARD, TransformType.INVERSE);



        double[] autoCovarianceReal = new double[inputCenteredPaddedComplex[0].length/2];

        for (int i = 0; i < autoCovarianceReal.length; i++) {
            autoCovarianceReal[i] = inputCenteredPaddedComplex[0][i];
        }


        return autoCovarianceReal;

    }


    private Complex[] inputFFT;
    private double[] fftAutoCorrelation(double[] input) {
        double[] inputCentered = centerDoubleArray(input);

        double[] zeroPadding  = new double[inputCentered.length];
        for(int i = 0; i < zeroPadding.length; i++) {
            zeroPadding[i] = 0.0;
        }

        double[] inputCenteredPadded = ArrayUtils.addAll(inputCentered,zeroPadding);



        inputFFT = FFTCalc.transform(inputCenteredPadded, TransformType.FORWARD);


        for (int i = 0; i < inputFFT.length; i ++) {
            inputFFT[i] = calculateNorm(inputFFT[i]);
        }

        Complex[] autoCovariance = FFTCalc.transform(inputFFT,TransformType.INVERSE);

        double[] autoCovarianceReal = new double[autoCovariance.length/2];

        for (int i = 0; i < autoCovariance.length/2; i++) {
            autoCovarianceReal[i] = autoCovariance[i].getReal();
        }


        return autoCovarianceReal;



    }


    private void calculateNormInPlace(double[][] input) {

        for (int i = 0; i < input[0].length; i++) {
            input[0][i] = input[0][i]*input[0][i]+input[1][i]*input[1][i];
            input[1][i] = 0;
        }
    }
    private Complex calculateNorm(Complex input) {
        return new Complex(input.getReal()*input.getReal()+input.getImaginary()*input.getImaginary(),0);
    }

    // Produces divisor terms from McLeod Pitch method by sequentially subtracting terms from auto covariance
    // (which is taken to be sum of squares across entire input)
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

    // !!! add description
    private double[] calculateSDF(double[] input) {

        double[] inputACV = fftAutoCorrelationFast(input);

        double[] inputDivisors = sdfDivisor(input,inputACV);

        double[] sdf = new double[inputACV.length];

        for (int i = 0 ; i < inputACV.length; i++) {
            sdf[i] = 2*inputACV[i]/inputDivisors[i];

        }

        return sdf;
    }

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
