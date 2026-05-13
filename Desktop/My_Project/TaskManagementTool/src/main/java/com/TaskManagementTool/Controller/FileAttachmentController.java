package com.TaskManagementTool.Controller;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.TaskManagementTool.Entity.FileAttachment;
import com.TaskManagementTool.Service.AttachmentService;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("api/attachments")
public class FileAttachmentController {

	@Autowired
	private AttachmentService attachmentService;
	
	@GetMapping("/issue/{issueId}")
	public ResponseEntity<List<FileAttachment>> getByIssue(@PathVariable Long issueId) {
	    return ResponseEntity.ok(attachmentService.getFileByIssueId(issueId));
	}
	
	@PostMapping("/upload/{issueId}")
	public ResponseEntity<FileAttachment>upload(@PathVariable Long issueId,
			                                   @RequestParam("File") MultipartFile file,
			                                   @RequestParam String uploadedBy){
		return ResponseEntity.ok(attachmentService.upload(issueId, file, uploadedBy));
	}
	
	@GetMapping("/download/{id}")
	public ResponseEntity<Void>download(@PathVariable Long id){
		
		FileAttachment attachment= attachmentService.getFileById(id);
		
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION,attachment.getStoragePath() ).build();
	}

	@GetMapping("/download/stream/{id}")
	public ResponseEntity<Resource>stream(@PathVariable long id) throws IOException, java.io.IOException{
		FileAttachment attachment= attachmentService.getFileById(id);
		
		URL url= new URL(attachment.getStoragePath());
		InputStream inputStream= url.openStream();
		
		InputStreamResource resource= new InputStreamResource(inputStream);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_LOCATION, "attachment; fileName=\""
				                                                  + attachment.getFileName() +" \"")
				                .contentType(MediaType.parseMediaType(attachment.getFileContentType())).body(resource);	
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String>delete(@PathVariable Long id){
		attachmentService.delete(id);
		return ResponseEntity.ok("file deleted successfully");
	}

}
