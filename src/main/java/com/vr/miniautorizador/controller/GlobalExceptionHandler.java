package com.vr.miniautorizador.controller;

import com.vr.miniautorizador.dto.CartaoRequestDTO;
import com.vr.miniautorizador.exception.CardAlreadyExistsException;
import com.vr.miniautorizador.exception.CardNotFoundException;
import com.vr.miniautorizador.exception.InsufficientBalanceException;
import com.vr.miniautorizador.exception.InvalidPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<CartaoRequestDTO> handleCardAlreadyExistsException(CardAlreadyExistsException ex) {
        CartaoRequestDTO responseBody = new CartaoRequestDTO();
        responseBody.setNumeroCartao(ex.getNumeroCartao());
        responseBody.setSenha(ex.getSenha());
        return new ResponseEntity<>(responseBody, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException ex) {
        if (ex.getMessage().equals("CARTAO_INEXISTENTE")) {
            return new ResponseEntity<>("CARTAO_INEXISTENTE", HttpStatus.UNPROCESSABLE_ENTITY);
        } else { // For get balance endpoint
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException ex) {
        return new ResponseEntity<>("SENHA_INVALIDA", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return new ResponseEntity<>("SALDO_INSUFICIENTE", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // Generic handler for any other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("ERRO_INTERNO", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
