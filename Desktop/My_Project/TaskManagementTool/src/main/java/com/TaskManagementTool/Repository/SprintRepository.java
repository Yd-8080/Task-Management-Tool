package com.TaskManagementTool.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagementTool.Entity.Sprint;
import com.TaskManagementTool.Enum.SprintState;

@Repository
public interface SprintRepository extends JpaRepository<Sprint,Long>{

	List<Sprint>findByProjectId(Long projectId);
	List<Sprint>findBySprintState(SprintState sprintState);
}
