package com.TaskManagementTool.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="file_attach")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileAttachment {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Long issueId;
	private String fileName;
	private String fileContentType;
	private Long fileSize;
	
	private String storagePath;
	private String cloudId;
	private String uplodedBy;
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileContentType() {
		return fileContentType;
	}
	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getStoragePath() {
		return storagePath;
	}
	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}
	public String getCloudId() {
		return cloudId;
	}
	public void setCloudId(String cloudId) {
		this.cloudId = cloudId;
	}
	public String getUplodedBy() {
		return uplodedBy;
	}
	public void setUplodedBy(String uplodedBy) {
		this.uplodedBy = uplodedBy;
	}
	
	
}
