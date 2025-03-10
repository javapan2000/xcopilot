package com.xcopilot.investor.domain;

import static com.xcopilot.investor.domain.AssertUtils.bigDecimalCompareTo;
import static org.assertj.core.api.Assertions.assertThat;

public class HoldingAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertHoldingAllPropertiesEquals(Holding expected, Holding actual) {
        assertHoldingAutoGeneratedPropertiesEquals(expected, actual);
        assertHoldingAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertHoldingAllUpdatablePropertiesEquals(Holding expected, Holding actual) {
        assertHoldingUpdatableFieldsEquals(expected, actual);
        assertHoldingUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertHoldingAutoGeneratedPropertiesEquals(Holding expected, Holding actual) {
        assertThat(expected)
            .as("Verify Holding auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertHoldingUpdatableFieldsEquals(Holding expected, Holding actual) {
        assertThat(expected)
            .as("Verify Holding relevant properties")
            .satisfies(e -> assertThat(e.getSymbol()).as("check symbol").isEqualTo(actual.getSymbol()))
            .satisfies(e ->
                assertThat(e.getQuantity()).as("check quantity").usingComparator(bigDecimalCompareTo).isEqualTo(actual.getQuantity())
            )
            .satisfies(e ->
                assertThat(e.getAverageCost())
                    .as("check averageCost")
                    .usingComparator(bigDecimalCompareTo)
                    .isEqualTo(actual.getAverageCost())
            )
            .satisfies(e ->
                assertThat(e.getCurrentPrice())
                    .as("check currentPrice")
                    .usingComparator(bigDecimalCompareTo)
                    .isEqualTo(actual.getCurrentPrice())
            );
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertHoldingUpdatableRelationshipsEquals(Holding expected, Holding actual) {
        assertThat(expected)
            .as("Verify Holding relationships")
            .satisfies(e -> assertThat(e.getPortfolio()).as("check portfolio").isEqualTo(actual.getPortfolio()));
    }
}
