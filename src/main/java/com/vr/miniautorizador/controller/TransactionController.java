package com.vr.miniautorizador.controller;

import com.vr.miniautorizador.dto.TransactionRequestDTO;
import com.vr.miniautorizador.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

/**
 * REST controller responsável por autorizar transações de débito em cartões.
 */
@RestController
@RequestMapping("/transacoes")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Autoriza uma transação debitando o valor do cartão após validações de senha e
     * saldo.
     *
     * @param transactionRequestDTO DTO contendo número do cartão, senha e valor da
     *                              compra
     * @return string "OK" com status HTTP 201 (Created) quando a transação é
     *         autorizada
     */
    @PostMapping
    public ResponseEntity<String> authorizeTransaction(
            @Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        transactionService.authorizeTransaction(
                transactionRequestDTO.getNumeroCartao(),
                transactionRequestDTO.getSenhaCartao(),
                transactionRequestDTO.getValor());
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }
}
