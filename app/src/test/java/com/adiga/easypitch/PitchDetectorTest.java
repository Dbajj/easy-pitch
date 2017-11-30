package com.adiga.easypitch;

import com.adiga.easypitch.pitch.PitchDetector;

import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by dbajj on 2017-11-27.
 */

public class PitchDetectorTest {

    @Test
    public void testFindPitch() {

        double[] input = new double[4096];

        for (int i = 0; i < input.length; i++) {
            input[i] = i % 40;
        }

        assertEquals(40, PitchDetector.findPitch(input,40*40),0);

    }
}
