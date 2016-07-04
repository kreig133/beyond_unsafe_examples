package com.kreig133.beyond_unsafe;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.HotspotCompilationProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class VarHandleExample {
    private static final VarHandle VH_LONG = MethodHandles.byteBufferViewVarHandle(
            long[].class, java.nio.ByteOrder.nativeOrder());

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ByteBuffer buffer = ByteBuffer.allocateDirect(3 * 1024).alignedSlice(8);
    }

    @Benchmark
    public void benchMark(BenchmarkState state) {
        for (int i = 0; i < 100; i++) {
            int curIndex = i << 4;
            VH_LONG.set(state.buffer, curIndex, i);
            VH_LONG.set(state.buffer, curIndex + 8, i);
        }

        long sum = 0;
        long sum2 = 0;
        for (int i = 0; i < 100; i++) {
            sum2 += i * 2;
            int curIndex = i << 4;
            sum += (long) VH_LONG.get(state.buffer, curIndex);
            sum += (long) VH_LONG.get(state.buffer, curIndex + 8);
        }

        if (sum != sum2) {
            System.out.println(sum);
            System.out.println(sum2);
            throw new AssertionError();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(VarHandleExample.class.getSimpleName())
                .forks(1)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupIterations(5)
                .mode(Mode.Throughput)
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(10))
                .build();

        new Runner(opt).run();
    }
}
