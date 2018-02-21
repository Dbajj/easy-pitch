package com.adiga.easypitch;

import com.adiga.easypitch.pitch.ScaleData;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by dbajj on 2018-02-13.
 */

public class TestScaleData {

    @Test
    public void testA4() {
        double A4_freq = ScaleData.getNoteFrequency("A4");

        assertEquals(440,A4_freq,0.1);
    }
    @Test
    public void testC0() {
        double C0_freq = ScaleData.getNoteFrequency("C0");

        assertEquals(16.35,C0_freq,0.1);
    }

    @Test
    public void testB8() {
        double B8_freq = ScaleData.getNoteFrequency("B8");

        assertEquals(7902.13,B8_freq,0.1);
    }

    @Test
    public void testSearch() {

        double B8_freq = ScaleData.getNoteFrequency("B8");
        double B8_freq_offset_up = ScaleData.getNoteFrequency("B8")+ScaleData.NOTE_STEP*0.49;
        double B8_freq_offset_down = ScaleData.getNoteFrequency("B8")-ScaleData.NOTE_STEP*0.49;

        assertEquals(ScaleData.getNoteFrequency("B8"),ScaleData.getClosestPitch(B8_freq),0);
        assertEquals(ScaleData.getNoteFrequency("B8"),ScaleData.getClosestPitch(B8_freq_offset_down),0);
        assertEquals(ScaleData.getNoteFrequency("B8"),ScaleData.getClosestPitch(B8_freq_offset_up),0);
    }

    // TODO: add tests for below bottom of range and above top of range
}

