package com.vr.miniautorizador.service;

import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    public Cartao criarCartao(Cartao cartao) {
        Optional<Cartao> existingCartao = cartaoRepository.findByNumeroCartao(cartao.getNumeroCartao());
        if (existingCartao.isPresent()) {
            throw new RuntimeException("Cartão já existe");
        }
        cartao.setSaldo(new BigDecimal("500.00"));
        return cartaoRepository.save(cartao);
    }
}
