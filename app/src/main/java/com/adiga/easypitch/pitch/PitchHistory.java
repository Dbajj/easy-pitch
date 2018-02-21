package com.adiga.easypitch.pitch;

import android.os.Debug;
import android.util.Log;

import org.apache.commons.collections4.comparators.FixedOrderComparator;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.math3.exception.NullArgumentException;

import java.security.spec.ECField;
import java.util.Queue;

/**
 * Created by dbajj on 2018-02-12.
 */

public class PitchHistory {

    private static final String CLEAR_HISTORY = "clear";
    private static final String FAR_VALUE = "far";
    private static final String CLOSE_VALUE = "close";

    private static final double FAR_CUTOFF = 20;
    private static final double CLEAR_CUTOFF = 5;

    private int mDifferenceCount;
    private Queue<Double> mCircularQueue;
    private final int runningAverageSize;


    public PitchHistory(int size) {
        mDifferenceCount = 0;
        runningAverageSize = size;
        mCircularQueue = new CircularFifoQueue<Double>(runningAverageSize);
    }

    public double add(Double d) {
        if(d == 0) {
            return getAverage();
        }

        String compare = farValue(d);

        switch(compare) {
            case(CLEAR_HISTORY):
                mCircularQueue.clear();
                mCircularQueue.add(d);
                return getAverage();
            case(FAR_VALUE):
                return getAverage();
            case(CLOSE_VALUE):
                mCircularQueue.add(d);
                return getAverage();

            default:
                throw new RuntimeException("Switch statement unexpected case reached");
        }

    }

    private double getAverage() {
        if(mCircularQueue.size() == 0) {
            return 0;
        } else {
            double sum = 0;
            int num = 0;
            for(Double d : mCircularQueue) {
                sum += d;
                num++;
            }
            return sum/(double)num;
        }
    }

    private String farValue(Double d) {
        double difference = Math.abs(d-getAverage());

        if(difference > FAR_CUTOFF && mDifferenceCount > CLEAR_CUTOFF) {
            mDifferenceCount = 0;
            return CLEAR_HISTORY;
        } else if (difference > FAR_CUTOFF) {
            mDifferenceCount++;
            return FAR_VALUE;
        } else {
            mDifferenceCount = 0;
            return CLOSE_VALUE;
        }
    }




}
