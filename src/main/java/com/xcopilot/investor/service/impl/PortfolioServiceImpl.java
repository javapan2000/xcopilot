package com.xcopilot.investor.service.impl;

import com.xcopilot.investor.domain.Portfolio;
import com.xcopilot.investor.repository.PortfolioRepository;
import com.xcopilot.investor.service.PortfolioService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xcopilot.investor.domain.Portfolio}.
 */
@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {

    private static final Logger LOG = LoggerFactory.getLogger(PortfolioServiceImpl.class);

    private final PortfolioRepository portfolioRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        LOG.debug("Request to save Portfolio : {}", portfolio);
        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio update(Portfolio portfolio) {
        LOG.debug("Request to update Portfolio : {}", portfolio);
        return portfolioRepository.save(portfolio);
    }

    @Override
    public Optional<Portfolio> partialUpdate(Portfolio portfolio) {
        LOG.debug("Request to partially update Portfolio : {}", portfolio);

        return portfolioRepository
            .findById(portfolio.getId())
            .map(existingPortfolio -> {
                if (portfolio.getName() != null) {
                    existingPortfolio.setName(portfolio.getName());
                }
                if (portfolio.getDescription() != null) {
                    existingPortfolio.setDescription(portfolio.getDescription());
                }
                if (portfolio.getTotalValue() != null) {
                    existingPortfolio.setTotalValue(portfolio.getTotalValue());
                }
                if (portfolio.getLastUpdated() != null) {
                    existingPortfolio.setLastUpdated(portfolio.getLastUpdated());
                }

                return existingPortfolio;
            })
            .map(portfolioRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Portfolio> findAll(Pageable pageable) {
        LOG.debug("Request to get all Portfolios");
        return portfolioRepository.findAll(pageable);
    }

    public Page<Portfolio> findAllWithEagerRelationships(Pageable pageable) {
        return portfolioRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Portfolio> findOne(Long id) {
        LOG.debug("Request to get Portfolio : {}", id);
        return portfolioRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Portfolio : {}", id);
        portfolioRepository.deleteById(id);
    }
}
