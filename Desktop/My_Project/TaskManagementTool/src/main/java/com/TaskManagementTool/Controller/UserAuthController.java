package com.TaskManagementTool.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagementTool.DTO.AuthResponseDTO;
import com.TaskManagementTool.DTO.LoginRequestDTO;
import com.TaskManagementTool.DTO.RegisterRequestDTO;
import com.TaskManagementTool.Service.UserAuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-auth")
@RequiredArgsConstructor
public class UserAuthController {

	@Autowired
	private UserAuthService userAuthService;
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponseDTO>register(@RequestBody RegisterRequestDTO register){
		return ResponseEntity.ok(userAuthService.register(register));
	}
	
	@PostMapping("/login")
	public ResponseEntity<String>login(@RequestBody LoginRequestDTO login){
		return ResponseEntity.ok(userAuthService.login(login));
	}
	
	@PostMapping("/forgot-password")
	public ResponseEntity<String>fogotPassword(@RequestParam String userOfficialEmail){
		userAuthService.forgotPassword(userOfficialEmail);
		return ResponseEntity.ok("Reset Email got sent over your Email");
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<String>resetPassword(@RequestParam String token,@RequestParam String newPassword){
		userAuthService.resetPassword(token, newPassword);
		return ResponseEntity.ok("Password Reset Successfully");
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String>logout(HttpServletRequest request){
		return ResponseEntity.ok(userAuthService.logout(request));
	}
}
 