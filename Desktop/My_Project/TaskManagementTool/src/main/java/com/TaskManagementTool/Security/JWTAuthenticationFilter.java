package com.TaskManagementTool.Security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.TaskManagementTool.Service.CustomUserDetailsService;
import com.TaskManagementTool.Service.TokenBlockService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter{

	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private TokenBlockService blockTokenService;
	
	@Autowired
	private CustomUserDetailsService userDetailService;
	
	@Override
	public void doFilterInternal(HttpServletRequest request,
								HttpServletResponse response,
								FilterChain filterChain) throws ServletException, IOException{
		
		String header = request.getHeader("Authorization");
		
		String token = null;
		
		if(header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
		}
		
		if(token != null && blockTokenService.IsBlockToken(token)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		if(StringUtils.hasText(header)&& header.startsWith("Bearer ")) {
			token = header.substring(7);
		}
		
		if(token !=null && jwtUtil.validateToken(token)) {
			String userEamil = jwtUtil.getUserEmail(token);
			
			try {
				UserDetails userDetails = userDetailService.loadUserByUsername(userEamil);
			
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());	
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		
		}catch(Exception e) {
			SecurityContextHolder.clearContext();
			}
		}
		
		filterChain.doFilter(request, response);
	}
}
 