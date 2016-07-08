package com.kreig133.beyond_unsafe.example2;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

public class BenchRunner {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(VarHandleExample.class.getSimpleName())
                .include(VarHandle2Example.class.getSimpleName())
                .include(UnsafeExample.class.getSimpleName())
                .include(ByteBufferExample.class.getSimpleName())
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
