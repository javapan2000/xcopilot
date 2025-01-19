package com.xcopilot.investor.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PortfolioTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Portfolio getPortfolioSample1() {
        return new Portfolio().id(1L).name("name1").description("description1");
    }

    public static Portfolio getPortfolioSample2() {
        return new Portfolio().id(2L).name("name2").description("description2");
    }

    public static Portfolio getPortfolioRandomSampleGenerator() {
        return new Portfolio().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
