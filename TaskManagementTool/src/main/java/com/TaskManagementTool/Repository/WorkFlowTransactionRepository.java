package com.TaskManagementTool.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagementTool.Entity.WorkFlowTransaction;

@Repository
public interface WorkFlowTransactionRepository extends JpaRepository<WorkFlowTransaction,Long>{

	List<WorkFlowTransaction>findByWorkFlowId(Long workFlowId);

    List<WorkFlowTransaction>findByWorkFlowIdAndFromStatus(Long workFlowId, String fromStatus);
}
