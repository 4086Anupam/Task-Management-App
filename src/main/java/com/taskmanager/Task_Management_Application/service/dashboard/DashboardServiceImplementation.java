package com.taskmanager.Task_Management_Application.service.dashboard;

import com.taskmanager.Task_Management_Application.dto.DashboardSummaryDto;
import com.taskmanager.Task_Management_Application.dto.ProjectProgressDto;
import com.taskmanager.Task_Management_Application.dto.TaskDto;
import com.taskmanager.Task_Management_Application.entities.Project;
import com.taskmanager.Task_Management_Application.entities.Task;
import com.taskmanager.Task_Management_Application.enums.TaskStatus;
import com.taskmanager.Task_Management_Application.repository.ProjectRepository;
import com.taskmanager.Task_Management_Application.repository.TaskRepository;
import com.taskmanager.Task_Management_Application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImplementation
        implements DashboardService {

    private final ProjectRepository projectRepository;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    public DashboardSummaryDto getDashboardSummary() {

        DashboardSummaryDto dto =
                new DashboardSummaryDto();

        dto.setTotalProjects(projectRepository.count());

        dto.setTotalTasks(taskRepository.count());

        dto.setCompletedTasks(
                taskRepository.countByTaskStatus(
                        TaskStatus.DONE
                )
        );

        dto.setPendingTasks(
                taskRepository.countByTaskStatus(
                        TaskStatus.TODO
                )
        );

        dto.setInProgressTasks(
                taskRepository.countByTaskStatus(
                        TaskStatus.IN_PROGRESS
                )
        );

        dto.setTotalUsers(userRepository.count());

        return dto;
    }

    @Override
    public List<ProjectProgressDto> getProjectsProgress() {

        List<Project> projects =
                projectRepository.findAll();

        List<ProjectProgressDto> response =
                new ArrayList<>();

        for(Project project : projects){

            long totalTasks =
                    taskRepository.countByProject(project);

            long completedTasks =
                    taskRepository
                            .countByProjectAndTaskStatus(
                                    project,
                                    TaskStatus.DONE
                            );

            long pendingTasks =
                    taskRepository
                            .countByProjectAndTaskStatus(
                                    project,
                                    TaskStatus.TODO
                            );

            double progress = 0;

            if(totalTasks > 0){

                progress =
                        ((double) completedTasks
                                / totalTasks) * 100;
            }

            ProjectProgressDto dto =
                    new ProjectProgressDto();

            dto.setProjectId(project.getId());

            dto.setProjectName(
                    project.getName()
            );

            dto.setTotalTasks(totalTasks);

            dto.setCompletedTasks(completedTasks);

            dto.setPendingTasks(pendingTasks);

            dto.setProgressPercentage(progress);

            response.add(dto);
        }

        return response;
    }

    @Override
    public List<TaskDto> filterTasks(
            TaskStatus status,
            String priority,
            Long assigneeId
    ) {

        List<Task> tasks;

        if(status != null && priority != null){

            tasks =
                    taskRepository
                            .findByTaskStatusAndPriority(
                                    status,
                                    priority
                            );

        } else if(status != null){

            tasks =
                    taskRepository.findByTaskStatus(status);

        } else if(priority != null){

            tasks =
                    taskRepository.findByPriority(priority);

        } else if(assigneeId != null){

            tasks =
                    taskRepository.findByUser_Id(
                            assigneeId
                    );

        } else {

            tasks = taskRepository.findAll();
        }

        return tasks.stream()
                .map(task ->
                        modelMapper.map(
                                task,
                                TaskDto.class
                        )
                )
                .toList();
    }
}