package com.TaskManagementTool.Entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.TaskManagementTool.Enum.IssuePriority;
import com.TaskManagementTool.Enum.IssueStatus;
import com.TaskManagementTool.Enum.IssueType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="issues", indexes= {@Index(name="idx_issue_key",columnList="issueKey"),
								@Index(name="idx_issue_assignee",columnList="assigneeEmail")})

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true,nullable=false)
	private String issueKey;
	
	@Column(nullable=false)
	private String issueTitle;
	
	@Column(length=5000)
	private String issueDescription;
	
	@Enumerated(EnumType.STRING)
	private IssueType issueType;
	
	@Enumerated(EnumType.STRING)
	private IssuePriority priyority;
	
	@Enumerated(EnumType.STRING)
	private IssueStatus issueStatus;
	
	private String assigneeEmail;
	private String reporterEmail;
	
	private Long sprintId;
	private Long epicId;
	private Long ParentIssueId;
	private Long projectId;
	
	@Column(name = "back_log_position")
	private Integer backLogPosition;
	
	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt = LocalDateTime.now();
	private LocalDateTime dueDate;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="issue_label",
				joinColumns= @JoinColumn(name="issue-id"),
				inverseJoinColumns= @JoinColumn(name="label-id"))
	
	private Set<Label>labels = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Label> getLabels() {
		return labels;
	}

	public void setLabels(Set<Label> labels) {
		this.labels = labels;
	}

	public String getIssueKey() {
		return issueKey;
	}

	public void setIssueKey(String issueKey) {
		this.issueKey = issueKey;
	}

	public String getIssueTitle() {
		return issueTitle;
	}

	public void setIssueTitle(String issueTitle) {
		this.issueTitle = issueTitle;
	}

	public String getIssueDescription() {
		return issueDescription;
	}

	public void setIssueDescription(String issueDescription) {
		this.issueDescription = issueDescription;
	}

	public IssueType getIssueType() {
		return issueType;
	}

	public void setIssueType(IssueType issueType) {
		this.issueType = issueType;
	}

	public IssuePriority getPriyority() {
		return priyority;
	}

	public void setPriyority(IssuePriority priyority) {
		this.priyority = priyority;
	}

	public IssueStatus getIssueStatus() {
		return issueStatus;
	}

	public void setIssueStatus(IssueStatus issueStatus) {
		this.issueStatus = issueStatus;
	}

	public String getAssigneeEmail() {
		return assigneeEmail;
	}

	public void setAssigneeEmail(String assigneeEmail) {
		this.assigneeEmail = assigneeEmail;
	}

	public String getReporterEmail() {
		return reporterEmail;
	}

	public void setReporterEmail(String reporterEmail) {
		this.reporterEmail = reporterEmail;
	}

	public Long getSprintId() {
		return sprintId;
	}

	public void setSprintId(Long sprintId) {
		this.sprintId = sprintId;
	}

	public Long getEpicId() {
		return epicId;
	}

	public void setEpicId(Long epicId) {
		this.epicId = epicId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public Long getParentIssueId() {
		return ParentIssueId;
	}

	public void setParentIssueId(Long parentIssueId) {
		ParentIssueId = parentIssueId;
	}

	public Integer getBackLogPosition() {
		return backLogPosition;
	}

	public void setBackLogPosition(Integer backLogPosition) {
		this.backLogPosition = backLogPosition;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	
	
		
}
