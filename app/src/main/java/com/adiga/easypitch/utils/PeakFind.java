package com.adiga.easypitch.utils;

/**
 * Created by dbajj on 2017-11-27.
 */

import android.support.v4.util.Pools;
import android.util.DebugUtils;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleFunction;

public class PeakFind {

    private static final int MAX_POOL_SIZE = 5000;

    private GraphCoordinate max;
    private GraphCoordinate left;
    private GraphCoordinate center;
    private GraphCoordinate right;

    private static final PeakFind INSTANCE = new PeakFind();

    private List<GraphCoordinate> maxima;
    private GraphCoordinatePool coordinatePool;



    public static PeakFind getInstance() {
        return INSTANCE;
    }

    private PeakFind() {
        max = new GraphCoordinate(0,0);
        left = new GraphCoordinate(0,0);
        center = new GraphCoordinate(0,0);
        right = new GraphCoordinate(0,0);

        maxima = new ArrayList<GraphCoordinate>();
        coordinatePool = new GraphCoordinatePool(MAX_POOL_SIZE);


    }


    // Finds all local maxima (as x,y pairs) after first zero crossing of input.
    // If no zero crossing, will return empty array
    public synchronized List<GraphCoordinate> findMaxima(double[] input) {
        int zeroCrossing = 0;

        for (int i = 1; i < input.length - 1; i++) {

            if (input[i-1] > 0 & input[i] <= 0 & zeroCrossing == 0) zeroCrossing = i;

            if (zeroCrossing == 0) continue;


            if (input[i-1] < input[i] && input[i] > input[i+1])  {

                left.setX(i-1.0);
                left.setY(input[i-1]);

                center.setX(i*1.0);
                center.setY(input[i]);

                right.setX(i+1.0);
                right.setY(input[i+1]);

                parabolicInterpolate();

                GraphCoordinate foundMax = null;
                try {
                    foundMax = coordinatePool.acquire();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                foundMax.setX(max.getX());
                foundMax.setY(max.getY());

                maxima.add(foundMax);

            }
        }


        return maxima;

    }

    public void clearMaxima() {
        for(GraphCoordinate g : maxima) {
            coordinatePool.release(g);
            // TODO figure out why this is only clearing a space every other loop
        }

        maxima.clear();

    }



    // Produces the x,y coordinate of maximum between left, center, right using parabolic interpolation
    // based off of a lagrange polynomial drawn between the three points
    private GraphCoordinate parabolicInterpolate() {

        calcMax();

        while (Math.abs(max.getX() - center.getX()) >= 0.01) {

            if (max.getX() > center.getX()) {
                left = center;
                center = max;
            }

            else {
                right = center;
                center = max;

            }

            calcMax();
        }

        return max;

    }

    private void calcMax() {
        double f_a = (double) left.getY();
        double f_b = (double) center.getY();
        double f_c = (double) right.getY();

        double a = (double) left.getX();
        double b = (double) center.getX();
        double c = (double) right.getX();

        double x_max = b + 0.5 * ((f_a - f_b) * Math.pow((c - b), 2) - (f_c - f_b) * Math.pow((b - a), 2))
                / ((f_a - f_b) * (c - b) + (f_c - f_b) * (b - a));

        double y_max = f_a * (((x_max - b) * (x_max - c)) / ((a - b) * (a - c)))
                + f_b * (((x_max - c) * (x_max - a)) / ((b - c) * (b - a)))
                + f_c * (((x_max - a) * (x_max - b)) / ((c - a) * (c - b)));


        max.setX(x_max);
        max.setY(y_max);
    }


}
