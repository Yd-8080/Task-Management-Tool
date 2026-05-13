package com.TaskManagementTool.Entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="work_flows")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkFlow {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true)
	private String name;
	
	@Column(length=2000)
	private String workDescription;
	
	@OneToMany(mappedBy="workFlow",cascade=CascadeType.ALL,orphanRemoval=true)
	private List<WorkFlowTransaction>transaction=new ArrayList<>();

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

	public List<WorkFlowTransaction> getTransaction() {
		return transaction;
	}

	public void setTransaction(List<WorkFlowTransaction> transaction) {
		this.transaction = transaction;
	}

	public String getWorkDescription() {
		return workDescription;
	}

	public void setWorkDescription(String workDescription) {
		this.workDescription = workDescription;
	}
	
	
	
}
