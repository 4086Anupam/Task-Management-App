package com.taskmanager.Task_Management_Application.dto;


import lombok.Data;

@Data
public class SignupRequest {

    private String name;
    private String email;
    private String password;
}
