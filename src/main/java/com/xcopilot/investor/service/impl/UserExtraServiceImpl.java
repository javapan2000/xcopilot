package com.xcopilot.investor.service.impl;

import com.xcopilot.investor.domain.UserExtra;
import com.xcopilot.investor.repository.UserExtraRepository;
import com.xcopilot.investor.service.UserExtraService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xcopilot.investor.domain.UserExtra}.
 */
@Service
@Transactional
public class UserExtraServiceImpl implements UserExtraService {

    private static final Logger LOG = LoggerFactory.getLogger(UserExtraServiceImpl.class);

    private final UserExtraRepository userExtraRepository;

    public UserExtraServiceImpl(UserExtraRepository userExtraRepository) {
        this.userExtraRepository = userExtraRepository;
    }

    @Override
    public UserExtra save(UserExtra userExtra) {
        LOG.debug("Request to save UserExtra : {}", userExtra);
        return userExtraRepository.save(userExtra);
    }

    @Override
    public UserExtra update(UserExtra userExtra) {
        LOG.debug("Request to update UserExtra : {}", userExtra);
        return userExtraRepository.save(userExtra);
    }

    @Override
    public Optional<UserExtra> partialUpdate(UserExtra userExtra) {
        LOG.debug("Request to partially update UserExtra : {}", userExtra);

        return userExtraRepository
            .findById(userExtra.getId())
            .map(existingUserExtra -> {
                if (userExtra.getFullName() != null) {
                    existingUserExtra.setFullName(userExtra.getFullName());
                }
                if (userExtra.getPhoneNumber() != null) {
                    existingUserExtra.setPhoneNumber(userExtra.getPhoneNumber());
                }

                return existingUserExtra;
            })
            .map(userExtraRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserExtra> findAll() {
        LOG.debug("Request to get all UserExtras");
        return userExtraRepository.findAll();
    }

    public Page<UserExtra> findAllWithEagerRelationships(Pageable pageable) {
        return userExtraRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserExtra> findOne(Long id) {
        LOG.debug("Request to get UserExtra : {}", id);
        return userExtraRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserExtra : {}", id);
        userExtraRepository.deleteById(id);
    }
}
