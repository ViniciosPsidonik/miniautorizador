package com.vr.miniautorizador.service.validation;

import com.vr.miniautorizador.exception.InsufficientBalanceException;
import com.vr.miniautorizador.model.Cartao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class BalanceValidationStrategy implements TransactionValidationStrategy {

    @Override
    public void validate(Cartao cartao, String senhaCartao, BigDecimal valor) {
        Optional.ofNullable(cartao.getSaldo())
                .filter(saldo -> saldo.compareTo(valor) >= 0)
                .orElseThrow(() -> new InsufficientBalanceException("SALDO_INSUFICIENTE"));
    }
}
