package com.taskmanager.Task_Management_Application.service.admin;

import com.taskmanager.Task_Management_Application.dto.TaskDto;
import com.taskmanager.Task_Management_Application.dto.UserDto;
import com.taskmanager.Task_Management_Application.entities.Project;
import com.taskmanager.Task_Management_Application.entities.Task;
import com.taskmanager.Task_Management_Application.entities.User;
import com.taskmanager.Task_Management_Application.enums.TaskStatus;
import com.taskmanager.Task_Management_Application.enums.UserRole;
import com.taskmanager.Task_Management_Application.repository.ProjectRepository;
import com.taskmanager.Task_Management_Application.repository.TaskRepository;
import com.taskmanager.Task_Management_Application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    // Get All Employees
    @Override
    public List<UserDto> getUsers() {

        return userRepository.findAll().stream().filter(user -> user.getRole() == UserRole.EMPLOYEE).map(User::getUserDto).collect(Collectors.toList());
    }

    // Create Task
    @Override
    public TaskDto createTask(TaskDto taskDto) {

        User user = userRepository.findById(taskDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(taskDto.getProjectId()).orElseThrow(() -> new RuntimeException("Project not found"));

        Task task = new Task();

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskDto.getTaskStatus());
        task.setPriority(taskDto.getPriority());
        task.setDueDate(taskDto.getDueDate());

        task.setUser(user);
        task.setProject(project);

        Task savedTask = taskRepository.save(task);

        return savedTask.getTaskDto();
    }

    // Get All Tasks
    @Override
    public List<TaskDto> getAllTasks() {

        return taskRepository.findAll().stream().sorted((t1, t2) -> Long.compare(t2.getId(), t1.getId())).map(Task::getTaskDto).toList();
    }

    // Delete Task
    @Override
    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        taskRepository.delete(task);
    }

    // Get Task By id
    @Override
    public TaskDto getTaskById(Long id) {

        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        return task.getTaskDto();
    }

    // Update Task
    @Override
    public TaskDto updateTask(Long id, TaskDto taskDto) {

        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        existingTask.setTitle(taskDto.getTitle());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setPriority(taskDto.getPriority());
        existingTask.setTaskStatus(taskDto.getTaskStatus());
        existingTask.setDueDate(taskDto.getDueDate());

        // Update Assignee
        if (taskDto.getUserId() != null) {

            User user = userRepository.findById(taskDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

            existingTask.setUser(user);
        }

        Task updatedTask = taskRepository.save(existingTask);

        return updatedTask.getTaskDto();
    }

    // Update Task Status
    @Override
    public TaskDto updateTaskStatus(Long id, TaskStatus status) {

        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        existingTask.setTaskStatus(status);

        Task updatedTask = taskRepository.save(existingTask);

        return updatedTask.getTaskDto();
    }
}