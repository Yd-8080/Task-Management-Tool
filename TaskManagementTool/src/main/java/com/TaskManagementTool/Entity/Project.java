package com.TaskManagementTool.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    private String ownerEmail;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_members",
        joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "member_email")
    private Set<String> memberEmails = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public Set<String> getMemberEmails() {
		return memberEmails;
	}

	public void setMemberEmails(Set<String> memberEmails) {
		this.memberEmails = memberEmails;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}

