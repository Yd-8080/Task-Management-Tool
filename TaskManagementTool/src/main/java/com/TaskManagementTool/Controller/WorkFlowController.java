package com.TaskManagementTool.Controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagementTool.Entity.WorkFlow;
import com.TaskManagementTool.Service.WorkFlowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/workflows")
@RequiredArgsConstructor
public class WorkFlowController {
	
	@Autowired
    private WorkFlowService workFlowService;

    @PostMapping("/create")
    public ResponseEntity<WorkFlow> createWork(@RequestBody WorkFlow workFlow) {
        return ResponseEntity.ok(workFlowService.createWorkFlow(workFlow));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<WorkFlow> updateWork(@PathVariable Long id,
                                               @RequestBody WorkFlow workFlow) {
        return ResponseEntity.ok(workFlowService.updateWork(id, workFlow));
    }

    @GetMapping("/all")
    public ResponseEntity<List<WorkFlow>> getAll() {
        return ResponseEntity.ok(workFlowService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkFlow> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workFlowService.getWorkById(id));
    }
    
    @GetMapping("/{id}/transactions")
    public ResponseEntity<Boolean> allowedTransaction(
            @PathVariable Long id,
            @RequestParam String from,
            @RequestParam String to,
            @RequestBody Set<String> userRole) {

        return ResponseEntity.ok(
                workFlowService.isTransactionAllowed(id, from, to, userRole)
        );
    }

}
