package com.TaskManagementTool.Security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.TaskManagementTool.Enum.Permission;
import com.TaskManagementTool.Enum.Role;

public class RoleBasedPermissionConfig {
	
//	private static final Map<Role,Set<Permission>>role_permisstion = new HashMap<>();
	
	public static Map<Role, Set<Permission>>getRole_permisstion(){
		Map<Role,Set<Permission>>map = new HashMap<>();
		map.put(Role.ADMIN, new HashSet<>(Arrays.asList(Permission.ISSUE_VIEW,
				Permission.ISSUE_EDIT,
				Permission.ISSUE_CREATE,
				Permission.ISSUE_ASSIGN,
				Permission.ISSUE_DELETE,
				Permission.USER_MANAGE,
				Permission.COMMENT_ADD,
				Permission.COMMENT_DELETE)));
		
		map.put(Role.MANAGER, new HashSet<>(Arrays.asList(Permission.ISSUE_VIEW,
				Permission.ISSUE_ASSIGN,
				Permission.ISSUE_CREATE,
				Permission.ISSUE_EDIT,
				Permission.COMMENT_ADD)));
		
		map.put(Role.DEVELOPER, new HashSet<>(Arrays.asList(Permission.ISSUE_VIEW,
				Permission.ISSUE_EDIT,
				Permission.COMMENT_ADD)));
		
		map.put(Role.TESTER, new HashSet<>(Arrays.asList(Permission.ISSUE_VIEW,
				Permission.COMMENT_ADD)));
		
		return map;
		
	}
	
	
}