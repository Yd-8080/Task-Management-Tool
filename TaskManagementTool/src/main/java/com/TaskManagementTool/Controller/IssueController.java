package com.TaskManagementTool.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagementTool.DTO.IssueDTO;
import com.TaskManagementTool.Entity.Issue;
import com.TaskManagementTool.Entity.IssueComment;
import com.TaskManagementTool.Entity.Sprint;
import com.TaskManagementTool.Enum.IssueStatus;
import com.TaskManagementTool.Service.IssueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

	// Injecting IssueService automatically
    @Autowired
    private IssueService issueService;

    // API to create a new issue
    @PostMapping("/createIssue")
    public ResponseEntity<IssueDTO> createIssue(@RequestBody IssueDTO issues) {
        // Calls service method to create issue
        return ResponseEntity.ok(issueService.createIssue(issues));
    }

    // API to get issue by ID
    @GetMapping("/{id}")
    public ResponseEntity<IssueDTO> getById(@PathVariable Long id) {
        // Fetch issue using ID
        return ResponseEntity.ok(issueService.getById(id));
    }

    // API to get issues assigned to a specific email
    @GetMapping("/assignee/{email}")
    public ResponseEntity<List<IssueDTO>> getByAssigneeEmail(
            @PathVariable("email") String userOfficialEmail) {
        // Calls service to fetch issues by assignee email
        return ResponseEntity.ok(issueService.getByAssigneeEmail(userOfficialEmail));
    }

    // API to add a comment to an issue
    @PostMapping("/{id}/comment")
    public ResponseEntity<IssueComment> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X_User_Email", required = false) String user) {

        // Extracting comment body from request
        String commentBody = body.get("body");

        // Deciding author (header priority, else request body, else default)
        String author = (user == null)
                ? body.getOrDefault("authorEmail", "system@gmail")
                : user;

        // Calling service to add comment
        return ResponseEntity.ok(issueService.addComment(id, author, commentBody));
    }

    // API to update issue status
    @PatchMapping("/{id}/status")
    public ResponseEntity<IssueDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam IssueStatus issueStatus,
            @RequestHeader(value = "X_User_Email", required = false) String user) {

        // Calls service to update issue status
        return ResponseEntity.ok(issueService.updateIssueStatus(id, issueStatus, user));
    }

    // API to create a new sprint
    @PostMapping("/sprints")
    public ResponseEntity<Sprint> createSprint(@RequestBody Sprint sprint) {
        // Calls service to create sprint
        return ResponseEntity.ok(issueService.createSprint(sprint));
    }

    // API to search issues based on parameters
    @GetMapping("/search")
    public ResponseEntity<List<IssueDTO>> search(@RequestParam Map<String, String> allRequestParams) {
        return ResponseEntity.ok(issueService.search(allRequestParams));
    }
    
    @PatchMapping("/{id}/update")
    public ResponseEntity<IssueDTO> updateIssue(
            @PathVariable Long id,
            @RequestBody IssueDTO issueDTO) {
        return ResponseEntity.ok(issueService.updateIssue(id, issueDTO));
    }

}



















