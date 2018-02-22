package com.adiga.easypitch;

import com.adiga.easypitch.pitch.WaveletCalculator;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by dbajj on 2018-02-22.
 */

public class WaveletCalculatorTest {

    @Test
    public void testPitch() {
        double[] input1 = new double[4096];

        for (int i = 0; i < input1.length; i++) {
            input1[i] = i % 40;
        }

        WaveletCalculator calculator = new WaveletCalculator(40*40,4096);

        double pitch = calculator.findPitch(input1);

        assertEquals(40,pitch,0.1);

    }
}
