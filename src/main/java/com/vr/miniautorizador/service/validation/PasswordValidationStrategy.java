package com.vr.miniautorizador.service.validation;

import com.vr.miniautorizador.exception.InvalidPasswordException;
import com.vr.miniautorizador.model.Cartao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class PasswordValidationStrategy implements TransactionValidationStrategy {

    @Override
    public void validate(Cartao cartao, String senhaCartao, BigDecimal valor) {
        Optional.ofNullable(cartao.getSenha())
                .filter(senhaCartao::equals)
                .orElseThrow(() -> new InvalidPasswordException("SENHA_INVALIDA"));
    }
}
