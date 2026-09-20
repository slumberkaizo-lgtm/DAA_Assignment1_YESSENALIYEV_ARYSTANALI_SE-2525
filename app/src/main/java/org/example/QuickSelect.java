package org.example;

import java.util.Random;

public class QuickSelect {
    public static int select(int[] a, int k) {
        return select(a, k, new Metrics());
    }

    public static int select(int[] a, int k, Metrics m) {
        if (a.length == 0) {
            throw new IllegalArgumentException("array must not be empty");
        }
        if (k < 0 || k >= a.length) {
            throw new IllegalArgumentException("k must be in range [0, " + (a.length - 1) + "]");
        }

        m.startTimer();
        int result = select(a, k, m, new Random(0));
        m.stopTimer();
        return result;
    }

    private static int select(int[] a, int k, Metrics m, Random random) {
        int left = 0;
        int right = a.length;
        int depth = 1;

        while (right - left > 1) {
            m.recordDepth(depth);
            int[] equal = QuickSort.partition(a, left, right, m, random);
            if (k < equal[0]) {
                right = equal[0];
            } else if (k >= equal[1]) {
                left = equal[1];
            } else {
                return a[k];
            }
            depth++;
        }

        m.recordDepth(depth);
        return a[left];
    }
}
