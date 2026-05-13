package com.TaskManagementTool.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.TaskManagementTool.Entity.FileAttachment;
import com.TaskManagementTool.Repository.FileAttachmentRepository;
import com.cloudinary.Cloudinary;


@Service
public class AttachmentService {

	@Autowired
	private Cloudinary cloudinary;
	
	@Autowired
	private FileAttachmentRepository attachmentRepo;
	
	public FileAttachment upload(Long issueId,MultipartFile file,String uploadedBy) {
		
		validateFile(file);
		
		try {
			
			Map<String, Object> uploadOption= new HashMap<>();
			uploadOption.put("resource_type", "auto");
			
			Map UploadResult= cloudinary.uploader().upload(file.getBytes(), uploadOption);
			
			
			FileAttachment attach= new FileAttachment();
			
			attach.setIssueId(issueId);
			attach.setFileName(file.getOriginalFilename());
			attach.setFileContentType(file.getContentType());
			attach.setFileSize(file.getSize());
			attach.setStoragePath(UploadResult.get("secure_url").toString());
			attach.setCloudId(UploadResult.get("public_id").toString());
			attach.setUplodedBy(uploadedBy);
			
			return attachmentRepo.save(attach);
			
		} catch (Exception e) {
			throw new RuntimeException("Cloud upload failed");
			
		}	
	}
	

	private void validateFile(MultipartFile file) {
		
		if(file.isEmpty()) {
			throw new RuntimeException("file can not be empty");
		}
		
		long MAX= 5*1024*1024;
		
		if(file.getSize()>MAX) {
			throw new RuntimeException("Max file size is 5MB") ;
			
		}
		
		List<String>allowedFile=Arrays.asList("image/png","image/jpeg","application/pdf","text/plain");
		
		if(!allowedFile.contains(file.getContentType())) {
			throw new RuntimeException("invalid file format");
		}
	}
	
	public List<FileAttachment>getFileByIssueId(Long issueId){
		return attachmentRepo.findByIssueId(issueId);
	}
	
	public FileAttachment getFileById(Long id) {
		return attachmentRepo.findById(id).orElseThrow(()-> new RuntimeException("file not found"));
	}
	
	public void delete(Long id) {
		FileAttachment atch= getFileById(id);
		
		try {
			
			Map<String,Object>options= new HashMap<>();
			options.put("resource_type", "auto");
			
			cloudinary.uploader().destroy(atch.getCloudId(), options);
			attachmentRepo.delete(atch);
				
		} catch (Exception e) {
			
		  throw new RuntimeException("Delete Failed",e);
		
		}
	}
	
}
