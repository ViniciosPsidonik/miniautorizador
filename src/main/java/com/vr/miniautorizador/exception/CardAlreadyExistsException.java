package com.vr.miniautorizador.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@Getter
public class CardAlreadyExistsException extends RuntimeException {
    private final String numeroCartao;
    private final String senha;

    public CardAlreadyExistsException(String numeroCartao, String senha) {
        super("Cartão já existe");
        this.numeroCartao = numeroCartao;
        this.senha = senha;
    }
}
