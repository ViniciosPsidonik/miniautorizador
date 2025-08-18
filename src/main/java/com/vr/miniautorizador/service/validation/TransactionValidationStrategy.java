package com.vr.miniautorizador.service.validation;

import com.vr.miniautorizador.model.Cartao;

import java.math.BigDecimal;

public interface TransactionValidationStrategy {
    void validate(Cartao cartao, String senhaCartao, BigDecimal valor);
}
