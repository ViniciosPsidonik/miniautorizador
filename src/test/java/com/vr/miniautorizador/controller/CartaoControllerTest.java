package com.vr.miniautorizador.controller;

import com.vr.miniautorizador.dto.CartaoRequestDTO;
import com.vr.miniautorizador.exception.CardAlreadyExistsException;
import com.vr.miniautorizador.exception.CardNotFoundException;
import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.service.CartaoService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartaoControllerTest {

    @Mock
    private CartaoService cartaoService;

    @InjectMocks
    private CartaoController cartaoController;

    private Cartao cartao;
    private CartaoRequestDTO cartaoRequestDTO;

    @BeforeEach
    void setUp() {
        cartao = new Cartao();
        cartao.setNumeroCartao("6549873025634501");
        cartao.setSenha("1234");
        cartao.setSaldo(new BigDecimal("500.00"));

        cartaoRequestDTO = new CartaoRequestDTO();
        cartaoRequestDTO.setNumeroCartao("6549873025634501");
        cartaoRequestDTO.setSenha("1234");
    }

    @Test
    void createCard_Success() {
        when(cartaoService.createCard(any(Cartao.class))).thenReturn(cartao);

        ResponseEntity<CartaoRequestDTO> response = cartaoController.createCard(cartaoRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cartaoRequestDTO.getNumeroCartao(), response.getBody().getNumeroCartao());
        assertEquals(cartaoRequestDTO.getSenha(), response.getBody().getSenha());
        verify(cartaoService, times(1)).createCard(any(Cartao.class));
    }

    @Test
    void createCard_CardAlreadyExists_ThrowsException() {
        when(cartaoService.createCard(any(Cartao.class)))
                .thenThrow(new CardAlreadyExistsException(cartaoRequestDTO.getNumeroCartao(),
                        cartaoRequestDTO.getSenha()));

        CardAlreadyExistsException ex = assertThrows(CardAlreadyExistsException.class,
                () -> cartaoController.createCard(cartaoRequestDTO));

        assertEquals("Cartão já existe", ex.getMessage());
        verify(cartaoService, times(1)).createCard(any(Cartao.class));
    }

    @Test
    void getCardBalance_Success() {
        when(cartaoService.getCardBalance(anyString())).thenReturn(new BigDecimal("495.15"));

        ResponseEntity<BigDecimal> response = cartaoController.getCardBalance("6549873025634501");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new BigDecimal("495.15"), response.getBody());
        verify(cartaoService, times(1)).getCardBalance("6549873025634501");
    }

    @Test
    void getCardBalance_CardNotFound_ThrowsException() {
        when(cartaoService.getCardBalance(anyString())).thenThrow(new CardNotFoundException("CARTAO_INEXISTENTE"));

        CardNotFoundException ex = assertThrows(CardNotFoundException.class,
                () -> cartaoController.getCardBalance("nonexistent_card"));

        assertEquals("CARTAO_INEXISTENTE", ex.getMessage());
        verify(cartaoService, times(1)).getCardBalance("nonexistent_card");
    }
}
