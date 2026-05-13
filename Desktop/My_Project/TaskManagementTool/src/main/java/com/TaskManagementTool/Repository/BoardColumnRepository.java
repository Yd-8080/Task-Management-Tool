package com.TaskManagementTool.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagementTool.Entity.Board;
import com.TaskManagementTool.Entity.BoardColumn;

@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn,Long>{

	List<BoardColumn>findByBoardIdOrderByPosition(Long boardId);
}
