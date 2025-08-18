package com.vr.miniautorizador.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando tenta-se criar um cartão que já existe na base.
 * Retorna HTTP 422 (Unprocessable Entity) com os dados do cartão existente.
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@Getter
public class CardAlreadyExistsException extends RuntimeException {
    private final String numeroCartao;
    private final String senha;

    /**
     * @param numeroCartao número do cartão já existente
     * @param senha        senha enviada na requisição
     */
    public CardAlreadyExistsException(String numeroCartao, String senha) {
        super("Cartão já existe");
        this.numeroCartao = numeroCartao;
        this.senha = senha;
    }
}
