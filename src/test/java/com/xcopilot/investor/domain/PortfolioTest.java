package com.xcopilot.investor.domain;

import static com.xcopilot.investor.domain.HoldingTestSamples.*;
import static com.xcopilot.investor.domain.PortfolioTestSamples.*;
import static com.xcopilot.investor.domain.UserExtraTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xcopilot.investor.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PortfolioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Portfolio.class);
        Portfolio portfolio1 = getPortfolioSample1();
        Portfolio portfolio2 = new Portfolio();
        assertThat(portfolio1).isNotEqualTo(portfolio2);

        portfolio2.setId(portfolio1.getId());
        assertThat(portfolio1).isEqualTo(portfolio2);

        portfolio2 = getPortfolioSample2();
        assertThat(portfolio1).isNotEqualTo(portfolio2);
    }

    @Test
    void holdingsTest() {
        Portfolio portfolio = getPortfolioRandomSampleGenerator();
        Holding holdingBack = getHoldingRandomSampleGenerator();

        portfolio.addHoldings(holdingBack);
        assertThat(portfolio.getHoldings()).containsOnly(holdingBack);
        assertThat(holdingBack.getPortfolio()).isEqualTo(portfolio);

        portfolio.removeHoldings(holdingBack);
        assertThat(portfolio.getHoldings()).doesNotContain(holdingBack);
        assertThat(holdingBack.getPortfolio()).isNull();

        portfolio.holdings(new HashSet<>(Set.of(holdingBack)));
        assertThat(portfolio.getHoldings()).containsOnly(holdingBack);
        assertThat(holdingBack.getPortfolio()).isEqualTo(portfolio);

        portfolio.setHoldings(new HashSet<>());
        assertThat(portfolio.getHoldings()).doesNotContain(holdingBack);
        assertThat(holdingBack.getPortfolio()).isNull();
    }

    @Test
    void ownerTest() {
        Portfolio portfolio = getPortfolioRandomSampleGenerator();
        UserExtra userExtraBack = getUserExtraRandomSampleGenerator();

        portfolio.setOwner(userExtraBack);
        assertThat(portfolio.getOwner()).isEqualTo(userExtraBack);

        portfolio.owner(null);
        assertThat(portfolio.getOwner()).isNull();
    }
}
