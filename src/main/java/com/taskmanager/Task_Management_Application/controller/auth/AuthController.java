package com.taskmanager.Task_Management_Application.controller.auth;

import com.taskmanager.Task_Management_Application.dto.AuthenticationRequest;
import com.taskmanager.Task_Management_Application.dto.AuthenticationResponse;
import com.taskmanager.Task_Management_Application.dto.SignupRequest;
import com.taskmanager.Task_Management_Application.dto.UserDto;
import com.taskmanager.Task_Management_Application.entities.User;
import com.taskmanager.Task_Management_Application.repository.UserRepository;
import com.taskmanager.Task_Management_Application.service.auth.AuthService;
import com.taskmanager.Task_Management_Application.service.jwt.UserService;
import com.taskmanager.Task_Management_Application.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserRepository userRepository;

    private final UserService userService;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(
            @RequestBody SignupRequest signupRequest) {

        if (authService.hasUserWithEmail(
                signupRequest.getEmail())) {

            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body("User already exists with this email");
        }

        UserDto createdUserDto =
                authService.signupUser(signupRequest);

        if (createdUserDto == null) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User not created");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUserDto);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(
            @RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        try {

            authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );

            // extra

            final UserDetails userDetails =
                    userService.userDetailsService()
                            .loadUserByUsername(
                                    authenticationRequest.getEmail()
                            );

            Optional<User> optionalUser =
                    userRepository.findByEmail(
                            authenticationRequest.getEmail()
                    );

            final String jwtToken =
                    jwtUtil.generateToken(userDetails);

            AuthenticationResponse authenticationResponse =
                    new AuthenticationResponse();

            if (optionalUser.isPresent()) {

                authenticationResponse.setJwt(jwtToken);

                authenticationResponse.setUserId(
                        optionalUser.get().getId()
                );

                authenticationResponse.setUserRole(
                        optionalUser.get().getRole()
                );
            }

            return authenticationResponse;

        } catch (Exception e) {

//            throw new BadCredentialsException(
//                    "Incorrect email or password"
//            );
            System.out.println(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}