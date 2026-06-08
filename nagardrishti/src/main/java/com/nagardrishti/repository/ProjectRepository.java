package com.nagardrishti.repository;

import com.nagardrishti.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    // Pincode
    List<Project> findByPincode(String pincode);

    // Zone
    List<Project> findByZoneIgnoreCase(String zone);
    @Query("SELECT p FROM Project p WHERE LOWER(p.zone) LIKE %:zone%")
    List<Project> searchByZone(@Param("zone") String zone);

    // District / Ward / Location
    List<Project> findByDistrictIgnoreCase(String district);
    List<Project> findByWardIgnoreCase(String ward);

    // Filters
    List<Project> findByCategoryIgnoreCase(String category);
    List<Project> findByStatusIgnoreCase(String status);
    List<Project> findByDepartmentCodeIgnoreCase(String code);
    List<Project> findBySanctioningAuthorityIgnoreCase(String auth);

    // Smart area match:
    // projects where pincode matches OR (pincode is null AND zone matches)
    @Query("SELECT p FROM Project p WHERE p.pincode = :pincode " +
            "OR (p.pincode IS NULL AND LOWER(p.zone) = LOWER(:zone))")
    List<Project> findByPincodeOrZoneWhenPincodeNull(
            @Param("pincode") String pincode,
            @Param("zone")    String zone);

    // Only zone match (when user has no pincode)
    @Query("SELECT p FROM Project p WHERE LOWER(p.zone) = LOWER(:zone) " +
            "OR LOWER(p.zone) LIKE %:zone%")
    List<Project> findByZoneMatch(@Param("zone") String zone);

    // New projects since timestamp — for notifications
    @Query("SELECT p FROM Project p WHERE p.createdAt > :since " +
            "AND (p.pincode = :pincode OR (p.pincode IS NULL AND LOWER(p.zone) = LOWER(:zone)))")
    List<Project> findNewInArea(
            @Param("since")   LocalDateTime since,
            @Param("pincode") String pincode,
            @Param("zone")    String zone);

    @Query("SELECT p FROM Project p WHERE p.createdAt > :since " +
            "AND LOWER(p.zone) = LOWER(:zone)")
    List<Project> findNewByZone(
            @Param("since") LocalDateTime since,
            @Param("zone")  String zone);
}