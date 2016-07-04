package com.kreig133.beyond_unsafe;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

import static uk.co.real_logic.agrona.UnsafeAccess.UNSAFE;


public class UnsafeExample {

    private static final int ID_OFFSET = 0;
    private static final int DATE_OFFSET = 8;


    @State(Scope.Benchmark)
    public static class BenchmarkState {
        long address = UNSAFE.allocateMemory(3 * 1024);

        {
            long address_mod = address % 8;
            if (address_mod != 0) {
                address += 8 - address_mod;
            }
        }
    }

    @Benchmark
    public void benchMark(BenchmarkState state) {
        for (int i = 0; i < 100; i++) {
            int curStart = i << 4;
            UNSAFE.putLong(state.address + curStart + ID_OFFSET, i);
            UNSAFE.putLong(state.address + curStart + DATE_OFFSET, i);
        }

        long sum = 0;
        long sum2 = 0;
        for (int i = 0; i < 100; i++) {
            sum2 += i * 2;
            int curStart = i << 4;
            sum += UNSAFE.getLong(state.address + curStart + ID_OFFSET);
            sum += UNSAFE.getLong(state.address + curStart + DATE_OFFSET);
        }

        if (sum != sum2) {
            System.out.println(sum);
            System.out.println(sum2);
            throw new AssertionError();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(UnsafeExample.class.getSimpleName())
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
