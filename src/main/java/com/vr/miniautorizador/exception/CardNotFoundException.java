package com.vr.miniautorizador.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando o cartão requisitado não é encontrado.
 * Retorna HTTP 404 por padrão; global handler converte para 422 em operações
 * POST.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CardNotFoundException extends RuntimeException {
    /**
     * @param message chave de mensagem para o manipulador global
     */
    public CardNotFoundException(String message) {
        super(message);
    }
}
