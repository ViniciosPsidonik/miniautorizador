package com.vr.miniautorizador.exception;

import com.vr.miniautorizador.dto.CartaoRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<CartaoRequestDTO> handleCardAlreadyExistsException(CardAlreadyExistsException ex) {
        CartaoRequestDTO responseBody = new CartaoRequestDTO();
        responseBody.setNumeroCartao(ex.getNumeroCartao());
        responseBody.setSenha(ex.getSenha());
        return new ResponseEntity<>(responseBody, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private static final Map<String, ResponseEntity<String>> CARD_NOT_FOUND_RESPONSES = Map.of(
            "CARTAO_INEXISTENTE", new ResponseEntity<>("CARTAO_INEXISTENTE", HttpStatus.UNPROCESSABLE_ENTITY));

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException ex, HttpServletRequest request) {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return CARD_NOT_FOUND_RESPONSES.getOrDefault(ex.getMessage(), new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException ex) {
        return new ResponseEntity<>("SENHA_INVALIDA", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return new ResponseEntity<>("SALDO_INSUFICIENTE", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "DADOS_INVALIDOS";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream().findFirst()
                .map(cv -> cv.getMessage()).orElse("DADOS_INVALIDOS");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    // Generic fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("ERRO_INTERNO", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
