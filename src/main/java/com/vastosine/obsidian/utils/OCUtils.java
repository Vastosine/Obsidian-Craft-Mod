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

    public static int[] getSequence(int size) {
        return getSequence(0, size, 1);
    }

    public static int abs(int x) {
        return x < 0 ? -x : x;
    }

    public static int sign(int x) {
        return Integer.compare(x, 0);
    }

    public static int getCeilFromDivision(int a, int b) {
        if (b == 0) throw new RuntimeException();
        if (a == 0) return 0;
        return  sign(a) * sign(b) < 0 ? -getCeilFromDivision(abs(a), abs(b)) : (a - 1) / b + 1;
    }
}
