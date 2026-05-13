package com.TaskManagementTool.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaskManagementTool.Entity.Board;
import com.TaskManagementTool.Entity.BoardCard;
import com.TaskManagementTool.Entity.BoardColumn;
import com.TaskManagementTool.Entity.Issue;
import com.TaskManagementTool.Enum.IssueStatus;
import com.TaskManagementTool.Repository.BoardCardRepository;
import com.TaskManagementTool.Repository.BoardColumnRepository;
import com.TaskManagementTool.Repository.BoardRepository;
import com.TaskManagementTool.Repository.IssueRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepo;
	
	@Autowired
	private BoardColumnRepository boardColumnRepo;
	
	@Autowired
	private BoardCardRepository boardCardRepo;
	
	@Autowired
	private IssueRepository issueRepo;
	
	
	public Board createBoard(Board board) {
		return boardRepo.save(board);
		
		
		}
	
	public Optional<Board>getByBoardId(Long id){
		return boardRepo.findById(id);
	}
	
	public List<BoardColumn>getBoardColumns( Long boardId){
		return boardColumnRepo.findByBoardIdOrderByPosition(boardId);
	}
	
	public List<BoardCard>getBoardByCards(Long boardId,Long columnId){
		return boardCardRepo.findByBoardIdAndColumnsIdOrderByPosition(boardId, columnId);
	}
	
	public Optional<Board>findById(Long id){
		return boardRepo.findById(id);
	}
	
	@Transactional
	public BoardCard addIssueToBoard(Long boardId,Long columnId,Long issueId) {
		
		Issue issue = issueRepo.findById(issueId).orElseThrow(()-> new RuntimeException("Issue not found"));
		
		boardCardRepo.findByIssueId(issueId).ifPresent(boardCardRepo::delete);
		
		BoardColumn column= boardColumnRepo.findById(columnId).orElseThrow(()-> new RuntimeException("column not found"));
		
		if(column.getWipLimits()!=null && column.getWipLimits()>0) {
			long count= boardCardRepo.countByBoardIdAndColumnsId(boardId, columnId);
			if(count >= column.getWipLimits()) {
				throw new RuntimeException("Wip limit exceeded for column:" +column.getName());
			}
		}
		
		
		List<BoardCard>existing= boardCardRepo.findByBoardIdAndColumnsIdOrderByPosition(boardId, columnId);
		int postion = existing.size();
		
		BoardCard card= new BoardCard();
		card.setBoardId(boardId);
		card.setColumns(column);
		card.setIssueId(issueId);
		card.setPosition(postion);
		

		return boardCardRepo.save(card);
	}
	
	@Transactional
	public void moveCards(Long boardId,Long columnId,Long CardId,int position,String performedBy) {
		
		BoardCard card= boardCardRepo.findById(CardId).orElseThrow(()-> new RuntimeException("Cards not avilable"));
		
		
		BoardColumn fromColumn= card.getColumns();
		BoardColumn toColumn= boardColumnRepo.findById(columnId).orElseThrow(()-> new RuntimeException("column not found"));
		
		if(toColumn.getWipLimits()!=null && toColumn.getWipLimits()>0 ) {
			long count =boardCardRepo.countByBoardIdAndColumnsId(boardId, columnId);
			
			if(!Objects.equals(fromColumn.getId(), toColumn.getId()) && count >=toColumn.getWipLimits() ) {
			throw new RuntimeException("Wip limit exceeded for column:"+toColumn.getName());
			}
			
		}
		
		List<BoardCard>fromCards= boardCardRepo.findByBoardIdAndColumnsIdOrderByPosition(boardId,fromColumn.getId() );
		
		for(BoardCard c : fromCards) {
			if(c.getPosition()> card.getPosition() ) {
				c.setPosition(c.getPosition()-1);
				boardCardRepo.save(c);
			}
		}
		
		List<BoardCard>toCards=boardCardRepo.findByBoardIdAndColumnsIdOrderByPosition(boardId,toColumn.getId());
		
		for(BoardCard c : toCards) {
			if(c.getPosition()> card.getPosition() ) {
				c.setPosition(c.getPosition()+1);
				boardCardRepo.save(c);
			}
		}
		
		issueRepo.findById(card.getIssueId()).ifPresent(issue -> updateIssueStatus(issue, IssueStatus.valueOf(toColumn.getStatusKey())));
		
	}
	
	
	private void updateIssueStatus(Issue issue,IssueStatus issueStatus ) {
		if(issueStatus==null) {
			return;
		}
		try {
		
		issue.setIssueStatus(issueStatus);
		issueRepo.save(issue);
		}catch(Exception e) {
			throw new RuntimeException("Invalid statusKey mapping:"+issueStatus,e);
		}
	}
	
	
	@Transactional
	public void recordColumn(Long boardId,Long columnId,List<Long>orderedByCardIds) {
		int position = 0;
		for(Long cid:orderedByCardIds ) {
			BoardCard card=boardCardRepo.findById(cid).orElseThrow(()-> new RuntimeException(" card not found"));
			card.setPosition(position++);
			boardCardRepo.save(card);
		}
		
	}
	@Transactional
	public void startSprint(Long sprintId) {
		
	}
	
	@Transactional
	public void completeSprint(Long sprintId) {
		
	}
}
