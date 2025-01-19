package com.xcopilot.investor.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserExtraTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserExtra getUserExtraSample1() {
        return new UserExtra().id(1L).fullName("fullName1").phoneNumber("phoneNumber1");
    }

    public static UserExtra getUserExtraSample2() {
        return new UserExtra().id(2L).fullName("fullName2").phoneNumber("phoneNumber2");
    }

    public static UserExtra getUserExtraRandomSampleGenerator() {
        return new UserExtra()
            .id(longCount.incrementAndGet())
            .fullName(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString());
    }
}
