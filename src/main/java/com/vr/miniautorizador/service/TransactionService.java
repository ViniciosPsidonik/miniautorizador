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

/**
 * Serviço responsável por autorizar transações de débito.
 * <p>
 * Validações de negócio (senha, saldo, etc.) são aplicadas via o conjunto de
 * {@link com.vr.miniautorizador.service.validation.TransactionValidationStrategy}
 * injetado.
 * </p>
 */
@Service
public class TransactionService {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired(required = false)
    private List<TransactionValidationStrategy> validationStrategies = java.util.List.of(
            new PasswordValidationStrategy(),
            new BalanceValidationStrategy());

    /**
     * Autoriza uma transação debitando valor do saldo do cartão.
     * <p>
     * A sequência é:
     * <ol>
     * <li>Busca o cartão com lock pessimista.</li>
     * <li>Executa as validações definidas nas estratégias.</li>
     * <li>Debita o valor e persiste.</li>
     * </ol>
     * </p>
     * 
     * @param numeroCartao número do cartão
     * @param senhaCartao  senha informada na requisição
     * @param valor        valor da transação
     * @throws CardNotFoundException        se o cartão não existir
     * @throws InvalidPasswordException     se a senha não corresponder
     * @throws InsufficientBalanceException se o saldo for insuficiente
     */
    @Transactional
    public void authorizeTransaction(String numeroCartao, String senhaCartao, BigDecimal valor) {
        Cartao cartao = cartaoRepository.findByNumeroCartaoForUpdate(numeroCartao)
                .orElseThrow(() -> new CardNotFoundException("CARTAO_INEXISTENTE"));

        validationStrategies.forEach(strategy -> strategy.validate(cartao, senhaCartao, valor));

        cartao.setSaldo(cartao.getSaldo().subtract(valor));
        cartaoRepository.save(cartao);
    }
}
