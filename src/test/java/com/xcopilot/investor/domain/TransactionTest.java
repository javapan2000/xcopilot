package com.xcopilot.investor.domain;

import static com.xcopilot.investor.domain.HoldingTestSamples.*;
import static com.xcopilot.investor.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xcopilot.investor.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transaction.class);
        Transaction transaction1 = getTransactionSample1();
        Transaction transaction2 = new Transaction();
        assertThat(transaction1).isNotEqualTo(transaction2);

        transaction2.setId(transaction1.getId());
        assertThat(transaction1).isEqualTo(transaction2);

        transaction2 = getTransactionSample2();
        assertThat(transaction1).isNotEqualTo(transaction2);
    }

    @Test
    void holdingTest() {
        Transaction transaction = getTransactionRandomSampleGenerator();
        Holding holdingBack = getHoldingRandomSampleGenerator();

        transaction.setHolding(holdingBack);
        assertThat(transaction.getHolding()).isEqualTo(holdingBack);

        transaction.holding(null);
        assertThat(transaction.getHolding()).isNull();
    }
}
