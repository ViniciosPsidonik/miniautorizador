package com.vr.miniautorizador.service;

import com.vr.miniautorizador.exception.CardNotFoundException;
import com.vr.miniautorizador.exception.InsufficientBalanceException;
import com.vr.miniautorizador.exception.InvalidPasswordException;
import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Transactional
    public void authorizeTransaction(String numeroCartao, String senhaCartao, BigDecimal valor) {
        Optional<Cartao> cartaoOptional = cartaoRepository.findByNumeroCartao(numeroCartao);

        if (cartaoOptional.isEmpty()) {
            throw new CardNotFoundException("CARTAO_INEXISTENTE");
        }

        Cartao cartao = cartaoOptional.get();

        if (!cartao.getSenha().equals(senhaCartao)) {
            throw new InvalidPasswordException("SENHA_INVALIDA");
        }

        if (cartao.getSaldo().compareTo(valor) < 0) {
            throw new InsufficientBalanceException("SALDO_INSUFICIENTE");
        }

        cartao.setSaldo(cartao.getSaldo().subtract(valor));
        cartaoRepository.save(cartao);
    }
}
