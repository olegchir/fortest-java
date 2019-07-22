package org.olegchir;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MyBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @State(Scope.Thread)
    public static class MyState {

        int[] arr = new int[1000];
        Blackhole blackhole;
        IntConsumer testFn = (i) -> {
            blackhole.consume(i * 3 * 8);
        };

        @Setup(Level.Trial)
        public void doSetup() {
            for (int i = 0; i < 1000; i++) {
                arr[i] = i;
            }
            System.out.println("Do Setup");
        }
    }

    @Benchmark
    public void testNativeLoop(Blackhole blackhole, MyState state) {
        state.blackhole = blackhole;
        for (int i = 0, len = state.arr.length; i < len; i++) {
            state.testFn.accept(state.arr[i]);
        }
    }

    @Benchmark
    public void testForEachLoop(Blackhole blackhole, MyState state) {
        state.blackhole = blackhole;
        Arrays.stream(state.arr).forEach(state.testFn);
    }
}
