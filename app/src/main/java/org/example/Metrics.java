package org.example;

public class Metrics {
    private long comparisons;
    private int maxDepth;
    private long startNanos;
    private long elapsedNanos;

    public void startTimer() {
        startNanos = System.nanoTime();
    }

    public void stopTimer() {
        elapsedNanos = System.nanoTime() - startNanos;
    }

    public int compare(int left, int right) {
        comparisons++;
        return Integer.compare(left, right);
    }

    public void recordDepth(int depth) {
        if (depth > maxDepth) {
            maxDepth = depth;
        }
    }

    public long comparisons() {
        return comparisons;
    }

    public int maxDepth() {
        return maxDepth;
    }

    public double elapsedMillis() {
        return elapsedNanos / 1_000_000.0;
    }
}
