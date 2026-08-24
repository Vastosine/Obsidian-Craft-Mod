package com.vastosine.obsidian.utils;

public class OCUtils {
    public static boolean isInRange(final int x, final int min, final int max) {
        return min <= x && x < max;
    }

    public static boolean isInRange(final int x, final int max) {
        return isInRange(x, 0, max);
    }

    public static int[] getSequence(int begin, int size, int step) {
        int[] s = new int[size];
        for (int i = 0; i < size; i++) {
            s[i] = begin + i * step;
        }
        return s;
    }

    public static int[] getSequence(int begin, int size) {
        return getSequence(begin, size, 1);
    }
}
