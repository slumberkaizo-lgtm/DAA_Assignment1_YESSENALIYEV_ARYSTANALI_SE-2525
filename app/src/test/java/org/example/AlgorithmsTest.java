package org.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

class AlgorithmsTest {
    @Test
    void mergeSortMatchesArraysSortOnRandomArrays() {
        Random random = new Random(1);
        for (int t = 0; t < 100; t++) {
            int[] actual = randomArray(random, random.nextInt(500));
            int[] expected = actual.clone();
            Arrays.sort(expected);
            MergeSort.sort(actual, new Metrics());
            assertArrayEquals(expected, actual);
        }
    }

    @Test
    void quickSortMatchesArraysSortOnRandomArrays() {
        Random random = new Random(2);
        for (int t = 0; t < 100; t++) {
            int[] actual = randomArray(random, random.nextInt(500));
            int[] expected = actual.clone();
            Arrays.sort(expected);
            QuickSort.sort(actual, new Metrics(), new Random(t));
            assertArrayEquals(expected, actual);
        }
    }

    @Test
    void sortingEdgeCases() {
        int[][] cases = {
                {},
                {42},
                {7, 7, 7, 7, 7},
                {1, 2, 3, 4, 5},
                {5, 4, 3, 2, 1}
        };

        for (int[] original : cases) {
            int[] expected = original.clone();
            Arrays.sort(expected);

            int[] merge = original.clone();
            MergeSort.sort(merge, new Metrics());
            assertArrayEquals(expected, merge);

            int[] quick = original.clone();
            QuickSort.sort(quick, new Metrics(), new Random(10));
            assertArrayEquals(expected, quick);
        }
    }

    @Test
    void quickSortDepthIsBoundedOnSortedLargeInput() {
        int n = 100_000;
        int[] input = InputGenerator.generate(n, InputType.SORTED, 0);
        Metrics metrics = new Metrics();
        QuickSort.sort(input, metrics, new Random(42));

        int allowedDepth = (int) (2 * (Math.log(n) / Math.log(2)));
        assertTrue(metrics.maxDepth() <= allowedDepth);
    }

    @Test
    void quickSelectMatchesSortedKOnRandomArrays() {
        Random random = new Random(3);
        for (int t = 0; t < 100; t++) {
            int n = 1 + random.nextInt(500);
            int[] input = randomArray(random, n);
            int[] sorted = input.clone();
            Arrays.sort(sorted);
            int k = random.nextInt(n);
            assertEquals(sorted[k], QuickSelect.select(input, k, new Metrics()));
        }
    }

    @Test
    void quickSelectRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.select(new int[0], 0, new Metrics()));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.select(new int[] {1, 2}, -1, new Metrics()));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.select(new int[] {1, 2}, 2, new Metrics()));
    }

    private static int[] randomArray(Random random, int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = random.nextInt(2_000) - 1_000;
        }
        return a;
    }
}
