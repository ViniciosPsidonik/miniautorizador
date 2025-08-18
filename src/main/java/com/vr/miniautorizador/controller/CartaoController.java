package com.vr.miniautorizador.controller;

import com.vr.miniautorizador.dto.CartaoRequestDTO;
import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.service.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.math.BigDecimal;

/**
 * REST controller responsável por operações relacionadas a cartões,
 * incluindo criação de cartão e consulta de saldo.
 */
@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    /**
     * Cria um novo cartão com saldo inicial padrão.
     *
     * @param cartaoRequestDTO DTO contendo número do cartão e senha
     * @return DTO do cartão criado com status HTTP 201 (Created)
     */
    @PostMapping
    public ResponseEntity<CartaoRequestDTO> createCard(@Valid @RequestBody CartaoRequestDTO cartaoRequestDTO) {
        Cartao cartao = new Cartao();
        cartao.setNumeroCartao(cartaoRequestDTO.getNumeroCartao());
        cartao.setSenha(cartaoRequestDTO.getSenha());
        Cartao novoCartao = cartaoService.createCard(cartao);

        CartaoRequestDTO responseDTO = new CartaoRequestDTO();
        responseDTO.setNumeroCartao(novoCartao.getNumeroCartao());
        responseDTO.setSenha(novoCartao.getSenha());

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Retorna o saldo atual do cartão informado.
     *
     * @param numeroCartao número do cartão informado na URL
     * @return saldo disponível no cartão com status HTTP 200 (OK)
     */
    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> getCardBalance(@PathVariable String numeroCartao) {
        BigDecimal saldo = cartaoService.getCardBalance(numeroCartao);
        return new ResponseEntity<>(saldo, HttpStatus.OK);
    }
}
