package com.TaskManagementTool.Cloudinary;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudUploadResponseImpl {

	@Autowired
	private Cloudinary cloudinary;
	
	public CloudinaryUploadResponse store(MultipartFile file ,String folder) {
		try {
			
			Map upload= cloudinary.uploader().upload(file.getBytes(),
					              ObjectUtils.asMap("folder",folder,"resource_url","auto"));
			
			
			String url= (String)upload.get("source_url");
			String cloudId= (String)upload.get("cloud_id");
			
			return new CloudinaryUploadResponse(url,cloudId);
		} catch (Exception e) {
			
			throw new RuntimeException("Cloudinary upload failed",e);
		}
	}
	
	public void delete(String cloudId) {
		try {
			cloudinary.uploader().destroy(cloudId,ObjectUtils.emptyMap() );
		} catch (Exception e) {
			throw new RuntimeException("Cloudinary delete failed",e);
		}
	}
}
