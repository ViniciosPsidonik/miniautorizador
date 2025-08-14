package com.vr.miniautorizador.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {
    private String numeroCartao;
    private String senhaCartao;
    private BigDecimal valor;
}
