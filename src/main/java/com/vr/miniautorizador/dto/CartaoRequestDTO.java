package com.vr.miniautorizador.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class CartaoRequestDTO {
    @NotBlank(message = "numeroCartao é obrigatório")
    @Pattern(regexp = "\\d{16}", message = "numeroCartao deve conter 16 dígitos")
    private String numeroCartao;

    @NotBlank(message = "senha é obrigatória")
    @Pattern(regexp = "\\d{4}", message = "senha deve conter 4 dígitos")
    private String senha;
}
