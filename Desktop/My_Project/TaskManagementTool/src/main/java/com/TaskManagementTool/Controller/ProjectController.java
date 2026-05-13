package com.TaskManagementTool.Controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TaskManagementTool.Entity.Project;
import com.TaskManagementTool.Service.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<Project> create(
            @RequestBody Project project,
            @RequestParam String ownerEmail) {
        return ResponseEntity.ok(
            projectService.createProject(project, ownerEmail)
        );
    }

    @GetMapping("/my-projects")
    public ResponseEntity<List<Project>> getMyProjects(
            @RequestParam String email) {
        return ResponseEntity.ok(
            projectService.getMyProjects(email)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @PostMapping("/{id}/add-member")
    public ResponseEntity<Project> addMember(
            @PathVariable Long id,
            @RequestParam String email) {
        return ResponseEntity.ok(projectService.addMember(id, email));
    }

    @DeleteMapping("/{id}/remove-member")
    public ResponseEntity<Project> removeMember(
            @PathVariable Long id,
            @RequestParam String email) {
        return ResponseEntity.ok(projectService.removeMember(id, email));
    }
}