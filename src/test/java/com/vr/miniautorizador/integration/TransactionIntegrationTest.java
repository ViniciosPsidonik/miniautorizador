package com.vr.miniautorizador.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vr.miniautorizador.dto.CartaoRequestDTO;
import com.vr.miniautorizador.dto.TransactionRequestDTO;
import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private CartaoRepository cartaoRepository;

        private CartaoRequestDTO cartaoRequestDTO;
        private TransactionRequestDTO transactionRequestDTO;

        @BeforeEach
        void setUp() {
                cartaoRepository.deleteAll(); // Clear the database before each test

                cartaoRequestDTO = new CartaoRequestDTO();
                cartaoRequestDTO.setNumeroCartao("6549873025634501");
                cartaoRequestDTO.setSenha("1234");

                transactionRequestDTO = new TransactionRequestDTO();
                transactionRequestDTO.setNumeroCartao("6549873025634501");
                transactionRequestDTO.setSenhaCartao("1234");
                transactionRequestDTO.setValor(new BigDecimal("10.00"));
        }

        @Test
        void createCardAndAuthorizeTransaction_Success() throws Exception {
                // 1. Create a card
                mockMvc.perform(post("/cartoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartaoRequestDTO))
                                .with(csrf())
                                .with(httpBasic("username", "password")))
                                .andExpect(status().isCreated())
                                .andExpect(content()
                                                .json("{\"numeroCartao\":\"6549873025634501\",\"senha\":\"1234\"}"));

                // 2. Authorize a transaction
                mockMvc.perform(post("/transacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(transactionRequestDTO))
                                .with(csrf())
                                .with(httpBasic("username", "password")))
                                .andExpect(status().isCreated())
                                .andExpect(content().string("OK"));

                // 3. Check the balance
                mockMvc.perform(get("/cartoes/6549873025634501")
                                .with(httpBasic("username", "password")))
                                .andExpect(status().isOk())
                                .andExpect(content().string("490.00"));
        }

        @Test
        void authorizeTransaction_CardNotFound_ReturnsUnprocessableEntity() throws Exception {
                mockMvc.perform(post("/transacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(transactionRequestDTO))
                                .with(csrf())
                                .with(httpBasic("username", "password")))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(content().string("CARTAO_INEXISTENTE"));
        }

        @Test
        void authorizeTransaction_InvalidPassword_ReturnsUnprocessableEntity() throws Exception {
                // Create a card first
                mockMvc.perform(post("/cartoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartaoRequestDTO))
                                .with(csrf())
                                .with(httpBasic("username", "password")));

                transactionRequestDTO.setSenhaCartao("wrong_password"); // Set wrong password

                mockMvc.perform(post("/transacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(transactionRequestDTO))
                                .with(csrf())
                                .with(httpBasic("username", "password")))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(content().string("SENHA_INVALIDA"));
        }

        @Test
        void authorizeTransaction_InsufficientBalance_ReturnsUnprocessableEntity() throws Exception {
                // Create a card first
                mockMvc.perform(post("/cartoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartaoRequestDTO))
                                .with(csrf())
                                .with(httpBasic("username", "password")));

                transactionRequestDTO.setValor(new BigDecimal("600.00")); // Set value greater than initial balance

                mockMvc.perform(post("/transacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(transactionRequestDTO))
                                .with(csrf())
                                .with(httpBasic("username", "password")))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(content().string("SALDO_INSUFICIENTE"));
        }

        @Test
        void createCard_CardAlreadyExists_ReturnsUnprocessableEntity() throws Exception {
                // Create card for the first time
                mockMvc.perform(post("/cartoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartaoRequestDTO))
                                .with(csrf())
                                .with(httpBasic("username", "password")))
                                .andExpect(status().isCreated());

                // Try to create the same card again
                mockMvc.perform(post("/cartoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartaoRequestDTO))
                                .with(csrf())
                                .with(httpBasic("username", "password")))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(content()
                                                .json("{\"numeroCartao\":\"6549873025634501\",\"senha\":\"1234\"}"));
        }

        @Test
        void getBalance_CardNotFound_ReturnsNotFound() throws Exception {
                mockMvc.perform(get("/cartoes/1234567890123456")
                                .with(httpBasic("username", "password")))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string(""));
        }
}
