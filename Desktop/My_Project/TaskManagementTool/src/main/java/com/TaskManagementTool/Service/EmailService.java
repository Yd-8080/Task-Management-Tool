package com.TaskManagementTool.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	public void sendResetPassword(String to, String token) {
		String resetLink = "http://localhost:8080/user-auth/reset-password?token="+token;
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Reset Your password");
		message.setText("Click the link to reset the password:\n"+resetLink);
		
		mailSender.send(message);
	}
	// ✅ ADDED - notify developer when issue is assigned
    public void sendIssueAssigned(String to, String issueTitle, String issueKey, String assignedBy) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Issue Assigned: " + issueKey);
        message.setText(
            "Hello,\n\n" +
            "A new issue has been assigned to you.\n\n" +
            "Issue: " + issueTitle + "\n" +
            "Issue Key: " + issueKey + "\n" +
            "Assigned By: " + assignedBy + "\n\n" +
            "Login to Task Management Tool to view details:\n" +
            "http://localhost:3000\n\n" +
            "Regards,\n" +
            "Task Management Tool"
        );
        mailSender.send(message);
    }

    // ✅ ADDED - notify when issue status changes
    public void sendStatusChanged(String to, String issueTitle, String issueKey,
                                   String oldStatus, String newStatus, String changedBy) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Issue Status Updated: " + issueKey);
        message.setText(
            "Hello,\n\n" +
            "An issue assigned to you has been updated.\n\n" +
            "Issue: " + issueTitle + "\n" +
            "Issue Key: " + issueKey + "\n" +
            "Status Changed: " + oldStatus + " → " + newStatus + "\n" +
            "Changed By: " + changedBy + "\n\n" +
            "Login to Task Management Tool to view details:\n" +
            "http://localhost:3000\n\n" +
            "Regards,\n" +
            "Task Management Tool"
        );
        mailSender.send(message);
    }

    // ✅ ADDED - notify when comment is added
    public void sendCommentAdded(String to, String issueTitle, String issueKey,
                                  String commentBy, String commentBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Comment on: " + issueKey);
        message.setText(
            "Hello,\n\n" +
            "A new comment was added to your issue.\n\n" +
            "Issue: " + issueTitle + "\n" +
            "Issue Key: " + issueKey + "\n" +
            "Comment By: " + commentBy + "\n" +
            "Comment: " + commentBody + "\n\n" +
            "Login to Task Management Tool to view details:\n" +
            "http://localhost:3000\n\n" +
            "Regards,\n" +
            "Task Management Tool"
        );
        mailSender.send(message);
    }
}
