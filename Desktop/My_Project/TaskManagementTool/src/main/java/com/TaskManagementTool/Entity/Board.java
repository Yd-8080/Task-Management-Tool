package com.TaskManagementTool.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.TaskManagementTool.Enum.BoardType;

import jakarta.persistence.*;
import lombok.*;

@Entity 
@Table(name="boards")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	@Column(unique=true)
	private String projectKey;
	
	@Enumerated(EnumType.STRING)
	private BoardType boardType;	
	
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@OneToMany(mappedBy="board",cascade=CascadeType.ALL,orphanRemoval=true)
	@OrderBy("position")
	private List<BoardColumn> columns = new  ArrayList<>();
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public BoardType getBoardType() {
		return boardType;
	}

	public void setBoardType(BoardType boardType) {
		this.boardType = boardType;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<BoardColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<BoardColumn> columns) {
		this.columns = columns;
	}
	
	
	
	
}
