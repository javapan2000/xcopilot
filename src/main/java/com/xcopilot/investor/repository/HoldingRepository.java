package com.xcopilot.investor.repository;

import com.xcopilot.investor.domain.Holding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Holding entity.
 */
@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
    default Optional<Holding> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Holding> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Holding> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select holding from Holding holding left join fetch holding.portfolio",
        countQuery = "select count(holding) from Holding holding"
    )
    Page<Holding> findAllWithToOneRelationships(Pageable pageable);

    @Query("select holding from Holding holding left join fetch holding.portfolio")
    List<Holding> findAllWithToOneRelationships();

    @Query("select holding from Holding holding left join fetch holding.portfolio where holding.id =:id")
    Optional<Holding> findOneWithToOneRelationships(@Param("id") Long id);
}
