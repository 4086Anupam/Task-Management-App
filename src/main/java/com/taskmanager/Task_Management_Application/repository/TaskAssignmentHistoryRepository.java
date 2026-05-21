package com.taskmanager.Task_Management_Application.repository;

import com.taskmanager.Task_Management_Application.entities.TaskAssignmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAssignmentHistoryRepository extends JpaRepository<TaskAssignmentHistory, Long> {
}
