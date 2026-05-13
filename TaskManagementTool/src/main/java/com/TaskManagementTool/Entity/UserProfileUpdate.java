package com.TaskManagementTool.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="profile_updates")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdate {

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserOfficialEmail() {
		return userOfficialEmail;
	}

	public void setUserOfficialEmail(String userOfficialEmail) {
		this.userOfficialEmail = userOfficialEmail;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false,unique=true)
	private String userOfficialEmail; 
	
	private String department;
	private String designation;
	private String organizationName;
	private String active;
	private LocalDateTime createdAt = LocalDateTime.now();
}
