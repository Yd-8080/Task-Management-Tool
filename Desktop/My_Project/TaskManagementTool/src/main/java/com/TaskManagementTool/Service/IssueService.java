package com.TaskManagementTool.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TaskManagementTool.DTO.IssueDTO;
import com.TaskManagementTool.Entity.Issue;
import com.TaskManagementTool.Entity.IssueComment;
import com.TaskManagementTool.Entity.Label;
import com.TaskManagementTool.Entity.Sprint;
import com.TaskManagementTool.Enum.IssueStatus;
import com.TaskManagementTool.Enum.IssueType;
import com.TaskManagementTool.Repository.IssueRepository;
import com.TaskManagementTool.Repository.LabelRepository;
import com.TaskManagementTool.Repository.SprintRepository;
import com.TaskManagementTool.Service.EmailService; // ✅ ADDED

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueService {

    private final IssueRepository issueRepo;
    private final LabelRepository labelRepo;
    private final SprintRepository sprintRepo;

    @Autowired // ✅ ADDED
    private EmailService emailService;

    public IssueService(IssueRepository issueRepo,
                        LabelRepository labelRepo,
                        SprintRepository sprintRepo) {
        this.issueRepo = issueRepo;
        this.labelRepo = labelRepo;
        this.sprintRepo = sprintRepo;
    }

    private String generateKey(Long id) {
        return "PROJ-" + id;
    }

    @Transactional
    public IssueDTO createIssue(IssueDTO dto) {

        Issue issue = new Issue();

        issue.setIssueTitle(dto.getIssueTitle());
        issue.setIssueDescription(dto.getIssueDescription());
        issue.setIssueType(dto.getIssueType() != null ? dto.getIssueType() : IssueType.TASK);
        issue.setIssueStatus(IssueStatus.OPEN);
        issue.setAssigneeEmail(dto.getAssigneeEmail());
        issue.setReporterEmail(dto.getReporterEmail());
        issue.setPriyority(dto.getPriority());
        issue.setDueDate(dto.getDueDate());
        issue.setProjectId(dto.getProjectId());
        issue.setSprintId(dto.getSprintId());
        issue.setEpicId(dto.getEpicId());
        issue.setAssigneeEmail(dto.assigneeEmail);

        if (dto.getLabels() != null) {
            Set<Label> labels = new HashSet<>();
            for (String name : dto.getLabels()) {
                Label label = labelRepo.findByName(name)
                        .orElseGet(() -> {
                            Label l = new Label();
                            l.setName(name);
                            return labelRepo.save(l);
                        });
                labels.add(label);
            }
            issue.setLabels(labels);
        }

        String issueKey = "PROJ-" + System.currentTimeMillis();
        issue.setIssueKey(issueKey);

        issue = issueRepo.save(issue);
        issue.setIssueKey(generateKey(issue.getId()));
        issue = issueRepo.save(issue);

        // ✅ Send email if assignee is set at creation
        if (issue.getAssigneeEmail() != null && !issue.getAssigneeEmail().isEmpty()) {
            try {
                emailService.sendIssueAssigned(
                    issue.getAssigneeEmail(),
                    issue.getIssueTitle(),
                    issue.getIssueKey(),
                    issue.getReporterEmail() != null ? issue.getReporterEmail() : "System"
                );
            } catch (Exception e) {
                System.err.println("Email failed (createIssue): " + e.getMessage());
            }
        }

        return toDTO(issue);
    }

    @Transactional
    public IssueComment addComment(Long issueId, String authorEmail, String body) {

        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("issue not found"));

        Long id = issue.getId();

        IssueComment comment = new IssueComment();
        comment.setIssueId(id);
        comment.setAuthorEmail(authorEmail);
        comment.setBody(body);

        // ✅ Notify assignee about new comment (if different from commenter)
        if (issue.getAssigneeEmail() != null
                && !issue.getAssigneeEmail().isEmpty()
                && !issue.getAssigneeEmail().equals(authorEmail)) {
            try {
                emailService.sendCommentAdded(
                    issue.getAssigneeEmail(),
                    issue.getIssueTitle(),
                    issue.getIssueKey(),
                    authorEmail,
                    body
                );
            } catch (Exception e) {
                System.err.println("Email failed (addComment): " + e.getMessage());
            }
        }

        return comment;
    }

    @Transactional
    public IssueDTO updateIssueStatus(Long id, IssueStatus newStatus, String performedBy) {

        Issue issue = issueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("issue not found"));

        if (newStatus == null) {
            throw new RuntimeException("Status can not be null");
        }

        String oldStatus = issue.getIssueStatus() != null
            ? issue.getIssueStatus().name()
            : "UNKNOWN";

        issue.setIssueStatus(newStatus);
        issueRepo.save(issue);

        // ✅ Notify assignee about status change
        if (issue.getAssigneeEmail() != null && !issue.getAssigneeEmail().isEmpty()) {
            try {
                emailService.sendStatusChanged(
                    issue.getAssigneeEmail(),
                    issue.getIssueTitle(),
                    issue.getIssueKey(),
                    oldStatus,
                    newStatus.name(),
                    performedBy != null ? performedBy : "System"
                );
            } catch (Exception e) {
                System.err.println("Email failed (updateStatus): " + e.getMessage());
            }
        }

        return toDTO(issue);
    }

    public List<IssueDTO> search(Map<String, String> filters) {

        if (filters.containsKey("assignee")) {
            return issueRepo.findByAssigneeEmail(filters.get("assignee"))
                    .stream().map(this::toDTO).collect(Collectors.toList());
        }

        if (filters.containsKey("projectId")) {
            return issueRepo.findByProjectId(Long.valueOf(filters.get("projectId")))
                    .stream().map(this::toDTO).collect(Collectors.toList());
        }

        if (filters.containsKey("sprint")) {
            return issueRepo.findBySprintId(Long.valueOf(filters.get("sprint")))
                    .stream().map(this::toDTO).collect(Collectors.toList());
        }

        if (filters.containsKey("status")) {
            IssueStatus status = IssueStatus.valueOf(filters.get("status").toUpperCase());
            return issueRepo.findByIssueStatus(status)
                    .stream().map(this::toDTO).collect(Collectors.toList());
        }

        return issueRepo.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Sprint createSprint(Sprint sprint) {
        return sprintRepo.save(sprint);
    }

    public IssueDTO getById(Long id) {
        Issue issue = issueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        return toDTO(issue);
    }

    public List<IssueDTO> getByAssigneeEmail(String email) {
        return issueRepo.findByAssigneeEmail(email)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private IssueDTO toDTO(Issue issue) {

        IssueDTO dto = new IssueDTO();

        dto.setId(issue.getId());
        dto.setIssueKey(issue.getIssueKey());
        dto.setIssueTitle(issue.getIssueTitle());
        dto.setIssueDescription(issue.getIssueDescription());
        dto.setIssueType(issue.getIssueType());
        dto.setIssueStatus(issue.getIssueStatus());
        dto.setAssigneeEmail(issue.getAssigneeEmail());
        dto.setReporterEmail(issue.getReporterEmail());
        dto.setPriority(issue.getPriyority());
        dto.setDueDate(issue.getDueDate());
        dto.setCreatedAt(issue.getCreatedAt());
        dto.setUpdatedAt(issue.getUpdatedAt());
        dto.setProjectId(issue.getProjectId());
        dto.setSprintId(issue.getSprintId());
        dto.setEpicId(issue.getEpicId());

        if (issue.getLabels() != null) {
            dto.setLabels(
                    issue.getLabels().stream()
                            .map(Label::getName)
                            .collect(Collectors.toSet())
            );
        }

        return dto;
    }

    @Transactional
    public IssueDTO updateIssue(Long id, IssueDTO dto) {
        Issue issue = issueRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Issue not found"));

        String oldAssignee = issue.getAssigneeEmail();

        if (dto.getIssueTitle() != null) {
            issue.setIssueTitle(dto.getIssueTitle());
        }
        if (dto.getIssueDescription() != null) {
            issue.setIssueDescription(dto.getIssueDescription());
        }
        if (dto.getAssigneeEmail() != null) {
            issue.setAssigneeEmail(dto.getAssigneeEmail());
        }
        if (dto.getPriority() != null) {
            issue.setPriyority(dto.getPriority());
        }
        if (dto.getDueDate() != null) {
            issue.setDueDate(dto.getDueDate());
        }

        issue.setUpdatedAt(java.time.LocalDateTime.now());
        issueRepo.save(issue);

        // ✅ Send email if assignee changed to a new person
        String newAssignee = issue.getAssigneeEmail();
        if (newAssignee != null
                && !newAssignee.isEmpty()
                && !newAssignee.equals(oldAssignee)) {
            try {
                emailService.sendIssueAssigned(
                    newAssignee,
                    issue.getIssueTitle(),
                    issue.getIssueKey(),
                    "Project Manager"
                );
            } catch (Exception e) {
                System.err.println("Email failed (updateIssue): " + e.getMessage());
            }
        }

        return toDTO(issue);
    }
}