package com.vr.miniautorizador.service;

import com.vr.miniautorizador.exception.CardAlreadyExistsException;
import com.vr.miniautorizador.exception.CardNotFoundException;
import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;
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
class CartaoServiceTest {

    @Mock
    private CartaoRepository cartaoRepository;

    @InjectMocks
    private CartaoService cartaoService;

    private Cartao cartao;

    @BeforeEach
    void setUp() {
        cartao = new Cartao();
        cartao.setNumeroCartao("1234567890123456");
        cartao.setSenha("1234");
        cartao.setSaldo(new BigDecimal("500.00"));
    }

    @Test
    void createCard_Success() {
        when(cartaoRepository.findByNumeroCartao(anyString())).thenReturn(Optional.empty());
        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);

        Cartao createdCartao = cartaoService.createCard(cartao);

        assertNotNull(createdCartao);
        assertEquals("1234567890123456", createdCartao.getNumeroCartao());
        assertEquals(new BigDecimal("500.00"), createdCartao.getSaldo());
        verify(cartaoRepository, times(1)).findByNumeroCartao(anyString());
        verify(cartaoRepository, times(1)).save(any(Cartao.class));
    }

    @Test
    void createCard_CardAlreadyExists_ThrowsException() {
        when(cartaoRepository.findByNumeroCartao(anyString())).thenReturn(Optional.of(cartao));

        CardAlreadyExistsException exception = assertThrows(CardAlreadyExistsException.class, () -> {
            cartaoService.createCard(cartao);
        });

        assertEquals("Cartão já existe", exception.getMessage());
        assertEquals(cartao.getNumeroCartao(), exception.getNumeroCartao());
        assertEquals(cartao.getSenha(), exception.getSenha());
        verify(cartaoRepository, times(1)).findByNumeroCartao(anyString());
        verify(cartaoRepository, never()).save(any(Cartao.class));
    }

    @Test
    void getCardBalance_Success() {
        when(cartaoRepository.findByNumeroCartao(anyString())).thenReturn(Optional.of(cartao));

        BigDecimal balance = cartaoService.getCardBalance("1234567890123456");

        assertNotNull(balance);
        assertEquals(new BigDecimal("500.00"), balance);
        verify(cartaoRepository, times(1)).findByNumeroCartao(anyString());
    }

    @Test
    void getCardBalance_CardNotFound_ThrowsException() {
        when(cartaoRepository.findByNumeroCartao(anyString())).thenReturn(Optional.empty());

        CardNotFoundException exception = assertThrows(CardNotFoundException.class, () -> {
            cartaoService.getCardBalance("nonexistent_card");
        });

        assertEquals("Cartão inexistente", exception.getMessage());
        verify(cartaoRepository, times(1)).findByNumeroCartao(anyString());
    }
}
