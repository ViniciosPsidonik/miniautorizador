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

    public Cartao createCard(Cartao cartao) {
        Optional<Cartao> existingCartao = cartaoRepository.findByNumeroCartao(cartao.getNumeroCartao());
        if (existingCartao.isPresent()) {
            throw new RuntimeException("Cartão já existe");
        }
        cartao.setSaldo(new BigDecimal("500.00"));
        return cartaoRepository.save(cartao);
    }

    public BigDecimal getCardBalance(String numeroCartao) {
        Optional<Cartao> cartao = cartaoRepository.findByNumeroCartao(numeroCartao);
        if (cartao.isEmpty()) {
            throw new RuntimeException("Cartão inexistente");
        }
        return cartao.get().getSaldo();
    }
}
