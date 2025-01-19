package com.xcopilot.investor.domain;

import static com.xcopilot.investor.domain.HoldingTestSamples.*;
import static com.xcopilot.investor.domain.PortfolioTestSamples.*;
import static com.xcopilot.investor.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xcopilot.investor.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HoldingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Holding.class);
        Holding holding1 = getHoldingSample1();
        Holding holding2 = new Holding();
        assertThat(holding1).isNotEqualTo(holding2);

        holding2.setId(holding1.getId());
        assertThat(holding1).isEqualTo(holding2);

        holding2 = getHoldingSample2();
        assertThat(holding1).isNotEqualTo(holding2);
    }

    @Test
    void transactionsTest() {
        Holding holding = getHoldingRandomSampleGenerator();
        Transaction transactionBack = getTransactionRandomSampleGenerator();

        holding.addTransactions(transactionBack);
        assertThat(holding.getTransactions()).containsOnly(transactionBack);
        assertThat(transactionBack.getHolding()).isEqualTo(holding);

        holding.removeTransactions(transactionBack);
        assertThat(holding.getTransactions()).doesNotContain(transactionBack);
        assertThat(transactionBack.getHolding()).isNull();

        holding.transactions(new HashSet<>(Set.of(transactionBack)));
        assertThat(holding.getTransactions()).containsOnly(transactionBack);
        assertThat(transactionBack.getHolding()).isEqualTo(holding);

        holding.setTransactions(new HashSet<>());
        assertThat(holding.getTransactions()).doesNotContain(transactionBack);
        assertThat(transactionBack.getHolding()).isNull();
    }

    @Test
    void portfolioTest() {
        Holding holding = getHoldingRandomSampleGenerator();
        Portfolio portfolioBack = getPortfolioRandomSampleGenerator();

        holding.setPortfolio(portfolioBack);
        assertThat(holding.getPortfolio()).isEqualTo(portfolioBack);

        holding.portfolio(null);
        assertThat(holding.getPortfolio()).isNull();
    }
}
