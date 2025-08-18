package com.vr.miniautorizador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Entidade JPA que representa um cartão pré-pago.
 * <p>
 * Cada cartão é identificado unicamente por {@code numeroCartao} e contém
 * uma {@code senha} e um {@code saldo} monetário.
 * </p>
 */
@Entity
@Data
public class Cartao {

    /** Número do cartão (chave primária). */
    @Id
    private String numeroCartao;

    /** Senha numérica do cartão. */
    private String senha;

    /** Saldo disponível para transações. */
    private BigDecimal saldo;

}
