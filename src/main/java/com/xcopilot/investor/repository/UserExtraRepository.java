package com.xcopilot.investor.repository;

import com.xcopilot.investor.domain.UserExtra;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserExtra entity.
 */
@Repository
public interface UserExtraRepository extends JpaRepository<UserExtra, Long> {
    default Optional<UserExtra> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserExtra> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserExtra> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select userExtra from UserExtra userExtra left join fetch userExtra.user",
        countQuery = "select count(userExtra) from UserExtra userExtra"
    )
    Page<UserExtra> findAllWithToOneRelationships(Pageable pageable);

    @Query("select userExtra from UserExtra userExtra left join fetch userExtra.user")
    List<UserExtra> findAllWithToOneRelationships();

    @Query("select userExtra from UserExtra userExtra left join fetch userExtra.user where userExtra.id =:id")
    Optional<UserExtra> findOneWithToOneRelationships(@Param("id") Long id);
}
