package com.adiga.easypitch.pitch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbajj on 2018-02-13.
 */

public final class ScaleData {

    private static final double A4 = 440;
    private static final double STEP = Math.pow(2,1/12.0);
    private static double[] frequencies = new double[12*9];
    public static final Map<String,Double> NOTE_FREQUENCIES = generateFrequencies();



    private static Map<String,Double> generateFrequencies() {
        Map<String,Double> noteFrequencies = new HashMap<String,Double>();

        frequencies[57] = A4;

        for(int i = 56; i >= 0; i--) {
            double frequency = frequencies[i+1]/STEP;
            frequencies[i] = frequency;
        }

        for(int i = 58; i < 12*9; i++) {
            double frequency = frequencies[i-1]*STEP;
            frequencies[i] = frequency;
        }

        for(int i = 0; i < frequencies.length; i++) {
            int note = i % 12;
            int octave = i/12;
            switch(note) {
                case(0):
                    noteFrequencies.put("C"+String.valueOf(octave),frequencies[i]);
                    break;
                case(1):
                    noteFrequencies.put("C#"+String.valueOf(octave),frequencies[i]);
                    break;
                case(2):
                    noteFrequencies.put("D"+String.valueOf(octave),frequencies[i]);
                    break;
                case(3):
                    noteFrequencies.put("D#"+String.valueOf(octave),frequencies[i]);
                    break;
                case(4):
                    noteFrequencies.put("E"+String.valueOf(octave),frequencies[i]);
                    break;
                case(5):
                    noteFrequencies.put("F"+String.valueOf(octave),frequencies[i]);
                    break;
                case(6):
                    noteFrequencies.put("F#"+String.valueOf(octave),frequencies[i]);
                    break;
                case(7):
                    noteFrequencies.put("G"+String.valueOf(octave),frequencies[i]);
                    break;
                case(8):
                    noteFrequencies.put("G#"+String.valueOf(octave),frequencies[i]);
                    break;
                case(9):
                    noteFrequencies.put("A"+String.valueOf(octave),frequencies[i]);
                    break;
                case(10):
                    noteFrequencies.put("A#"+String.valueOf(octave),frequencies[i]);
                    break;
                case(11):
                    noteFrequencies.put("B"+String.valueOf(octave),frequencies[i]);
                    break;
            }

        }

        return noteFrequencies;
    }

}
