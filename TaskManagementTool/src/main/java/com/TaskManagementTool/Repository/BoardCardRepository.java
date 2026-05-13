package com.TaskManagementTool.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagementTool.Entity.BoardCard;

@Repository
public interface BoardCardRepository extends JpaRepository<BoardCard,Long>{

	List<BoardCard>findByBoardIdAndColumnsIdOrderByPosition(Long boardId, Long columnId);
	
	long countByBoardIdAndColumnsId(Long boardId,Long columnId);
	Optional<BoardCard>findByIssueId(Long issueId);
}
