package com.TaskManagementTool.Client;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TaskManagementTool.Enum.Role;

public interface UserClient {
	
	@GetMapping("api/users/{email}/roles")
	Set<Role>getRoles(@RequestParam String officialEmail);

}
