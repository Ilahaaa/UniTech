package com.example.unitech.controller;

import com.example.unitech.config.JwtUtil;
import com.example.unitech.dto.UserDto;
import com.example.unitech.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = TransferController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class TransferControllerTests {


    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    private TransferService transferService;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    public void setup() {

    }

    @Test
    public void testMakeTransfer_SuccessfulTransfer() throws Exception {
        doNothing().when(transferService).makeTransfer(anyLong(), anyLong(), any(BigDecimal.class));

        ResultActions resultActions = mockMvc.perform(post("/api/make-transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .param("senderAccountId", "1")
                .param("recipientAccountId", "2")
                .param("amount", "100"));

        resultActions.andExpect(status().isOk())
                .andExpect(content().string("Transfer successful"));

        verify(transferService, times(1)).makeTransfer(1L, 2L, BigDecimal.valueOf(100));
    }


    @Test
    public void testMakeTransfer_AccountNotFound() throws Exception {

        doThrow(EntityNotFoundException.class).when(transferService).makeTransfer(anyLong(), anyLong(), any(BigDecimal.class));

        ResultActions resultActions = mockMvc.perform(post("/api/make-transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .param("senderAccountId", "1")
                .param("recipientAccountId", "2")
                .param("amount", "100"));

        resultActions.andExpect(status().isNotFound())
                .andExpect(content().string("Account not found"));

        verify(transferService, times(1)).makeTransfer(1L, 2L, BigDecimal.valueOf(100));
    }
    @Test
    public void testMakeTransfer_InvalidAmount() throws Exception {
        doThrow(new IllegalStateException("Invalid amount")).when(transferService)
                .makeTransfer(anyLong(), anyLong(), any(BigDecimal.class));

        ResultActions resultActions = mockMvc.perform(post("/api/make-transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .param("senderAccountId", "1")
                .param("recipientAccountId", "2")
                .param("amount", "-100"));

        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid amount"));
    }


}
