package com.taskmanager.Task_Management_Application.service.project;

import com.taskmanager.Task_Management_Application.dto.ProjectDto;
import com.taskmanager.Task_Management_Application.entities.Project;
import com.taskmanager.Task_Management_Application.entities.User;
import com.taskmanager.Task_Management_Application.repository.ProjectRepository;
import com.taskmanager.Task_Management_Application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public ProjectDto createProject(ProjectDto projectDto) {

        Project project = new Project();

        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());

        Project savedProject = projectRepository.save(project);

        ProjectDto dto = new ProjectDto();

        dto.setId(savedProject.getId());
        dto.setName(savedProject.getName());
        dto.setDescription(savedProject.getDescription());

        return dto;
    }

    @Override
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project Not Found"));

        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());

        Project updatedProject = projectRepository.save(project);

        ProjectDto dto = new ProjectDto();

        dto.setId(updatedProject.getId());
        dto.setName(updatedProject.getName());
        dto.setDescription(updatedProject.getDescription());

        return dto;
    }

    @Override
    public void deleteProject(Long id) {

        projectRepository.deleteById(id);
    }

    @Override
    public void assignMemberToProject(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project Not Found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        project.getMembers().add(user);

        projectRepository.save(project);
    }
}