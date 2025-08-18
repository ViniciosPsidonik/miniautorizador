package com.vr.miniautorizador.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando a senha informada não corresponde à senha do cartão.
 * Mapeada para HTTP 422 (Unprocessable Entity).
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidPasswordException extends RuntimeException {
    /**
     * @param message normalmente "SENHA_INVALIDA"
     */
    public InvalidPasswordException(String message) {
        super(message);
    }
}
