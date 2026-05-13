package com.TaskManagementTool.Service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaskManagementTool.Entity.WorkFlow;
import com.TaskManagementTool.Entity.WorkFlowTransaction;
import com.TaskManagementTool.Repository.WorkFlowRepository;
import com.TaskManagementTool.Repository.WorkFlowTransactionRepository;

@Service
public class WorkFlowService {

	@Autowired
    private WorkFlowRepository workFlowRepo;

    @Autowired
    private WorkFlowTransactionRepository workFlowTransactionRepo;
    
    // CREATE
    @Transactional
    public WorkFlow createWorkFlow(WorkFlow workFlow) {
        for (WorkFlowTransaction t : workFlow.getTransaction()) {
            t.setWorkFlow(workFlow);
        }
        return workFlowRepo.save(workFlow);
    }

 // GET ALL
    public List<WorkFlow> listAll() {
        return workFlowRepo.findAll();
    }

    // GET BY ID
    public WorkFlow getWorkById(Long id) {
        return workFlowRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("workflow not found"));
    }
    
 // UPDATE
    @Transactional
    public WorkFlow updateWork(Long id, WorkFlow update) {

        WorkFlow wf = getWorkById(id);

        wf.setName(update.getName());
        wf.setWorkDescription(update.getWorkDescription());

        wf.getTransaction().clear();

        if (update.getTransaction() != null) {
            for (WorkFlowTransaction t : update.getTransaction()) {
                t.setWorkFlow(wf);
                wf.getTransaction().add(t);
            }
        }

        return workFlowRepo.save(wf);
    }

    // DELETE
    @Transactional
    public void deleteWork(Long id) {
        workFlowRepo.deleteById(id);
    }
    
 // CHECK TRANSITION
    public boolean isTransactionAllowed(Long workflowId, String from, String to, Set<String> userRole) {

        List<WorkFlowTransaction> transactions =
                workFlowTransactionRepo.findByWorkFlowIdAndFromStatus(workflowId, from);

        for (WorkFlowTransaction t : transactions) {

            if (!t.getToStatus().equals(to)) {
                continue;
            }

            if (t.getAllowedRole() == null || t.getAllowedRole().isEmpty()) {
                return true;
            }

            // chhota_safety fix (null check)
            if (userRole != null) {
                for (String role : userRole) {
                    if (t.getAllowedRole().contains(role)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    
}
