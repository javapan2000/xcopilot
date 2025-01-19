package com.xcopilot.investor.repository;

import com.xcopilot.investor.domain.Portfolio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Portfolio entity.
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    default Optional<Portfolio> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Portfolio> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Portfolio> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select portfolio from Portfolio portfolio left join fetch portfolio.owner",
        countQuery = "select count(portfolio) from Portfolio portfolio"
    )
    Page<Portfolio> findAllWithToOneRelationships(Pageable pageable);

    @Query("select portfolio from Portfolio portfolio left join fetch portfolio.owner")
    List<Portfolio> findAllWithToOneRelationships();

    @Query("select portfolio from Portfolio portfolio left join fetch portfolio.owner where portfolio.id =:id")
    Optional<Portfolio> findOneWithToOneRelationships(@Param("id") Long id);
}
