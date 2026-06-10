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

    List<Scheme> findByActiveTrueAndCreatedAtAfter(LocalDateTime since);

    @Query("SELECT s FROM Scheme s WHERE s.active = true AND (" +
            "LOWER(s.name) LIKE CONCAT('%', :q, '%') OR " +
            "LOWER(s.shortTitle) LIKE CONCAT('%', :q, '%') OR " +
            "LOWER(s.description) LIKE CONCAT('%', :q, '%'))")
    List<Scheme> searchActive(@Param("q") String q);
}