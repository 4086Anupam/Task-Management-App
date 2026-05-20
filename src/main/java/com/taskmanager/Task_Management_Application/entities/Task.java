package com.taskmanager.Task_Management_Application.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmanager.Task_Management_Application.dto.TaskDto;
import com.taskmanager.Task_Management_Application.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    private String priority;

    private LocalDate dueDate;

    // =====================================
    // User Mapping
    // =====================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    // =====================================
    // Project Mapping
    // =====================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    // =====================================
    // Convert Entity to DTO
    // =====================================

    public TaskDto getTaskDto() {

        TaskDto taskDto = new TaskDto();

        taskDto.setId(id);

        taskDto.setTitle(title);

        taskDto.setDescription(description);

        taskDto.setTaskStatus(taskStatus);

        taskDto.setPriority(priority);

        taskDto.setDueDate(dueDate);

        taskDto.setUserId(user.getId());

        taskDto.setProjectId(project.getId());

        // =========================
        // User DTO
        // =========================

        taskDto.setUser(
                user.getUserDto()
        );

        // =========================
        // Project DTO
        // =========================

        if(project != null){
            taskDto.setProject(
                    project.getProjectDto()
            );
        }

        return taskDto;
    }
}