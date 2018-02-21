package com.adiga.easypitch.pitch;

import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by dbajj on 2018-02-13.
 */

public final class ScaleData {

    private static final double A4 = 440;
    public static final double NOTE_STEP = Math.pow(2,1/12.0);
    private static double[] frequencies = new double[12*9];
    public static final NavigableMap<Double,String> NOTE_FREQUENCIES;

    static {
        NOTE_FREQUENCIES = generateFrequencies();
    }

    private static NavigableMap<Double,String> generateFrequencies() {
        NavigableMap<Double,String> noteFrequencies = new TreeMap<Double,String>();

        frequencies[57] = A4;

        for(int i = 56; i >= 0; i--) {
            double frequency = frequencies[i+1]/ NOTE_STEP;
            frequencies[i] = frequency;
        }

        for(int i = 58; i < 12*9; i++) {
            double frequency = frequencies[i-1]* NOTE_STEP;
            frequencies[i] = frequency;
        }

        for(int i = 0; i < frequencies.length; i++) {
            int note = i % 12;
            int octave = i/12;
            switch(note) {
                case(0):
                    noteFrequencies.put(frequencies[i],"C"+String.valueOf(octave));
                    break;
                case(1):
                    noteFrequencies.put(frequencies[i],"C#"+String.valueOf(octave));
                    break;
                case(2):
                    noteFrequencies.put(frequencies[i],"D"+String.valueOf(octave));
                    break;
                case(3):
                    noteFrequencies.put(frequencies[i],"D#"+String.valueOf(octave));
                    break;
                case(4):
                    noteFrequencies.put(frequencies[i],"E"+String.valueOf(octave));
                    break;
                case(5):
                    noteFrequencies.put(frequencies[i],"F"+String.valueOf(octave));
                    break;
                case(6):
                    noteFrequencies.put(frequencies[i],"F#"+String.valueOf(octave));
                    break;
                case(7):
                    noteFrequencies.put(frequencies[i],"G"+String.valueOf(octave));
                    break;
                case(8):
                    noteFrequencies.put(frequencies[i],"G#"+String.valueOf(octave));
                    break;
                case(9):
                    noteFrequencies.put(frequencies[i],"A"+String.valueOf(octave));
                    break;
                case(10):
                    noteFrequencies.put(frequencies[i],"A#"+String.valueOf(octave));
                    break;
                case(11):
                    noteFrequencies.put(frequencies[i],"B"+String.valueOf(octave));
                    break;
            }

        }

        return noteFrequencies;
    }

    public static double getNoteFrequency(String note) {

        for(Map.Entry<Double,String> e : NOTE_FREQUENCIES.entrySet()) {
            if(e.getValue().equals(note)) {
                return e.getKey();
            }
        }

        throw new IllegalArgumentException("No frequency data available for note: " + note);
    }

    public static double getClosestPitch(double pitch) {
        Map.Entry<Double,String> floor = NOTE_FREQUENCIES.floorEntry(pitch);
        Map.Entry<Double,String> ceil = NOTE_FREQUENCIES.ceilingEntry(pitch);

        if(floor == null && ceil == null) {
            throw new UnsupportedOperationException("Pitch found no floor or ceiling in set");
        } else if(floor == null) {
            return ceil.getKey();
        } else if (ceil == null) {
            return floor.getKey();
        }


        if(floor.equals(ceil)) {
            return floor.getKey();
        } else if (Math.abs(floor.getKey() - pitch) < Math.abs(ceil.getKey() - pitch)) {
            return floor.getKey();
        } else {
            return ceil.getKey();
        }
    }

    public static double getOffset(double pitch) {
        double closest = getClosestPitch(pitch);

        double higher = NOTE_FREQUENCIES.higherKey(closest);
        double lower = NOTE_FREQUENCIES.lowerKey(closest);

        double upper_range = Math.abs(higher-closest);
        double lower_range = Math.abs(lower-closest);

        double difference = Math.abs(pitch-closest);
        if(pitch == closest) {
            return 0.0;
        } else if(pitch > closest) {
            return difference/upper_range;
        } else {
            return -1*(difference/lower_range);
        }

    }


}
