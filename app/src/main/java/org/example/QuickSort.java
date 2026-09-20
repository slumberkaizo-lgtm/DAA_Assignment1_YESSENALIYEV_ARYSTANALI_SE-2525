package org.example;

import java.util.Random;

public class QuickSort {
    public static void sort(int[] a) {
        sort(a, new Metrics());
    }

    public static void sort(int[] a, Metrics m) {
        sort(a, m, new Random());
    }

    static void sort(int[] a, Metrics m, Random random) {
        m.startTimer();
        sort(a, 0, a.length, m, random, 1);
        m.stopTimer();
    }

    private static void sort(int[] a, int left, int right, Metrics m, Random random, int depth) {
        while (right - left > 1) {
            m.recordDepth(depth);
            int[] equal = partition(a, left, right, m, random);
            int leftSize = equal[0] - left;
            int rightSize = right - equal[1];

            if (leftSize < rightSize) {
                sort(a, left, equal[0], m, random, depth + 1);
                left = equal[1];
            } else {
                sort(a, equal[1], right, m, random, depth + 1);
                right = equal[0];
            }
        }
        m.recordDepth(depth);
    }

    static int[] partition(int[] a, int left, int right, Metrics m, Random random) {
        int pivotIndex = left + random.nextInt(right - left);
        int pivot = a[pivotIndex];
        swap(a, left, pivotIndex);

        int lt = left;
        int i = left + 1;
        int gt = right;
        while (i < gt) {
            int cmp = m.compare(a[i], pivot);
            if (cmp < 0) {
                swap(a, lt++, i++);
            } else if (cmp > 0) {
                swap(a, i, --gt);
            } else {
                i++;
            }
        }
        return new int[] {lt, gt};
    }

    private static void swap(int[] a, int i, int j) {
        if (i == j) {
            return;
        }
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}
