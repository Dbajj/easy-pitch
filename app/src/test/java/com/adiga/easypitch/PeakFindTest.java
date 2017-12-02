package com.adiga.easypitch;

import com.adiga.easypitch.utils.GraphCoordinate;
import com.adiga.easypitch.utils.PeakFind;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dbajj on 2017-12-01.
 */

public class PeakFindTest {

    @Test
    public void testFindMaxima1() {

        double[] input = new double[]{2,-3,0,2,0};

        assertEquals(3, PeakFind.findMaxima(input)[0].getX(),0);
        assertEquals(2,PeakFind.findMaxima(input)[0].getY(),0);
    }

    @Test
    public void testFindMaxima2() {
        double[] input = new double[]{2,-3,0,5,-2};

        assertTrue(PeakFind.findMaxima(input)[0].getX() % 1 != 0);
    }
}
