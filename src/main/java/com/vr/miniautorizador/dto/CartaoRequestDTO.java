package com.vr.miniautorizador.dto;

import lombok.Data;

@Data
public class CartaoRequestDTO {
    private String numeroCartao;
    private String senha;
}
