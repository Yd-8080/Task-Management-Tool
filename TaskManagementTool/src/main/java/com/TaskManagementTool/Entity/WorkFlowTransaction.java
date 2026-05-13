package com.TaskManagementTool.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="workflow_Transations")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkFlowTransaction {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String fromStatus;
	private String toStatus;
	private String actionName;
	private String allowedRole;
	
	@ManyToOne()
	@JoinColumn(name="workflow_id ")
	private WorkFlow workFlow;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFromStatus() {
		return fromStatus;
	}
	public void setFromStatus(String fromStatus) {
		this.fromStatus = fromStatus;
	}
	public String getToStatus() {
		return toStatus;
	}
	public void setToStatus(String toStatus) {
		this.toStatus = toStatus;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getAllowedRole() {
		return allowedRole;
	}
	public void setAllowedRole(String allowedRole) {
		this.allowedRole = allowedRole;
	}
	public WorkFlow getWorkFlow() {
		return workFlow;
	}
	public void setWorkFlow(WorkFlow workFlow) {
		this.workFlow = workFlow;
	}
	
}
