package com.TaskManagementTool.Service;

import java.sql.Date;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.TaskManagementTool.DTO.AuthResponseDTO;
import com.TaskManagementTool.DTO.LoginRequestDTO;
import com.TaskManagementTool.DTO.RegisterRequestDTO;
import com.TaskManagementTool.Entity.UserAuth;
import com.TaskManagementTool.Repository.UserAuthRepository;
import com.TaskManagementTool.Security.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserAuthService {

	@Autowired
	private UserAuthRepository userAuthRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder; 
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private TokenBlockService blockTokenService ;
	
	
	public AuthResponseDTO register(RegisterRequestDTO register) {
		
		Optional<UserAuth> existing = userAuthRepo.findByUserOfficialEmail(register.userOfficialEmail);
		if(existing.isPresent()){
			
			throw new RuntimeException("User Already Exist");
			
		}
		
		UserAuth user = new UserAuth();
		user.setUserName(register.userName);
		user.setUserOfficialEmail(register.userOfficialEmail);
		user.setPassword(passwordEncoder.encode(register.password));
		user.setRole(register.role);
		 
		userAuthRepo.save(user);
		
		String token = jwtUtil.generateToken(user);
		
		return new AuthResponseDTO(token,"User Registered Successfully");
	}
	
	public String login(LoginRequestDTO login) {
		
		UserAuth user = userAuthRepo.findByUserOfficialEmail(login.userOfficialEmail)
										.orElseThrow(()-> new RuntimeException("User not found"));
		
		if(!passwordEncoder.matches(login.password, user.getPassword())) {
		    throw new RuntimeException("Invalid Credential");
		}
		
		String token = jwtUtil.generateToken(user);

	    return token;
		
	}
	
	public void forgotPassword(String userOfficialEmail) {

        UserAuth user = userAuthRepo
                .findByUserOfficialEmail(userOfficialEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(10)
        );

        userAuthRepo.save(user);

        // Correct parameter order
        emailService.sendResetPassword(userOfficialEmail, token);
    }
	
	public void resetPassword(String token, String newPassword) {

        UserAuth user = userAuthRepo.findByResetToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid token"));

        if (user.getResetTokenExpiry()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userAuthRepo.save(user);
    }
	
	public String logout(HttpServletRequest Request) {
		String header = Request.getHeader("Authorization");
		String token = jwtUtil.extractToken(header);
		
		if(token != null) {
			blockTokenService.blockToken(token);
		}
		
		return "Logged out Successfully";
	}
}
