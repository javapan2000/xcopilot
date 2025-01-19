package com.xcopilot.investor.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HoldingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Holding getHoldingSample1() {
        return new Holding().id(1L).symbol("symbol1");
    }

    public static Holding getHoldingSample2() {
        return new Holding().id(2L).symbol("symbol2");
    }

    public static Holding getHoldingRandomSampleGenerator() {
        return new Holding().id(longCount.incrementAndGet()).symbol(UUID.randomUUID().toString());
    }
}
