package com.TaskManagementTool.Entity;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity


@Table(
 name = "issue_comments",
 indexes = {
     @Index(name = "idx_comment_issue", columnList = "issue_id") // Index on issue_id
 }
)


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueComment {

 // Primary key
 @Id

 // Auto-increment ID generation
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 // Foreign key reference to issue
 @Column(name = "issue_id", nullable = false)
 private Long issueId;

 // Email of comment author
 private String authorEmail;

 // Comment content (max length 5000)
 @Column(length = 5000)
 private String body;

 // Default value for created time when using builder
 @Builder.Default
 private LocalDateTime createdAt = LocalDateTime.now();

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public Long getIssueId() {
	return issueId;
}

public void setIssueId(Long issueId) {
	this.issueId = issueId;
}

public String getAuthorEmail() {
	return authorEmail;
}

public void setAuthorEmail(String authorEmail) {
	this.authorEmail = authorEmail;
}

public String getBody() {
	return body;
}

public void setBody(String body) {
	this.body = body;
}

public LocalDateTime getCreatedAt() {
	return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
}
 
 
}