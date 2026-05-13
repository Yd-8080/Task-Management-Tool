package com.TaskManagementTool.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagementTool.Service.ReportService;

@RestController
@RequestMapping("/api/report")
public class ReportController {

	@Autowired
	private ReportService reportService;
	
	@GetMapping("/burndownReport/{sprintId}")
	
	public ResponseEntity<Map<String,Object>> getBurnDown(@PathVariable Long sprintId){
		return ResponseEntity.ok(reportService.burnDownData(sprintId));
	}
	
	@GetMapping("/velocityReport/{projectId}")
	public ResponseEntity<Map<String,Object>>getVelocity(@PathVariable Long projectId){
		return ResponseEntity.ok(reportService.velocity(projectId));
	}
	@GetMapping("/sprintReport/{sprintId}")
	public ResponseEntity<Map<String,Object>>getSprintRepot(@PathVariable Long sprintId){
		return ResponseEntity.ok(reportService.sprintReport(sprintId));
		
	}

	@GetMapping("/epicReport/{epicId}")
	public ResponseEntity<Map<String,Object>>getEpicRepot(@PathVariable Long epicId){
		return ResponseEntity.ok(reportService.epicProgessReport(epicId));
		
	}
	@GetMapping("/cumultaive/{sprintId}")
	public ResponseEntity<Map<String,Object>>getCumulativeFlowDataReport(@PathVariable Long sprintId){
		return ResponseEntity.ok(reportService.cumulativeFlow(sprintId));
	}
	@GetMapping("/workLoadReport/{sprintId}")
	public ResponseEntity<Map<String,Object>>getWorkLoadReport(@PathVariable Long sprintId){
		return ResponseEntity.ok(reportService.workLodDistribution(sprintId));
	}

}
