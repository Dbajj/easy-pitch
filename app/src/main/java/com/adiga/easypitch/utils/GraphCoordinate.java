package com.adiga.easypitch.utils;

import android.support.v4.util.Pools;

/**
 * Created by dbajj on 2017-11-27.
 */


// A simple paired data structure, used to represent cartesian coordinates
public class GraphCoordinate {

        private double x;
        private double y;


        public GraphCoordinate() {
            x = 0;
            y = 0;
        }
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

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

    }
