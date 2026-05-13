package com.TaskManagementTool.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name="board_cards", indexes= {@Index(columnList = "board_id,column_id,position")})

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCard {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Long boardId;
	private Long issueId;
	
	private Integer position;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="column_id")
	private BoardColumn columns;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

	public Long getIssueId() {
		return issueId;
	}

	public void setIssueId(Long issueId) {
		this.issueId = issueId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public BoardColumn getColumns() {
		return columns;
	}

	public void setColumns(BoardColumn columns) {
		this.columns = columns;
	}
	
	
	
}
