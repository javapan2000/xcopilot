package com.xcopilot.investor.domain;

import static com.xcopilot.investor.domain.PortfolioTestSamples.*;
import static com.xcopilot.investor.domain.UserExtraTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xcopilot.investor.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserExtraTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserExtra.class);
        UserExtra userExtra1 = getUserExtraSample1();
        UserExtra userExtra2 = new UserExtra();
        assertThat(userExtra1).isNotEqualTo(userExtra2);

        userExtra2.setId(userExtra1.getId());
        assertThat(userExtra1).isEqualTo(userExtra2);

        userExtra2 = getUserExtraSample2();
        assertThat(userExtra1).isNotEqualTo(userExtra2);
    }

    @Test
    void portfoliosTest() {
        UserExtra userExtra = getUserExtraRandomSampleGenerator();
        Portfolio portfolioBack = getPortfolioRandomSampleGenerator();

        userExtra.addPortfolios(portfolioBack);
        assertThat(userExtra.getPortfolios()).containsOnly(portfolioBack);
        assertThat(portfolioBack.getOwner()).isEqualTo(userExtra);

        userExtra.removePortfolios(portfolioBack);
        assertThat(userExtra.getPortfolios()).doesNotContain(portfolioBack);
        assertThat(portfolioBack.getOwner()).isNull();

        userExtra.portfolios(new HashSet<>(Set.of(portfolioBack)));
        assertThat(userExtra.getPortfolios()).containsOnly(portfolioBack);
        assertThat(portfolioBack.getOwner()).isEqualTo(userExtra);

        userExtra.setPortfolios(new HashSet<>());
        assertThat(userExtra.getPortfolios()).doesNotContain(portfolioBack);
        assertThat(portfolioBack.getOwner()).isNull();
    }
}
