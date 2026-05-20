package com.taskmanager.Task_Management_Application.service.auth;

import com.taskmanager.Task_Management_Application.dto.AuthenticationRequest;
import com.taskmanager.Task_Management_Application.dto.AuthenticationResponse;
import com.taskmanager.Task_Management_Application.dto.SignupRequest;
import com.taskmanager.Task_Management_Application.dto.UserDto;

import com.taskmanager.Task_Management_Application.entities.User;
import com.taskmanager.Task_Management_Application.enums.UserRole;
import com.taskmanager.Task_Management_Application.repository.UserRepository;
import com.taskmanager.Task_Management_Application.utils.JwtUtil;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @PostConstruct
    public void createAnAdminAccount() {

        Optional<User> optionalUser =
                userRepository.findByRole(UserRole.ADMIN);

        if (optionalUser.isEmpty()) {

            User user = new User();

            user.setEmail("admin@test.com");

            user.setName("admin");

            user.setPassword(
                    new BCryptPasswordEncoder().encode("admin")
            );

            user.setRole(UserRole.ADMIN);

            userRepository.save(user);

            System.out.println(
                    "Admin Account Created Successfully"
            );

        } else {

            System.out.println(
                    "Admin Account Already Exists"
            );
        }
    }

    @Override
    public UserDto signupUser(
            SignupRequest signupRequest) {

        User user = new User();

        user.setEmail(signupRequest.getEmail());

        user.setName(signupRequest.getName());

        user.setPassword(
                new BCryptPasswordEncoder()
                        .encode(signupRequest.getPassword())
        );

        user.setRole(UserRole.EMPLOYEE);

        User createdUser = userRepository.save(user);

        return createdUser.getUserDto();
    }

    @Override
    public boolean hasUserWithEmail(String email) {

        return userRepository
                .findFirstByEmail(email)
                .isPresent();
    }

    @Override
    public AuthenticationResponse login(
            AuthenticationRequest request) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow();

        String jwtToken =
                jwtUtil.generateToken(user);

        AuthenticationResponse response =
                new AuthenticationResponse();

        response.setJwt(jwtToken);

        return response;
    }
}