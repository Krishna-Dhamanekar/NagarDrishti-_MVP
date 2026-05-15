package com.nagardrishti.repository;
import com.nagardrishti.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, String> {
    List<Scheme> findByIsActiveTrue();
    List<Scheme> findByCategoryIgnoreCaseAndIsActiveTrue(String category);
}
