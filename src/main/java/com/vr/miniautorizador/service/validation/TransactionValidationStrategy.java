package com.vr.miniautorizador.service.validation;

import com.vr.miniautorizador.model.Cartao;

import java.math.BigDecimal;

/**
 * Estratégia de validação executada antes de autorizar uma transação.
 * Cada implementação representa uma regra de negócio isolada (ex.: senha,
 * saldo).
 */
public interface TransactionValidationStrategy {
    /**
     * Executa a validação para a transação.
     *
     * @param cartao      entidade do cartão envolvido na transação
     * @param senhaCartao senha informada pelo portador
     * @param valor       valor da transação
     * @throws RuntimeException exceções de domínio caso a validação falhe
     */
    void validate(Cartao cartao, String senhaCartao, BigDecimal valor);
}
