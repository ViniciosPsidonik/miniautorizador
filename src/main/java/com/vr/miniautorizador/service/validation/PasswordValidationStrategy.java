package com.vr.miniautorizador.service.validation;

import com.vr.miniautorizador.exception.InvalidPasswordException;
import com.vr.miniautorizador.model.Cartao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Implementação da estratégia que valida se a senha informada na transação
 * corresponde à senha armazenada no cartão.
 */
@Component
public class PasswordValidationStrategy implements TransactionValidationStrategy {

    /**
     * Verifica se a senha fornecida é igual à senha do cartão.
     *
     * @param cartao      cartão alvo da transação
     * @param senhaCartao senha fornecida pelo portador
     * @param valor       valor da transação (não utilizado nesta validação)
     * @throws InvalidPasswordException quando a senha não corresponder
     */
    @Override
    public void validate(Cartao cartao, String senhaCartao, BigDecimal valor) {
        Optional.ofNullable(cartao.getSenha())
                .filter(senhaCartao::equals)
                .orElseThrow(() -> new InvalidPasswordException("SENHA_INVALIDA"));
    }
}
