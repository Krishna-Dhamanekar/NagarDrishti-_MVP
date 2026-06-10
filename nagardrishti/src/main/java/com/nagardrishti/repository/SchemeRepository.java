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

    // Safely fetches all active schemes
    List<Scheme> findByActiveTrue();

    // Required by the newEligibleSchemesSince method in SchemeService
    List<Scheme> findByActiveTrueAndCreatedAtAfter(LocalDateTime since);

    // FIXED: Removed s.shortTitle and s.schemeFor references
    @Query("SELECT s FROM Scheme s WHERE s.active = true AND (LOWER(s.name) LIKE CONCAT('%', :q, '%') OR LOWER(s.description) LIKE CONCAT('%', :q, '%'))")
    List<Scheme> searchActive(@Param("q") String q);
}