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

        double[] input1 = new double[4096];

        for (int i = 0; i < input1.length; i++) {
            input1[i] = i % 40;
        }


        assertEquals(40, PitchDetector.findPitch(input1,40*40),0);

    }

    @Test
    public void testFindPitch2() {
        double[] input2 = new double[4096];

        for (int i = 0; i < input2.length; i++) {
            input2[i] = Math.sin(Math.PI*2*i/1000);
        }

        assertEquals(1000,PitchDetector.findPitch(input2,1000*1000),0);
    }

    @Test
    public void testFindPitch3() {


    }


}
