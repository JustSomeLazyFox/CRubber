package com.foppykitty.debugging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profiler extends Debugger{
    private static final Map<String, Benchmark> benchmarks = new HashMap<>();

    public static final int NANOSECONDS = 0;
    public static final int MICROSECONDS = 1;
    public static final int MILLISECONDS = 2;
    public static final int SECONDS = 3;

    public static void startBenchmark(String name, int unit) {
        if (isProduction) return;
        Benchmark benchmark = benchmarks.get(name);
        if (benchmark == null) {
            benchmark = new Benchmark(unit);
            benchmarks.put(name, benchmark);
            benchmark.start();
        } else Logger.error("Benchmark \"$name\" already started!");
    }

    public static void startBenchmark(String name) {
        if (isProduction) return;
        Benchmark benchmark = benchmarks.get(name);
        if (benchmark == null) {
            benchmark = new Benchmark();
            benchmarks.put(name, benchmark);
            benchmark.start();
        } else Logger.error("Benchmark \"$name\" already started!");
    }

    public static void endBenchmark(String name) {
        if (isProduction) return;
        Benchmark benchmark = benchmarks.get(name);
        if (benchmark == null) Logger.error("Benchmark \"$name\" does not exist!");
        else benchmark.end();
    }

    public static void displayResult(String name) {
        if (isProduction) return;
        Benchmark benchmark = benchmarks.get(name);
        if (benchmark == null) {
            System.err.println("Benchmark \"" + name + "\" does not exist!");
            return;
        }
        long timeTaken = benchmark.endTime - benchmark.startTime;
        int unit = benchmark.unit;
        switch (unit) {
            case NANOSECONDS -> Logger.info("Benchmark \"$name\" took $timeTaken nanoseconds to complete.");
            case MICROSECONDS -> Logger.info("Benchmark \"$name\" took ${timeTaken * 1e-3} microseconds to complete.");
            case MILLISECONDS -> Logger.info("Benchmark \"$name\" took ${timeTaken * 1e-6} milliseconds to complete.");
            case SECONDS -> Logger.info("Benchmark \"$name\" took ${timeTaken * 1e-9} seconds to complete.");
            default -> {
                Logger.warn("The unit specified for recording the benchmark is undefined!");
                Logger.info("Available units are: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS.");
                Logger.info("Defaulting to NANOSECONDS.");
                Logger.info("Benchmark \"$name\" took $timeTaken nanoseconds to complete.");
            }
        }
    }

    public static void displayAllResults() {
        if (isProduction) return;
        benchmarks.forEach((name, _) -> displayResult(name));
    }

    public static double getResult(String name) {
        if (isProduction) return 0;
        Benchmark benchmark = benchmarks.get(name);
        if (benchmark == null) {
            System.err.println("Benchmark \"" + name + "\" does not exist!");
            return 0;
        }
        long timeTaken = benchmark.endTime - benchmark.startTime;
        int unit = benchmark.unit;
        return switch (unit) {
            case NANOSECONDS -> timeTaken;
            case MICROSECONDS -> timeTaken * 1e-3;
            case MILLISECONDS -> timeTaken * 1e-6;
            case SECONDS -> timeTaken * 1e-9;
            default -> {
                Logger.warn("The unit specified for recording the benchmark is undefined!");
                Logger.info("Available units are: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS.");
                Logger.info("Defaulting to NANOSECONDS.");
                yield timeTaken;
            }
        };
    }

    public static ArrayList<Double> getAllResults() {
        if (isProduction) return new ArrayList<>();
        ArrayList<Double> results = new ArrayList<>();
        benchmarks.forEach((name, _) -> results.add(getResult(name)));
        return results;
    }

    static class Benchmark {
        long startTime, endTime;
        int unit;

        public Benchmark() {
            this.startTime = -1;
            this.endTime = -1;
            this.unit = NANOSECONDS;
        }

        public Benchmark(int unit) {
            this.startTime = -1;
            this.endTime = -1;
            this.unit = unit;
        }

        public void start() {
            this.startTime = System.nanoTime();
        }

        public void end() {
            this.endTime = System.nanoTime();
        }
    }
}
