package com.xcopilot.investor.service.impl;

import com.xcopilot.investor.domain.Holding;
import com.xcopilot.investor.repository.HoldingRepository;
import com.xcopilot.investor.service.HoldingService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xcopilot.investor.domain.Holding}.
 */
@Service
@Transactional
public class HoldingServiceImpl implements HoldingService {

    private static final Logger LOG = LoggerFactory.getLogger(HoldingServiceImpl.class);

    private final HoldingRepository holdingRepository;

    public HoldingServiceImpl(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    @Override
    public Holding save(Holding holding) {
        LOG.debug("Request to save Holding : {}", holding);
        return holdingRepository.save(holding);
    }

    @Override
    public Holding update(Holding holding) {
        LOG.debug("Request to update Holding : {}", holding);
        return holdingRepository.save(holding);
    }

    @Override
    public Optional<Holding> partialUpdate(Holding holding) {
        LOG.debug("Request to partially update Holding : {}", holding);

        return holdingRepository
            .findById(holding.getId())
            .map(existingHolding -> {
                if (holding.getSymbol() != null) {
                    existingHolding.setSymbol(holding.getSymbol());
                }
                if (holding.getQuantity() != null) {
                    existingHolding.setQuantity(holding.getQuantity());
                }
                if (holding.getAverageCost() != null) {
                    existingHolding.setAverageCost(holding.getAverageCost());
                }
                if (holding.getCurrentPrice() != null) {
                    existingHolding.setCurrentPrice(holding.getCurrentPrice());
                }

                return existingHolding;
            })
            .map(holdingRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Holding> findAll(Pageable pageable) {
        LOG.debug("Request to get all Holdings");
        return holdingRepository.findAll(pageable);
    }

    public Page<Holding> findAllWithEagerRelationships(Pageable pageable) {
        return holdingRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Holding> findOne(Long id) {
        LOG.debug("Request to get Holding : {}", id);
        return holdingRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Holding : {}", id);
        holdingRepository.deleteById(id);
    }
}
