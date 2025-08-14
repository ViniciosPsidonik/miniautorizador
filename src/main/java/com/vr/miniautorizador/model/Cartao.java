package com.vr.miniautorizador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Cartao {

    @Id
    private String numeroCartao;
    private String senha;
    private BigDecimal saldo;

}
