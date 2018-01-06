package com.adiga.easypitch.utils;

/**
 * Created by dbajj on 2017-11-27.
 */

import java.util.ArrayList;
import java.util.function.DoubleFunction;

public class PeakFind {


    private static GraphCoordinate max = new GraphCoordinate(0,0);


    // Finds all local maxima (as x,y pairs) after first zero crossing of input.
    // If no zero crossing, will return empty array
    public static GraphCoordinate[] findMaxima(double[] input) {

        ArrayList<GraphCoordinate> maxima = new ArrayList<GraphCoordinate>();
        GraphCoordinate left = new GraphCoordinate(0,0);
        GraphCoordinate center = new GraphCoordinate(0,0);
        GraphCoordinate right = new GraphCoordinate(0,0);

        int zeroCrossing = 0;

        for (int i = 1; i < input.length - 1; i++) {

            if (input[i-1] > 0 & input[i] <= 0 & zeroCrossing == 0) zeroCrossing = i;

            if (zeroCrossing == 0) continue;


            if (input[i-1] < input[i] & input[i] > input[i+1])  {

                left.setX(i-1.0);
                left.setY(input[i-1]);

                center.setX(i*1.0);
                center.setY(input[i]);

                right.setX(i+1.0);
                right.setY(input[i+1]);

                parabolicInterpolate(left,center,right);

                GraphCoordinate foundMax = new GraphCoordinate(max.getX(),max.getY());

                maxima.add(foundMax);

            }
        }

        return maxima.toArray(new GraphCoordinate[0]);

    }

    // Produces the x,y coordinate of maximum between left, center, right using parabolic interpolation
    // based off of a lagrange polynomial drawn between the three points
    private static GraphCoordinate parabolicInterpolate(GraphCoordinate left, GraphCoordinate center, GraphCoordinate right) {


        calcMax(left,center,right);

        while (Math.abs(max.getX() - center.getX()) >= 0.01) {

            if (max.getX() > center.getX()) {
                left = center;
                center = max;
            }

            else {
                right = center;
                center = max;

            }

            calcMax(left,center,right);
        }

        return max;

    }

    private static void calcMax(GraphCoordinate left, GraphCoordinate center, GraphCoordinate right) {
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
