package com.adiga.easypitch.utils;

/**
 * Created by dbajj on 2017-11-27.
 */


// A simple paired data structure, used to represent cartesian coordinates
public class GraphCoordinate {

        private double x;
        private double y;


        public GraphCoordinate(double xInput, double yInput) {
            x = xInput;
            y = yInput;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

    }
