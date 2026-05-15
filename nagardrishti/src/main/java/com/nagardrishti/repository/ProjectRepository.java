package com.nagardrishti.repository;
import com.nagardrishti.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findByPincode(String pincode);
    List<Project> findByLocationIgnoreCase(String location);
    List<Project> findByDistrictIgnoreCase(String district);
    List<Project> findByCategoryIgnoreCase(String category);
    List<Project> findByStatusIgnoreCase(String status);
}
