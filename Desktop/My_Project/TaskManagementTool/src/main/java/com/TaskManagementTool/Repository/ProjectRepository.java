package com.TaskManagementTool.Repository;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.TaskManagementTool.Entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByMemberEmailsContaining(String email);
    List<Project> findByOwnerEmail(String email);
}