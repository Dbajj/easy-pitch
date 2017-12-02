package com.adiga.easypitch.utils;

/**
 * Created by dbajj on 2017-11-27.
 */

import java.util.ArrayList;
import java.util.function.DoubleFunction;

public class PeakFind {



    // Finds all local maxima (as x,y pairs) after first zero crossing of input.
    // If no zero crossing, will return empty array
    public static GraphCoordinate[] findMaxima(double[] input) {

        ArrayList<GraphCoordinate> maxima = new ArrayList<GraphCoordinate>();

        int zeroCrossing = 0;

        for (int i = 1; i < input.length - 1; i++) {

            if (input[i-1] > 0 & input[i] <= 0 & zeroCrossing == 0) zeroCrossing = i;

            if (zeroCrossing == 0) continue;


            if (input[i-1] < input[i] & input[i] > input[i+1])  {

                GraphCoordinate max = parabolicInterpolate(new GraphCoordinate(i-1.0,input[i-1]),
                        new GraphCoordinate(i*1.0,input[i]),
                        new GraphCoordinate(i+1.0,input[i+1]));

                maxima.add(max);
            }
        }

        GraphCoordinate[] maximaOut =  new GraphCoordinate[maxima.size()];

        for (int i = 0; i < maxima.size(); i++) {
            maximaOut[i] = maxima.get(i);
        }

        return maximaOut;

    }

    // Produces the x,y coordinate of maximum between left, center, right using parabolic interpolation
    // based off of a lagrange polynomial drawn between the three points
    private static GraphCoordinate parabolicInterpolate(GraphCoordinate left, GraphCoordinate center, GraphCoordinate right) {


        GraphCoordinate max = calcMax(left,center,right);

        while (Math.abs(max.getX() - center.getX()) >= 0.01) {

            if (max.getX() > center.getX()) {
                left = center;
                center = max;
            }

            else {
                right = center;
                center = max;

            }

            max = calcMax(left,center,right);
        }

        return max;

    }

    private static GraphCoordinate calcMax(GraphCoordinate left, GraphCoordinate center, GraphCoordinate right) {
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


        return new GraphCoordinate(x_max,y_max);
    }


}
