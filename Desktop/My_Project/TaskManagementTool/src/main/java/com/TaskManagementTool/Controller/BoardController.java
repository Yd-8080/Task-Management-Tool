package com.TaskManagementTool.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagementTool.Entity.Board;
import com.TaskManagementTool.Entity.BoardCard;
import com.TaskManagementTool.Entity.BoardColumn;
import com.TaskManagementTool.Service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	
	@PostMapping
	public ResponseEntity<Board>create(@RequestBody Board board){
		return ResponseEntity.ok(boardService.createBoard(board));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Board>> getBoardById(@PathVariable Long id){
		return ResponseEntity.ok(boardService.getByBoardId(id));
	}
	
	@GetMapping("/{id}/columns")
	public ResponseEntity<List<BoardColumn>> getBoardByColumn(@PathVariable Long boardId){
		return ResponseEntity.ok(boardService.getBoardColumns(boardId));
	}
	
	@GetMapping("/{id}/cards")
	public ResponseEntity<List<BoardCard>> getBoarrdByCards(@PathVariable Long boardId,@PathVariable Long columnId){
		return ResponseEntity.ok(boardService.getBoardByCards(boardId, columnId));
	}
	
	@PostMapping("/{id}/card")
	public ResponseEntity<BoardCard> addCard(@PathVariable Long id,@RequestBody Map<String,Object> body){
		Long columnId= Long.valueOf(String.valueOf(body.get("columnId")));
		Long issueId= Long.valueOf(String.valueOf(body.get("issueId")));
		return ResponseEntity.ok(boardService.addIssueToBoard(id, columnId, issueId));
	}
	@PostMapping("/{id}/column")
	public ResponseEntity<Board> addColumn(@PathVariable Long id, @RequestBody BoardColumn column){
		column.setBoard(boardService.findById(id).orElseThrow(()-> new RuntimeException("Board not found")));
		return ResponseEntity.ok(boardService.createBoard(column.getBoard()));
	}

}
