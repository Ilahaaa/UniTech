package com.example.unitech.controller;



import com.example.unitech.config.JwtUtil;
import com.example.unitech.dto.UserDto;
import com.example.unitech.entity.User;
import com.example.unitech.service.AccountService;
import com.example.unitech.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)

public class AccountControllerTests {

    @MockBean
    private AccountService accountService;

    @MockBean
    private UserService userService;
    @MockBean
    JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;
    @BeforeEach
    public void init() {
        userDto = new UserDto("12345", "password");
    }


    @Test
    public void testGetActiveAccounts_AuthenticatedUser() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("12345");

        User user = new User();
        user.setPin("12345");
        when(userService.getUserByPin("12345")).thenReturn(user);

        List<BigDecimal> balances = new ArrayList<>();
        balances.add(BigDecimal.valueOf(1000));
        when(accountService.getActiveAccountsBalance(user)).thenReturn(balances);

        ResultActions resultActions = mockMvc.perform(get("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(balances.size()));
    }

    @Test
    public void testGetActiveAccounts_UnauthenticatedUser() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        ResultActions resultActions = mockMvc.perform(get("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetActiveAccounts_UserNotFound() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("nonExistentUser");

        when(userService.getUserByPin("nonExistentUser")).thenReturn(null);

        ResultActions resultActions = mockMvc.perform(get("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }
}
