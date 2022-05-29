package com.example.emercare.Filter;

import android.text.InputFilter;
import android.text.Spanned;

public class DoubleInputFilter implements InputFilter {
    double min, max;

    public DoubleInputFilter(String min, String max) {
        this.min = Double.parseDouble(min);
        this.max = Double.parseDouble(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned destination, int dStart, int dEnd) {
        try {
            String lastValue = destination.toString().substring(0, dStart) + destination.toString().substring(dEnd);
            String newValue = lastValue.substring(0, dStart) + source.toString() + lastValue.substring(dStart);
            double input = Double.parseDouble(newValue);

            if (isInRange(min, max, input)) {
                return null;
            }
        } catch (NumberFormatException ignored) {
        }
        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}