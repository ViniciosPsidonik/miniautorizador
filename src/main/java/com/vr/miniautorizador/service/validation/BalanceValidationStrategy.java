package com.vr.miniautorizador.service.validation;

import com.vr.miniautorizador.exception.InsufficientBalanceException;
import com.vr.miniautorizador.model.Cartao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Implementação da estratégia que garante saldo suficiente para a transação.
 */
@Component
public class BalanceValidationStrategy implements TransactionValidationStrategy {

    /**
     * Verifica se o saldo do cartão é maior ou igual ao valor solicitado.
     *
     * @param cartao      cartão alvo da transação
     * @param senhaCartao senha fornecida (não utilizada nesta validação)
     * @param valor       valor da transação
     * @throws InsufficientBalanceException quando o saldo é insuficiente
     */
    @Override
    public void validate(Cartao cartao, String senhaCartao, BigDecimal valor) {
        Optional.ofNullable(cartao.getSaldo())
                .filter(saldo -> saldo.compareTo(valor) >= 0)
                .orElseThrow(() -> new InsufficientBalanceException("SALDO_INSUFICIENTE"));
    }
}
