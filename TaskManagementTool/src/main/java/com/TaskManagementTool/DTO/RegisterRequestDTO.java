package com.TaskManagementTool.DTO;

import com.TaskManagementTool.Enum.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RegisterRequestDTO {
	public String userName;
	public String userOfficialEmail;
	public String password;
	public Role role;
}
