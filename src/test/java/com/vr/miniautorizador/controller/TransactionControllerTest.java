package com.vr.miniautorizador.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vr.miniautorizador.dto.TransactionRequestDTO;
import com.vr.miniautorizador.exception.CardNotFoundException;
import com.vr.miniautorizador.exception.InsufficientBalanceException;
import com.vr.miniautorizador.exception.InvalidPasswordException;
import com.vr.miniautorizador.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TransactionRequestDTO transactionRequestDTO;

    @BeforeEach
    void setUp() {
        transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setNumeroCartao("6549873025634501");
        transactionRequestDTO.setSenhaCartao("1234");
        transactionRequestDTO.setValor(new BigDecimal("10.00"));
    }

    @Test
    @WithMockUser(username = "username", password = "password", roles = "USER")
    void authorizeTransaction_Success() throws Exception {
        doNothing().when(transactionService).authorizeTransaction(anyString(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequestDTO))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));
    }

    @Test
    @WithMockUser(username = "username", password = "password", roles = "USER")
    void authorizeTransaction_CardNotFound_ReturnsUnprocessableEntity() throws Exception {
        doThrow(new CardNotFoundException("CARTAO_INEXISTENTE"))
                .when(transactionService).authorizeTransaction(anyString(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequestDTO))
                .with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("CARTAO_INEXISTENTE"));
    }

    @Test
    @WithMockUser(username = "username", password = "password", roles = "USER")
    void authorizeTransaction_InvalidPassword_ReturnsUnprocessableEntity() throws Exception {
        doThrow(new InvalidPasswordException("SENHA_INVALIDA"))
                .when(transactionService).authorizeTransaction(anyString(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequestDTO))
                .with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SENHA_INVALIDA"));
    }

    @Test
    @WithMockUser(username = "username", password = "password", roles = "USER")
    void authorizeTransaction_InsufficientBalance_ReturnsUnprocessableEntity() throws Exception {
        doThrow(new InsufficientBalanceException("SALDO_INSUFICIENTE"))
                .when(transactionService).authorizeTransaction(anyString(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequestDTO))
                .with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SALDO_INSUFICIENTE"));
    }

    @Test
    void authorizeTransaction_Unauthorized() throws Exception {
        mockMvc.perform(post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequestDTO)))
                .andExpect(status().isForbidden());
    }
}
