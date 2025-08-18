package com.vr.miniautorizador.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {
    @NotBlank(message = "numeroCartao é obrigatório")
    @Pattern(regexp = "\\d{16}", message = "numeroCartao deve conter 16 dígitos")
    private String numeroCartao;

    @NotBlank(message = "senhaCartao é obrigatória")
    @Pattern(regexp = "\\d{4}", message = "senhaCartao deve conter 4 dígitos")
    private String senhaCartao;

    @NotNull(message = "valor é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "valor deve ser positivo")
    private BigDecimal valor;
}
