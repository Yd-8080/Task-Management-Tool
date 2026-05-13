package com.TaskManagementTool.Service;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.TaskManagementTool.Entity.Issue;
import com.TaskManagementTool.Entity.Sprint;
import com.TaskManagementTool.Enum.IssueStatus;
import com.TaskManagementTool.Enum.SprintState;
import com.TaskManagementTool.Repository.IssueRepository;
import com.TaskManagementTool.Repository.SprintRepository;

import jakarta.transaction.Transactional;

@Service
public class SprintService {
	
	@Autowired
	private SprintRepository sprintRepo;

	@Autowired
	private IssueRepository issueRepo;
	
	// ✅ ADD THIS - needed by frontend
    public List<Sprint> getSprintsByProjectId(Long projectId) {
        return sprintRepo.findByProjectId(projectId);
    }
	
	public Sprint createSprint(Sprint sprint) {
		sprint.setSprintState(SprintState.PLANNED);
		return sprintRepo.save(sprint);
		
	}
	
	@Transactional
	public Issue assignIssueToSprint(Long sprintId, Long issueId) {
		Sprint sprint= sprintRepo.findById(sprintId).orElseThrow(()-> new RuntimeException("Sprint not found"));
		
		Issue issue = issueRepo.findById(issueId).orElseThrow(()-> new RuntimeException("Issue not found"));
		
		if(sprint.getSprintState()== SprintState.COMPLETED) {
			throw new RuntimeException("can not add task to completed sprint");
		}
		
		issue.setSprintId(sprintId);
		return issueRepo.save(issue);
	}
	
	@Transactional
	public Sprint startSprint(Long sprintId) {
		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(()-> new RuntimeException("Sprint not found"));
		if(sprint.getSprintState()!= SprintState.PLANNED) {
			throw new RuntimeException("Sprint can not started");
		}
		sprint.setSprintState(SprintState.ACTIVE);
		
		// ✅ FIXED: check startDate not sprintState
        if (sprint.getStartDate() == null) {
            sprint.setStartDate(LocalDate.now());
        }
        
		return sprintRepo.save(sprint);
	}
	
	@Transactional
	public Sprint endSprint(Long sprintId) {
		Sprint sprint= sprintRepo.findById(sprintId).orElseThrow(()-> new RuntimeException("Sprint not found"));
		
		sprint.setSprintState(SprintState.COMPLETED);
		
		if(sprint.getEndDate()==null) {
			sprint.setEndDate(LocalDate.now());
			
		}
		
		List<Issue>issues= issueRepo.findBySprintId(sprintId);
		
		for(Issue i: issues) {
			if(i.getIssueStatus()==IssueStatus.DONE) {
				i.setSprintId(null);
				issueRepo.save(i);
			}
		}
		return sprintRepo.save(sprint);
		
	}
	
	public Map<String, Object>getBurnDownData(Long sprintId){
		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(()-> new RuntimeException("Sprint not found"));
		
		LocalDate start = sprint.getStartDate();
		LocalDate end = sprint.getEndDate()!= null? sprint.getEndDate():LocalDate.now();
		
		List<Issue>issues = issueRepo.findBySprintId(sprintId);
		
		int totalTask = issues.size();
		
		Map<String,Integer>chart = new LinkedHashMap<>();
		
		LocalDate cursor = start;
		
		while(!cursor.isAfter(end)) {
			int completed=(int)issues.stream().filter(i->i.getIssueStatus()==IssueStatus.DONE).count();
			
			int remaining = totalTask-completed;
			
			chart.put(cursor.toString(), remaining);
			cursor = cursor.plusDays(1);
		}
		Map<String,Object>response = new HashMap<>();
		
		response.put("sprintId", sprintId);
		response.put("startDate", start);
		response.put("endDate", end);
		response.put("burnDownData", chart);
		
		return response;
	}
	
}
