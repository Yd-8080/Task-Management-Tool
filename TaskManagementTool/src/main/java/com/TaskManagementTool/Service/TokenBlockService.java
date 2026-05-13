package com.TaskManagementTool.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class TokenBlockService {
	
	private final Set<String>blockTokens = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());
	
	public void blockToken(String token) {
		blockTokens.add(token);
	}
	
	public boolean IsBlockToken(String token) {
		return blockTokens.contains(token);
	}

}
 