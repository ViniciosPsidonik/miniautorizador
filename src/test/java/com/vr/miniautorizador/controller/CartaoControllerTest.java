package com.vr.miniautorizador.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vr.miniautorizador.dto.CartaoRequestDTO;
import com.vr.miniautorizador.exception.CardAlreadyExistsException;
import com.vr.miniautorizador.exception.CardNotFoundException;
import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.service.CartaoService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(CartaoController.class)
class CartaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartaoService cartaoService;

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
    @WithMockUser(username = "username", password = "password", roles = "USER")
    void createCard_Success() throws Exception {
        when(cartaoService.createCard(any(Cartao.class))).thenReturn(cartao);

        mockMvc.perform(post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartaoRequestDTO))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value("6549873025634501"))
                .andExpect(jsonPath("$.senha").value("1234"));
    }

    @Test
    @WithMockUser(username = "username", password = "password", roles = "USER")
    void createCard_CardAlreadyExists_ReturnsUnprocessableEntity() throws Exception {
        when(cartaoService.createCard(any(Cartao.class)))
                .thenThrow(new CardAlreadyExistsException(cartaoRequestDTO.getNumeroCartao(),
                        cartaoRequestDTO.getSenha()));

        mockMvc.perform(post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartaoRequestDTO))
                .with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.numeroCartao").value("6549873025634501"))
                .andExpect(jsonPath("$.senha").value("1234"));
    }

    @Test
    void createCard_Unauthorized() throws Exception {
        mockMvc.perform(post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartaoRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "username", password = "password", roles = "USER")
    void getCardBalance_Success() throws Exception {
        when(cartaoService.getCardBalance(anyString())).thenReturn(new BigDecimal("495.15"));

        mockMvc.perform(get("/cartoes/{numeroCartao}", "6549873025634501"))
                .andExpect(status().isOk())
                .andExpect(content().string("495.15"));
    }

    @Test
    @WithMockUser(username = "username", password = "password", roles = "USER")
    void getCardBalance_CardNotFound_ReturnsNotFound() throws Exception {
        when(cartaoService.getCardBalance(anyString())).thenThrow(new CardNotFoundException("Cartão inexistente"));

        mockMvc.perform(get("/cartoes/{numeroCartao}", "nonexistent_card"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCardBalance_Unauthorized() throws Exception {
        mockMvc.perform(get("/cartoes/{numeroCartao}", "6549873025634501"))
                .andExpect(status().isUnauthorized());
    }
}
