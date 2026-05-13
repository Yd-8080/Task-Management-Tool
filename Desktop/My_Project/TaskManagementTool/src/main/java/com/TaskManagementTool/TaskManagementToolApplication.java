package com.TaskManagementTool;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFeignClients
@SpringBootApplication
public class TaskManagementToolApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementToolApplication.class, args);
	}

}
