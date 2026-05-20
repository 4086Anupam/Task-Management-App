package com.taskmanager.Task_Management_Application.dto;


import com.taskmanager.Task_Management_Application.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
}
