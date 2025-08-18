package com.vr.miniautorizador.service.transaction;

import com.vr.miniautorizador.exception.CardNotFoundException;
import com.vr.miniautorizador.exception.InsufficientBalanceException;
import com.vr.miniautorizador.exception.InvalidPasswordException;
import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;
import com.vr.miniautorizador.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CartaoRepository cartaoRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Cartao cartao;

    @BeforeEach
    void setUp() {
        cartao = new Cartao();
        cartao.setNumeroCartao("1234567890123456");
        cartao.setSenha("1234");
        cartao.setSaldo(new BigDecimal("500.00"));
    }

    @Test
    void authorizeTransaction_Success() {
        when(cartaoRepository.findByNumeroCartaoForUpdate(anyString())).thenReturn(Optional.of(cartao));
        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);

        BigDecimal transactionValue = new BigDecimal("100.00");
        transactionService.authorizeTransaction(cartao.getNumeroCartao(), cartao.getSenha(), transactionValue);

        assertEquals(new BigDecimal("400.00"), cartao.getSaldo());
        verify(cartaoRepository, times(1)).findByNumeroCartaoForUpdate(anyString());
        verify(cartaoRepository, times(1)).save(any(Cartao.class));
    }

    @Test
    void authorizeTransaction_CardNotFound_ThrowsException() {
        when(cartaoRepository.findByNumeroCartaoForUpdate(anyString())).thenReturn(Optional.empty());

        CardNotFoundException exception = assertThrows(CardNotFoundException.class, () -> {
            transactionService.authorizeTransaction("nonexistent_card", "1234", new BigDecimal("10.00"));
        });

        assertEquals("CARTAO_INEXISTENTE", exception.getMessage());
        verify(cartaoRepository, times(1)).findByNumeroCartaoForUpdate(anyString());
        verify(cartaoRepository, never()).save(any(Cartao.class));
    }

    @Test
    void authorizeTransaction_InvalidPassword_ThrowsException() {
        when(cartaoRepository.findByNumeroCartaoForUpdate(anyString())).thenReturn(Optional.of(cartao));
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            transactionService.authorizeTransaction(cartao.getNumeroCartao(), "wrong_password",
                    new BigDecimal("10.00"));
        });

        assertEquals("SENHA_INVALIDA", exception.getMessage());
        verify(cartaoRepository, times(1)).findByNumeroCartaoForUpdate(anyString());
        verify(cartaoRepository, never()).save(any(Cartao.class));
    }

    @Test
    void authorizeTransaction_InsufficientBalance_ThrowsException() {
        when(cartaoRepository.findByNumeroCartaoForUpdate(anyString())).thenReturn(Optional.of(cartao));
        BigDecimal largeValue = new BigDecimal("600.00");

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.authorizeTransaction(cartao.getNumeroCartao(), cartao.getSenha(), largeValue);
        });

        assertEquals("SALDO_INSUFICIENTE", exception.getMessage());
        verify(cartaoRepository, times(1)).findByNumeroCartaoForUpdate(anyString());
        verify(cartaoRepository, never()).save(any(Cartao.class));
    }
}
