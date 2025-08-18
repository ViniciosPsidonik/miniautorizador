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

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

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

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> getCardBalance(@PathVariable String numeroCartao) {
        BigDecimal saldo = cartaoService.getCardBalance(numeroCartao);
        return new ResponseEntity<>(saldo, HttpStatus.OK);
    }
}
