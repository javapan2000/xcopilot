package com.xcopilot.investor.service.impl;

import com.xcopilot.investor.domain.Transaction;
import com.xcopilot.investor.repository.TransactionRepository;
import com.xcopilot.investor.service.TransactionService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xcopilot.investor.domain.Transaction}.
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        LOG.debug("Request to save Transaction : {}", transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Transaction transaction) {
        LOG.debug("Request to update Transaction : {}", transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> partialUpdate(Transaction transaction) {
        LOG.debug("Request to partially update Transaction : {}", transaction);

        return transactionRepository
            .findById(transaction.getId())
            .map(existingTransaction -> {
                if (transaction.getTransactionType() != null) {
                    existingTransaction.setTransactionType(transaction.getTransactionType());
                }
                if (transaction.getQuantity() != null) {
                    existingTransaction.setQuantity(transaction.getQuantity());
                }
                if (transaction.getPrice() != null) {
                    existingTransaction.setPrice(transaction.getPrice());
                }
                if (transaction.getTimestamp() != null) {
                    existingTransaction.setTimestamp(transaction.getTimestamp());
                }

                return existingTransaction;
            })
            .map(transactionRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> findAll(Pageable pageable) {
        LOG.debug("Request to get all Transactions");
        return transactionRepository.findAll(pageable);
    }

    public Page<Transaction> findAllWithEagerRelationships(Pageable pageable) {
        return transactionRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findOne(Long id) {
        LOG.debug("Request to get Transaction : {}", id);
        return transactionRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Transaction : {}", id);
        transactionRepository.deleteById(id);
    }
}
