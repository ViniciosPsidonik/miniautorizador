package com.vr.miniautorizador.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando o saldo do cartão é insuficiente para a transação.
 * Mapeada para HTTP 422 (Unprocessable Entity).
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InsufficientBalanceException extends RuntimeException {
    /**
     * @param message normalmente "SALDO_INSUFICIENTE"
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
