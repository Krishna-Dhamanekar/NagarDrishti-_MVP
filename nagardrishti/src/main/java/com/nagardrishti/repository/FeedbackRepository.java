package com.nagardrishti.repository;
import com.nagardrishti.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    // By related entity
    List<Feedback> findByRelatedIdOrderByCreatedAtDesc(String relatedId);
    List<Feedback> findByTypeAndRelatedIdOrderByCreatedAtDesc(String type, String relatedId);

    // By user
    List<Feedback> findByUserIdOrderByCreatedAtDesc(String userId);

    // Admin
    List<Feedback> findAllByOrderByCreatedAtDesc();
    List<Feedback> findByStatusOrderByCreatedAtDesc(String status);
    List<Feedback> findByTypeOrderByCreatedAtDesc(String type);

    // Count helpers
    long countByRelatedId(String relatedId);
    long countByRelatedIdAndStatus(String relatedId, String status);

    // Summary stats per related item
    @Query("SELECT f.relatedId, COUNT(f), SUM(f.upvoteCount) FROM Feedback f WHERE f.relatedId = :relatedId GROUP BY f.relatedId")
    Object[] statsForRelated(String relatedId);
}
