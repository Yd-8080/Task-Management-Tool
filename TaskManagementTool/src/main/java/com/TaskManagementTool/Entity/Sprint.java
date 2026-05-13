package com.TaskManagementTool.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.TaskManagementTool.Enum.SprintState;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="sprints")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sprint {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String sprintName;
	private String sprintGoal;
	private LocalDate startDate;
	private LocalDate endDate;
	
	@Enumerated(EnumType.STRING)
	private SprintState sprintState;
	
	private Long projectId;
	
	private LocalDateTime createdAt = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSprintName() {
		return sprintName;
	}

	public void setSprintName(String sprintName) {
		this.sprintName = sprintName;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public SprintState getSprintState() {
		return sprintState;
	}

	public void setSprintState(SprintState sprintState) {
		this.sprintState = sprintState;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getSprintGoal() {
		return sprintGoal;
	}

	public void setSprintGoal(String sprintGoal) {
		this.sprintGoal = sprintGoal;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	
	
	
}

