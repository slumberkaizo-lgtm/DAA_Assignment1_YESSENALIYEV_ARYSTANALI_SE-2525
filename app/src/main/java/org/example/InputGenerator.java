package org.example;

import java.util.Random;

public class InputGenerator {
    public static int[] generate(int n, InputType type, long seed) {
        int[] a = new int[n];
        Random random = new Random(seed);

        switch (type) {
            case RANDOM -> {
                for (int i = 0; i < n; i++) {
                    a[i] = random.nextInt();
                }
            }
            case SORTED -> {
                for (int i = 0; i < n; i++) {
                    a[i] = i;
                }
            }
            case DUPLICATES -> {
                for (int i = 0; i < n; i++) {
                    a[i] = random.nextInt(10);
                }
            }
        }
        return a;
    }
}
