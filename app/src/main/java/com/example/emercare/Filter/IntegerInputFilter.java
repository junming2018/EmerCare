package com.example.emercare.Filter;

import android.text.InputFilter;
import android.text.Spanned;

public class IntegerInputFilter implements InputFilter {
    int min, max;

    public IntegerInputFilter(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned destination, int dStart, int dEnd) {
        try {
            String lastValue = destination.toString().substring(0, dStart) + destination.toString().substring(dEnd);
            String newValue = lastValue.substring(0, dStart) + source.toString() + lastValue.substring(dStart);
            int input = Integer.parseInt(newValue);

            if (isInRange(min, max, input)) {
                return null;
            }
        } catch (NumberFormatException ignored) {
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}