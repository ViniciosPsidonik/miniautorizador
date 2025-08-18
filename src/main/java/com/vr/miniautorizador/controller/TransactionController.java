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

@RestController
@RequestMapping("/transacoes")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

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
