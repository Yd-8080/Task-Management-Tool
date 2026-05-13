package com.TaskManagementTool.Service;



import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.TaskManagementTool.Entity.Project;
import com.TaskManagementTool.Repository.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepo;

    public Project createProject(Project project, String ownerEmail) {
        project.setOwnerEmail(ownerEmail);
        project.getMemberEmails().add(ownerEmail);
        return projectRepo.save(project);
    }

    public List<Project> getMyProjects(String email) {
        return projectRepo.findByMemberEmailsContaining(email);
    }

    public Project getById(Long id) {
        return projectRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project addMember(Long projectId, String email) {
        Project project = getById(projectId);
        project.getMemberEmails().add(email);
        return projectRepo.save(project);
    }

    public Project removeMember(Long projectId, String email) {
        Project project = getById(projectId);
        project.getMemberEmails().remove(email);
        return projectRepo.save(project);
    }
}