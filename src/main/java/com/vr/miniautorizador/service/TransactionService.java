package com.vr.miniautorizador.service;

import com.vr.miniautorizador.exception.CardNotFoundException;
import com.vr.miniautorizador.service.validation.TransactionValidationStrategy;
import com.vr.miniautorizador.service.validation.PasswordValidationStrategy;
import com.vr.miniautorizador.service.validation.BalanceValidationStrategy;
import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired(required = false)
    private List<TransactionValidationStrategy> validationStrategies = java.util.List.of(
            new PasswordValidationStrategy(),
            new BalanceValidationStrategy());

    @Transactional
    public void authorizeTransaction(String numeroCartao, String senhaCartao, BigDecimal valor) {
        Cartao cartao = cartaoRepository.findByNumeroCartaoForUpdate(numeroCartao)
                .orElseThrow(() -> new CardNotFoundException("CARTAO_INEXISTENTE"));

        validationStrategies.forEach(strategy -> strategy.validate(cartao, senhaCartao, valor));

        cartao.setSaldo(cartao.getSaldo().subtract(valor));
        cartaoRepository.save(cartao);
    }
}
