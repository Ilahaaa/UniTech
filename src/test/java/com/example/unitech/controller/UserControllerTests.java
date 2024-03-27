package com.example.unitech.controller;

import com.example.unitech.config.JwtUtil;
import com.example.unitech.dto.UserDto;
import com.example.unitech.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.coyote.BadRequestException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
              AuthenticationManager manager;

    @MockBean
    JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    private UserDto userDto;

    @BeforeEach
    public void init() {
        userDto = new UserDto("12345", "password");
    }

    @Test
    public void UserController_RegisterUser_ReturnCreated() throws Exception {
        given(userService.registerUser(any())).willReturn(userDto);

        ResultActions response = mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User registered successfully."));
    }


    @Test
    public void UserController_AuthenticateUser_SuccessfulAuthentication_ReturnToken() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("12345", "password");
        given(manager.authenticate(any())).willReturn(authentication);
        given(jwtUtil.createToken(any())).willReturn("mockedToken");

        UserDto userDto = new UserDto("12345", "password");

        ResultActions response = mockMvc.perform(post("/api/user/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));


        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User signed-in successfully! mockedToken"));
    }


    @Test
    public void UserController_AuthenticateUser_UserNotFound_ReturnNotFound() throws Exception {
        given(manager.authenticate(any())).willThrow(new UsernameNotFoundException("User not found"));

        UserDto userDto = new UserDto("12345", "password");

        ResultActions response = mockMvc.perform(post("/api/user/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User is not found."));

    }

    @Test
    public void UserController_AuthenticateUser_InvalidCredentials_ReturnUnauthorized() throws Exception {
        given(manager.authenticate(any())).willThrow(new BadCredentialsException("Invalid credentials"));

        UserDto userDto = new UserDto("12345", "password");

        ResultActions response = mockMvc.perform(post("/api/user/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string("Invalid credentials."));
    }




}
