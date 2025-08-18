package com.vr.miniautorizador.service;

import com.vr.miniautorizador.model.Cartao;
import com.vr.miniautorizador.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import com.vr.miniautorizador.exception.CardAlreadyExistsException;
import com.vr.miniautorizador.exception.CardNotFoundException;

/**
 * Camada de serviço responsável por operações de criação de cartão e consulta
 * de saldo.
 * Atende ao princípio de manter a lógica de negócio fora do controller.
 */
@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    /**
     * Cria um novo cartão com saldo inicial fixo de 500,00.
     *
     * @param cartao entidade contendo número e senha
     * @return cartão persistido com saldo inicial atribuído
     * @throws CardAlreadyExistsException quando já existir cartão com o mesmo
     *                                    número
     */
    public Cartao createCard(Cartao cartao) {
        cartaoRepository.findByNumeroCartao(cartao.getNumeroCartao())
                .ifPresent(c -> {
                    throw new CardAlreadyExistsException(cartao.getNumeroCartao(), cartao.getSenha());
                });

        cartao.setSaldo(new BigDecimal("500.00"));
        return cartaoRepository.save(cartao);
    }

    /**
     * Obtém o saldo de um cartão existente.
     *
     * @param numeroCartao número do cartão
     * @return saldo disponível
     * @throws CardNotFoundException quando não existir cartão com o número
     *                               informado
     */
    public BigDecimal getCardBalance(String numeroCartao) {
        return cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new CardNotFoundException("CARTAO_INEXISTENTE"))
                .getSaldo();
    }
}
