package com.vr.miniautorizador.controller;

import com.vr.miniautorizador.dto.TransactionRequestDTO;
import com.vr.miniautorizador.exception.CardNotFoundException;
import com.vr.miniautorizador.exception.InsufficientBalanceException;
import com.vr.miniautorizador.exception.InvalidPasswordException;
import com.vr.miniautorizador.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

        @Mock
        private TransactionService transactionService;

        @InjectMocks
        private TransactionController transactionController;

        private TransactionRequestDTO transactionRequestDTO;

        @BeforeEach
        void setUp() {
                transactionRequestDTO = new TransactionRequestDTO();
                transactionRequestDTO.setNumeroCartao("6549873025634501");
                transactionRequestDTO.setSenhaCartao("1234");
                transactionRequestDTO.setValor(new BigDecimal("10.00"));
        }

        @Test
        void authorizeTransaction_Success() {
                doNothing().when(transactionService).authorizeTransaction(any(), any(), any());

                ResponseEntity<String> response = transactionController.authorizeTransaction(transactionRequestDTO);

                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertEquals("OK", response.getBody());
                verify(transactionService, times(1))
                                .authorizeTransaction(transactionRequestDTO.getNumeroCartao(),
                                                transactionRequestDTO.getSenhaCartao(),
                                                transactionRequestDTO.getValor());
        }

        @Test
        void authorizeTransaction_CardNotFound_ThrowsException() {
                doThrow(new CardNotFoundException("CARTAO_INEXISTENTE"))
                                .when(transactionService).authorizeTransaction(any(), any(), any());

                CardNotFoundException ex = assertThrows(CardNotFoundException.class,
                                () -> transactionController.authorizeTransaction(transactionRequestDTO));

                assertEquals("CARTAO_INEXISTENTE", ex.getMessage());
                verify(transactionService, times(1))
                                .authorizeTransaction(transactionRequestDTO.getNumeroCartao(),
                                                transactionRequestDTO.getSenhaCartao(),
                                                transactionRequestDTO.getValor());
        }

        @Test
        void authorizeTransaction_InvalidPassword_ThrowsException() {
                doThrow(new InvalidPasswordException("SENHA_INVALIDA"))
                                .when(transactionService).authorizeTransaction(any(), any(), any());

                InvalidPasswordException ex = assertThrows(InvalidPasswordException.class,
                                () -> transactionController.authorizeTransaction(transactionRequestDTO));

                assertEquals("SENHA_INVALIDA", ex.getMessage());
                verify(transactionService, times(1))
                                .authorizeTransaction(transactionRequestDTO.getNumeroCartao(),
                                                transactionRequestDTO.getSenhaCartao(),
                                                transactionRequestDTO.getValor());
        }

        @Test
        void authorizeTransaction_InsufficientBalance_ThrowsException() {
                doThrow(new InsufficientBalanceException("SALDO_INSUFICIENTE"))
                                .when(transactionService).authorizeTransaction(any(), any(), any());

                InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class,
                                () -> transactionController.authorizeTransaction(transactionRequestDTO));

                assertEquals("SALDO_INSUFICIENTE", ex.getMessage());
                verify(transactionService, times(1))
                                .authorizeTransaction(transactionRequestDTO.getNumeroCartao(),
                                                transactionRequestDTO.getSenhaCartao(),
                                                transactionRequestDTO.getValor());
        }
}
