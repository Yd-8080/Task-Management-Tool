package com.TaskManagementTool.Security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	
	@Autowired
	private JWTAuthenticationFilter jwtFilter;
	
	@Bean
	public AuthenticationManager authentication(AuthenticationConfiguration config)throws Exception {
		
		return config.getAuthenticationManager();
		
	}  
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();   
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
	    config.setAllowedHeaders(List.of("*"));
	    config.setAllowCredentials(true);
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}

	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))  
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/user-auth/**").permitAll()
            .requestMatchers("/api/issues/**").permitAll()  // ✅ ADD temporarily
            .requestMatchers("/api/backlog/**").permitAll() // ✅ ADD temporarily
            .requestMatchers("/api/sprints/**").permitAll() // ✅ ADD temporarily
            .requestMatchers("/api/boards/**").permitAll()   // ✅ ADD
            .requestMatchers("/api/report/**").permitAll()
            .requestMatchers("/api/attachments/**").permitAll()
            .requestMatchers("/api/projects/**").permitAll()
            .requestMatchers("/api/projects/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);;

    return http.build();
	}
}
