package com.TaskManagementTool.Cloudinary;

public class CloudinaryUploadResponse {

	private String cloudURL;
	private String cloudId;
	
	public CloudinaryUploadResponse(String cloudURL,String cloudId) {
		this.cloudId=cloudId;
		this.cloudURL=cloudURL;	
	}

	public String getCloudURL() {
		return cloudURL;
	}

	public void setCloudURL(String cloudURL) {
		this.cloudURL = cloudURL;
	}

	public String getCloudId() {
		return cloudId;
	}

	public void setCloudId(String cloudId) {
		this.cloudId = cloudId;
	}
	
	
}
