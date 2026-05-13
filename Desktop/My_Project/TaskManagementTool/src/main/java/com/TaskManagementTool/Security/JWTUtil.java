package com.TaskManagementTool.Security;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.TaskManagementTool.Entity.UserAuth;
import com.TaskManagementTool.Enum.Permission;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {
	
	private final Key key;
	private final long validityTime = 12*60*60*1000L;
	
	public JWTUtil() {
		String secret = System.getenv("JWT_SECRET");
		if(secret == null || secret.isEmpty()) {
			secret = "myVeryStrongSecretKeyForJWTAuthentication123456";
		}
		
		key = Keys.hmacShaKeyFor(secret.getBytes());
	}
	
	public String generateToken(UserAuth user) {
		
		Map<String ,Object> claims = new HashMap<>();
		claims.put("role",user.getRole().name());
		
		Set<Permission> permissions = RoleBasedPermissionConfig.getRole_permisstion().get(user.getRole());
//		List<String> permName = permissions == null? new ArrayList<>(): permissions.stream().map(Enum::name).collect(Collectors.toList());
		claims.put("permissions", permissions.stream().map(Enum::name).collect(Collectors.toList()));
		
		Date now = new Date();
		Date expire = new Date(now.getTime()+validityTime);
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(user.getUserOfficialEmail())
				.setIssuedAt(now)
				.setExpiration(expire)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
			
		} catch (JwtException e) {
			return false;
			
		}
	}
	
	public Claims getClaim(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}
	
	public String getUserEmail(String token) {
		return getClaim(token).getSubject();
		
	}
	
	public String extractToken(String header) {
		 if(header != null && header.startsWith("Bearer ")) {
			 return header.substring(7);
		 }
		 
		 return null;
	}
	
}
 