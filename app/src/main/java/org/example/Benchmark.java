package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static final int[] SIZES = {1_000, 10_000, 100_000, 1_000_000};
    private static final int RUNS = 5;

    public static void main(String[] args) throws IOException {
        Path output = Path.of("results.csv");
        run(output);
        System.out.println("Wrote " + output.toAbsolutePath());
    }

    public static void run(Path output) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(output, StandardCharsets.UTF_8))) {
            writer.println("algorithm,input,n,time_ms,comparisons,max_depth");
            for (String algorithm : List.of("MergeSort", "QuickSort", "QuickSelect")) {
                for (InputType type : InputType.values()) {
                    for (int n : SIZES) {
                        Result median = medianRun(algorithm, type, n);
                        writer.printf(Locale.US, "%s,%s,%d,%.3f,%d,%d%n",
                                algorithm, type.csvName(), n, median.timeMs(), median.comparisons(), median.maxDepth());
                    }
                }
            }
        }
    }

    private static Result medianRun(String algorithm, InputType type, int n) {
        List<Result> results = new ArrayList<>();
        for (int run = 0; run < RUNS; run++) {
            int[] input = InputGenerator.generate(n, type, 10_000L * n + run);
            Metrics metrics = new Metrics();

            switch (algorithm) {
                case "MergeSort" -> MergeSort.sort(input, metrics);
                case "QuickSort" -> QuickSort.sort(input, metrics, new Random(31L * n + run));
                case "QuickSelect" -> QuickSelect.select(input, n / 2, metrics);
                default -> throw new IllegalArgumentException("unknown algorithm: " + algorithm);
            }

            results.add(new Result(metrics.elapsedMillis(), metrics.comparisons(), metrics.maxDepth()));
        }

        results.sort(Comparator.comparingDouble(Result::timeMs));
        return results.get(results.size() / 2);
    }

    private record Result(double timeMs, long comparisons, int maxDepth) {
    }
}
