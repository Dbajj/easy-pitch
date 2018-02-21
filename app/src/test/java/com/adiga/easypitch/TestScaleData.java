package com.adiga.easypitch;

import com.adiga.easypitch.pitch.ScaleData;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by dbajj on 2018-02-13.
 */

public class TestScaleData {

    @Test
    public void testA4() {
        double A4_freq = ScaleData.NOTE_FREQUENCIES.get("A4");

        assertEquals(440,A4_freq,0.1);
    }
    @Test
    public void testC0() {
        double C0_freq = ScaleData.NOTE_FREQUENCIES.get("C0");

        assertEquals(16.35,C0_freq,0.1);
    }

    @Test
    public void testB8() {
        double B8_freq = ScaleData.NOTE_FREQUENCIES.get("B8");

        assertEquals(7902.13,B8_freq,0.1);
    }
}

