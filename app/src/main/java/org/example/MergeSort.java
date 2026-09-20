package org.example;

public class MergeSort {
    private static final int INSERTION_SORT_CUTOFF = 15;

    public static void sort(int[] a) {
        sort(a, new Metrics());
    }

    public static void sort(int[] a, Metrics m) {
        m.startTimer();
        if (a.length > 1) {
            int[] buffer = new int[a.length];
            sort(a, buffer, 0, a.length, m, 1);
        }
        m.stopTimer();
    }

    private static void sort(int[] a, int[] buffer, int left, int right, Metrics m, int depth) {
        m.recordDepth(depth);
        int length = right - left;
        if (length <= INSERTION_SORT_CUTOFF) {
            insertionSort(a, left, right, m);
            return;
        }

        int mid = left + length / 2;
        sort(a, buffer, left, mid, m, depth + 1);
        sort(a, buffer, mid, right, m, depth + 1);
        merge(a, buffer, left, mid, right, m);
    }

    private static void insertionSort(int[] a, int left, int right, Metrics m) {
        for (int i = left + 1; i < right; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= left && m.compare(a[j], key) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static void merge(int[] a, int[] buffer, int left, int mid, int right, Metrics m) {
        System.arraycopy(a, left, buffer, left, right - left);

        int i = left;
        int j = mid;
        int k = left;
        while (i < mid && j < right) {
            if (m.compare(buffer[i], buffer[j]) <= 0) {
                a[k++] = buffer[i++];
            } else {
                a[k++] = buffer[j++];
            }
        }
        while (i < mid) {
            a[k++] = buffer[i++];
        }
        while (j < right) {
            a[k++] = buffer[j++];
        }
    }
}
