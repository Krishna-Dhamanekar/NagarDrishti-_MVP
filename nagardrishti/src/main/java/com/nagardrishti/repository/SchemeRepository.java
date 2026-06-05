package com.nagardrishti.repository;

import com.nagardrishti.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, String> {

    List<Scheme> findByActiveTrue();

    List<Scheme> findByActiveTrueAndLevelIgnoreCase(String level);

    // Full-text search on name / shortTitle / description
    @Query("SELECT s FROM Scheme s WHERE s.active = true AND " +
            "(LOWER(s.name) LIKE %:q% OR LOWER(s.shortTitle) LIKE %:q% OR " +
            "LOWER(s.description) LIKE %:q% OR LOWER(s.schemeFor) LIKE %:q%)")
    List<Scheme> searchActive(@Param("q") String q);

    // ── NEW: fetch schemes added after a given timestamp ─────────────────────
    List<Scheme> findByActiveTrueAndCreatedAtAfter(LocalDateTime since);
}