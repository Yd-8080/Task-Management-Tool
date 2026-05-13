package com.TaskManagementTool.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TaskManagementTool.Entity.Sprint;
import com.TaskManagementTool.Service.SprintService;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

	@Autowired
    private SprintService sprintService;

    @PostMapping("/create/{projectId}")
    public ResponseEntity<Sprint> createSprint(
            @PathVariable Long projectId,
            @RequestBody Sprint sprint) {
        sprint.setProjectId(projectId);
        return ResponseEntity.ok(sprintService.createSprint(sprint));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Sprint>> getSprintsByProject(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(sprintService.getSprintsByProjectId(projectId));
    }

    @PutMapping("/start/{sprintId}")
    public ResponseEntity<Sprint> startSprint(@PathVariable Long sprintId) {
        return ResponseEntity.ok(sprintService.startSprint(sprintId));
    }

    @PutMapping("/end/{sprintId}")
    public ResponseEntity<Sprint> endSprint(@PathVariable Long sprintId) {
        return ResponseEntity.ok(sprintService.endSprint(sprintId));
    }
}
