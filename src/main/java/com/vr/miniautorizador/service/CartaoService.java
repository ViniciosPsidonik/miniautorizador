package com.vr.miniautorizador.service;

import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import com.vr.miniautorizador.exception.CardAlreadyExistsException;
import com.vr.miniautorizador.exception.CardNotFoundException;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    public Cartao createCard(Cartao cartao) {
        cartaoRepository.findByNumeroCartao(cartao.getNumeroCartao())
                .ifPresent(c -> {
                    throw new CardAlreadyExistsException(cartao.getNumeroCartao(), cartao.getSenha());
                });

        cartao.setSaldo(new BigDecimal("500.00"));
        return cartaoRepository.save(cartao);
    }

    public BigDecimal getCardBalance(String numeroCartao) {
        return cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new CardNotFoundException("CARTAO_INEXISTENTE"))
                .getSaldo();
    }
}
