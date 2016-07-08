package com.kreig133.beyond_unsafe.example3;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import uk.co.real_logic.agrona.UnsafeAccess;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class SumBenchmark {

    private static final ThreadLocalRandom THREAD_LOCAL_RANDOM = ThreadLocalRandom.current();
    private byte[] array;

    @Setup
    public void setup() {
        array = new byte[256];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) THREAD_LOCAL_RANDOM.nextInt();
        }
    }


    @Benchmark
    public int benchmarkArray() {
        int checkSum = 0;

        for (int i = 0; i < array.length; i++) {
            checkSum += array[i];
        }
        checkSum %= 256;

        return checkSum;
    }



    private static final long ARRAY_BASE_OFFSET = UnsafeAccess.UNSAFE.arrayBaseOffset(byte[].class);

    @Benchmark
    public int benchmarkUnsafeArray() {
        int checkSum = 0;


        for (int i = 0; i < array.length; i++) {
            checkSum += UnsafeAccess.UNSAFE.getByte(array, ARRAY_BASE_OFFSET + i);
        }
        checkSum %= 256;

        return checkSum;
    }

    @Benchmark
    public int benchmarkUnsafeIntArray() {
        int checkSum = 0;

        for (int i = 0; i < array.length; i += 4) {
            int aLong = UnsafeAccess.UNSAFE.getInt(array, ARRAY_BASE_OFFSET + i);
            checkSum += (byte) aLong;
            checkSum += (byte) (aLong >>= 8);
            checkSum += (byte) (aLong >>= 8);
            checkSum += (byte) (aLong >>= 8);
        }
        checkSum %= 256;

        return checkSum;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SumBenchmark.class.getSimpleName())
                .forks(1)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupIterations(5)
                .mode(Mode.Throughput)
                .measurementIterations(2)
                .measurementTime(TimeValue.seconds(5))
                .build();


        new Runner(opt).run();
    }
}

